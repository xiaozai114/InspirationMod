package com.starcrosser.inspiration.machine.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

/**
 * 灵感装配台的输入：若干物品槽 + 若干独立的流体输入槽。
 * <p>
 * 流体单独成槽而不是复用机器的灵感充能罐 —— 灵感充能是机器的燃料，
 * 这里的流体是装配原料，两者互不相干。
 * 槽数由机器自己决定（普通装配台 4 物品 + 1 流体，高能装配台 6 + 2），
 * 配方只要求「每条需求都能在某个槽 / 罐里找到对应的东西」。
 */
public record AssemblingRecipeInput(List<ItemStack> items, List<FluidStack> fluids) implements RecipeInput {

    /** 普通灵感装配台的物品输入槽数量。 */
    public static final int SLOT_COUNT = 4;

    /** 高能灵感装配台的物品输入槽数量。 */
    public static final int HIGH_ENERGY_SLOT_COUNT = 6;

    @Override
    public ItemStack getItem(int index) {
        return index >= 0 && index < items.size() ? items.get(index) : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty)
                && fluids.stream().allMatch(FluidStack::isEmpty);
    }
}
