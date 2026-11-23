package com.starcrosser.inspiration.multiblock.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.MachineTile;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.lowdragmc.mbd2.api.pattern.MultiblockState;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import com.starcrosser.inspiration.multiblock.data.DimensionDataItem;
import com.starcrosser.inspiration.multiblock.data.MineralTable;
import com.starcrosser.inspiration.multiblock.pattern.MineralPattern;
import com.starcrosser.inspiration.multiblock.blockentity.part.DataHatchTile;
import com.starcrosser.inspiration.multiblock.blockentity.part.EnergyPortTile;
import com.starcrosser.inspiration.multiblock.blockentity.part.OutputHatchTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * 矿物模拟机的控制器。
 * <p>
 * 成型逻辑与结构模拟机一致（MBD2 结构层 + 每 20 刻重检）；差别在「作业」：
 * 读数据仓里那份 {@link DimensionDataItem} 的权重表，按表轮产矿物。
 * 数据本身**不消耗**，拔掉就停机。
 * <p>
 * 电只从能量端口进；控制器内部的缓冲只作周转。
 */
public class MineralSimulatorTile extends MachineTile<MineralSimulatorTile> {

    public static final int ENERGY_BUFFER = 64_000;
    public static final int ENERGY_TRANSFER = 8_000;
    private static final int CHECK_INTERVAL = 20;
    private static final int PROGRESS_X = 78;
    private static final int PROGRESS_Y = 38;

    /** 界面状态：0 未成型 / 1 缺数据 / 2 缺电 / 3 运行中。 */
    public static final int STATUS_NOT_FORMED = 0;
    public static final int STATUS_NO_DATA = 1;
    public static final int STATUS_NO_ENERGY = 2;
    public static final int STATUS_RUNNING = 3;

    private MultiblockState multiblockState;
    private boolean formed;
    private Direction facing = Direction.NORTH;

    @Save
    private ProgressBarComponent<MineralSimulatorTile> progressBar;
    @Save
    private int status = STATUS_NOT_FORMED;

    private final List<BlockPos> energyPorts = new ArrayList<>();
    private final List<BlockPos> outputHatches = new ArrayList<>();
    private final List<BlockPos> dataHatches = new ArrayList<>();

    /** 本轮使用的表；每轮开始时从数据仓读取。 */
    private MineralTable activeTable;

