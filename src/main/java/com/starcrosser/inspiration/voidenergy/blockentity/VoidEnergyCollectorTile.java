package com.starcrosser.inspiration.voidenergy.blockentity;

import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.voidenergy.ModVoidEnergy;
import com.starcrosser.inspiration.voidenergy.VoidEnergyNetwork;
import com.starcrosser.inspiration.voidenergy.entity.VoidClusterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class VoidEnergyCollectorTile extends BlockEntity {
    public static final int RANGE = 2;
    public static final int PER_SOURCE = 16;

    public VoidEnergyCollectorTile(BlockPos pos, BlockState state) {
        super(ModVoidEnergy.VOID_ENERGY_COLLECTOR_TYPE.get(), pos, state);
    }

    public void collect(Level level, BlockPos pos) {
        BlockPos outlet = pos.above();
        VoidClusterEntity target = VoidEnergyNetwork.clusterAt(level, outlet);
        if (target == null) {
            BlockState state = level.getBlockState(outlet);
            if (!state.canBeReplaced() && !state.is(ModVoidEnergy.VOID_ENERGY_RELAY.get())
                    && !state.is(ModMachines.RESONANCE_CHAMBER.getBlock())) {
                return;
            }
        } else if (target.energy() >= VoidClusterEntity.MAX_ENERGY) {
            return;
        }
        AABB range = new AABB(pos).inflate(RANGE);
        var sources = level.getEntitiesOfClass(VoidClusterEntity.class, range, source ->
                source.energy() > 0 && !source.blockPosition().equals(outlet)
                        && range.contains(source.position()));
        if (sources.isEmpty()) {
            return;
        }
        if (target == null) {
            target = new VoidClusterEntity(ModVoidEnergy.VOID_CLUSTER.get(), level);
            target.moveTo(outlet.getX() + 0.5, outlet.getY() + 0.5, outlet.getZ() + 0.5);
            if (!level.addFreshEntity(target)) {
                return;
            }
        }
        for (VoidClusterEntity source : sources) {
            int room = VoidClusterEntity.MAX_ENERGY - target.energy();
            if (room == 0) {
                break;
            }
            int transfer = Math.min(room, Math.min(source.energy(), PER_SOURCE));
            source.addEnergy(-transfer);
            target.addEnergy(transfer);
        }
    }
}
