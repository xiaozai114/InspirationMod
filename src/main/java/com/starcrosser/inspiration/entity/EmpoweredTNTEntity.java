package com.starcrosser.inspiration.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;

public class EmpoweredTNTEntity extends PrimedTnt {
    private static final float EXPLOSION_POWER = 16.0F;

    public EmpoweredTNTEntity(EntityType<? extends PrimedTnt> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    public EmpoweredTNTEntity(Level level, double x, double y, double z, LivingEntity igniter) {
        super(level, x, y, z, igniter);
    }

    @Override
    public void explode() {
        this.level().explode(
                this,
                this.getX(), this.getY(), this.getZ(),
                EXPLOSION_POWER,
                Level.ExplosionInteraction.BLOCK
        );
    }
}