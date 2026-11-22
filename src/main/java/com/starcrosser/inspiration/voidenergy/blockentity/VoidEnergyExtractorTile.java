package com.starcrosser.inspiration.voidenergy.blockentity;

import com.starcrosser.inspiration.voidenergy.ModVoidEnergy;
import com.starcrosser.inspiration.voidenergy.VoidEnergyNetwork;
import com.starcrosser.inspiration.voidenergy.block.CrackedBedrockBlock;
import com.starcrosser.inspiration.voidenergy.entity.VoidClusterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * 虚空能量提取器：站在开裂的基岩上，每刻给上方一格 +1 ve。
 * <p>
 * 需要电力：每提 1 ve 扣 {@link #ENERGY_PER_TICK} FE，电量不够就停着不动（不产 ve、也不啃电）。
 * 上方没有团簇且可以放置时自动生成一个；上方被方块占住就什么也不做。
 * 能量接口只进不出，挂载见 {@code VoidEnergyCapabilities}。
 */
public class VoidEnergyExtractorTile extends BlockEntity {

    /** 每刻供给的虚空能量（ve）。 */
    public static final int PER_TICK = 1;
    public static final int ENERGY_CAPACITY = 20_000;
    public static final int MAX_ENERGY_INPUT = 1_000;
    /** 产出 1 ve 需要的电量。 */
    public static final int ENERGY_PER_TICK = 50;

    private int energy;

    private final IEnergyStorage energyHandler = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int toReceive, boolean simulate) {
            int accepted = Math.min(toReceive, Math.min(MAX_ENERGY_INPUT, ENERGY_CAPACITY - energy));
            if (accepted <= 0) {
                return 0;
            }
            if (!simulate) {
                energy += accepted;
                setChanged();
            }
            return accepted;
        }

        @Override
        public int extractEnergy(int toExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return energy;
        }

        @Override
        public int getMaxEnergyStored() {
            return ENERGY_CAPACITY;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    };

    public VoidEnergyExtractorTile(BlockPos pos, BlockState state) {
        super(ModVoidEnergy.VOID_ENERGY_EXTRACTOR_TYPE.get(), pos, state);
    }

    public IEnergyStorage energyHandler() {
        return energyHandler;
    }

    public int energy() {
        return energy;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (energy < ENERGY_PER_TICK) {
            return;
        }
        if (!(level.getBlockState(pos.below()).getBlock() instanceof CrackedBedrockBlock)) {
            return;
        }
        BlockPos above = pos.above();
        VoidClusterEntity cluster = VoidEnergyNetwork.clusterAt(level, above);
        if (cluster == null) {
            if (!level.getBlockState(above).canBeReplaced()) {
                return;
            }
            cluster = new VoidClusterEntity(ModVoidEnergy.VOID_CLUSTER.get(), level);
            cluster.moveTo(above.getX() + 0.5, above.getY() + 0.5, above.getZ() + 0.5);
            level.addFreshEntity(cluster);
        }
        energy -= ENERGY_PER_TICK;
        cluster.addEnergy(PER_TICK);
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Energy", energy);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energy = tag.getInt("Energy");
    }
}
