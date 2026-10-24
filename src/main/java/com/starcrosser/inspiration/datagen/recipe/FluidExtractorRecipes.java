package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.fluid.ModFluids;
import com.starcrosser.inspiration.machine.recipe.FluidExtractingRecipe;
import com.starcrosser.inspiration.material.ModMaterials;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * 流体提取机的配方：材料 → 对应的液态金属。
 * <p>
 * 比例统一按「一锭 = 144 mB」折算（{@link FluidExtractingRecipe#INGOT_AMOUNT}），
 * 所以九粒 = 一锭、一块 = 九锭在流体这一侧也对得上。
 * <p>
 * 目前只有等离子有熔融流体；以后给别的材料开 {@code molten} 开关时，
 * 往 {@link #build} 里补一条即可。
 */
public final class FluidExtractorRecipes {

    private FluidExtractorRecipes() {
    }

    public static void build(RecipeOutput recipeOutput) {
        // 等离子锭 -> 液态等离子（144 mB）
        melt(recipeOutput, "plasma_ingot_to_molten_plasma",
                Ingredient.of(ModMaterials.PLASMA.item().get()),
                new FluidStack(ModFluids.Fluids.MOLTEN_PLASMA.get(), FluidExtractingRecipe.INGOT_AMOUNT));
    }

    private static void melt(RecipeOutput recipeOutput, String id, Ingredient input, FluidStack result) {
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "fluid_extracting/" + id),
                new FluidExtractingRecipe(input, 1, result,
                        FluidExtractingRecipe.DEFAULT_DURATION,
                        FluidExtractingRecipe.DEFAULT_ENERGY_PER_TICK),
                null);
    }
}
