package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.material.ModMaterials;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import static com.starcrosser.inspiration.datagen.recipe.RecipeUnlocks.getHasName;
import static com.starcrosser.inspiration.datagen.recipe.RecipeUnlocks.has;

/** 烧炼类配方：熔炉 / 高炉 / 烟熏 / 营火。 */
public final class CookingRecipes {

    private CookingRecipes() {
    }

    public static void build(RecipeOutput recipeOutput) {
        // 褐煤 -> 焦煤（高炉），默认 ID 即为 "coke"，无需指定
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ModItems.LIGNITE.get()), RecipeCategory.MISC,
                        ModItems.COKE.get(), 0.8f, 100)
                .unlockedBy(getHasName(ModItems.LIGNITE.get()), has(ModItems.LIGNITE.get()))
                .save(recipeOutput);

        // 灵感 -> 烤灵感（烟熏），需要指定文件名匹配原 JSON
        SimpleCookingRecipeBuilder.smoking(Ingredient.of(ModItems.INSPIRATION.get()), RecipeCategory.FOOD,
                        ModItems.COOKED_INSPIRATION.get(), 0.8f, 200)
                .unlockedBy(getHasName(ModItems.INSPIRATION.get()), has(ModItems.INSPIRATION.get()))
                .save(recipeOutput, "cooked_inspiration_from_smoking");

        // 灵感 -> 烤灵感（营火），需要指定文件名
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(ModItems.INSPIRATION.get()), RecipeCategory.FOOD,
                        ModItems.COOKED_INSPIRATION.get(), 0.8f, 1200)
                .unlockedBy(getHasName(ModItems.INSPIRATION.get()), has(ModItems.INSPIRATION.get()))
                .save(recipeOutput, "cooked_inspiration_from_campfire_cooking");

        // 无恒矿石 -> 无恒物质（熔炉），需要指定文件名
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModBlocks.EPHEMERAL_ORE.get()), RecipeCategory.MISC,
                        ModItems.EPHEMERAL_MATERIAL.get(), 2.0f, 200)
                .unlockedBy(getHasName(ModBlocks.EPHEMERAL_ORE.get()), has(ModBlocks.EPHEMERAL_ORE.get()))
                .save(recipeOutput, "ephemeral_material");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModBlocks.CESIUM_SALT_ORE.get()), RecipeCategory.MISC,
                        ModItems.CESIUM_SALT.get(), 2.0f, 100)
                .unlockedBy(getHasName(ModBlocks.CESIUM_SALT_ORE.get()), has(ModBlocks.CESIUM_SALT_ORE.get()))
                .save(recipeOutput, "cesium_salt_from_ore");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.RAW_CESIUM_SALT.get()), RecipeCategory.MISC,
                        ModItems.CESIUM_SALT.get(), 2.0f, 100)
                .unlockedBy(getHasName(ModBlocks.CESIUM_SALT_ORE.get()), has(ModItems.RAW_CESIUM_SALT.get()))
                .save(recipeOutput, "cesium_salt_from_raw");

        // 铯粉 -> 铯锭（高炉）
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ModItems.CESIUM_DUST.get()), RecipeCategory.MISC,
                        ModMaterials.CESIUM.item().get(), 0.8f, 100)
                .unlockedBy(getHasName(ModItems.CESIUM_DUST.get()), has(ModItems.CESIUM_DUST.get()))
                .save(recipeOutput);

        // 钙粉 -> 钙锭（高炉）：原料精炼机的机壳材料
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ModItems.CALCIUM_DUST.get()), RecipeCategory.MISC,
                        ModMaterials.CALCIUM.item().get(), 0.8f, 100)
                .unlockedBy(getHasName(ModItems.CALCIUM_DUST.get()), has(ModItems.CALCIUM_DUST.get()))
                .save(recipeOutput, "calcium_ingot");
    }
}
