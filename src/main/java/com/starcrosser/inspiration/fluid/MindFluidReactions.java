package com.starcrosser.inspiration.fluid;

import com.starcrosser.inspiration.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * 思维流体与水 / 岩浆的反应表。
 * <p>
 * 四条规则按「触发流体 × 思维流体是源还是流动」组合决定产物：
 * <table>
 *   <tr><td>水</td><td>源 → 浓缩灵感结晶</td><td>流动 → 稀释灵感结晶</td></tr>
 *   <tr><td>岩浆</td><td>源 → 思维水晶岩</td><td>流动 → 灵感凝胶</td></tr>
 * </table>
 * 反应只替换思维流体所在的那一格，水与岩浆本身不受影响，
 * 因此可以反复往同一处浇水稳定量产结晶。
 * <p>
 * 调用方是 {@code MindFluidBlock}：它在方块被放下与收到邻居更新时各查一次。
 */
public final class MindFluidReactions {

    private MindFluidReactions() {
    }

    /**
     * 检查 {@code pos} 处的思维流体是否与水或岩浆相邻，是则把自己替换成对应的结晶。
     *
     * @param mindFluid {@code pos} 处的思维流体状态，用来区分源与流动
     * @return 是否发生了反应；发生反应后原本的思维流体方块已经不存在了
     */
    public static boolean react(Level level, BlockPos pos, FluidState mindFluid) {
        BlockState replacement = replacementFor(level, pos, mindFluid.isSource());
        if (replacement == null) {
            return false;
        }
        // 走原版事件，让其它模组有机会改写这次流体放置
        level.setBlockAndUpdate(pos, EventHooks.fireFluidPlaceBlockEvent(level, pos, pos, replacement));
        // 1501 是原版岩浆遇水的“淬火”音效
        level.levelEvent(1501, pos, 0);
        return true;
    }

    @Nullable
    private static BlockState replacementFor(Level level, BlockPos pos, boolean isSource) {
        for (Direction direction : Direction.values()) {
            FluidState neighbour = level.getFluidState(pos.relative(direction));
            if (neighbour.is(FluidTags.WATER)) {
                return pick(isSource, ModBlocks.CONCENTRATED_INSPIRATION_CRYSTAL,
                        ModBlocks.DILUTED_INSPIRATION_CRYSTAL);
            }
            if (neighbour.is(FluidTags.LAVA)) {
                return pick(isSource, ModBlocks.MIND_CRYSTAL_ROCK, ModBlocks.INSPIRATION_GEL);
            }
        }
        return null;
    }

    /**
     * 源流体与流动流体得到不同产物。
     * <p>
     * 这里收的是 {@link Supplier} 而不是直接的方块实例：本类可能在方块注册尚未完成时
     * 就被加载，取 {@code DeferredBlock#get()} 会拿到空值。
     */
    private static BlockState pick(boolean isSource, Supplier<? extends Block> sourceBlock,
                                   Supplier<? extends Block> flowingBlock) {
        return (isSource ? sourceBlock.get() : flowingBlock.get()).defaultBlockState();
    }
}
