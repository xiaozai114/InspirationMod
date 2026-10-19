package com.starcrosser.inspiration.injection.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * 一次注入的输入：束缚器上的基底物质 + 注入枪上的注入物质。
 * <p>
 * 两者不来自同一个方块，但注入是它们共同构成的一次操作，因此合并成一个输入对象。
 */
public record InjectionRecipeInput(ItemStack base, ItemStack inject) implements RecipeInput {

    public static final int SLOT_COUNT = 2;

    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> base;
            case 1 -> inject;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return base.isEmpty() && inject.isEmpty();
    }
}
