package com.starcrosser.inspiration.block.custom;

import com.starcrosser.inspiration.fluid.MindFluidReactions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;

/**
 * 思维流体方块：在水或岩浆旁边会凝固成对应的结晶。
 * <p>
 * 这里没有用 NeoForge 的 {@code FluidInteractionRegistry}，原因是它沿用原版
 * {@code LiquidBlock#shouldSpreadLiquid} 的方向表（正上方 + 四个水平方向），
 * 「思维流体正好落在一格水上」这种正下方相邻的情况不会被判定，
 * 结果会变成思维流体把水顶掉而不是结晶。
 * <p>
 * 因此改为直接接管方块被放下（{@link #onPlace}）与邻居更新
 * （{@link #neighborChanged}）两个时机，六个方向一并检查。
 */
public class MindFluidBlock extends LiquidBlock {

    public MindFluidBlock(FlowingFluid fluid, BlockBehaviour.Properties properties) {
        super(fluid, properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!react(level, pos, state)) {
            super.onPlace(state, level, pos, oldState, isMoving);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                   BlockPos neighborPos, boolean isMoving) {
        if (!react(level, pos, state)) {
            super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        }
    }

    /**
     * 相邻方块里若有水或岩浆，就把自己换成对应的结晶。
     * 反应成功后不再执行父类逻辑——此时本方块已经被替换掉了，不必再安排流体刻。
     */
    private boolean react(Level level, BlockPos pos, BlockState state) {
        FluidState fluid = state.getFluidState();
        return !fluid.isEmpty() && MindFluidReactions.react(level, pos, fluid);
    }
}
