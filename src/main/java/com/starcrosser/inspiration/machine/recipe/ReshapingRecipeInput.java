package com.starcrosser.inspiration.machine.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * 物质重塑机的输入。
 * <p>
 * 两个槽位含义固定、顺序不可交换：{@code first} 是「待重塑的物质」，
 * {@code second} 是「辅助材料」（单输入配方留空）。
 */
public record ReshapingRecipeInput(ItemStack first, ItemStack second) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? first : second;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return first.isEmpty() && second.isEmpty();
    }
}
