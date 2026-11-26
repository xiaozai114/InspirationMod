package com.starcrosser.inspiration.machine.blockentity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

/**
 * 物质重塑机当前待执行的一次重塑。
 * <p>
 * 由配方解析结果统一产出，机器不需要再区分底层是重塑配方还是右键转化配方。
 *
 * @param result           完成后产出的物品
 * @param firstInputCount  需要从第一个输入槽扣除的数量，至少为 1
 * @param duration         所需时长（刻）
 * @param energyPerTick    每刻消耗的能量
 * @param secondInputCount 需要从第二个输入槽扣除的数量，0 表示该配方只用主输入
 */
public record ReshaperTask(ItemStack result, int firstInputCount, int duration, int energyPerTick,
                           int secondInputCount, ResourceLocation recipeId) {

    public ReshaperTask(ItemStack result, int firstInputCount, int duration, int energyPerTick, int secondInputCount) {
        this(result, firstInputCount, duration, energyPerTick, secondInputCount, null);
    }

    public boolean consumesSecondInput() {
        return secondInputCount > 0;
    }
}
