package com.starcrosser.inspiration.machine.spec;

import net.minecraft.world.item.DyeColor;

/**
 * 一台机器在注册时的全部可变参数：**等级**与**输入槽数量**。
 * <p>
 * 为什么要有它：原先「普通机型 / 高能机型」是两套几乎逐字相同的方块实体类（每组约 250 行、
 * 相似度 74%~83%），差别只是能量容量、每刻耗电、充能消耗、运行速度和槽数。改一处逻辑要改两个
 * 文件，迟早漏一处。现在这些差异全部由这份配置表达，**同族机器共用一个方块实体类**。
 * <p>
 * **等级与槽数互相独立**：等级只决定数值倍率，槽数只决定一次能接几条材料，两者在注册时分别填。
 * 所以将来可以出现「等级 3 但仍只有 2 个输入槽」这种机型，不需要改任何方块实体代码。
 *
 * <pre>
 * 用法（在机器注册处）：
 *   MachineSpec TIER_1 = MachineSpec.of(1, 2);   // 物质融合机：等级 1，2 个物品输入槽
 *   MachineSpec TIER_2 = MachineSpec.of(2, 3);   // 强能物质融合机：等级 2，3 个物品输入槽
 * </pre>
 */
public record MachineSpec(int tier, int itemInputSlots, int fluidInputSlots, DyeColor barColor) {

    /** 进度条颜色的默认值。 */
    private static final DyeColor DEFAULT_BAR_COLOR = DyeColor.LIGHT_BLUE;

    /** 最低等级。等级从 1 起：1 是普通机型，2 是现在的高能机型。 */
    public static final int MIN_TIER = 1;

    /** 倍率再往上抬就接近 int 溢出（2^30），超出这个等级没有意义。 */
    public static final int MAX_TIER = 16;

    public MachineSpec {
        if (tier < MIN_TIER || tier > MAX_TIER) {
            throw new IllegalArgumentException("机器等级必须在 " + MIN_TIER + "~" + MAX_TIER + " 之间，收到 " + tier);
        }
        if (itemInputSlots < 0) {
            throw new IllegalArgumentException("物品输入槽数不能为负，收到 " + itemInputSlots);
        }
        if (fluidInputSlots < 0) {
            throw new IllegalArgumentException("流体输入槽数不能为负，收到 " + fluidInputSlots);
        }
    }

    /** 只有物品输入槽、没有流体输入槽的机型，进度条用默认颜色。 */
    public static MachineSpec of(int tier, int itemInputSlots) {
        return new MachineSpec(tier, itemInputSlots, 0, DEFAULT_BAR_COLOR);
    }

    /** 指定进度条颜色（物质重塑机两档的颜色不同，是历史遗留，为了不改观感原样保留）。 */
    public static MachineSpec of(int tier, int itemInputSlots, DyeColor barColor) {
        return new MachineSpec(tier, itemInputSlots, 0, barColor);
    }

    /** 同时有物品与流体输入槽的机型（灵感装配台一族）。 */
    public static MachineSpec of(int tier, int itemInputSlots, int fluidInputSlots, DyeColor barColor) {
        return new MachineSpec(tier, itemInputSlots, fluidInputSlots, barColor);
    }

    /**
     * 等级倍率：**{@code 2^(tier-1)}** —— tier 1 = ×1、2 = ×2、3 = ×4。
     * <p>
     * 能量容量、最大输入功率、每刻耗电、每刻充能消耗、灵感罐容量、运行速度，
     * 这六项统一按它放大，所以「升一级翻一倍」对玩家是可预期的。
     */
    public int multiplier() {
        return 1 << (tier - 1);
    }

    /** 按等级放大一个基准数值（基准值指的是 tier 1 时的数值）。 */
    public int scale(int base) {
        return base * multiplier();
    }
}
