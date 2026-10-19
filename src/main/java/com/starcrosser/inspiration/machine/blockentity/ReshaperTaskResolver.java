package com.starcrosser.inspiration.machine.blockentity;

import com.starcrosser.inspiration.machine.recipe.ReshapingRecipe;
import com.starcrosser.inspiration.machine.recipe.ReshapingRecipeInput;
import com.starcrosser.inspiration.recipe.ModRecipes;
import com.starcrosser.inspiration.recipe.RightClickBlockRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * 物质重塑机的配方解析。
 * <p>
 * 优先级：先查物质重塑配方，再退回到右键转化配方。这样「高级配方」可以覆盖同名的基础转化。
 */
public final class ReshaperTaskResolver {

    /** 右键转化配方在重塑机中的耗时：比物质融合器慢一倍。 */
    public static final int RIGHT_CLICK_DURATION = 400;

    /** 右键转化配方在重塑机中的能耗：约为物质融合器的五倍。 */
    public static final int RIGHT_CLICK_ENERGY_PER_TICK = 200;

    /**
     * 根据两个输入槽的内容找出可执行的任务。
     *
     * @param first  主输入：待重塑的物质
     * @param second 辅助材料，可为空
     * @return 匹配到的任务；没有匹配时返回 {@code null}
     */
    @Nullable
    public static ReshaperTask resolve(Level level, ItemStack first, ItemStack second) {
        if (first.isEmpty()) {
            return null;
        }
        ReshaperTask reshaping = resolveReshaping(level, first, second);
        return reshaping != null ? reshaping : resolveRightClick(level, first, second);
    }

    @Nullable
    private static ReshaperTask resolveReshaping(Level level, ItemStack first, ItemStack second) {
        ReshapingRecipeInput input = new ReshapingRecipeInput(first, second);
        ReshapingRecipe recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.RESHAPING_TYPE.get(), input, level)
                .map(RecipeHolder::value)
                .orElse(null);
        if (recipe == null) {
            return null;
        }
        return new ReshaperTask(recipe.result().copy(), recipe.inputCount(), recipe.duration(),
                recipe.energyPerTick(), recipe.hasSecondInput() ? 1 : 0);
    }

    @Nullable
    private static ReshaperTask resolveRightClick(Level level, ItemStack first, ItemStack second) {
        if (second.isEmpty()) {
            return null;
        }
        for (RecipeHolder<RightClickBlockRecipe> holder : level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.RIGHT_CLICK_BLOCK_TYPE.get())) {
            RightClickBlockRecipe recipe = holder.value();
            if (!recipe.matchesStacks(first, second)) {
                continue;
            }
            ItemStack result = recipe.displayResult();
            if (result.isEmpty()) {
                continue;
            }
            return new ReshaperTask(result.copy(), ReshapingRecipe.DEFAULT_INPUT_COUNT, RIGHT_CLICK_DURATION,
                    RIGHT_CLICK_ENERGY_PER_TICK, recipe.inputItem().getCount());
        }
        return null;
    }

    private ReshaperTaskResolver() {
    }
}
