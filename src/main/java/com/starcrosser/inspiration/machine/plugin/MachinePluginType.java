package com.starcrosser.inspiration.machine.plugin;

import com.hrznstudio.titanium.api.augment.IAugmentType;

import java.util.Locale;

/**
 * 机器插件的种类。
 * <p>
 * 插件装在机器界面右侧的四个插件槽里，每台机器最多同时装四个，同类插件的效果按各自的
 * {@link #multiplier()} 叠乘。
 * <p>
 * 这里刻意实现 Titanium 的 {@link IAugmentType}：类型名与 Titanium 的 augment 数值表
 * 用的是同一套字符串，将来若要让别的模组也能识别本模组的插件，只需再挂上
 * {@code AugmentWrapper.ATTACHMENT} 数据组件即可。
 */
public enum MachinePluginType implements IAugmentType {

    /** 电容插件：机器的能量缓冲容量翻倍。 */
    ENERGY_CAPACITY("energy_capacity", 2.0F),
    /** 灵感插件：机器的灵感充能罐容量翻倍。 */
    CHARGE_CAPACITY("charge_capacity", 2.0F),
    /** 速度插件：运行速度翻倍，代价是每刻耗电与耗充能同时上升。 */
    SPEED("speed", 2.0F),
    /**
     * 电容降级插件：机器的能量缓冲容量降到 67%。
     * 配合爆炸类机制使用 —— 上限被压低后，玩家就攒不出足以引爆的能量。
     */
    ENERGY_CAPACITY_DOWNGRADE("energy_capacity_downgrade", 0.67F);

    private final String type;
    private final float multiplier;

    MachinePluginType(String type, float multiplier) {
        this.type = type;
        this.multiplier = multiplier;
    }

    @Override
    public String getType() {
        return type;
    }

    /** 单个插件提供的倍率，多个同类插件叠乘。 */
    public float multiplier() {
        return multiplier;
    }

    /**
     * 倍率的展示文本：整数不带小数点，其余最多两位小数并去掉末尾的零。
     * 语言文件里用 {@code %s} 接住即可。
     */
    public String multiplierText() {
        if (multiplier == (long) multiplier) {
            return String.valueOf((long) multiplier);
        }
        String text = String.format(Locale.ROOT, "%.2f", multiplier);
        return text.replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    /** 该类型插件的说明文字所用的语言键。 */
    public String descriptionKey() {
        return "tooltip.inspiration.plugin." + type;
    }
}
