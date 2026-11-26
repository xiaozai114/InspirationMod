package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.machine.recipe.ResonanceRecipe;
import com.starcrosser.inspiration.material.ModMaterials;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public final class ResonanceRecipes {
    private ResonanceRecipes() {}

    public static void build(RecipeOutput out) {
        add(out, ModMaterials.PLASMA.item().get(), ModItems.QUANTUM_INGOT.get());
        add(out, ModMaterials.END_ALLOY.item().get(), ModItems.RESONANT_END_ALLOY.get());
        add(out, ModItems.INSPIRATION.get(), ModItems.RESONANT_INSPIRATION.get());
        add(out, ModMaterials.MIND_MATRIX.item().get(), ModItems.RESONANT_MIND_LATTICE.get());
    }

    private static void add(RecipeOutput out, Item input, Item result) {
        String path = "resonance/" + BuiltInRegistries.ITEM.getKey(result).getPath();
        out.accept(ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, path),
                new ResonanceRecipe(Ingredient.of(input), new ItemStack(result),
                        ResonanceRecipe.DURATION, ResonanceRecipe.FE_PER_TICK), null);
    }
}
