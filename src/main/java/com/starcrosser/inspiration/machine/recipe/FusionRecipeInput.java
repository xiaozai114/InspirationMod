package com.starcrosser.inspiration.machine.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

/**
 * 物质融合器的配方输入。
 * <p>
 * 用列表而不是固定的两个字段：基础机型只有两个输入槽，强能机型有三个，
 * 列表可以让两种机型共用同一个 {@link FusionRecipe} 的匹配逻辑。
 * 列表顺序与机器里的槽位序号一致（允许含空格 —— 匹配时会被跳过）。
 */
public record FusionRecipeInput(List<ItemStack> items) implements RecipeInput {

    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < items.size() ? items.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return items.size();
    }
}
