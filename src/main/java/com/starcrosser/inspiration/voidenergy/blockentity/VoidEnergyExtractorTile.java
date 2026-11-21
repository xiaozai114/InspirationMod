package com.starcrosser.inspiration.voidenergy.blockentity;

import com.starcrosser.inspiration.voidenergy.ModVoidEnergy;
import com.starcrosser.inspiration.voidenergy.VoidEnergyNetwork;
import com.starcrosser.inspiration.voidenergy.block.CrackedBedrockBlock;
import com.starcrosser.inspiration.voidenergy.entity.VoidClusterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 虚空能量提取器：站在开裂的基岩上，每刻给上方一格 +1 ve。
 * <p>
 * 上方没有团簇且可以放置时自动生成一个；上方被方块占住就什么也不做 ——
 * 这比「强行替换方块」安全，玩家把机器封起来时不会把建筑吃掉。
 */
public class VoidEnergyExtractorTile extends BlockEntity {

    /** 每刻供给的能量（ve）。 */
    public static final int PER_TICK = 1;

    public VoidEnergyExtractorTile(BlockPos pos, BlockState state) {
        super(ModVoidEnergy.VOID_ENERGY_EXTRACTOR_TYPE.get(), pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
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
        cluster.addEnergy(PER_TICK);
    }
}
