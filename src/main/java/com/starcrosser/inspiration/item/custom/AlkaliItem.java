package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.alkali.AlkaliWaterReaction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AlkaliItem extends Item {
    private final float radius;

    public AlkaliItem(Item.Properties properties, float radius) {
        super(properties);
        this.radius = radius;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        return AlkaliWaterReaction.react(entity, radius);
    }
}
