package com.starcrosser.inspiration.effect;

import com.starcrosser.inspiration.radiation.RadiationSymptoms;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class RadiationResistanceEffect extends MobEffect {
    public RadiationResistanceEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x64CF9C);
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player && !player.level().isClientSide) {
            RadiationSymptoms.clear(player);
        }
    }
}
