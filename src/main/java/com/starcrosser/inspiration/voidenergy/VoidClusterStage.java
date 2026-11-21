package com.starcrosser.inspiration.voidenergy;

import net.minecraft.util.RandomSource;

/**
 * 虚空能量团簇的阶段。区间左闭右开（{@code [min, max)}），与设定表一致。
 * <p>
 * 每个阶段只有三件事不同：颜色、每刻对自身能量的修正、以及是否可能自爆。
 * 新增阶段只要在这里加一行，实体与界面都按 {@link #of(int)} 自动跟上。
 */
public enum VoidClusterStage {

    WEAK("weak", 0, 1_000, 0xFF6E6E6E),
    CLIMBING("climbing", 1_000, 2_000, 0xFF7BD389),
    PLATEAU("plateau", 2_000, 4_000, 0xFF58A6D6),
    OSCILLATING("oscillating", 4_000, 8_000, 0xFFC77DFF),
    PEAK("peak", 8_000, 32_000, 0xFFFFC300),
    DANGEROUS("dangerous", 32_000, 36_000, 0xFFFF5252),
    CELESTIAL("celestial", 36_000, Integer.MAX_VALUE, 0xFFFFFFFF);

    /** 震荡阶段的随机区间（含两端）。 */
    private static final int OSCILLATE_MIN = -60;
    private static final int OSCILLATE_MAX = 50;

    private final String key;
    private final int min;
    private final int max;
    private final int color;

    VoidClusterStage(String key, int min, int max, int color) {
        this.key = key;
        this.min = min;
        this.max = max;
        this.color = color;
    }

    public static VoidClusterStage of(int energy) {
        for (VoidClusterStage stage : values()) {
            if (energy >= stage.min && energy < stage.max) {
                return stage;
            }
        }
        return WEAK;
    }

    public String key() {
        return key;
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    public int color() {
        return color;
    }

    /** 这一阶段每刻对自身能量的修正；自爆与否不在这里处理。 */
    public int tickDelta(RandomSource random) {
        return switch (this) {
            case CLIMBING -> -2;
            case OSCILLATING -> random.nextInt(OSCILLATE_MAX - OSCILLATE_MIN + 1) + OSCILLATE_MIN;
            default -> 0;
        };
    }

    /** 危险阶段每刻 1% 概率自爆。 */
    public boolean rollsExplosion(RandomSource random) {
        return this == DANGEROUS && random.nextFloat() < 0.01F;
    }
}
