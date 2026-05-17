package com.starcrosser.inspiration.entity;

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Inspiration.MODID);

    // 注册 BigTntEntity
    public static final DeferredHolder<EntityType<?>, EntityType<ReinforcedTNTEntity>> INTENSIFIED_TNT =
            ENTITIES.register(
                    "intensified_tnt", // 实体的注册名
                    () -> EntityType.Builder.<ReinforcedTNTEntity>of(ReinforcedTNTEntity::new, MobCategory.MISC)
                            .sized(0.98F, 0.98F) // 设置实体的碰撞箱大小，与原版TNT保持一致
                            .clientTrackingRange(10) // 客户端追踪距离
                            .updateInterval(10) // 更新间隔
                            .fireImmune() // 爆炸物自然免疫火焰伤害
                            .build("intensified_tnt") // 构建并返回EntityType实例
            );
}
