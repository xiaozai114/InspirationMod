package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.material.ModMaterials;
import com.starcrosser.inspiration.voidenergy.recipe.VoidInjectionRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * 虚空注入配方：只灌能量、不消耗 FE。
 * <p>
 * 窗口写的是「团簇能量的 10%」（成本），不是团簇当前能量本身。
 */
public final class VoidInjectionRecipes {

    private VoidInjectionRecipes() {
    }

    public static void build(RecipeOutput recipeOutput) {
        // 强化核心 + 64~256 ve -> 弱化核心
        voidInjection(recipeOutput, "void_injection/weakened_core",
                Ingredient.of(ModItems.REINFORCED_CORE.get()), 64, 256,
                new ItemStack(ModItems.WEAKENED_CORE.get()));

        // 等离子锭 + 32~128 ve -> 虚空等离子锭
        voidInjection(recipeOutput, "void_injection/void_plasma_ingot",
                Ingredient.of(ModMaterials.PLASMA.item().get()), 32, 128,
                new ItemStack(ModItems.VOID_PLASMA_INGOT.get()));
    }

    private static void voidInjection(RecipeOutput output, String path, Ingredient base,
                                      int minVe, int maxVe, ItemStack result) {
        output.accept(ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, path),
                new VoidInjectionRecipe(base, null, 1, minVe, maxVe, result, 100), null);
    }
}
