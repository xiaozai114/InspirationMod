package com.starcrosser.inspiration.voidenergy;

import com.starcrosser.inspiration.voidenergy.block.VoidEnergyRelayBlock;
import com.starcrosser.inspiration.voidenergy.entity.VoidClusterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 机器找团簇的唯一入口。
 * <p>
 * 两条路：机器自己那一格就有团簇（注入枪直接搭在团簇上），或者中继器在 5×5×5 内
 * 且中继器所在格有团簇。两者都找不到就返回 null，调用方按「没有能量」处理。
 */
public final class VoidEnergyNetwork {

    private VoidEnergyNetwork() {
    }

    /** 某一格里的团簇（实体与方块可以同格，所以按格子包围盒找）。 */
    public static VoidClusterEntity clusterAt(Level level, BlockPos pos) {
        List<VoidClusterEntity> found = level.getEntitiesOfClass(VoidClusterEntity.class, new AABB(pos));
        return found.isEmpty() ? null : found.getFirst();
    }

    /** 机器可用的团簇：先看自己那格，再看半径 2 内的中继器。 */
    public static VoidClusterEntity findCluster(Level level, BlockPos machinePos) {
        VoidClusterEntity direct = clusterAt(level, machinePos);
        if (direct != null) {
            return direct;
        }
        for (BlockPos cursor : BlockPos.betweenClosed(
                machinePos.offset(-VoidEnergyRelayBlock.RANGE, -VoidEnergyRelayBlock.RANGE, -VoidEnergyRelayBlock.RANGE),
                machinePos.offset(VoidEnergyRelayBlock.RANGE, VoidEnergyRelayBlock.RANGE, VoidEnergyRelayBlock.RANGE))) {
            if (!(level.getBlockState(cursor).getBlock() instanceof VoidEnergyRelayBlock)) {
                continue;
            }
            VoidClusterEntity relayed = clusterAt(level, cursor);
            if (relayed != null) {
                return relayed;
            }
        }
        return null;
    }
}
