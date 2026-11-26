package com.starcrosser.inspiration.alkali;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

public final class AlkaliWaterReaction {
    private AlkaliWaterReaction() {}

    public static boolean react(ItemEntity entity, float radius) {
        Level level = entity.level();
        if (level.isClientSide || !entity.isInWater() || entity.isRemoved()) {
            return false;
        }
        entity.discard();
        level.explode(entity, entity.getX(), entity.getY() + 0.5, entity.getZ(), radius, Level.ExplosionInteraction.TNT);
        return true;
    }
}
