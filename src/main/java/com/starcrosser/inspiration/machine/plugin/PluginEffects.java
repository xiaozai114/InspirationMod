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
 *     <li>{@code energyCapacity} / {@code chargeCapacity} —— 两种缓冲的容量。</li>
 * </ul>
 * 同类插件每多装一个就再乘一次 {@link MachinePluginType#MULTIPLIER}。
 */
public record PluginEffects(float speed, float energyUse, float chargeUse,
                            float energyCapacity, float chargeCapacity) {

    /** 没装任何插件时的中性倍率。 */
    public static final PluginEffects NONE = new PluginEffects(1F, 1F, 1F, 1F, 1F);

    /** 按插件槽里的物品折算倍率，非插件物品直接忽略。 */
    public static PluginEffects from(List<ItemStack> installed) {
        float speed = 1F;
        float energyCapacity = 1F;
        float chargeCapacity = 1F;
        for (ItemStack stack : installed) {
            if (stack.isEmpty() || !(stack.getItem() instanceof MachinePluginItem plugin)) {
                continue;
            }
            MachinePluginType type = plugin.pluginType();
            float factor = type.multiplier();
            switch (type) {
                case SPEED -> speed *= factor;
                case ENERGY_CAPACITY, ENERGY_CAPACITY_DOWNGRADE -> energyCapacity *= factor;
                case CHARGE_CAPACITY -> chargeCapacity *= factor;
            }
        }
        return new PluginEffects(speed, speed * speed, speed, energyCapacity, chargeCapacity);
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

    /** 是否装了任何插件。 */
    public boolean isEmpty() {
        return speed == 1F && energyCapacity == 1F && chargeCapacity == 1F;
    }
}
