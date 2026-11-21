package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.PoweredTile;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.client.util.FacingLabels;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import com.starcrosser.inspiration.voidenergy.VoidEnergyNetwork;
import com.starcrosser.inspiration.voidenergy.entity.VoidClusterEntity;
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

import javax.annotation.Nonnull;

/**
 * 虚空能量凝聚器：把团簇的 ve 凝成虚空露珠。
 * <p>
 * 不消耗灵感充能，所以与培养缸一样直接继承 {@link PoweredTile}（没有充能槽与插件槽），
 * 只吃电 + ve：每刻 16 ve、共 1024 ve（64 刻）产出 1 个露珠。
 * ve 从 {@link VoidEnergyNetwork} 取：自己那格有团簇，或半径 2 内有中继器。
 */
public class VoidCondenserTile extends PoweredTile<VoidCondenserTile> {

    public static final int ENERGY_CAPACITY = 200_000;
    public static final int MAX_ENERGY_INPUT = 10_000;
    /** 每刻最多吃掉的 ve，也是进度条每刻的步长。 */
    public static final int VE_PER_TICK = 16;
    /** 产出一个露珠需要的 ve。 */
    public static final int VE_PER_DEW = 1_024;
    /** 每刻消耗的电力。 */
    public static final int ENERGY_PER_TICK = 500;
    public static final int DURATION = VE_PER_DEW / VE_PER_TICK;

    @Save
    private SidedInventoryComponent<VoidCondenserTile> outputSlot;
    @Save
    private ProgressBarComponent<VoidCondenserTile> progressBar;

    public VoidCondenserTile(MachineSpec spec, BasicTileBlock<VoidCondenserTile> block,
                             BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(block, type, pos, state);

        SidedInventoryComponent<VoidCondenserTile> output = new SidedInventoryComponent<>("output", 116, 39, 1, 0);
        output.setRange(1, 1);
        output.setColor(DyeColor.LIGHT_BLUE);
        output.setFacingHandlerPos(26, 84);
        this.outputSlot = output;
        addInventory(this.outputSlot);

        ProgressBarComponent<VoidCondenserTile> bar = new ProgressBarComponent<>(78, 38, DURATION);
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        bar.setColor(spec.barColor());
        bar.setCanIncrease(this::canProcess);
        bar.setCanReset(tile -> tile.progressBar.getProgress() >= tile.progressBar.getMaxProgress());
        bar.setOnTickWork(this::consume);
        bar.setOnFinishWork(this::produce);
        this.progressBar = bar;
        addProgressBar(this.progressBar);
    }

    @Nonnull
    @Override
    protected EnergyStorageComponent<VoidCondenserTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(ENERGY_CAPACITY, MAX_ENERGY_INPUT, 0, 8, 14);
    }

    @Nonnull
    @Override
    public VoidCondenserTile getSelf() {
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

    private VoidClusterEntity cluster() {
        if (level == null || level.isClientSide) {
            return null;
        }
        return VoidEnergyNetwork.findCluster(level, worldPosition);
    }

    private boolean canAccept() {
        ItemStack current = outputSlot.getStackInSlot(0);
        return current.isEmpty()
                || (current.is(ModItems.VOID_DEW.get()) && current.getCount() < outputSlot.getSlotLimit(0));
    }

    private boolean canProcess(VoidCondenserTile tile) {
        if (level == null || level.isClientSide) {
            return false;
        }
        VoidClusterEntity cluster = cluster();
        if (cluster == null || cluster.energy() < VE_PER_TICK) {
            return false;
        }
        return getEnergyStorage().getEnergyStored() >= ENERGY_PER_TICK && canAccept();
    }

    private void consume() {
        VoidClusterEntity cluster = cluster();
        if (cluster == null || level == null || level.isClientSide) {
            return;
        }
        cluster.addEnergy(-VE_PER_TICK);
        getEnergyStorage().setEnergyStored(getEnergyStorage().getEnergyStored() - ENERGY_PER_TICK);
    }

    private void produce() {
        outputSlot.insertItem(0, new ItemStack(ModItems.VOID_DEW.get()), false);
        markForUpdate();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        FacingLabels.add(this, outputSlot);
    }
}
