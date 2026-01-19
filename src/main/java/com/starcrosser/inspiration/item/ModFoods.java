package com.starcrosser.inspiration.item;

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.starcrosser.inspiration.item.ModItems.ITEMS;

public class ModFoods {
    public static final FoodProperties INSPIREAD =
            (new FoodProperties.Builder().nutrition(10).saturationModifier(0.8F)
                    .effect(new MobEffectInstance(MobEffects.JUMP,200),1)
                    .build());
    public static final FoodProperties COOKED_INSPIRATION =
            (new FoodProperties.Builder().nutrition(5).saturationModifier(0.4F)
                    .build());
    public static final FoodProperties INSPIRRY =
            (new FoodProperties.Builder().nutrition(3).saturationModifier(0.4F)
                    .effect(new MobEffectInstance(MobEffects.REGENERATION,100),1)
                    .build());
    public static final FoodProperties SOD =
            (new FoodProperties.Builder().nutrition(1).saturationModifier(0.0F)
                    .effect(new MobEffectInstance(MobEffects.CONFUSION,600),1)
                    .build());
}
