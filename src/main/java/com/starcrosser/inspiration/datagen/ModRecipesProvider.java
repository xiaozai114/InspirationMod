package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipesProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }
    //public static final List<ItemLike> COOKED_INSPIRATION = List.of(ModItems.INSPIRATION);

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput){
        super.buildRecipes(recipeOutput);
        //oreSmelting(recipeOutput, COOKED_INSPIRATION, RecipeCategory.MISC, ModItems.COOKED_INSPIRATION,0.25f,200,"COOKED_INSPIRATION");

//        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIGNITE_BLOCK)
//                .pattern("###")
//                .pattern("###")
//                .pattern("###")
//                .define('#', ModItems.LIGNITE)
//                .unlockedBy(getHasName(ModItems.LIGNITE), has(ModItems.LIGNITE))
//                .save(recipeOutput);
//
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.LIGNITE,9)
//                .requires(ModBlocks.LIGNITE_BLOCK)
//                .unlockedBy(getHasName(ModBlocks.LIGNITE_BLOCK), has(ModBlocks.LIGNITE_BLOCK))
//                .save(recipeOutput,Inspiration.MODID+":"+"lignite_from_lignite_block");
//        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INSPIRATION)
//                .pattern(" # ")
//                .pattern("#Y#")
//                .pattern(" # ")
//                .define('#', Items.COPPER_INGOT)
//                .define('Y', Items.QUARTZ)
//                .unlockedBy(getHasName(Items.QUARTZ), has(Items.QUARTZ))
//                .save(recipeOutput);
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.LIGNITE,6)
//                .requires(ModItems.INSPIRATION)
//                .requires(Items.COAL)
//                .requires(Items.COAL)
//                .requires(Items.COAL)
//                .requires(Items.COAL)
//                .unlockedBy(getHasName(Items.COAL), has(Items.COAL))
//                .save(recipeOutput);
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.EXPERIENCE_BOTTLE,4)
//                .requires(ModItems.INSPIRATION)
//                .unlockedBy(getHasName(ModItems.INSPIRATION), has(ModItems.INSPIRATION))
//                .save(recipeOutput, Inspiration.MODID+":"+"experience_bottle");

    }
    protected static void oreSmelting(
            RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group
    ) {
        oreCooking(
                recipeOutput,
                RecipeSerializer.SMELTING_RECIPE,
                SmeltingRecipe::new,
                ingredients,
                category,
                result,
                experience,
                cookingTime,
                group,
                "_from_smelting"
        );
    }

    protected static void oreBlasting(
            RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group
    ) {
        oreCooking(
                recipeOutput,
                RecipeSerializer.BLASTING_RECIPE,
                BlastingRecipe::new,
                ingredients,
                category,
                result,
                experience,
                cookingTime,
                group,
                "_from_blasting"
        );
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(
            RecipeOutput recipeOutput,
            RecipeSerializer<T> serializer,
            AbstractCookingRecipe.Factory<T> recipeFactory,
            List<ItemLike> ingredients,
            RecipeCategory category,
            ItemLike result,
            float experience,
            int cookingTime,
            String group,
            String suffix
    ) {
        for (ItemLike itemlike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), category, result, experience, cookingTime, serializer, recipeFactory)
                    .group(group)
                    .unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, Inspiration.MODID+":"+getItemName(result) + suffix + "_" + getItemName(itemlike));
        }
    }
}
