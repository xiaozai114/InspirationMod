package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class ModRecipesProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // ========== 烧炼 / 烟熏 / 营火 / 高炉配方 ==========
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

        // ========== 有序合成配方 ==========
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.INTENSIFIED_TNT.get())
                .pattern("Y#Y")
                .pattern("#Y#")
                .pattern("Y#Y")
                .define('#', Blocks.SAND)
                .define('Y', ModItems.CESIUM_SALT.get())
                .unlockedBy(getHasName(ModItems.CESIUM_SALT.get()), has(ModItems.CESIUM_SALT.get()))
                .save(recipeOutput);
        // 灵感（默认 ID "inspiration"）
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INSPIRATION.get())
                .pattern(" # ")
                .pattern("#Y#")
                .pattern(" # ")
                .define('#', Items.COPPER_INGOT)
                .define('Y', Items.QUARTZ)
                .unlockedBy(getHasName(Items.QUARTZ), has(Items.QUARTZ))
                .save(recipeOutput);

        // 红石锭（默认 ID "redstone_ingot"）
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.REDSTONE_INGOT.get(), 2)
                .group("redstone_ingot")
                .pattern("###")
                .pattern("#Y#")
                .pattern("#X#")
                .define('#', Items.REDSTONE)
                .define('Y', ModItems.INSPIRATION.get())
                .define('X', Items.GOLD_INGOT)
                .unlockedBy(getHasName(ModItems.INSPIRATION.get()), has(ModItems.INSPIRATION.get()))
                .save(recipeOutput);

        // 青金石锭（默认 ID "lapis_ingot"）
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LAPIS_INGOT.get(), 2)
                .group("lapis_ingot")
                .pattern("###")
                .pattern("#Y#")
                .pattern("#X#")
                .define('#', Items.LAPIS_LAZULI)
                .define('Y', ModItems.INSPIRATION.get())
                .define('X', Items.COPPER_INGOT)
                .unlockedBy(getHasName(ModItems.INSPIRATION.get()), has(ModItems.INSPIRATION.get()))
                .save(recipeOutput);

        // 褐煤块（默认 ID "lignite_block"）
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIGNITE_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.LIGNITE.get())
                .unlockedBy(getHasName(ModItems.LIGNITE.get()), has(ModItems.LIGNITE.get()))
                .save(recipeOutput);

        // 红石锭块（默认 ID "redstone_ingot_block"）
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.REDSTONE_INGOT_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.REDSTONE_INGOT.get())
                .unlockedBy(getHasName(ModItems.REDSTONE_INGOT.get()), has(ModItems.REDSTONE_INGOT.get()))
                .save(recipeOutput);

        // 青金石锭块（默认 ID "lapis_ingot_block"）
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LAPIS_INGOT_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.LAPIS_INGOT.get())
                .unlockedBy(getHasName(ModItems.LAPIS_INGOT.get()), has(ModItems.LAPIS_INGOT.get()))
                .save(recipeOutput);

        // 极限合金（默认 ID "extreme_alloy"）
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EXTREME_ALLOY.get())
                .pattern("###")
                .pattern("#YZ")
                .pattern("ZZZ")
                .define('#', ModBlocks.REDSTONE_INGOT_BLOCK.get())
                .define('Y', ModItems.INSPIRATION.get())
                .define('Z', ModBlocks.LAPIS_INGOT_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.REDSTONE_INGOT_BLOCK.get()), has(ModBlocks.REDSTONE_INGOT_BLOCK.get()))
                .save(recipeOutput);

        // 空信标（默认 ID "empty_beacon"）
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EMPTY_BEACON.get())
                .group("empty_beacon")
                .pattern("###")
                .pattern("#Y#")
                .pattern("ZXZ")
                .define('#', Items.GLASS)
                .define('Y', ModItems.UNSTABLE_CORE.get())
                .define('X', ModItems.EPHEMERAL_MATERIAL.get())
                .define('Z', Items.OBSIDIAN)
                .unlockedBy(getHasName(ModItems.UNSTABLE_CORE.get()), has(ModItems.UNSTABLE_CORE.get()))
                .save(recipeOutput);

        // 灵感之剑（默认 ID "exp_sword"）
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.EXP_SWORD.get())
                .pattern("X")
                .pattern("Y")
                .pattern("#")
                .define('#', Tags.Items.RODS_WOODEN)
                .define('X', Items.CACTUS)
                .define('Y', ModItems.INSPIRATION.get())
                .unlockedBy(getHasName(ModItems.INSPIRATION.get()), has(ModItems.INSPIRATION.get()))
                .save(recipeOutput);

        // 灵感之镐（默认 ID "exp_pickaxe"）
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.EXP_PICKAXE.get())
                .pattern("ZXW")
                .pattern(" Y ")
                .pattern(" # ")
                .define('#', Tags.Items.RODS_WOODEN)
                .define('X', Items.CRYING_OBSIDIAN)
                .define('Y', ModItems.INSPIRATION.get())
                .define('W', ModItems.REDSTONE_INGOT.get())
                .define('Z', ModItems.LAPIS_INGOT.get())
                .unlockedBy(getHasName(ModItems.INSPIRATION.get()), has(ModItems.INSPIRATION.get()))
                .save(recipeOutput);

        // 灵感之锄（默认 ID "exp_hoe"）
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.EXP_HOE.get())
                .pattern("ZX")
                .pattern(" Y")
                .pattern(" #")
                .define('#', Tags.Items.RODS_WOODEN)
                .define('X', Items.MOSS_BLOCK)
                .define('Y', ModItems.INSPIRATION.get())
                .define('Z', Items.FERN)
                .unlockedBy(getHasName(ModItems.INSPIRATION.get()), has(ModItems.INSPIRATION.get()))
                .save(recipeOutput);

        // 灵感之斧（粘性斧，默认 ID "sticky_axe"）
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.STICKY_AXE.get())
                .pattern("ZX")
                .pattern("ZY")
                .pattern(" #")
                .define('#', Tags.Items.RODS_WOODEN)
                .define('X', Items.WARPED_WART_BLOCK)
                .define('Y', ModItems.INSPIRATION.get())
                .define('Z', Items.EMERALD)
                .unlockedBy(getHasName(ModItems.INSPIRATION.get()), has(ModItems.INSPIRATION.get()))
                .save(recipeOutput);

        // ========== 无序合成配方 ==========
        // 灵感 -> 4 瓶经验瓶（默认 ID "experience_bottle"）
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.EXPERIENCE_BOTTLE, 4)
                .requires(ModItems.INSPIRATION.get())
                .unlockedBy(getHasName(ModItems.INSPIRATION.get()), has(ModItems.INSPIRATION.get()))
                .save(recipeOutput);

        // 褐煤（从灵感合成），默认 ID "lignite"，无需指定
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.LIGNITE.get(), 6)
                .group("lignite")
                .requires(ModItems.INSPIRATION.get())
                .requires(Items.COAL)
                .requires(Items.COAL)
                .requires(Items.COAL)
                .requires(Items.COAL)
                .unlockedBy(getHasName(Items.COAL), has(Items.COAL))
                .save(recipeOutput);

        // 褐煤块分解，需要指定 ID 避免与上面冲突
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.LIGNITE.get(), 9)
                .group("lignite")
                .requires(ModBlocks.LIGNITE_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.LIGNITE_BLOCK.get()), has(ModBlocks.LIGNITE_BLOCK.get()))
                .save(recipeOutput, "lignite_from_lignite_block");

        // 红石锭块分解，需要指定 ID
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.REDSTONE_INGOT.get(), 9)
                .group("redstone_ingot")
                .requires(ModBlocks.REDSTONE_INGOT_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.REDSTONE_INGOT_BLOCK.get()), has(ModBlocks.REDSTONE_INGOT_BLOCK.get()))
                .save(recipeOutput, "redstone_ingot_from_redstone_ingot_block");

        // 青金石锭块分解，需要指定 ID
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.LAPIS_INGOT.get(), 9)
                .group("lapis_ingot")
                .requires(ModBlocks.LAPIS_INGOT_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.LAPIS_INGOT_BLOCK.get()), has(ModBlocks.LAPIS_INGOT_BLOCK.get()))
                .save(recipeOutput, "lapis_ingot_from_lapis_ingot_block");

        // 灵感面包（默认 ID "inspiread"）
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.INSPIREAD.get())
                .requires(ModItems.COOKED_INSPIRATION.get())
                .requires(Items.BREAD)
                .unlockedBy(getHasName(ModItems.COOKED_INSPIRATION.get()), has(ModItems.COOKED_INSPIRATION.get()))
                .save(recipeOutput);

        // 灵感浆果（默认 ID "inspirry"）
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.INSPIRRY.get(), 4)
                .requires(ModItems.COOKED_INSPIRATION.get())
                .requires(Items.GLOW_BERRIES)
                .requires(Items.GLOW_BERRIES)
                .requires(Items.SWEET_BERRIES)
                .requires(Items.SWEET_BERRIES)
                .unlockedBy(getHasName(ModItems.COOKED_INSPIRATION.get()), has(ModItems.COOKED_INSPIRATION.get()))
                .save(recipeOutput);

        // 草皮 + 泥土 -> 草方块，需要指定 ID 与原 JSON 一致
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.GRASS_BLOCK)
                .requires(ModItems.SOD.get())
                .requires(Items.DIRT)
                .unlockedBy(getHasName(ModItems.SOD.get()), has(ModItems.SOD.get()))
                .save(recipeOutput, "grass_block_from_sod");

        // ========== 锻造转换配方 ==========
        // 不稳定锻造模板（默认 ID "unstable_smithing_template"）
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(ModItems.UNSTABLE_CORE.get()),
                        RecipeCategory.MISC, ModItems.UNSTABLE_SMITHING_TEMPLATE.get())
                .unlocks("has_unstable_core", has(ModItems.UNSTABLE_CORE.get()))
                .save(recipeOutput,"unstable_smithing_template_smithing");

        // 斩首者，需要指定 ID
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE),
                        Ingredient.of(ModItems.STICKY_AXE.get()),
                        Ingredient.of(Items.CARVED_PUMPKIN),
                        RecipeCategory.COMBAT, ModItems.HEAD_HUNTER_AXE.get())
                .unlocks("has_sticky_axe", has(ModItems.STICKY_AXE.get()))
                .save(recipeOutput, "head_hunter_smithing");

        // 风暴军刀，需要指定 ID
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE),
                        Ingredient.of(ModItems.EXP_SWORD.get()),
                        Ingredient.of(Items.BREEZE_ROD),
                        RecipeCategory.COMBAT, ModItems.STORM_SWORD.get())
                .unlocks("has_exp_sword", has(ModItems.EXP_SWORD.get()))
                .save(recipeOutput, "storm_sword_smithing");

        // 死神之镰，需要指定 ID
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE),
                        Ingredient.of(ModItems.EXP_HOE.get()),
                        Ingredient.of(ModItems.EPHEMERAL_MATERIAL.get()),
                        RecipeCategory.TOOLS, ModItems.SCYTHE_OF_DEATH.get())
                .unlocks("has_exp_hoe", has(ModItems.EXP_HOE.get()))
                .save(recipeOutput, "scythe_of_death_smithing");

        // 世界破坏者（默认 ID "world_breaker"）
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.UNSTABLE_SMITHING_TEMPLATE.get()),
                        Ingredient.of(ModItems.EXP_PICKAXE.get()),
                        Ingredient.of(ModItems.EXTREME_ALLOY.get()),
                        RecipeCategory.TOOLS, ModItems.WORLD_BREAKER.get())
                .unlocks("has_extreme_alloy", has(ModItems.EXTREME_ALLOY.get()))
                .save(recipeOutput,"world_breaker_smithing");
    }
}