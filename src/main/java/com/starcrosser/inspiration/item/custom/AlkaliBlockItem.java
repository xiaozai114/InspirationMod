package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.alkali.AlkaliWaterReaction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class AlkaliBlockItem extends BlockItem {
    private final float radius;

    public AlkaliBlockItem(Block block, Item.Properties properties, float radius) {
        super(block, properties);
        this.radius = radius;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        return AlkaliWaterReaction.react(entity, radius);
    }
}
