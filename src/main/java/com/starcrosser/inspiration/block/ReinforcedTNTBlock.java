package com.starcrosser.inspiration.block;

import com.starcrosser.inspiration.entity.ReinforcedTNTEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ReinforcedTNTBlock extends TntBlock {


    public ReinforcedTNTBlock(BlockBehaviour.Properties props) {
        super(props);
    }
    @Override
    public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {
        // 只在服务端执行爆炸逻辑，避免客户端/服务端不同步的问题
        if (!level.isClientSide) {
            // 1. 移除被点燃的 TNT 方块本身
            level.removeBlock(pos, false);

            // 2. 生成你的自定义强化 TNT 实体（ReinforcedTntEntity）
            ReinforcedTNTEntity tntEntity = new ReinforcedTNTEntity(
                    level,
                    (double)pos.getX() + 0.5D,
                    (double)pos.getY(),
                    (double)pos.getZ() + 0.5D,
                    igniter
            );
            level.addFreshEntity(tntEntity);

            // 3. 播放引燃音效
            level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);

            // 4. 触发游戏事件
            level.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
        }
    }
    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        if (!level.isClientSide) {
            ReinforcedTNTEntity primedtnt = new ReinforcedTNTEntity(
                    level, (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, explosion.getIndirectSourceEntity()
            );
            int i = primedtnt.getFuse();
            primedtnt.setFuse((short)(level.random.nextInt(i / 4) + i / 8));
            level.addFreshEntity(primedtnt);
        }
    }

}
