package com.starcrosser.inspiration.block.custom;

import com.starcrosser.inspiration.entity.EmpoweredTNTEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

public class EmpoweredTNTBlock extends TntBlock {

    public EmpoweredTNTBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide && level.getFluidState(pos).isSource()) {
            // 放置在水下时立即爆炸
            explode(level, pos, null);
        }
    }

    @Override
    public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {
        if (!level.isClientSide) {
            explode(level, pos, igniter);
        }
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        if (!level.isClientSide) {
            EmpoweredTNTEntity tnt = new EmpoweredTNTEntity(
                    level, (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, explosion.getIndirectSourceEntity()
            );
            int i = tnt.getFuse();
            tnt.setFuse((short)(level.random.nextInt(i / 4) + i / 8));
            level.addFreshEntity(tnt);
        }
    }

    private void explode(Level level, BlockPos pos, @Nullable LivingEntity igniter) {
        level.removeBlock(pos, false);

        EmpoweredTNTEntity tntEntity = new EmpoweredTNTEntity(
                level,
                (double)pos.getX() + 0.5D,
                (double)pos.getY(),
                (double)pos.getZ() + 0.5D,
                igniter
        );
        level.addFreshEntity(tntEntity);

        level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
    }
}