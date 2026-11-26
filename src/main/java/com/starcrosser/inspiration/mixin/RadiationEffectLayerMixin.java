package com.starcrosser.inspiration.mixin;

import com.starcrosser.inspiration.radiation.RadiationSymptoms;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Keep an independently applied potion or mob debuff distinguishable from radiation of the same type. */
@Mixin(MobEffectInstance.class)
abstract class RadiationEffectLayerMixin implements RadiationSymptoms.Layer {
    @Shadow @Nullable private MobEffectInstance hiddenEffect;
    @Shadow abstract void setDetailsFrom(MobEffectInstance other);

    @Override
    public MobEffectInstance inspiration$underlyingEffect() {
        return hiddenEffect;
    }

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void inspiration$mergeExternalEffect(MobEffectInstance incoming, CallbackInfoReturnable<Boolean> result) {
        MobEffectInstance current = (MobEffectInstance) (Object) this;
        if (!RadiationSymptoms.isSymptom(current) || RadiationSymptoms.isSymptom(incoming)) {
            return;
        }
        hiddenEffect = RadiationSymptoms.liveUnderlying(current);
        if (hiddenEffect == null) {
            hiddenEffect = incoming;
        } else {
            hiddenEffect.update(incoming);
        }
        boolean takesPriority = hiddenEffect.getAmplifier() > current.getAmplifier()
                || (hiddenEffect.getAmplifier() == current.getAmplifier()
                && (hiddenEffect.isInfiniteDuration() || hiddenEffect.getDuration() >= current.getDuration()));
        if (takesPriority) {
            MobEffectInstance original = hiddenEffect;
            setDetailsFrom(original);
            hiddenEffect = ((RadiationSymptoms.Layer) original).inspiration$underlyingEffect();
        }
        result.setReturnValue(takesPriority);
    }
}
