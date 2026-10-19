package com.starcrosser.inspiration.radiation;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * 玩家辐射中毒的分层表现。
 * <p>
 * 每一层完全包含上一层，因此按阈值从低到高依次写入同一张表、后写覆盖先写即可，
 * 不需要在每个分支里重复低层的效果。
 * <p>
 * 效果时长给 3 秒、每秒刷新一次：离开辐射区后最多 3 秒就会自行消退，
 * 不需要额外写「清除」逻辑。只有等级变化或剩余时间不足时才重新施加，避免每秒无谓刷新。
 */
public final class RadiationEffects {

    /** 10K：虚弱 1 + 饥饿 1。 */
    public static final int MILD = 10_000;
    /** 50K：虚弱 2 + 挖掘疲劳 1。 */
    public static final int MODERATE = 50_000;
    /** 100K：挖掘疲劳 2，且治疗被换算成辐射衰减。 */
    public static final int SEVERE = 100_000;
    /** 500K：缓慢 1 + 失明。 */
    public static final int ACUTE = 500_000;
    /** 1M：凋零 1 + 饥饿 2 + 缓慢 2 + 挖掘疲劳 3 + 虚弱 3。 */
    public static final int LETHAL = 1_000_000;

    /** 效果时长（刻）。每秒刷新，给 3 秒留余量。 */
    private static final int DURATION = 60;
    /** 剩余时间短于这个值就重新施加，避免效果在下一次刷新前断档。 */
    private static final int REFRESH_THRESHOLD = 30;

    /** 治疗换算成辐射衰减的比例：1 点治疗换 10 点辐射衰减。 */
    public static final int HEALING_CONVERSION_RATE = 10;

    private RadiationEffects() {
    }

    /** 当前所处的中毒层数，0 表示无症状。 */
    public static int tierOf(int radiation) {
        if (radiation > LETHAL) {
            return 5;
        }
        if (radiation > ACUTE) {
            return 4;
        }
        if (radiation > SEVERE) {
            return 3;
        }
        if (radiation > MODERATE) {
            return 2;
        }
        if (radiation > MILD) {
            return 1;
        }
        return 0;
    }

    /** 治疗是否被换算成辐射衰减（100K 以上）。 */
    public static boolean convertsHealing(int radiation) {
        return radiation > SEVERE;
    }

    /** 按当前辐射值施加对应的负面效果。 */
    public static void apply(Player player, int radiation) {
        for (Map.Entry<Holder<MobEffect>, Integer> entry : effectsFor(radiation).entrySet()) {
            refresh(player, entry.getKey(), entry.getValue());
        }
    }

    private static Map<Holder<MobEffect>, Integer> effectsFor(int radiation) {
        Map<Holder<MobEffect>, Integer> effects = new HashMap<>();
        if (radiation > MILD) {
            effects.put(MobEffects.WEAKNESS, 0);
            effects.put(MobEffects.HUNGER, 0);
        }
        if (radiation > MODERATE) {
            effects.put(MobEffects.WEAKNESS, 1);
            effects.put(MobEffects.DIG_SLOWDOWN, 0);
        }
        if (radiation > SEVERE) {
            effects.put(MobEffects.DIG_SLOWDOWN, 1);
        }
        if (radiation > ACUTE) {
            effects.put(MobEffects.MOVEMENT_SLOWDOWN, 0);
            effects.put(MobEffects.BLINDNESS, 0);
        }
        if (radiation > LETHAL) {
            effects.put(MobEffects.WITHER, 0);
            effects.put(MobEffects.HUNGER, 1);
            effects.put(MobEffects.MOVEMENT_SLOWDOWN, 1);
            effects.put(MobEffects.DIG_SLOWDOWN, 2);
            effects.put(MobEffects.WEAKNESS, 2);
        }
        return effects;
    }

    /**
     * 刷新单个效果：等级不符或剩余时间不足时先移除再施加。
     * 直接 {@code addEffect} 不行 —— 原版在已有更强/更久的同种效果时会忽略新效果，
     * 降级（比如从虚弱 2 退回虚弱 1）就永远退不回去。
     */
    private static void refresh(Player player, Holder<MobEffect> effect, int amplifier) {
        MobEffectInstance current = player.getEffect(effect);
        if (current != null
                && current.getAmplifier() == amplifier
                && current.getDuration() > REFRESH_THRESHOLD) {
            return;
        }
        player.removeEffect(effect);
        player.addEffect(new MobEffectInstance(effect, DURATION, amplifier, false, false, true));
    }
}
