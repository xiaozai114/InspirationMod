package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.datagen.RightClickBlockRecipeBuilder;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.material.ModMaterials;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import static com.starcrosser.inspiration.datagen.recipe.RecipeUnlocks.has;

/**
 * 右键方块转换配方：手持某物品右键某个方块，把它变成另一个方块。
 * <p>
 * 铯盐块 -> 铯块那条要消耗还原粉，是本 mod 把盐还原成金属的主要途径。
 */
public final class RightClickRecipes {

    private RightClickRecipes() {
    }

    public static void build(RecipeOutput recipeOutput) {
        // 还原粉右键转换
        RightClickBlockRecipeBuilder.recipe(ModBlocks.CESIUM_SALT_BLOCK.get(), ModMaterials.CESIUM.block().get(),
                        new ItemStack(ModItems.REDUCTION_POWDER.get()))
                .unlockedBy("has_reduction_powder", has(ModItems.REDUCTION_POWDER.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "cesium_block_from_salt"));

        RightClickBlockRecipeBuilder.recipe(Blocks.DIRT, Blocks.GRASS_BLOCK, ModItems.SOD.toStack())
                .unlockedBy("has_sod", has(ModItems.SOD))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "sod_grass"));
        RightClickBlockRecipeBuilder.recipe(Blocks.ICE, Blocks.PACKED_ICE, ModItems.INSPIRATION.toStack())
                .unlockedBy("has_inspiration", has(ModItems.INSPIRATION))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "insp_ice"));
        RightClickBlockRecipeBuilder.recipe(Blocks.PACKED_ICE, Blocks.BLUE_ICE, ModItems.INSPIRATION.toStack())
                .unlockedBy("has_inspiration", has(ModItems.INSPIRATION))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "insp_ice_blue"));
        RightClickBlockRecipeBuilder.recipe(Blocks.LAVA, Blocks.MAGMA_BLOCK, ModItems.INSPIRATION.toStack())
                .unlockedBy("has_inspiration", has(ModItems.INSPIRATION))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "insp_lava"));
        RightClickBlockRecipeBuilder.recipe(Blocks.MAGMA_BLOCK,Blocks.LAVA, ModItems.INSPIRATION.toStack())
                .unlockedBy("has_inspiration", has(ModItems.INSPIRATION))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "insp_lava_inv"));
        RightClickBlockRecipeBuilder.recipe(Blocks.COBBLESTONE,Blocks.MOSSY_COBBLESTONE, ModItems.SOD.toStack())
                .unlockedBy("has_sod", has(ModItems.SOD))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "sod_moss"));
        RightClickBlockRecipeBuilder.recipe(Blocks.OAK_LEAVES,Blocks.AIR, ModItems.INSPIRATION.toStack(),new ItemStack(Items.APPLE))
                .unlockedBy("has_inspiration", has(ModItems.INSPIRATION))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "insp_apple"));
        RightClickBlockRecipeBuilder.recipe(Blocks.BONE_BLOCK,Blocks.AIR, ModItems.INSPIRATION.toStack(),new ItemStack(ModItems.CALCIUM_DUST.get()))
                .unlockedBy("has_inspiration", has(ModItems.INSPIRATION))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "insp_calcium"));

        // 钫盐块 + 还原粉（右键）-> 钫块，与铯同法
        RightClickBlockRecipeBuilder.recipe(ModBlocks.FRANCIUM_SALT_BLOCK.get(), ModBlocks.FRANCIUM_BLOCK.get(),
                        new ItemStack(ModItems.REDUCTION_POWDER.get()))
                .unlockedBy("has_reduction_powder", has(ModItems.REDUCTION_POWDER.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "francium_block_from_salt"));
    }
}
