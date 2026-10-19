package com.starcrosser.inspiration.machine.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * 矿物扭曲机的输入。
 * <p>
 * 只有一个槽位：待扭曲的矿石。保留成独立类型而不是复用重塑机的双槽输入，
 * 以免将来给本机加槽位时影响到其它机器。
 */
public record TwistingRecipeInput(ItemStack stack) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? stack : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }
}
