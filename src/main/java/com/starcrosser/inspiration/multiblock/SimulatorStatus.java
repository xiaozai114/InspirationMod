package com.starcrosser.inspiration.multiblock;

/**
 * 结构模拟机的运行状态，主方块的界面上会显示对应的一行文字。
 * <p>
 * 顺序即优先级：{@link #current} 按这个顺序挑**第一个**成立的，所以玩家看到的永远是
 * 最该先解决的那个问题（结构没搭好比没电更重要）。
 */
public enum SimulatorStatus {

    /** 结构没成型。 */
    NOT_FORMED,
    /** 结构成型了但数据仓里没有结构数据。 */
    NO_DATA,
    /** 缺电：能量端口抽不出这一刻要用的电。 */
    NO_ENERGY,
    /** 缺灵感充能。 */
    NO_CHARGE,
    /** 上一轮的产物还没塞进输出仓。 */
    OUTPUT_FULL,
    /** 正在模拟。 */
    WORKING;

    private static final SimulatorStatus[] VALUES = values();

    public static SimulatorStatus fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : NOT_FORMED;
    }

    /** 界面语言键。 */
    public String translationKey() {
        return "gui.inspiration.structure_simulator.status." + name().toLowerCase(java.util.Locale.ROOT);
    }
}
