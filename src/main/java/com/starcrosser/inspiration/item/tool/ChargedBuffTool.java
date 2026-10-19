package com.starcrosser.inspiration.item.tool;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

/**
 * 「长按右键蓄力，消耗经验换取增益」的工具。
 * <p>
 * 注能翡翠钢系列五件各带一种增益（剑=速度、镐=急迫、斧=力量、锄=幸运、铲=跳跃提升），
 * 蓄力与结算流程完全一样，写在 {@link ChargedBuff} 里；本接口只声明「哪件工具给哪种效果」。
 * <p>
 * 注意：接口的 default 方法**压不过父类**（{@code Item#use} 等来自超类，优先级高于接口默认实现），
 * 所以五件工具都得自己覆写那几个方法再转调 {@link ChargedBuff}，这是 Java 的规则，不是冗余。
 */
public interface ChargedBuffTool {

    /** 蓄力完成后获得的增益。 */
    Holder<MobEffect> chargedEffect();

    /** 蓄力所需刻数。 */
    default int chargeTicks() {
        return ChargedBuff.CHARGE_TICKS;
    }

    /** 一次发动消耗的经验点数。 */
    default int experienceCost() {
        return ChargedBuff.EXPERIENCE_COST;
    }

    /** 增益持续刻数。 */
    default int effectDuration() {
        return ChargedBuff.EFFECT_DURATION;
    }
}
