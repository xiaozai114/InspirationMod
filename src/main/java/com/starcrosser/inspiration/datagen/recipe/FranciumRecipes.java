package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import static com.starcrosser.inspiration.datagen.recipe.RecipeUnlocks.getHasName;
import static com.starcrosser.inspiration.datagen.recipe.RecipeUnlocks.has;

/**
 * 钫盐系列的处理流程：逐条对应铯盐，差别只有两点 ——
 * 全流程产物都带辐射（数值挂在数据地图上），以及钫的碱性略强于铯。
 */
public final class FranciumRecipes {

    private FranciumRecipes() {
    }

    public static void build(RecipeOutput recipeOutput) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModBlocks.FRANCIUM_SALT_ORE.get()), RecipeCategory.MISC,
                        ModItems.FRANCIUM_SALT.get(), 2.0f, 100)
                .unlockedBy(getHasName(ModBlocks.FRANCIUM_SALT_ORE.get()), has(ModBlocks.FRANCIUM_SALT_ORE.get()))
                .save(recipeOutput, "francium_salt_from_ore");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.RAW_FRANCIUM_SALT.get()), RecipeCategory.MISC,
                        ModItems.FRANCIUM_SALT.get(), 2.0f, 100)
                .unlockedBy(getHasName(ModBlocks.FRANCIUM_SALT_ORE.get()), has(ModItems.RAW_FRANCIUM_SALT.get()))
                .save(recipeOutput, "francium_salt_from_raw");
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ModItems.FRANCIUM_DUST.get()), RecipeCategory.MISC,
                        ModItems.FRANCIUM_INGOT.get(), 0.8f, 100)
                .unlockedBy(getHasName(ModItems.FRANCIUM_DUST.get()), has(ModItems.FRANCIUM_DUST.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FRANCIUM_SALT_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.FRANCIUM_SALT.get())
                .unlockedBy(getHasName(ModItems.FRANCIUM_SALT.get()), has(ModItems.FRANCIUM_SALT.get()))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FRANCIUM_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.FRANCIUM_INGOT.get())
                .unlockedBy(getHasName(ModItems.FRANCIUM_INGOT.get()), has(ModItems.FRANCIUM_INGOT.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FRANCIUM_SALT.get(), 9)
                .requires(ModBlocks.FRANCIUM_SALT_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.FRANCIUM_SALT_BLOCK.get()), has(ModBlocks.FRANCIUM_SALT_BLOCK.get()))
                .save(recipeOutput, "francium_salt_from_block");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FRANCIUM_INGOT.get(), 9)
                .requires(ModBlocks.FRANCIUM_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.FRANCIUM_BLOCK.get()), has(ModBlocks.FRANCIUM_BLOCK.get()))
                .save(recipeOutput, "francium_ingot_from_block");
    }
}
