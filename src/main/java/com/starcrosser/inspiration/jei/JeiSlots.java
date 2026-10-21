package com.starcrosser.inspiration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.List;

/**
 * JEI 输入槽的统一建法。
 * <p>
 * 本模组的机器配方允许「一种材料一次消耗多个」（融合的 {@code first_count}、
 * 重塑的 {@code input_count}、装配的 {@code counts}）。JEI 的 {@code addIngredients}
 * 只会画出 1 个，玩家照着看就会少放材料，所以消耗量大于 1 时改用带数量的
 * {@link ItemStack} 展示 —— JEI 会在图标角上画出数字。
 * <p>
 * 消耗量为 1 时仍然交 {@link Ingredient}：这样标签类原料（{@code tag:minecraft:planks}）
 * 会保留「多种物品轮流展示」的效果，改成固定 ItemStack 后就只会显示第一种。
 * <p>
 * 新增配方类型时直接调 {@link #input}，不必再自己写一遍。
 */
public final class JeiSlots {

    private JeiSlots() {
    }

    /** 在 (x, y) 建一个标准输入槽，按消耗量决定展示方式。 */
    public static IRecipeSlotBuilder input(IRecipeLayoutBuilder builder, int x, int y,
                                           Ingredient ingredient, int count) {
        IRecipeSlotBuilder slot = builder.addInputSlot(x, y).setStandardSlotBackground();
        add(slot, ingredient, count);
        return slot;
    }

    /** 往已有槽位里放材料，按消耗量决定展示方式。 */
    public static void add(IRecipeSlotBuilder slot, Ingredient ingredient, int count) {
        if (count > 1) {
            List<ItemStack> stacks = Arrays.stream(ingredient.getItems())
                    .map(stack -> stack.copyWithCount(count))
                    .toList();
            slot.addItemStacks(stacks);
        } else {
            slot.addIngredients(ingredient);
        }
    }
}
