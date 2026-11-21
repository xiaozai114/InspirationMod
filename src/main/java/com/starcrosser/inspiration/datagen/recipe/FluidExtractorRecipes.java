package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.fluid.ModFluids;
import com.starcrosser.inspiration.machine.recipe.FluidExtractingRecipe;
import com.starcrosser.inspiration.material.ModMaterials;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

public final class FluidExtractorRecipes {

    private FluidExtractorRecipes() {
    }

    public static void build(RecipeOutput recipeOutput) {
        // 声明了 molten 的材料：锭 -> 144 mB 自身熔融流体
        for (com.starcrosser.inspiration.material.InspirationMaterial m : ModMaterials.ALL) {
            if (m.moltenEntry() == null) {
                continue;
            }
            melt(recipeOutput, m.baseId() + "_to_" + m.moltenId(),
                    Ingredient.of(m.item().get()),
                    new FluidStack(m.moltenEntry().source().get(), FluidExtractingRecipe.INGOT_AMOUNT));
        }

        melt(recipeOutput, "plasma_ingot_to_molten_plasma",
                Ingredient.of(ModMaterials.PLASMA.item().get()),
                new FluidStack(ModFluids.Fluids.MOLTEN_PLASMA.get(), FluidExtractingRecipe.INGOT_AMOUNT));

        for (Item item : BuiltInRegistries.ITEM) {
            FoodProperties food = item.getDefaultInstance().get(DataComponents.FOOD);
            if (food == null || food.nutrition() <= 0) {
                continue;
            }
            int amount = Math.round((food.nutrition() + food.saturation()) * 100F);
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
            String name = key.getNamespace().equals("minecraft")
                    ? key.getPath()
                    : key.getNamespace() + "_" + key.getPath();
            melt(recipeOutput, "nutrient_from_" + name, Ingredient.of(item),
                    new FluidStack(ModFluids.Fluids.NUTRIENT_FLUID.get(), amount));
        }
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