    @SuppressWarnings("unchecked")
    public MineralSimulatorTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<MineralSimulatorTile>) ModMultiblocks.MINERAL_SIMULATOR.getBlock(),
                ModMultiblocks.MINERAL_SIMULATOR.type().get(), pos, state);
        ProgressBarComponent<MineralSimulatorTile> bar =
                new ProgressBarComponent<>(PROGRESS_X, PROGRESS_Y, 1_200);
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        bar.setCanIncrease(this::canWork);
        bar.setCanReset(tile -> tile.progressBar.getProgress() >= tile.progressBar.getMaxProgress());
        bar.setOnTickWork(this::consume);
        bar.setOnFinishWork(this::produce);
        this.progressBar = bar;
        addProgressBar(this.progressBar);
    }

    @Override
    public MineralSimulatorTile getSelf() {
        return this;
    }

    public ProgressBarComponent<MineralSimulatorTile> getProgressBar() {
        return progressBar;
    }

    @Override
    protected EnergyStorageComponent<MineralSimulatorTile> createEnergyStorage() {
        // 不接外部线缆：电只从能量端口搬进内部缓冲
        return new EnergyStorageComponent<>(ENERGY_BUFFER, 0, 0, 8, 14);
    }

    @Override
    public ItemInteractionResult onActivated(Player player, InteractionHand hand, Direction side,
                                             double hitX, double hitY, double hitZ) {
        if (level != null && !level.isClientSide) {
            openGui(player);
        }
        return ItemInteractionResult.SUCCESS;
    }

    public int status() {
        return status;
    }

    public boolean isFormed() {
        return formed;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, MineralSimulatorTile self) {
        super.serverTick(level, pos, state, self);
        if (level.isClientSide) {
            return;
        }
        if (level.getGameTime() % CHECK_INTERVAL == 0) {
            checkStructure();
        }
        if (!formed) {
            return;
        }
        pullEnergy();
        refreshStatus();
    }

    /* ============================ 成型 ============================ */

    private void checkStructure() {
        MultiblockState matched = null;
        for (Direction candidate : Direction.Plane.HORIZONTAL) {
            MultiblockState candidateState = new MultiblockState(level, worldPosition);
            candidateState.setCheckingFacing(candidate);
            if (MineralPattern.get().checkPatternAtWithoutController(candidateState, candidate)) {
                matched = candidateState;
                this.facing = candidate;
                break;
            }
        }
        if (matched != null) {
            this.multiblockState = matched;
            this.formed = true;
            collectHatches(matched);
        } else {
            this.multiblockState = null;
            if (formed) {
                this.formed = false;
                clearHatches();
                progressBar.setProgress(0);
            }
        }
        refreshStatus();
    }

    private void collectHatches(MultiblockState state) {
        clearHatches();
        for (BlockPos p : state.getCache()) {
            if (p.equals(worldPosition)) {
                continue;
            }
            var tile = level.getBlockEntity(p);
            if (tile instanceof EnergyPortTile) {
                energyPorts.add(p.immutable());
            } else if (tile instanceof OutputHatchTile) {
                outputHatches.add(p.immutable());
            } else if (tile instanceof DataHatchTile) {
                dataHatches.add(p.immutable());
            }
        }
    }

    private void clearHatches() {
        energyPorts.clear();
        outputHatches.clear();
        dataHatches.clear();
        activeTable = null;
    }

    private void pullEnergy() {
        int room = getEnergyStorage().getMaxEnergyStored() - getEnergyStorage().getEnergyStored();
        for (BlockPos p : energyPorts) {
            if (room <= 0) {
                return;
            }
            if (level.getBlockEntity(p) instanceof EnergyPortTile port) {
                int moved = port.getEnergyStorage().extractEnergy(Math.min(room, ENERGY_TRANSFER), false);
                if (moved > 0) {
                    getEnergyStorage().setEnergyStored(getEnergyStorage().getEnergyStored() + moved);
                    room -= moved;
                }
            }
        }
    }

    /* ============================ 作业 ============================ */

    private MineralTable table() {
        for (BlockPos p : dataHatches) {
            if (level.getBlockEntity(p) instanceof DataHatchTile hatch) {
                ItemStack data = hatch.getInventory().getStackInSlot(0);
                if (data.getItem() instanceof DimensionDataItem item) {
                    return item.table();
                }
            }
        }
        return null;
    }

    private boolean hasOutputRoom(ItemStack stack) {
        for (BlockPos p : outputHatches) {
            if (level.getBlockEntity(p) instanceof OutputHatchTile hatch
                    && insert(hatch, stack.copy(), true) == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean canWork(MineralSimulatorTile tile) {
        if (level == null || level.isClientSide || !formed) {
            return false;
        }
        MineralTable table = table();
        if (table == null) {
            activeTable = null;
            return false;
        }
        if (activeTable != table) {
            activeTable = table;
            progressBar.setMaxProgress(table.duration());
        }
        return getEnergyStorage().getEnergyStored() >= table.energyPerTick()
                && hasOutputRoom(table.pick(net.minecraft.util.RandomSource.create(0)));
    }

    private void consume() {
        if (activeTable == null) {
            return;
        }
        getEnergyStorage().setEnergyStored(
                getEnergyStorage().getEnergyStored() - activeTable.energyPerTick());
    }

    private void produce() {
        if (activeTable == null) {
            return;
        }
        ItemStack roll = activeTable.pick(level.getRandom());
        for (BlockPos p : outputHatches) {
            if (level.getBlockEntity(p) instanceof OutputHatchTile hatch) {
                int left = insert(hatch, roll.copy(), false);
                if (left == 0) {
                    markForUpdate();
                    return;
                }
                roll.setCount(left);
            }
        }
        // 全塞不进去就掉在控制器上方，不让产物凭空消失
        net.minecraft.world.level.block.Block.popResource(level, worldPosition.above(), roll);
        markForUpdate();
    }

    /** 往输出仓里塞，返回没塞进去的剩余数量；{@code simulate} 为 true 时只算不改。 */
    private int insert(OutputHatchTile hatch, ItemStack stack, boolean simulate) {
        var inventory = hatch.getInventory();
        int left = stack.getCount();
        for (int slot = 0; slot < inventory.getSlots() && left > 0; slot++) {
            ItemStack inSlot = inventory.getStackInSlot(slot);
            if (inSlot.isEmpty()) {
                int fit = Math.min(left, inventory.getSlotLimit(slot));
                if (!simulate) {
                    inventory.setStackInSlot(slot, stack.copyWithCount(fit));
                }
                left -= fit;
            } else if (ItemStack.isSameItemSameComponents(inSlot, stack)) {
                int fit = Math.min(left, inventory.getSlotLimit(slot) - inSlot.getCount());
                if (fit > 0) {
                    if (!simulate) {
                        inSlot.grow(fit);
                    }
                    left -= fit;
                }
            }
        }
        return left;
    }

    private void refreshStatus() {
        int next;
        if (!formed) {
            next = STATUS_NOT_FORMED;
        } else if (table() == null) {
            next = STATUS_NO_DATA;
        } else if (getEnergyStorage().getEnergyStored() < (activeTable == null ? 1 : activeTable.energyPerTick())) {
            next = STATUS_NO_ENERGY;
        } else {
            next = STATUS_RUNNING;
        }
        if (next != status) {
            status = next;
            markForUpdate();
        }
    }
}
