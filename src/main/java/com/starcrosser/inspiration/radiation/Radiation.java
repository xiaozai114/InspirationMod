package com.starcrosser.inspiration.radiation;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;

/**
 * 辐射值的查询与展示。
 * <p>
 * 数值本身来自 {@link RadiationDataMaps}，这里只负责「取出来」和「显示成什么样子」：
 * 单个物品取单件值，整堆按件数相乘；方块按方块状态取值。
 */
public final class Radiation {

    private Radiation() {
    }

    /** 单个物品的辐射值（分子）；没有登记过就是 0。 */
    public static int of(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        Integer value = stack.getItem().builtInRegistryHolder().getData(RadiationDataMaps.ITEM_RADIATION);
        return value == null ? 0 : value;
    }

    /**
     * 单个物品的辐射周期（分母）：每几秒释放一次 {@link #of(ItemStack)} 那么多点。
     * 没登记过就是 1（每秒释放）。
     */
    public static int denominator(ItemStack stack) {
        if (stack.isEmpty()) {
            return 1;
        }
        Integer value = stack.getItem().builtInRegistryHolder()
                .getData(RadiationDataMaps.ITEM_RADIATION_DENOMINATOR);
        return value == null || value < 1 ? 1 : value;
    }

    /**
     * 一整堆物品在「第 {@code seconds} 秒」这一刻实际注入的点数。
     * <p>
     * 分母为 1（默认）时每秒注入「值 × 件数」；分母为 N 时每 N 秒才注入一次，其余秒为 0。
     * 这样「9 枚铀粒（2 / 9 秒）」的平均速率与「1 枚铀锭（2 / 秒）」相同。
     */
    public static int ofStack(ItemStack stack, long seconds) {
        int value = of(stack);
        if (value <= 0) {
            return 0;
        }
        int period = denominator(stack);
        return (period <= 1 || seconds % period == 0) ? value * stack.getCount() : 0;
    }

    /** 方块状态的辐射值；没有登记过就是 0。 */
    public static int of(BlockState state) {
        Integer value = BuiltInRegistries.BLOCK.wrapAsHolder(state.getBlock())
                .getData(RadiationDataMaps.BLOCK_RADIATION);
        return value == null ? 0 : value;
    }

    /**
     * 把辐射值压成短串，避免消息栏被长数字撑爆：
     * 千位以上用 K、百万以上用 M，均保留一位小数。
     */
    public static String format(int value) {
        if (Math.abs(value) >= 1_000_000) {
            return String.format(Locale.ROOT, "%.2fM", value / 1_000_000.0);
        }
        if (Math.abs(value) >= 10_000) {
            return String.format(Locale.ROOT, "%.1fK", value / 1_000.0);
        }
        return Integer.toString(value);
    }
}
