package com.starcrosser.inspiration.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/** 强化 TNT：威力 8，破坏方块。 */
public class ReinforcedTNTEntity extends CustomTntEntity {

    private static final float EXPLOSION_POWER = 8.0F;

    public ReinforcedTNTEntity(EntityType<? extends PrimedTnt> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * 生成一枚强化 TNT。
     * <p>
     * 必须传我们自己的实体类型（见 {@link CustomTntEntity}），不能走原版那个
     * 硬编码 {@code EntityType.TNT} 的构造函数。
     */
    public ReinforcedTNTEntity(Level level, double x, double y, double z, @Nullable LivingEntity igniter) {
        super(ModEntities.INTENSIFIED_TNT.get(), level, x, y, z, igniter);
    }

    @Override
    public void explode() {
        this.level().explode(
                this,                       // 爆炸源实体
                this.getX(), this.getY(), this.getZ(),
                EXPLOSION_POWER,
                Level.ExplosionInteraction.TNT
        );
    }
}
