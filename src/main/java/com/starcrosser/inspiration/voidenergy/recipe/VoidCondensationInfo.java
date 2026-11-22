package com.starcrosser.inspiration.voidenergy.recipe;

import net.minecraft.world.item.ItemStack;

/**
 * 虚空能量凝聚的展示数据：**不是数据包配方**，凝聚器是固定行为，没有 RecipeType。
 * <p>
 * JEI 分类需要一条「配方」对象才能展示，这里就用一条只读记录承载界面要写的数值。
 */
public record VoidCondensationInfo(ItemStack result, int vePerTick, int vePerDew, int energyPerTick) {

    public int ticksPerResult() {
        return Math.max(1, vePerDew / vePerTick);
    }
}
