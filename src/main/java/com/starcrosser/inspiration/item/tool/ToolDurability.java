package com.starcrosser.inspiration.item.tool;

/**
 * 每件装备各自的耐久。
 * <p>
 * 原版是「同一品质共用一份耐久」（钻石全套 1561、下界合金全套 2031），本模组改成按用途分：
 * 剑最少、斧次之、镐与铲居中、锄最多 —— 锄的用途最窄，给得高一点作为补偿。
 * 每档品质的基数乘以类型系数（剑 0.9 / 斧 1.0 / 镐 1.1 / 铲 1.1 / 锄 1.25）就是最终值。
 * <p>
 * 这些值要配 {@link ModToolTiers#withDurability} 一起用，单独写进
 * {@code Item.Properties#durability} 是无效的（会被 {@code TieredItem} 用品质耐久覆盖）。
 *
 * <pre>
 * 灵感档 800     剑 720   斧 800   镐 880   铲 880    锄 1000
 * 特殊武器 1800  风暴剑 / 斩首斧 / 死神镰刀 各 1800
 * 翡翠钢 2500    剑 2250  斧 2500  镐 2750  铲 2750   锄 3100
 * 注能 4000      剑 3600  斧 4000  镐 4400  铲 4400   锄 5000
 * </pre>
 */
public final class ToolDurability {

    /* ---------------- 灵感系列（基数 800） ---------------- */

    /** 灵感之剑：武器里用得最费的一件，给最低。 */
    public static final int EXP_SWORD = 720;
    /** 粘性斧：剥皮与砍树都掉耐久，居中。 */
    public static final int STICKY_AXE = 800;
    /** 灵感之镐：钻石档品质，耐久仍按灵感档给（挖掘等级与耐久是两回事）。 */
    public static final int EXP_PICKAXE = 880;
    /** 灵感之铲：压土径与挖土，与镐同档。 */
    public static final int EXP_SHOVEL = 880;
    /** 灵感之锄：用途最窄，耐久最高。 */
    public static final int EXP_HOE = 1000;

    /* ---------------- 特殊武器（钻石品质，基数 1800） ---------------- */

    /** 风暴之剑：右键发射风弹，耐久给足免得放两发就碎。 */
    public static final int STORM_SWORD = 1800;
    /** 斩首之斧。 */
    public static final int HEAD_HUNTER_AXE = 1800;
    /** 死神镰刀：本质是锄类，一并给足。 */
    public static final int SCYTHE_OF_DEATH = 1800;
    /**
     * 世界粉碎者：能挖基岩的镐，按「几乎用不完」设计。
     * 之前这里写的 999999 一直没生效（被下界合金品质的 2031 覆盖），现在才真正落地。
     */
    public static final int WORLD_BREAKER = 999_999;

    /* ---------------- 翡翠钢系列（基数 2500） ---------------- */

    public static final int JADE_STEEL_SWORD = 2_250;
    public static final int JADE_STEEL_AXE = 2_500;
    public static final int JADE_STEEL_PICKAXE = 2_750;
    public static final int JADE_STEEL_SHOVEL = 2_750;
    public static final int JADE_STEEL_HOE = 3_100;

    /* ---------------- 注能翡翠钢系列（基数 4000） ---------------- */

    public static final int ENERGIZED_JADE_STEEL_SWORD = 3_600;
    public static final int ENERGIZED_JADE_STEEL_AXE = 4_000;
    public static final int ENERGIZED_JADE_STEEL_PICKAXE = 4_400;
    public static final int ENERGIZED_JADE_STEEL_SHOVEL = 4_400;
    public static final int ENERGIZED_JADE_STEEL_HOE = 5_000;

    private ToolDurability() {
    }
}
