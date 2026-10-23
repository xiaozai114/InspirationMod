package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.material.InspirationMaterial;
import com.starcrosser.inspiration.material.ModMaterials;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;

import static com.starcrosser.inspiration.datagen.recipe.RecipeUnlocks.getHasName;
import static com.starcrosser.inspiration.datagen.recipe.RecipeUnlocks.has;

/**
 * 材料的形态转换：锭 / 粒 / 尘三档之间互相打包与拆包。
 * <p>
 * 这里是**表驱动**的：遍历 {@link ModMaterials#ALL}，凡是开了粒或尘的材料自动拿到
 * 两个方向的配方。所以以后再加一种金属，只要在 {@code ModMaterials} 里声明时打开
 * 开关，转换配方就跟着有了，不用回来补代码。
 * <p>
 * 数量关系是 1 锭 = 9 粒 = 81 尘，与原版铁/金的锭粒比例一致。
 */
public final class MaterialRecipes {

    private MaterialRecipes() {
    }

    public static void build(RecipeOutput recipeOutput) {
        for (InspirationMaterial material : ModMaterials.ALL) {
            if (material.hasNugget()) {
                pack(recipeOutput, material.nugget().get(), material.item().get(),
                        material.itemId() + "_from_nuggets");
                unpack(recipeOutput, material.item().get(), material.nugget().get(),
                        material.nuggetId() + "_from_ingot");
            }
            if (material.hasGrit()) {
                pack(recipeOutput, material.grit().get(), material.nugget().get(),
                        material.nuggetId() + "_from_grits");
                unpack(recipeOutput, material.nugget().get(), material.grit().get(),
                        material.gritId() + "_from_nugget");
            }
        }
    }

    /** 9 个小件压成 1 个大件（3×3 有序合成）。 */
    private static void pack(RecipeOutput recipeOutput, Item small, Item big, String id) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, big)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', small)
                .unlockedBy(getHasName(small), has(small))
                .save(recipeOutput, id);
    }

    /** 1 个大件拆成 9 个小件（无序合成）。 */
    private static void unpack(RecipeOutput recipeOutput, Item big, Item small, String id) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, small, 9)
                .requires(big)
                .unlockedBy(getHasName(big), has(big))
                .save(recipeOutput, id);
    }
}
