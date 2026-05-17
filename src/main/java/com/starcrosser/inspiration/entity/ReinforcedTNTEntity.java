package com.starcrosser.inspiration.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;

public class ReinforcedTNTEntity extends PrimedTnt {
    private static final float EXPLOSION_POWER = 8.0F;
    public ReinforcedTNTEntity(EntityType<? extends PrimedTnt> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }
    public ReinforcedTNTEntity(Level level, double x, double y, double z, LivingEntity igniter) {
        super(level, x, y, z, igniter);
    }
    @Override
    public void explode() {
        // 在实体所在位置创建一个更强大的爆炸
        this.level().explode(
                this,  // 爆炸源实体
                this.getX(), this.getY(), this.getZ(),
                EXPLOSION_POWER,  // 使用自定义的爆炸威力
                Level.ExplosionInteraction.TNT  // 模拟 TNT 的交互行为
        );
    }
}
