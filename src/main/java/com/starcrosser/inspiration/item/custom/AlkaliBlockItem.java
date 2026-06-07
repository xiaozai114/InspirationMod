package com.starcrosser.inspiration.item.custom;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class AlkaliBlockItem extends BlockItem {
    private final Item target;
    private final float explosionRange;

    public AlkaliBlockItem(Block block, Item.Properties properties, Item target, float explosionRange) {
        super(block, properties);
        this.target = target;
        this.explosionRange = explosionRange;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.isInWater()) {
            Level level = entity.level();
            if (level.isClientSide) {
                return false;
            }

            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();
            level.explode(entity, x, y + 0.5, z, explosionRange, Level.ExplosionInteraction.TNT);

            entity.discard();

            ItemEntity newEntity = new ItemEntity(level, x, y, z, new ItemStack(target, stack.getCount()));
            level.addFreshEntity(newEntity);

            return true;
        }
        return false;
    }
}