package com.starcrosser.inspiration.util;

import net.minecraft.util.RandomSource;

/**
 * 原版「矿石掉落」附魔公式（{@code minecraft:ore_drops}）的独立实现。
 * <p>
 * 原版中该公式写在 {@code ApplyBonusCount} 的战利品函数里，无法在战利品表之外复用，
 * 这里按同样的分布重新实现，供机器模拟「等效时运 N」的产量使用：
 * 先取 {@code nextInt(level + 2) - 1}，负值截断为 0，再 +1 作为倍率，
 * 即倍率落在 {@code 1 ~ level + 1}，期望为 {@code 1 + level * (level + 1) / (2 * (level + 2))}。
 * 例如时运 3 的期望是 2.2 倍，时运 6 的期望是 3.625 倍，与原版表现一致。
 */
public final class OreFortuneBonus {

    /**
     * 掷出一次矿石产量倍率。
     *
     * @param random       随机源，通常传世界或方块实体的 {@code RandomSource}
     * @param fortuneLevel 等效时运等级，小于等于 0 时恒返回 1
     * @return 倍率，范围 {@code 1 ~ fortuneLevel + 1}
     */
    public static int rollMultiplier(RandomSource random, int fortuneLevel) {
        if (fortuneLevel <= 0) {
            return 1;
        }
        int bonus = random.nextInt(fortuneLevel + 2) - 1;
        return Math.max(bonus, 0) + 1;
    }

    /** 倍率下界，用于判断输出槽容量与展示产量区间。 */
    public static int minMultiplier(int fortuneLevel) {
        return 1;
    }

    /** 倍率上界。 */
    public static int maxMultiplier(int fortuneLevel) {
        return Math.max(fortuneLevel, 0) + 1;
    }

    /** 倍率期望，仅用于界面展示，不参与实际计算。 */
    public static double averageMultiplier(int fortuneLevel) {
        if (fortuneLevel <= 0) {
            return 1.0D;
        }
        return 1.0D + (double) fortuneLevel * (fortuneLevel + 1) / (2.0D * (fortuneLevel + 2));
    }

    private OreFortuneBonus() {
    }
}
