package com.starcrosser.inspiration.machine.plugin;

import com.starcrosser.inspiration.item.custom.MachinePluginItem;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 一台机器上已安装插件折算出来的倍率。
 * <p>
 * 各倍率的定义：
 * <ul>
 *     <li>{@code speed} —— 进度条每刻推进量，等价于运行速度；</li>
 *     <li>{@code energyUse} —— 每刻耗电，为速度的平方（速度 ×2 时耗电 ×4）；</li>
 *     <li>{@code chargeUse} —— 每刻灵感充能消耗，与速度同步增长；</li>
 *     <li>{@code energyCapacity} / {@code chargeCapacity} —— 两种缓冲的容量；</li>
 *     <li>{@code parallel} —— 采用并行加工的机器每轮最多能运行的配方份数；</li>
 *     <li>{@code byproduct} —— 副产物产量倍率（唯一按加算叠加的那一项）。</li>
 * </ul>
 * 同类插件每多装一个就再乘一次 {@link MachinePluginItem#multiplier()}，
 * **只有 {@link MachinePluginType#BYPRODUCT} 是加算**：每个回收升级加 100%，
 * 装两个就是 3 倍而不是 4 倍。
 */
public record PluginEffects(float speed, float energyUse, float chargeUse,
                            float energyCapacity, float chargeCapacity, float byproduct, int parallel) {

    public PluginEffects(float speed, float energyUse, float chargeUse, float energyCapacity,
                         float chargeCapacity, float byproduct) {
        this(speed, energyUse, chargeUse, energyCapacity, chargeCapacity, byproduct, 1);
    }

    /** 没装任何插件时的中性倍率。 */
    public static final PluginEffects NONE = new PluginEffects(1F, 1F, 1F, 1F, 1F, 1F);

    /** 按插件槽里的物品折算倍率，非插件物品直接忽略。 */
    public static PluginEffects from(List<ItemStack> installed) {
        float speed = 1F;
        float energyCapacity = 1F;
        float chargeCapacity = 1F;
        float byproduct = 1F;
        int parallel = 1;
        for (ItemStack stack : installed) {
            if (stack.isEmpty() || !(stack.getItem() instanceof MachinePluginItem plugin)) {
                continue;
            }
            MachinePluginType type = plugin.pluginType();
            float factor = plugin.multiplier();
            switch (type) {
                case PARALLEL -> parallel *= Math.round(factor);
                case SPEED -> speed *= factor;
                case ENERGY_CAPACITY, ENERGY_CAPACITY_DOWNGRADE -> energyCapacity *= factor;
                case CHARGE_CAPACITY -> chargeCapacity *= factor;
                // 加算：倍率 2.0 的插件等价于「再多一整份」，两个就是 1 + 1 + 1
                case BYPRODUCT -> byproduct += factor - 1F;
            }
        }
        return new PluginEffects(speed, speedEnergyUse(speed), speed, energyCapacity, chargeCapacity, byproduct, parallel);
    }

    public static float speedEnergyUse(float speed) {
        return speed * speed;
    }

    /**
     * 进度条每刻的推进量。
     *
     * @param base 机器自身的基准速度；普通机器是 1，强能物质融合机是 2
     */
    public int progressIncrease(int base) {
        return Math.max(1, Math.round(base * speed));
    }

    public int energyPerTick(int base) {
        return Math.max(1, Math.round(base * energyUse));
    }

    public int chargePerTick(int base) {
        return Math.max(1, Math.round(base * chargeUse));
    }

    public int energyCapacity(int base) {
        return Math.max(1, Math.round(base * energyCapacity));
    }

    public int chargeCapacity(int base) {
        return Math.max(1, Math.round(base * chargeCapacity));
    }

    /**
     * 副产物的实际产量。
     *
     * @param base 配方里写的基准数量；没有这份副产物时传 0，结果也是 0
     */
    public int byproductCount(int base) {
        if (base <= 0) {
            return 0;
        }
        return Math.max(1, Math.round(base * byproduct));
    }

    /** 是否装了任何插件。 */
    public boolean isEmpty() {
        return speed == 1F && energyCapacity == 1F && chargeCapacity == 1F && byproduct == 1F && parallel == 1;
    }
}
