package com.starcrosser.inspiration.radiation;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.EffectCure;
import org.jetbrains.annotations.Nullable;

/** Marks only the illness layer. Vanilla ticks and saves any independent effect underneath it. */
public final class RadiationSymptoms {
    private static final EffectCure SYMPTOM = EffectCure.get("inspiration:radiation_sickness");
    private static final int DURATION = 60;

    public interface Layer {
        @Nullable MobEffectInstance inspiration$underlyingEffect();
    }

    private RadiationSymptoms() {}

    public static boolean isSymptom(MobEffectInstance effect) {
        return effect.getCures().contains(SYMPTOM);
    }

    @Nullable
    public static MobEffectInstance liveUnderlying(MobEffectInstance effect) {
        MobEffectInstance next = ((Layer) effect).inspiration$underlyingEffect();
        // Vanilla also ticks hidden effects. Skip expired layers, retaining longer effects below them.
        while (next != null && !next.isInfiniteDuration() && next.getDuration() <= 0) {
            next = ((Layer) next).inspiration$underlyingEffect();
        }
        return next;
    }

    static void refresh(Player player, Holder<MobEffect> type, int amplifier) {
        MobEffectInstance current = player.getEffect(type);
        if (current != null && isSymptom(current)) {
            if (current.getAmplifier() == amplifier && current.getDuration() > 30) {
                return;
            }
            current = liveUnderlying(current);
        }
        if (current != null && current.getAmplifier() >= amplifier) {
            if (current != player.getEffect(type)) {
                player.forceAddEffect(current, null);
            }
            return;
        }
        MobEffectInstance symptom = new MobEffectInstance(type, DURATION, amplifier, false, false, true, current);
        symptom.getCures().add(SYMPTOM);
        player.forceAddEffect(symptom, null);
    }

    public static void clear(Player player) {
        for (MobEffectInstance effect : List.copyOf(player.getActiveEffects())) {
            if (isSymptom(effect)) {
                MobEffectInstance original = liveUnderlying(effect);
                if (original == null) {
                    player.removeEffect(effect.getEffect());
                } else {
                    player.forceAddEffect(original, null);
                }
            }
        }
    }
}
