package com.starcrosser.inspiration.multiblock.blockentity.part;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 输出仓：模拟产出的战利品堆在这里，18 格（6 列 × 3 行）。
 * <p>
 * 一次沉船模拟能吐出小半箱东西，槽位给少了会卡住机器，所以这里比输入仓大一倍；
 * 仍然塞不下时控制器会把剩余产物攒在自己手里，等腾出位置再继续倒。
 */
public class OutputHatchTile extends HatchTile<OutputHatchTile> {

    public static final int SLOTS = 18;

    @Save
    private final InventoryComponent<OutputHatchTile> inventory;

    public OutputHatchTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<OutputHatchTile>) ModMultiblocks.OUTPUT_HATCH.getBlock(),
                ModMultiblocks.OUTPUT_HATCH.type().get(), pos, state);
        InventoryComponent<OutputHatchTile> inv = new InventoryComponent<>("output", 44, 30, SLOTS);
        inv.setRange(6, 3);
        this.inventory = inv;
        addInventory(this.inventory);
    }

    @Override
    public OutputHatchTile getSelf() {
        return this;
    }

    public InventoryComponent<OutputHatchTile> getInventory() {
        return inventory;
    }
}
