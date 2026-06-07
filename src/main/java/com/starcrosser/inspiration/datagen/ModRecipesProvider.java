package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
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

        // 粗翡翠钢 -> 翡翠钢锭（熔炉）
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.ROUGH_JADE_STEEL.get()), RecipeCategory.MISC,
                        ModItems.JADE_STEEL_INGOT.get(), 1.5f, 200)
                .unlockedBy(getHasName(ModItems.ROUGH_JADE_STEEL.get()), has(ModItems.ROUGH_JADE_STEEL.get()))
                .save(recipeOutput);
        // 铯粉 -> 铯锭（高炉）
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ModItems.CESIUM_DUST.get()), RecipeCategory.MISC,
                        ModItems.CESIUM_INGOT.get(), 0.8f, 100)
                .unlockedBy(getHasName(ModItems.CESIUM_DUST.get()), has(ModItems.CESIUM_DUST.get()))
                .save(recipeOutput);

        // ========== 有序合成配方 ==========

        //         // 还原粉：焦煤+钙粉+灵感
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.REDUCTION_POWDER.get(), 3)
                .requires(ModItems.COKE.get())
                .requires(ModItems.CALCIUM_DUST.get())
                .requires(ModItems.INSPIRATION.get())
                .unlockedBy(getHasName(ModItems.COKE.get()), has(ModItems.COKE.get()))
                .save(recipeOutput);

        // 铯盐块
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CESIUM_SALT_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.CESIUM_SALT.get())
                .unlockedBy(getHasName(ModItems.CESIUM_SALT.get()), has(ModItems.CESIUM_SALT.get()))
                .save(recipeOutput);

        // 铯块
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CESIUM_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.CESIUM_INGOT.get())
                .unlockedBy(getHasName(ModItems.CESIUM_INGOT.get()), has(ModItems.CESIUM_INGOT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.EMPOWERED_TNT_ITEM.get())
                .pattern("Y#Y")
                .pattern("#Y#")
                .pattern("Y#Y")
                .define('#', Blocks.SAND)
                .define('Y', ModItems.CESIUM_DUST.get())
                .unlockedBy(getHasName(ModItems.CESIUM_DUST.get()), has(ModItems.CESIUM_DUST.get()))
                .save(recipeOutput, "empowered_tnt");
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
        // 动力信标（默认 ID "power_beacon"）
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.POWER_BEACON.get())
                .group("power_beacon")
                .pattern("#Y#")
                .pattern("#Z#")
                .pattern("XXX")
                .define('#', ModItems.CESIUM_SALT.get())
                .define('Y', Items.WITHER_SKELETON_SKULL)
                .define('X', ModItems.EPHEMERAL_MATERIAL.get())
                .define('Z', ModItems.EMPTY_BEACON.get())
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

        // 铯盐块 -> 铯盐
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CESIUM_SALT.get(), 9)
                .requires(ModBlocks.CESIUM_SALT_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.CESIUM_SALT_BLOCK.get()), has(ModBlocks.CESIUM_SALT_BLOCK.get()))
                .save(recipeOutput, "cesium_salt_from_block");

        // 铯块 -> 铯锭
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CESIUM_INGOT.get(), 9)
                .requires(ModBlocks.CESIUM_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.CESIUM_BLOCK.get()), has(ModBlocks.CESIUM_BLOCK.get()))
                .save(recipeOutput, "cesium_ingot_from_block");

        //         // 绿宝石矿：灵感+绿宝石+石头
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.EMERALD_ORE)
                .pattern("SES")
                .pattern("EIE")
                .pattern("SES")
                .define('S', Items.STONE)
                .define('E', Items.EMERALD)
                .define('I', ModItems.INSPIRATION.get())
                .unlockedBy(getHasName(ModItems.INSPIRATION.get()), has(ModItems.INSPIRATION.get()))
                .save(recipeOutput, "emerald_ore_from_inspiration");


        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.EXPLODING_CANDY, 3)
                .requires(ModItems.CESIUM_SALT.get())
                .requires(ModItems.INSPIRATION.get())
                .requires(Items.SUGAR)
                .requires(Items.SUGAR)
                .unlockedBy(getHasName(ModItems.INSPIRATION.get()), has(ModItems.INSPIRATION.get()))
                .save(recipeOutput);
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

        // 还原粉右键转换
        RightClickBlockRecipeBuilder.recipe(ModBlocks.CESIUM_SALT_BLOCK.get(), ModBlocks.CESIUM_BLOCK.get(),
                        new ItemStack(ModItems.REDUCTION_POWDER.get()))
                .unlockedBy("has_reduction_powder", has(ModItems.REDUCTION_POWDER.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "cesium_block_from_salt"));

        RightClickBlockRecipeBuilder.recipe(Blocks.EMERALD_ORE, ModBlocks.JADE_STEEL_ORE.get(),
                        new ItemStack(ModItems.REDUCTION_POWDER.get()))
                .unlockedBy("has_reduction_powder", has(ModItems.REDUCTION_POWDER.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "jade_steel_ore_from_emerald"));

        RightClickBlockRecipeBuilder.recipe(Blocks.DEEPSLATE_EMERALD_ORE, ModBlocks.DEEPSLATE_JADE_STEEL_ORE.get(),
                        new ItemStack(ModItems.REDUCTION_POWDER.get()))
                .unlockedBy("has_reduction_powder", has(ModItems.REDUCTION_POWDER.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "deepslate_jade_steel_ore_from_emerald"));

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
    }
}