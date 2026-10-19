package com.starcrosser.inspiration.multiblock.blockentity.part;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 输入仓：模拟配方的物品输入，9 格。
 * <p>
 * 当前的结构数据都不吃物品，所以它暂时只是「结构里必须有它」的那个部件；
 * 以后加需要材料的模拟配方时，控制器会直接从这里取。
 */
public class InputHatchTile extends HatchTile<InputHatchTile> {

    /** 9 = 3 列 × 3 行，与机器输入区的排布保持同一套观感。 */
    public static final int SLOTS = 9;

    @Save
    private final InventoryComponent<InputHatchTile> inventory;

    public InputHatchTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<InputHatchTile>) ModMultiblocks.INPUT_HATCH.getBlock(),
                ModMultiblocks.INPUT_HATCH.type().get(), pos, state);
        InventoryComponent<InputHatchTile> inv = new InventoryComponent<>("input", 44, 30, SLOTS);
        inv.setRange(3, 3);
        this.inventory = inv;
        addInventory(this.inventory);
    }

    @Override
    public InputHatchTile getSelf() {
        return this;
    }

    public InventoryComponent<InputHatchTile> getInventory() {
        return inventory;
    }
}
