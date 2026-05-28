package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.recipe.RightClickBlockRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class RightClickBlockRecipeBuilder implements RecipeBuilder {
    private final Block inputBlock;
    private final Block outputBlock;
    private final ItemStack inputItem;
    private final ItemStack outputItem;
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();

    public RightClickBlockRecipeBuilder(Block inputBlock, Block outputBlock, ItemStack inputItem, ItemStack outputItem) {
        this.inputBlock = inputBlock;
        this.outputBlock = outputBlock;
        this.inputItem = inputItem;
        this.outputItem = outputItem;
    }

    public static RightClickBlockRecipeBuilder recipe(Block inputBlock, Block outputBlock, ItemStack inputItem) {
        return new RightClickBlockRecipeBuilder(inputBlock, outputBlock, inputItem,ItemStack.EMPTY);
    }
    public static RightClickBlockRecipeBuilder recipe(Block inputBlock, Block outputBlock, ItemStack inputItem, ItemStack outputItem) {
        return new RightClickBlockRecipeBuilder(inputBlock, outputBlock, inputItem,outputItem);
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.advancement.addCriterion(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        // 本配方类型不支持分组，忽略
        return this;
    }

    @Override
    public Item getResult() {
        return outputBlock.asItem();
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        this.advancement
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        output.accept(id, new RightClickBlockRecipe(inputBlock, outputBlock, inputItem, outputItem),
                this.advancement.build(id.withPrefix("recipes/")));
    }


}
