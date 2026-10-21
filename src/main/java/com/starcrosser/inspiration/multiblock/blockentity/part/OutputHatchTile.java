package com.starcrosser.inspiration.multiblock.blockentity.part;

import com.hrznstudio.titanium.block.BasicTileBlock;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 输出仓：模拟产出的战利品堆在这里，18 格（6 列 × 3 行）。
 * <p>
 * 一次沉船模拟能吐出小半箱东西，槽位给少了会卡住机器，所以这里比输入仓大一倍；
 * 仍然塞不下时控制器会把剩余产物攒在自己手里，等腾出位置再继续倒。
 */
public class OutputHatchTile extends InventoryHatchTile<OutputHatchTile> {

    public static final int SLOTS = 18;

    public OutputHatchTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<OutputHatchTile>) ModMultiblocks.OUTPUT_HATCH.getBlock(),
                ModMultiblocks.OUTPUT_HATCH.type().get(), pos, state,
                "output", 44, 30, SLOTS, 6, 3, null);
    }
}
