package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.client.util.FacingLabels;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;

/**
 * 零点能发电机：每 1200 刻发一次电，产出 {@code 1 + n × 1.2G} FE，n 是槽里的增幅器数量（最多 64）。
 * <p>
 * 128 GFE 远超 int，所以这里**不用 Titanium 的能量组件**：缓冲是 {@link #stored}（long），
 * 界面上那根「电量条」其实是百分比（0~1000 映射到 0~100%），每刻刷新一次。
 * 对外只出不进：每刻把缓冲往六个邻居推，单面单次最多 {@link #MAX_PUSH_PER_TICK}。
 */
public class ZeroPointGeneratorTile extends ActiveTile<ZeroPointGeneratorTile> {

    /** 128 GFE。 */
    public static final long CAPACITY = 128_000_000_000L;
    /** 每个增幅器每分钟贡献的 FE。 */
    public static final long FE_PER_AMPLIFIER = 1_200_000_000L;
    /** 发电周期（刻）。 */
    public static final int CYCLE_TICKS = 1_200;
    /** 单个增幅器槽的堆叠上限。 */
    public static final int MAX_AMPLIFIERS = 64;
    /** 每刻向单个邻居推送的上限。 */
    public static final int MAX_PUSH_PER_TICK = 200_000;
    /** 电量条的满值（百分比 ×10）。 */
    private static final int BUFFER_BAR_MAX = 1_000;

    @Save
    private SidedInventoryComponent<ZeroPointGeneratorTile> amplifierSlot;
    @Save
    private ProgressBarComponent<ZeroPointGeneratorTile> cycleBar;
    @Save
    private ProgressBarComponent<ZeroPointGeneratorTile> bufferBar;
    /** 真实电量（long），不受 Titanium 的 int 组件限制。 */
    @Save
    private long stored;

    public ZeroPointGeneratorTile(BasicTileBlock<ZeroPointGeneratorTile> block, BlockEntityType<?> type,
                                  BlockPos pos, BlockState state) {
        super(block, type, pos, state);

        SidedInventoryComponent<ZeroPointGeneratorTile> amplifiers =
                new SidedInventoryComponent<>("amplifiers", 80, 35, 1, 0);
        amplifiers.setRange(MAX_AMPLIFIERS, MAX_AMPLIFIERS);
        amplifiers.setColor(DyeColor.PURPLE);
        amplifiers.setInputFilter((stack, index) -> stack.is(ModItems.ZERO_POINT_AMPLIFIER.get()));
        amplifiers.setFacingHandlerPos(8, 84);
        this.amplifierSlot = amplifiers;
        addInventory(this.amplifierSlot);

        ProgressBarComponent<ZeroPointGeneratorTile> cycle =
                new ProgressBarComponent<>(78, 60, CYCLE_TICKS);
        cycle.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        cycle.setColor(DyeColor.LIME);
        cycle.setCanIncrease(tile -> true);
        cycle.setCanReset(tile -> tile.cycleBar.getProgress() >= tile.cycleBar.getMaxProgress());
        cycle.setOnFinishWork(this::generate);
        this.cycleBar = cycle;
        addProgressBar(this.cycleBar);

        ProgressBarComponent<ZeroPointGeneratorTile> buffer =
                new ProgressBarComponent<>(8, 14, BUFFER_BAR_MAX);
        buffer.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        buffer.setColor(DyeColor.LIGHT_BLUE);
        buffer.setCanIncrease(tile -> false);
        buffer.setCanReset(tile -> false);
        this.bufferBar = buffer;
        addProgressBar(this.bufferBar);
    }

    @Nonnull
    @Override
    public ZeroPointGeneratorTile getSelf() {
        return this;
    }

    @Override
    public ItemInteractionResult onActivated(Player player, InteractionHand hand, Direction facing,
                                             double hitX, double hitY, double hitZ) {
        if (level != null && !level.isClientSide) {
            openGui(player);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void serverTick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state,
                           ZeroPointGeneratorTile self) {
        super.serverTick(level, pos, state, self);
        if (level.isClientSide) {
            return;
        }
        pushEnergy(level, pos);
        int percentTenths = (int) Math.min(BUFFER_BAR_MAX, stored * BUFFER_BAR_MAX / CAPACITY);
        if (bufferBar.getProgress() != percentTenths) {
            bufferBar.setProgress(percentTenths);
            markForUpdate();
        }
    }

    private void generate() {
        int amplifiers = amplifierSlot.getStackInSlot(0).getCount();
        long produced = 1L + amplifiers * FE_PER_AMPLIFIER;
        stored = Math.min(CAPACITY, stored + produced);
        markForUpdate();
    }

    private void pushEnergy(net.minecraft.world.level.Level level, BlockPos pos) {
        if (stored <= 0) {
            return;
        }
        for (Direction side : Direction.values()) {
            if (stored <= 0) {
                return;
            }
            IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                    pos.relative(side), side.getOpposite());
            if (target == null || !target.canReceive()) {
                continue;
            }
            int request = (int) Math.min(stored, MAX_PUSH_PER_TICK);
            int accepted = target.receiveEnergy(request, false);
            if (accepted > 0) {
                stored -= accepted;
            }
        }
    }

    public long stored() {
        return stored;
    }

    public int amplifiers() {
        return amplifierSlot.getStackInSlot(0).getCount();
    }

    public ItemStack amplifierStack() {
        return amplifierSlot.getStackInSlot(0);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        FacingLabels.add(this, amplifierSlot);
        addGuiAddonFactory(() -> new com.starcrosser.inspiration.client.extensions
                .ZeroPointStatusAddon(this));
    }
}
