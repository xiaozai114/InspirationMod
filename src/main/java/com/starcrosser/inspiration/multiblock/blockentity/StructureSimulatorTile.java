package com.starcrosser.inspiration.multiblock.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.MachineTile;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.lowdragmc.mbd2.api.pattern.BlockPattern;
import com.lowdragmc.mbd2.api.pattern.MultiblockState;
import com.lowdragmc.mbd2.api.pattern.error.PatternError;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import com.starcrosser.inspiration.multiblock.SimulatorStatus;
import com.starcrosser.inspiration.multiblock.blockentity.part.DataHatchTile;
import com.starcrosser.inspiration.multiblock.blockentity.part.EnergyPortTile;
import com.starcrosser.inspiration.multiblock.blockentity.part.InspirationHatchTile;
import com.starcrosser.inspiration.multiblock.blockentity.part.OutputHatchTile;
import com.starcrosser.inspiration.multiblock.data.SimulationSpec;
import com.starcrosser.inspiration.multiblock.data.SimulationProgress;
import com.starcrosser.inspiration.multiblock.data.StructureDataItem;
import com.starcrosser.inspiration.multiblock.item.ProjectorItem;
import com.starcrosser.inspiration.multiblock.pattern.SimulatorPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 结构模拟机的控制器（主方块）。
 * <p>
 * 它是本模组第一台多方块机器：自己不存电、不装物品，所有输入输出都通过结构上的
 * **仓室**完成。每 {@link #CHECK_INTERVAL} 刻用 MBD2 的 {@link BlockPattern} 校验一次结构，
 * 成型后把结构里的仓室按类型收集起来，之后每刻从能量端口抽电、从灵感提供仓抽充能，
 * 按数据仓里那份结构数据的参数推进进度，跑完去战利品表里摸一次奖。
 * <p>
 * 只借 MBD2 的**结构层**：{@link #getPattern} 是形状定义，
 * {@link BlockPattern#checkPatternAtWithoutController} 是成型校验 ——
 * 控制器本身**不实现** MBD2 的 {@link IMultiController}，因为那会连带要求实现它的
 * 整套 {@code IMachine}（front facing、recipe logic、capability holder……），
 * 而这些正是我们想留在 Titanium 里的部分。代价是成型检测要自己遍历四个朝向。
 * <p>
 * 多方块结构**不能装插件**：Titanium 的插件槽依赖机器自身的能量缓冲与进度条，
 * 而这里的电和充能都来自外部仓室，插件改写容量没有意义，所以 {@code MachineTile}
 * 的四个 augment 槽保持空置（界面上也不显示）。
 */
public class StructureSimulatorTile extends MachineTile<StructureSimulatorTile> {

    /** 模拟的灵感充能消耗是固定的：8 mB/刻，与具体数据无关。 */
    public static final int CHARGE_PER_TICK = 8;

    /** 控制器内部的能量缓冲：只作周转，真正的电在能量端口里。 */
    private static final int ENERGY_BUFFER = 64_000;

    /** 每刻从能量端口往内部缓冲转运的上限。 */
    private static final int ENERGY_TRANSFER = 8_000;

    /** 结构重检间隔（刻）。 */
    private static final int CHECK_INTERVAL = 20;

    private static final int PROGRESS_X = 78;
    private static final int PROGRESS_Y = 38;

    private final SimulationProgress work = new SimulationProgress("shipwreck_data");

    private final Lock patternLock = new ReentrantLock();

    /** 上一次校验得到的多方块状态，里面保存着成员位置与错误信息。 */
    private MultiblockState multiblockState;

    /**
     * 校验失败时留住的那一次状态（取匹配到格子最多的一次）。
     * <p>
     * 以前失败就直接把 multiblockState 置 null，于是 {@link #getErrorPos()} 永远是 null，
     * 玩家搭错了只能瞎试。MBD2 在第一次不匹配的格子上会记录 {@code error}，留住它就能指出位置。
     */
    private MultiblockState failedState;

    private boolean formed;

    /** 成型后确定的结构朝向。 */
    private Direction facing = Direction.NORTH;

    @Save
    private ProgressBarComponent<StructureSimulatorTile> progressBar;

    /** 成型后收集到的仓室位置；结构失效时清空。 */
    private final List<BlockPos> energyPorts = new ArrayList<>();
    private final List<BlockPos> inspirationHatches = new ArrayList<>();
    private final List<BlockPos> outputHatches = new ArrayList<>();
    private final List<BlockPos> dataHatches = new ArrayList<>();

    /** 跑完一轮但还没塞进输出仓的产物。非空时不再开始下一轮。 */
    private final List<ItemStack> pendingOutput = new ArrayList<>();

    /** 供界面读取的状态；每次重算后由 {@code markForUpdate} 推给客户端。 */
    @Save
    private int status = SimulatorStatus.NOT_FORMED.id();

    /**
     * 同步给客户端的错误位置（{@code BlockPos.asLong()}），没有错误就是 {@link Long#MIN_VALUE}。
     * <p>
     * {@link #failedState} 只在服务端存在，客户端的界面读不到，所以另存一份随更新包推过去。
     */
    @Save
    private long errorPosPacked = Long.MIN_VALUE;

    public StructureSimulatorTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<StructureSimulatorTile>) ModMultiblocks.STRUCTURE_SIMULATOR.getBlock(),
                ModMultiblocks.STRUCTURE_SIMULATOR.type().get(), pos, state);

        ProgressBarComponent<StructureSimulatorTile> bar =
                new ProgressBarComponent<>(PROGRESS_X, PROGRESS_Y, 1);
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        bar.setColor(DyeColor.PURPLE);
        bar.setCanIncrease(this::canWork);
        bar.setCanReset(tile -> ((StructureSimulatorTile) tile).isProgressFull());
        bar.setOnTickWork(this::onWorkTick);
        bar.setOnFinishWork(this::onWorkFinish);
        this.progressBar = bar;
        addProgressBar(this.progressBar);
    }

    @Override
    public StructureSimulatorTile getSelf() {
        return this;
    }

    /**
     * 手持结构投影仪右键时**不打开界面**：投影仪要的就是「对着主方块投影」这个操作，
     * 一开 GUI 就把刚投出来的虚影挡住了。虚影本身在客户端画（{@code ProjectorPreviewHandler}）。
     */
    @Override
    public ItemInteractionResult onActivated(Player player, InteractionHand hand,
                                             Direction side, double hitX, double hitY, double hitZ) {
        if (player.getItemInHand(hand).getItem() instanceof ProjectorItem) {
            return ItemInteractionResult.SUCCESS;
        }
        return super.onActivated(player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    protected com.hrznstudio.titanium.component.energy.EnergyStorageComponent<StructureSimulatorTile> createEnergyStorage() {
        // 内部缓冲不接外部线缆（电只从能量端口来），所以进出上限都设 0，
        // 控制器自己用 setEnergyStored 读写。
        return new com.hrznstudio.titanium.component.energy.EnergyStorageComponent<>(
                ENERGY_BUFFER, 0, 0, 8, 14);
    }

    /* ============================ 每刻 ============================ */

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, StructureSimulatorTile self) {
        super.serverTick(level, pos, state, self);
        if (level.getGameTime() % CHECK_INTERVAL == 0) {
            checkStructure();
        }
        if (!formed) {
            return;
        }
        pullEnergy();
        flushPendingOutput();
        updateStatus();
    }

    /** 用 MBD2 的结构层校验一次，成功就收集仓室。 */
    private void checkStructure() {
        MultiblockState matched = null;
        MultiblockState closest = null;
        int closestMatched = -1;
        for (Direction candidate : Direction.Plane.HORIZONTAL) {
            // 锚点就是控制器自己的位置：MBD2 用图案里的 controller 谓词算 centerOffset，
            // 会自己把 (1,1,2) 那一格对齐到这里。别改成「图案原点」，否则整体平移匹配不上。
            MultiblockState state = new MultiblockState(level, worldPosition);
            state.setCheckingFacing(candidate);
            if (getPattern().checkPatternAtWithoutController(state, candidate)) {
                matched = state;
                this.facing = candidate;
                break;
            }
            // 没成型也留住「匹配到最多格子」的那一次，好告诉玩家错在哪一格
            int count = state.getCache() == null ? 0 : state.getCache().size();
            if (count > closestMatched) {
                closestMatched = count;
                closest = state;
            }
        }
        if (matched != null) {
            boolean wasFormed = formed;
            this.multiblockState = matched;
            this.failedState = null;
            this.formed = true;
            collectHatches(matched);
            if (!wasFormed) {
                markForUpdate();
            }
        } else {
            this.multiblockState = null;
            this.failedState = closest;
            if (formed) {
                this.formed = false;
                clearHatches();
            }
        }
        BlockPos error = getErrorPos();
        this.errorPosPacked = error == null ? Long.MIN_VALUE : error.asLong();
        updateStatus();
    }

    /** 客户端可见的错误位置（服务端算好后同步过来）；没有错误就是 null。 */
    public BlockPos getSyncedErrorPos() {
        return errorPosPacked == Long.MIN_VALUE ? null : BlockPos.of(errorPosPacked);
    }

    /** 把结构里的仓室按类型分好类。位置来自 MBD2 记录的匹配结果。 */
    private void collectHatches(MultiblockState state) {
        clearHatches();
        for (BlockPos p : state.getCache()) {
            if (p.equals(worldPosition)) {
                continue;
            }
            var tile = level.getBlockEntity(p);
            if (tile instanceof EnergyPortTile) {
                energyPorts.add(p.immutable());
            } else if (tile instanceof InspirationHatchTile) {
                inspirationHatches.add(p.immutable());
            } else if (tile instanceof OutputHatchTile) {
                outputHatches.add(p.immutable());
            } else if (tile instanceof DataHatchTile) {
                dataHatches.add(p.immutable());
            }
        }
    }

    private void clearHatches() {
        energyPorts.clear();
        inspirationHatches.clear();
        outputHatches.clear();
        dataHatches.clear();
    }

    /** 从能量端口把电搬到内部缓冲。 */
    private void pullEnergy() {
        int room = getEnergyStorage().getMaxEnergyStored() - getEnergyStorage().getEnergyStored();
        if (room <= 0) {
            return;
        }
        for (BlockPos p : energyPorts) {
            if (room <= 0) {
                break;
            }
            if (level.getBlockEntity(p) instanceof EnergyPortTile port) {
                int got = port.getEnergyStorage().extractEnergy(Math.min(room, ENERGY_TRANSFER), false);
                if (got > 0) {
                    getEnergyStorage().setEnergyStored(getEnergyStorage().getEnergyStored() + got);
                    room -= got;
                }
            }
        }
    }

    /* ============================ 进度条回调 ============================ */

    private boolean isProgressFull() {
        return progressBar.getProgress() >= progressBar.getMaxProgress();
    }

    private boolean canWork(StructureSimulatorTile tile) {
        if (!formed || level == null || level.isClientSide) {
            return false;
        }
        if (!pendingOutput.isEmpty()) {
            return false;
        }
        StructureDataItem card = dataItem();
        if (card == null) {
            return false;
        }
        SimulationSpec spec = card.spec();
        if (work.bind(card, spec.duration(), progressBar)) {
            setChanged();
        }
        return getEnergyStorage().getEnergyStored() >= spec.energyPerTick()
                && totalCharge() >= CHARGE_PER_TICK;
    }

    private void onWorkTick() {
        SimulationSpec spec = currentSpec();
        if (spec == null) {
            return;
        }
        getEnergyStorage().setEnergyStored(
                Math.max(0, getEnergyStorage().getEnergyStored() - spec.energyPerTick()));
        drainCharge(CHARGE_PER_TICK);
    }

    private void onWorkFinish() {
        SimulationSpec spec = currentSpec();
        if (spec == null) {
            return;
        }
        pendingOutput.addAll(rollLoot(spec));
        markForUpdate();
    }

    /* ============================ 数据与消耗 ============================ */

    /** 数据仓里那份结构数据；没有或不是数据就返回 null。 */
    private StructureDataItem dataItem() {
        for (BlockPos p : dataHatches) {
            if (level.getBlockEntity(p) instanceof DataHatchTile hatch) {
                ItemStack stack = hatch.getInventory().getStackInSlot(0);
                if (stack.getItem() instanceof StructureDataItem data) {
                    return data;
                }
            }
        }
        return null;
    }

    private SimulationSpec currentSpec() {
        StructureDataItem data = dataItem();
        return data == null ? null : data.spec();
    }

    /** 所有灵感提供仓里的充能总量（mB）。 */
    private int totalCharge() {
        int total = 0;
        for (BlockPos p : inspirationHatches) {
            if (level.getBlockEntity(p) instanceof InspirationHatchTile hatch) {
                total += hatch.getChargeAmount();
            }
        }
        return total;
    }

    /** 从灵感提供仓里扣充能，扣够为止。 */
    private void drainCharge(int amount) {
        int left = amount;
        for (BlockPos p : inspirationHatches) {
            if (left <= 0) {
                break;
            }
            if (level.getBlockEntity(p) instanceof InspirationHatchTile hatch) {
                left -= hatch.drainForced(left).getAmount();
            }
        }
    }

    /* ============================ 产出 ============================ */

    /** 按数据里的候选战利品表随机挑一张 roll 一次。 */
    private List<ItemStack> rollLoot(SimulationSpec spec) {
        if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel)
                || spec.lootTables().isEmpty()) {
            return List.of();
        }
        List<net.minecraft.resources.ResourceLocation> tables = spec.lootTables();
        net.minecraft.resources.ResourceLocation id =
                tables.get(level.random.nextInt(tables.size()));
        LootTable table = serverLevel.getServer().reloadableRegistries()
                .getLootTable(net.minecraft.resources.ResourceKey.create(
                        net.minecraft.core.registries.Registries.LOOT_TABLE, id));
        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .create(LootContextParamSets.CHEST);
        return new ArrayList<>(table.getRandomItems(params, level.random.nextLong()));
    }

    /** 把待输出产物塞进输出仓；塞不下的留在 {@link #pendingOutput} 里等下一刻。 */
    private void flushPendingOutput() {
        if (pendingOutput.isEmpty()) {
            return;
        }
        pendingOutput.removeIf(stack -> {
            if (stack.isEmpty()) {
                return true;
            }
            for (BlockPos p : outputHatches) {
                if (level.getBlockEntity(p) instanceof OutputHatchTile hatch) {
                    stack.setCount(insert(hatch, stack));
                    if (stack.isEmpty()) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    /** 往一个输出仓里塞，返回**没塞进去**的剩余数量。 */
    private int insert(OutputHatchTile hatch, ItemStack stack) {
        var inventory = hatch.getInventory();
        int left = stack.getCount();
        for (int slot = 0; slot < inventory.getSlots() && left > 0; slot++) {
            ItemStack inSlot = inventory.getStackInSlot(slot);
            if (inSlot.isEmpty()) {
                int fit = Math.min(left, inventory.getSlotLimit(slot));
                inventory.setStackInSlot(slot, stack.copyWithCount(fit));
                left -= fit;
            } else if (ItemStack.isSameItemSameComponents(inSlot, stack)) {
                int fit = Math.min(left, inventory.getSlotLimit(slot) - inSlot.getCount());
                if (fit > 0) {
                    inSlot.grow(fit);
                    left -= fit;
                }
            }
        }
        return left;
    }

    /* ============================ 状态 ============================ */

    private void updateStatus() {
        SimulatorStatus next;
        if (!formed) {
            next = SimulatorStatus.NOT_FORMED;
        } else if (currentSpec() == null) {
            next = SimulatorStatus.NO_DATA;
        } else if (!pendingOutput.isEmpty()) {
            next = SimulatorStatus.OUTPUT_FULL;
        } else if (getEnergyStorage().getEnergyStored() < currentSpec().energyPerTick()) {
            next = SimulatorStatus.NO_ENERGY;
        } else if (totalCharge() < CHARGE_PER_TICK) {
            next = SimulatorStatus.NO_CHARGE;
        } else {
            next = SimulatorStatus.WORKING;
        }
        if (next.id() != this.status) {
            this.status = next.id();
            markForUpdate();
        }
    }

    /** 当前状态，界面与自检都读它。 */
    public SimulatorStatus getStatus() {
        return SimulatorStatus.fromId(status);
    }

    /** 进度条；界面与自检读它。 */
    public ProgressBarComponent<StructureSimulatorTile> getProgressBar() {
        return progressBar;
    }

    public boolean isFormed() {
        return formed;
    }

    /** 结构校验失败的位置，界面上可以用来提示；没有错误就是 null。 */
    public BlockPos getErrorPos() {
        MultiblockState state = failedState;
        if (formed || state == null || !state.hasError()) {
            return null;
        }
        PatternError error = state.error;
        if (error != null) {
            BlockPos pos = error.getPos();
            if (pos != null) {
                return pos;
            }
        }
        return state.getPos();
    }

    /** 失败原因（"期望 X 但实际是 Y"），MBD2 自己拼好的；没有错误就是 null。 */
    public Component getErrorInfo() {
        MultiblockState state = failedState;
        if (formed || state == null || !state.hasError() || state.error == null) {
            return null;
        }
        return state.error.getErrorInfo();
    }

    /* ============================ MBD2 结构层 ============================ */

    /** 结构形状；MBD2 只在这一处参与。 */
    public BlockPattern getPattern() {
        return SimulatorPattern.get();
    }

    /** 成型后确定的朝向，供预览与调试读取。 */
    public Direction getStructureFacing() {
        return facing;
    }

    /* ============================ 客户端 ============================ */

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        addGuiAddonFactory(() -> new com.starcrosser.inspiration.client.extensions.StatusAddon(this));
    }

    /* ============================ 持久化 ============================ */

    /**
     * 已经 roll 出来、但还没塞进输出仓的产物必须落盘：
     * 否则重启或区块一卸载，这一轮的产出就凭空蒸发，玩家白跑一次模拟。
     */
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        work.save(tag);
        tag.putString("SimulatorFacing", facing.getName());
        if (!pendingOutput.isEmpty()) {
            ListTag list = new ListTag();
            for (ItemStack stack : pendingOutput) {
                if (!stack.isEmpty()) {
                    list.add(stack.save(registries));
                }
            }
            tag.put("PendingOutput", list);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        work.load(tag);
        Direction saved = Direction.byName(tag.getString("SimulatorFacing"));
        if (saved != null) {
            this.facing = saved;
        }
        pendingOutput.clear();
        ListTag list = tag.getList("PendingOutput", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = ItemStack.parseOptional(registries, list.getCompound(i));
            if (!stack.isEmpty()) {
                pendingOutput.add(stack);
            }
        }
    }
}
