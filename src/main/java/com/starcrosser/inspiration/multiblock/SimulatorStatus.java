package com.starcrosser.inspiration.multiblock;

/**
 * 结构模拟机的运行状态，主方块的界面上会显示对应的一行文字。
 * <p>
 * 顺序即优先级：{@link #current} 按这个顺序挑**第一个**成立的，所以玩家看到的永远是
 * 最该先解决的那个问题（结构没搭好比没电更重要）。
 */
public enum SimulatorStatus {

    /** 结构没成型。 */
    NOT_FORMED(0),
    /** 结构成型了但数据仓里没有结构数据。 */
    NO_DATA(1),
    /** 缺电：能量端口抽不出这一刻要用的电。 */
    NO_ENERGY(2),
    /** 缺灵感充能。 */
    NO_CHARGE(3),
    /** 上一轮的产物还没塞进输出仓。 */
    OUTPUT_FULL(4),
    /** 正在模拟。 */
    WORKING(5);

    /**
     * 存盘用的稳定编号。
     * <p>
     * 存档里存的是这个 id 而不是 {@code ordinal()}：以后调整枚举**顺序**（本枚举的顺序就是
     * 优先级，很可能要调）也不会让老存档把状态读成另一个。
     * 新增状态只能往末尾加编号，已有的编号永远不许改。
     */
    private final int id;

    SimulatorStatus(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static SimulatorStatus fromId(int id) {
        for (SimulatorStatus status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        return NOT_FORMED;
    }

    /** 界面语言键。 */
    public String translationKey() {
        return "gui.inspiration.structure_simulator.status." + name().toLowerCase(java.util.Locale.ROOT);
    }
}
