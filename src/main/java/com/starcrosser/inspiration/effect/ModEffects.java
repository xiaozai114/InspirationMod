package com.starcrosser.inspiration.effect;

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Inspiration.MODID);

    public static final int PARASITE_DURATION = 6000;

    public static final DeferredHolder<MobEffect, MobEffect> PARASITE =
            EFFECTS.register("parasite", () -> new MarkedEffect(MobEffectCategory.HARMFUL, 0x7A9B3A));

    public static final DeferredHolder<MobEffect, MobEffect> MECHANICAL_PARASITE =
            EFFECTS.register("mechanical_parasite",
                    () -> new MarkedEffect(MobEffectCategory.HARMFUL, 0x3FBFB0));

    private ModEffects() {
    }

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}
