package com.starcrosser.inspiration.voidenergy.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * 虚空注入的输入：束缚器上的底材 + 枪里的注入物（后者可以为空）。
 */
public record VoidInjectionRecipeInput(ItemStack base, ItemStack inject) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? base : inject;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return base.isEmpty() && inject.isEmpty();
    }
}
