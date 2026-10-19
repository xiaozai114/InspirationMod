package com.starcrosser.inspiration.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/** 强能 TNT：威力 16，破坏方块。 */
public class EmpoweredTNTEntity extends CustomTntEntity {

    private static final float EXPLOSION_POWER = 16.0F;

    public EmpoweredTNTEntity(EntityType<? extends PrimedTnt> entityType, Level level) {
        super(entityType, level);
    }

    /** 生成一枚强能 TNT，实体类型同样用我们自己的（见 {@link CustomTntEntity}）。 */
    public EmpoweredTNTEntity(Level level, double x, double y, double z, @Nullable LivingEntity igniter) {
        super(ModEntities.EMPOWERED_TNT.get(), level, x, y, z, igniter);
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
