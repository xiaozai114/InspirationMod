package com.starcrosser.inspiration.multiblock.blockentity.part;

import com.hrznstudio.titanium.block.BasicTileBlock;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import com.starcrosser.inspiration.multiblock.data.StructureDataItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 数据仓：放结构数据的地方，1 格，只收 {@link StructureDataItem}。
 * <p>
 * 数据是模拟的「蓝图」而不是燃料，运行期间**不会被消耗** ——
 * 控制器每个进度周期都重新读一遍槽里的数据，取出来就停机。
 */
public class DataHatchTile extends InventoryHatchTile<DataHatchTile> {

    public DataHatchTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<DataHatchTile>) ModMultiblocks.DATA_HATCH.getBlock(),
                ModMultiblocks.DATA_HATCH.type().get(), pos, state,
                "data", 79, 34, 1, 1, 1,
                (stack, slot) -> stack.getItem() instanceof StructureDataItem);
    }
}
