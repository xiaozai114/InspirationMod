package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.injection.recipe.InjectionRecipe;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import com.starcrosser.inspiration.machine.recipe.AssemblingRecipe;
import com.starcrosser.inspiration.machine.recipe.FluidRequirement;
import com.starcrosser.inspiration.machine.recipe.FusionRecipe;
import com.starcrosser.inspiration.machine.recipe.ReshapingRecipe;
import com.starcrosser.inspiration.machine.recipe.TwistingRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ModRecipesProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        buildMachineRecipes(recipeOutput);
        buildReshapingRecipes(recipeOutput);
        buildTwistingRecipes(recipeOutput);
        buildAssemblingRecipes(recipeOutput);
        buildInjectionRecipes(recipeOutput);
        buildFranciumRecipes(recipeOutput);

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
        // 思维流体桶：水桶 + 末影之眼 + 灵感。
        // 不传文件名，默认 id 即物品注册名 inspiration:mind_fluid_bucket
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MIND_FLUID_BUCKET.get())
                .requires(Items.WATER_BUCKET)
                .requires(Items.ENDER_EYE)
                .requires(ModItems.INSPIRATION.get())
                .unlockedBy(getHasName(ModItems.INSPIRATION.get()), has(ModItems.INSPIRATION.get()))
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

        // 4 金锭 + 1 紫水晶 -> 4 灵感
        // 无序：这套材料的形状本身就是「一堆金 + 一颗紫水晶」，摆法不该有意义
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INSPIRATION.get(), 4)
                .requires(Items.GOLD_INGOT, 4)
                .requires(Items.AMETHYST_SHARD)
                .unlockedBy(getHasName(Items.AMETHYST_SHARD), has(Items.AMETHYST_SHARD))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "inspiration_from_gold_and_amethyst"));

        // 指导书：书 + 灵感（书的装帧由灵感驱动，所以是无序的）
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GUIDE_BOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.INSPIRATION.get())
                .unlockedBy(getHasName(ModItems.INSPIRATION.get()), has(ModItems.INSPIRATION.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "guide_book"));

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

        // 灵感之铲（默认 ID "exp_shovel"）：竖排三格，由上到下是翡翠钢锭 / 灵感 / 粘液球。
        // 别家工具都是「材料 + 灵感 + 木棍」，铲子按用户设定改成粘液球收底。
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.EXP_SHOVEL.get())
                .pattern("J")
                .pattern("I")
                .pattern("S")
                .define('J', ModItems.JADE_STEEL_INGOT.get())
                .define('I', ModItems.INSPIRATION.get())
                .define('S', Items.SLIME_BALL)
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

        // ========== 翡翠钢升级：模板 + 灵感工具 + 翡翠钢锭 ==========
        buildJadeSteelUpgrades(recipeOutput);
        buildEnergizedJadeSteelUpgrades(recipeOutput);

        // 还原粉右键转换
        RightClickBlockRecipeBuilder.recipe(ModBlocks.CESIUM_SALT_BLOCK.get(), ModBlocks.CESIUM_BLOCK.get(),
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
    }

    /* ============================ 翡翠钢升级配方 ============================ */

    /**
     * 四件灵感工具的翡翠钢升级。
     * <p>
     * 与下界合金升级同构：模板 + 底材工具 + 材料锭 → 升级后的工具。
     */
    private void buildJadeSteelUpgrades(RecipeOutput recipeOutput) {
        smithingUpgrade(recipeOutput, "jade_steel_sword", ModItems.EXP_SWORD.get(),
                ModItems.JADE_STEEL_SWORD.get(), RecipeCategory.COMBAT);
        smithingUpgrade(recipeOutput, "jade_steel_axe", ModItems.STICKY_AXE.get(),
                ModItems.JADE_STEEL_AXE.get(), RecipeCategory.TOOLS);
        smithingUpgrade(recipeOutput, "jade_steel_pickaxe", ModItems.EXP_PICKAXE.get(),
                ModItems.JADE_STEEL_PICKAXE.get(), RecipeCategory.TOOLS);
        smithingUpgrade(recipeOutput, "jade_steel_hoe", ModItems.EXP_HOE.get(),
                ModItems.JADE_STEEL_HOE.get(), RecipeCategory.TOOLS);
        smithingUpgrade(recipeOutput, "jade_steel_shovel", ModItems.EXP_SHOVEL.get(),
                ModItems.JADE_STEEL_SHOVEL.get(), RecipeCategory.TOOLS);
    }

    /**
     * 注能翡翠钢升级：翡翠钢工具 + 注能翡翠钢升级模板 + 注能翡翠钢。
     * <p>
     * 与翡翠钢档同构，只是模板与材料都换成注能版本；模板本身要靠二级注入配方得到。
     */
    private void buildEnergizedJadeSteelUpgrades(RecipeOutput recipeOutput) {
        smithingUpgrade(recipeOutput, ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get(),
                ModItems.ENERGIZED_JADE_STEEL.get(), "energized_jade_steel_sword",
                ModItems.JADE_STEEL_SWORD.get(), ModItems.ENERGIZED_JADE_STEEL_SWORD.get(),
                RecipeCategory.COMBAT);
        smithingUpgrade(recipeOutput, ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get(),
                ModItems.ENERGIZED_JADE_STEEL.get(), "energized_jade_steel_axe",
                ModItems.JADE_STEEL_AXE.get(), ModItems.ENERGIZED_JADE_STEEL_AXE.get(),
                RecipeCategory.TOOLS);
        smithingUpgrade(recipeOutput, ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get(),
                ModItems.ENERGIZED_JADE_STEEL.get(), "energized_jade_steel_pickaxe",
                ModItems.JADE_STEEL_PICKAXE.get(), ModItems.ENERGIZED_JADE_STEEL_PICKAXE.get(),
                RecipeCategory.TOOLS);
        smithingUpgrade(recipeOutput, ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get(),
                ModItems.ENERGIZED_JADE_STEEL.get(), "energized_jade_steel_hoe",
                ModItems.JADE_STEEL_HOE.get(), ModItems.ENERGIZED_JADE_STEEL_HOE.get(),
                RecipeCategory.TOOLS);
        smithingUpgrade(recipeOutput, ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get(),
                ModItems.ENERGIZED_JADE_STEEL.get(), "energized_jade_steel_shovel",
                ModItems.JADE_STEEL_SHOVEL.get(), ModItems.ENERGIZED_JADE_STEEL_SHOVEL.get(),
                RecipeCategory.TOOLS);
    }

    /** 翡翠钢档的升级配方：模板与材料固定，进度条件挂在原工具上。 */
    private void smithingUpgrade(RecipeOutput recipeOutput, String path, Item base, Item result,
                                 RecipeCategory category) {
        smithingUpgrade(recipeOutput, ModItems.JADE_STEEL_UPGRADE_TEMPLATE.get(),
                ModItems.JADE_STEEL_INGOT.get(), path, base, result, category);
    }

    /** 写出一条锻造升级配方，进度条件挂在原工具上。 */
    private void smithingUpgrade(RecipeOutput recipeOutput, Item template, Item material, String path,
                                 Item base, Item result, RecipeCategory category) {
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(template),
                        Ingredient.of(base),
                        Ingredient.of(material),
                        category, result)
                .unlocks("has_" + BuiltInRegistries.ITEM.getKey(base).getPath(), has(base))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, path));
    }

    /** 机器本体与其合成材料 */
    private void buildMachineRecipes(RecipeOutput recipeOutput) {        // 强化核心：5 铯盐（四角 + 中心）+ 4 黑曜石（四边）
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.REINFORCED_CORE.get())
                .pattern("COC")
                .pattern("OCO")
                .pattern("COC")
                .define('C', ModItems.CESIUM_SALT.get())
                .define('O', Items.OBSIDIAN)
                .unlockedBy(getHasName(ModItems.CESIUM_SALT.get()), has(ModItems.CESIUM_SALT.get()))
                .save(recipeOutput);

        // 强能核心：4 铯锭（四角）+ 4 玄武岩合金（四边）+ 1 强化核心（中心）
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.POWERFUL_CORE.get())
                .pattern("CAC")
                .pattern("APA")
                .pattern("CAC")
                .define('C', ModItems.CESIUM_INGOT.get())
                .define('A', ModItems.BASALT_ALLOY.get())
                .define('P', ModItems.REINFORCED_CORE.get())
                .unlockedBy(getHasName(ModItems.REINFORCED_CORE.get()), has(ModItems.REINFORCED_CORE.get()))
                .save(recipeOutput);

        // ===== 机器本体：全部走 machine(...) 的统一形状（见方法注释） =====

        // 物质融合器：主要材料铁锭 + 次要材料红石锭 + 强化核心
        machine(recipeOutput, ModMachines.MATTER_FUSION_MACHINE.getBlock(),
                Items.IRON_INGOT, ModItems.REDSTONE_INGOT.get(), ModItems.REINFORCED_CORE.get(),
                null, ModItems.REINFORCED_CORE.get());

        // 强能物质融合机：物质融合器升级，次要材料换成红石锭块
        machine(recipeOutput, ModMachines.ENERGIZED_MATTER_FUSION_MACHINE.getBlock(),
                ModItems.ENERGIZED_JADE_STEEL.get(), ModBlocks.REDSTONE_INGOT_BLOCK.get(),
                ModItems.POWERFUL_CORE.get(), ModMachines.MATTER_FUSION_MACHINE.getBlock(),
                ModItems.ENERGIZED_JADE_STEEL.get());

        // 地核钻探者：翡翠钢 + 思维矩阵锭
        machine(recipeOutput, ModMachines.CORE_DRILLER.getBlock(),
                ModItems.JADE_STEEL_INGOT.get(), ModItems.MIND_MATRIX_INGOT.get(),
                ModItems.POWERFUL_CORE.get(), null, ModItems.MIND_MATRIX_INGOT.get());

        // 物质重塑机：翡翠钢 + 红石锭块
        machine(recipeOutput, ModMachines.MATTER_RESHAPER.getBlock(),
                ModItems.JADE_STEEL_INGOT.get(), ModBlocks.REDSTONE_INGOT_BLOCK.get(),
                ModItems.POWERFUL_CORE.get(), null, ModItems.POWERFUL_CORE.get());

        // 矿物扭曲机：与物质重塑机同构，次要材料换成青金石锭块
        machine(recipeOutput, ModMachines.MINERAL_TWISTER.getBlock(),
                ModItems.JADE_STEEL_INGOT.get(), ModBlocks.LAPIS_INGOT_BLOCK.get(),
                ModItems.POWERFUL_CORE.get(), null, ModItems.POWERFUL_CORE.get());

        // 灵感装配台：翡翠钢 + 等离子锭
        machine(recipeOutput, ModMachines.INSPIRATION_ASSEMBLER.getBlock(),
                ModItems.JADE_STEEL_INGOT.get(), ModItems.PLASMA_INGOT.get(),
                ModItems.POWERFUL_CORE.get(), null, ModItems.PLASMA_INGOT.get());

        // 高能矿物扭曲机：矿物扭曲机 + 高能核心 + 注能青金石锭
        machine(recipeOutput, ModMachines.HIGH_ENERGY_MINERAL_TWISTER.getBlock(),
                ModItems.EPHEMERAL_ALLOY.get(), ModItems.ENERGIZED_LAPIS_INGOT.get(),
                ModItems.HIGH_ENERGY_CORE.get(), ModMachines.MINERAL_TWISTER.getBlock(),
                ModItems.HIGH_ENERGY_CORE.get());

        // 高能灵感装配台：灵感装配台 + 高能核心 + 注能等离子锭
        machine(recipeOutput, ModMachines.HIGH_ENERGY_INSPIRATION_ASSEMBLER.getBlock(),
                ModItems.EPHEMERAL_ALLOY.get(), ModItems.ENERGIZED_PLASMA_INGOT.get(),
                ModItems.HIGH_ENERGY_CORE.get(), ModMachines.INSPIRATION_ASSEMBLER.getBlock(),
                ModItems.HIGH_ENERGY_CORE.get());

        // 碱性发电机：翡翠钢 + 红石锭 + 发电核心（发电类机器，不跑配方）
        machine(recipeOutput, ModMachines.ALKALINE_REACTOR.getBlock(),
                ModItems.JADE_STEEL_INGOT.get(), ModItems.REDSTONE_INGOT.get(),
                ModItems.GENERATION_CORE.get(), null, ModItems.GENERATION_CORE.get());

        // RTG反应堆：无恒合金 + 注能红石锭 + 反应核心
        machine(recipeOutput, ModMachines.RTG_GENERATOR.getBlock(),
                ModItems.EPHEMERAL_ALLOY.get(), ModItems.ENERGIZED_REDSTONE_INGOT.get(),
                ModItems.REACTION_CORE.get(), null, ModItems.REACTION_CORE.get());

        // ==================== 结构模拟机（多方块） ====================

        // 结构模拟机：无恒合金 + 幽匿合金锭 + 高能核心，形状与其他机器一致
        machine(recipeOutput, ModMultiblocks.STRUCTURE_SIMULATOR.getBlock(),
                ModItems.EPHEMERAL_ALLOY.get(), ModItems.SCULK_ALLOY.get(),
                ModItems.HIGH_ENERGY_CORE.get(), null, ModItems.HIGH_ENERGY_CORE.get());

        // 高能机器外壳：1 份无恒物质把 8 个铁块染成淡紫，一次出 8 个
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.HIGH_ENERGY_MACHINE_CASING.get(), 8)
                .pattern("III")
                .pattern("IXI")
                .pattern("III")
                .define('I', Blocks.IRON_BLOCK)
                .define('X', ModItems.EPHEMERAL_MATERIAL.get())
                .unlockedBy(getHasName(ModItems.EPHEMERAL_MATERIAL.get()),
                        has(ModItems.EPHEMERAL_MATERIAL.get()))
                .save(recipeOutput);

        // 五种仓室：都是「外壳 + 一种材料」的两格竖排，材料在上；
        // 输出仓反过来放（外壳在上、箱子在下），两个箱子配方才不会撞车
        ItemLike casing = ModBlocks.HIGH_ENERGY_MACHINE_CASING.get();
        hatch(recipeOutput, ModMultiblocks.INPUT_HATCH.getBlock(),
                Blocks.CHEST, casing, casing);
        hatch(recipeOutput, ModMultiblocks.OUTPUT_HATCH.getBlock(),
                casing, Blocks.CHEST, casing);
        hatch(recipeOutput, ModMultiblocks.ENERGY_PORT.getBlock(),
                ModItems.REDSTONE_INGOT.get(), casing, casing);
        hatch(recipeOutput, ModMultiblocks.INSPIRATION_HATCH.getBlock(),
                ModItems.INSPIRATION.get(), casing, casing);
        hatch(recipeOutput, ModMultiblocks.DATA_HATCH.getBlock(),
                ModItems.BLANK_DATA.get(), casing, casing);

        // 高能物质重塑机：物质重塑机 + 高能核心 + 注能红石锭（除高能倍率外无额外效果）
        machine(recipeOutput, ModMachines.HIGH_ENERGY_MATTER_RESHAPER.getBlock(),
                ModItems.EPHEMERAL_ALLOY.get(), ModItems.ENERGIZED_REDSTONE_INGOT.get(),
                ModItems.HIGH_ENERGY_CORE.get(), ModMachines.MATTER_RESHAPER.getBlock(),
                ModItems.HIGH_ENERGY_CORE.get());

        // 物质束缚器：8 等离子锭 + 1 不稳核心
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModInjection.MATTER_BINDER.getBlock())
                .pattern("PPP")
                .pattern("PCP")
                .pattern("PPP")
                .define('P', ModItems.PLASMA_INGOT.get())
                .define('C', ModItems.UNSTABLE_CORE.get())
                .unlockedBy(getHasName(ModItems.PLASMA_INGOT.get()), has(ModItems.PLASMA_INGOT.get()))
                .save(recipeOutput);

        // 注入枪：发射器外圈围 4 个翡翠钢锭
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModInjection.INJECTION_GUN.getBlock())
                .pattern(" J ")
                .pattern("JDJ")
                .pattern(" J ")
                .define('J', ModItems.JADE_STEEL_INGOT.get())
                .define('D', Items.DISPENSER)
                .unlockedBy(getHasName(ModItems.JADE_STEEL_INGOT.get()), has(ModItems.JADE_STEEL_INGOT.get()))
                .save(recipeOutput);

        // 绑定器：注入枪与束缚器之外的第三件工具，用翡翠钢 + 末影之眼
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BINDING_WAND.get())
                .pattern("  J")
                .pattern(" E ")
                .pattern("J  ")
                .define('J', ModItems.JADE_STEEL_INGOT.get())
                .define('E', Items.ENDER_EYE)
                .unlockedBy(getHasName(ModItems.JADE_STEEL_INGOT.get()), has(ModItems.JADE_STEEL_INGOT.get()))
                .save(recipeOutput);

        // 玄武岩合金块（1 级底座）
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BASALT_ALLOY_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.BASALT_ALLOY.get())
                .unlockedBy(getHasName(ModItems.BASALT_ALLOY.get()), has(ModItems.BASALT_ALLOY.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BASALT_ALLOY.get(), 9)
                .requires(ModBlocks.BASALT_ALLOY_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.BASALT_ALLOY_BLOCK.get()), has(ModBlocks.BASALT_ALLOY_BLOCK.get()))
                .save(recipeOutput, "basalt_alloy_from_block");

        // 幽匿合金块（2 级底座）
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SCULK_ALLOY_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.SCULK_ALLOY.get())
                .unlockedBy(getHasName(ModItems.SCULK_ALLOY.get()), has(ModItems.SCULK_ALLOY.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SCULK_ALLOY.get(), 9)
                .requires(ModBlocks.SCULK_ALLOY_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.SCULK_ALLOY_BLOCK.get()), has(ModBlocks.SCULK_ALLOY_BLOCK.get()))
                .save(recipeOutput,
                        ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "sculk_alloy_from_block"));

        // 绿宝石 + 铁锭 -> 翡翠钢锭
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "jade_steel_ingot_from_fusion"),
                new FusionRecipe(Ingredient.of(Items.EMERALD), Ingredient.of(Items.IRON_INGOT),
                        new ItemStack(ModItems.JADE_STEEL_INGOT.get(), 2), 200, 40),
                null);

        // 玄武岩 + 远古残骸 -> 玄武岩合金
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "basalt_alloy_from_fusion"),
                new FusionRecipe(Ingredient.of(Items.BASALT), Ingredient.of(Items.ANCIENT_DEBRIS),
                        new ItemStack(ModItems.BASALT_ALLOY.get(), 2), 400, 120),
                null);

        // 思维晶体 + 紫水晶 + 下界石英 -> 思维矩阵锭（三材料配方，只有强能机型能做）
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "mind_matrix_ingot_from_fusion"),
                new FusionRecipe(Ingredient.of(ModItems.MIND_CRYSTAL.get()),
                        Ingredient.of(Items.AMETHYST_SHARD),
                        new ItemStack(ModItems.MIND_MATRIX_INGOT.get()),
                        600, 300,
                        Ingredient.of(Items.QUARTZ)),
                null);

        // 4 黑曜石 + 末影珍珠 + 4 紫珀块 -> 末地合金
        // 三种材料各自声明消耗数量（4/1/4），同样只有三槽的强能机型能凑齐
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "end_alloy_from_fusion"),
                new FusionRecipe(Ingredient.of(Items.OBSIDIAN), 4,
                        Ingredient.of(Items.ENDER_PEARL), 1,
                        Ingredient.of(Items.PURPUR_BLOCK), 4,
                        new ItemStack(ModItems.END_ALLOY.get()),
                        800, 600),
                null);

        // 回响碎片 + 强化深板岩 -> 幽匿合金 ×2
        // 强化深板岩只有地核钻探者能挖，所以这是中期之后才做得出来的材料
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "sculk_alloy_from_fusion"),
                new FusionRecipe(Ingredient.of(Items.ECHO_SHARD),
                        Ingredient.of(Items.REINFORCED_DEEPSLATE),
                        new ItemStack(ModItems.SCULK_ALLOY.get(), 2), 400, 200),
                null);
    }

    /**
     * 机器本体的统一合成形状。之后新增机器一律按这个形状来做，差别只在三种材料与核心：
     * <pre>
     *   M S M
     *   S C S
     *   M X M
     * </pre>
     * 其中 M 为主要材料（四角 + 底中，共 5 份）、S 为次要材料（上中 + 中行两侧，共 3 份）、
     * C 为核心（正中）、X 为下级机器；基础机型没有下级机器，X 位置填主要材料（于是共 5 份 M）。
     * <p>
     * 升级机型因为 X 占了底中，主要材料只有四角 4 份 —— 这是刻意的：上位机型本身就要吃掉一台下级机器。
     *
     * @param lowerTier 下级机器，没有则传 {@code null}
     * @param unlock    进度解锁条件挂在哪个物品上
     */
    private void machine(RecipeOutput recipeOutput, ItemLike result, ItemLike main, ItemLike secondary,
                         ItemLike core, @Nullable ItemLike lowerTier, ItemLike unlock) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
                .pattern("MSM")
                .pattern("SCS")
                .pattern("MXM")
                .define('M', main)
                .define('S', secondary)
                .define('C', core)
                .define('X', lowerTier == null ? main : lowerTier)
                .unlockedBy(getHasName(unlock), has(unlock))
                .save(recipeOutput);
    }

    /**
     * 仓室配方：两格竖排，{@code top} 在上、{@code bottom} 在下。
     * <p>
     * 五种仓室都是「高能机器外壳 + 一样材料」，靠上下顺序区分（否则两个箱子配方会重合）。
     */
    private void hatch(RecipeOutput recipeOutput, ItemLike result, ItemLike top, ItemLike bottom,
                       ItemLike unlock) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
                .pattern("T")
                .pattern("B")
                .define('T', top)
                .define('B', bottom)
                .unlockedBy(getHasName(unlock), has(unlock))
                .save(recipeOutput);
    }

    /* ============================ 物质重塑配方 ============================ */

    /** 回炉配方：装备重塑回材料的默认耗时与能耗（比重塑核心类配方轻得多）。 */
    private static final int RECYCLE_DURATION = 160;
    private static final int RECYCLE_ENERGY_PER_TICK = 60;

    /** 简单的物质互换配方：只做压缩 / 形态转换，耗时与能耗都更低。 */
    private static final int SIMPLE_DURATION = 200;
    private static final int SIMPLE_ENERGY_PER_TICK = 80;

    /** 钻石 -> 等离子锭：与核心升阶同一档的高阶转化。 */
    private static final int PLASMA_DURATION = 600;
    private static final int PLASMA_ENERGY_PER_TICK = 400;

    /** 强能核心 -> 发电核心：与等离子锭同档。 */
    private static final int GENERATION_CORE_DURATION = PLASMA_DURATION;
    private static final int GENERATION_CORE_ENERGY_PER_TICK = PLASMA_ENERGY_PER_TICK;

    /** 高能核心 -> 反应核心：比发电核心再高一档（产物用在无恒合金级的机器上）。 */
    private static final int REACTION_CORE_DURATION = 800;
    private static final int REACTION_CORE_ENERGY_PER_TICK = 600;

    /** 成套盔甲的合成消耗：头盔 / 胸甲 / 护腿 / 靴子。 */
    private static final int[] ARMOR_COST = {5, 8, 7, 4};

    /** 成套工具的合成消耗：剑 / 镐 / 斧 / 锹 / 锄。 */
    private static final int[] TOOL_COST = {2, 3, 3, 1, 2};

    /**
     * 一种材料的整套可回炉装备。
     *
     * @param name     用于生成配方 ID 的前缀
     * @param material 回炉后得到的材料
     * @param armor    四件套，顺序与 {@link #ARMOR_COST} 对应；没有则传空数组
     * @param tools    五种工具，顺序与 {@link #TOOL_COST} 对应；没有则传空数组
     */
    private record EquipmentSet(String name, Item material, Item[] armor, Item[] tools) {
    }

    /** 物质重塑机的专属配方 */
    private void buildReshapingRecipes(RecipeOutput recipeOutput) {
        // 任意盔甲纹饰模板 -> 翡翠钢升级模板
        reshaping(recipeOutput, "reshaping/jade_steel_upgrade_template_from_trim_template",
                Ingredient.of(ItemTags.TRIM_TEMPLATES), null,
                new ItemStack(ModItems.JADE_STEEL_UPGRADE_TEMPLATE.get()), 400, 200);

        // 煤炭 -> 2 褐煤
        reshaping(recipeOutput, "reshaping/lignite_from_coal", 1,
                Ingredient.of(Items.COAL), null,
                new ItemStack(ModItems.LIGNITE.get(), 2), SIMPLE_DURATION, SIMPLE_ENERGY_PER_TICK);

        // 3 红石 -> 红石锭
        reshaping(recipeOutput, "reshaping/redstone_ingot_from_redstone", 3,
                Ingredient.of(Items.REDSTONE), null,
                new ItemStack(ModItems.REDSTONE_INGOT.get()), SIMPLE_DURATION, SIMPLE_ENERGY_PER_TICK);

        // 3 青金石 -> 青金石锭
        reshaping(recipeOutput, "reshaping/lapis_ingot_from_lapis", 3,
                Ingredient.of(Items.LAPIS_LAZULI), null,
                new ItemStack(ModItems.LAPIS_INGOT.get()), SIMPLE_DURATION, SIMPLE_ENERGY_PER_TICK);

        // 钻石 -> 等离子锭：重塑机最高档的单材料转化
        reshaping(recipeOutput, "reshaping/plasma_ingot_from_diamond", 1,
                Ingredient.of(Items.DIAMOND), null,
                new ItemStack(ModItems.PLASMA_INGOT.get()), PLASMA_DURATION, PLASMA_ENERGY_PER_TICK);

        // 玄武岩合金 -> 玄武岩合金钻头：地核钻探者的耗材，与等离子锭同档
        reshaping(recipeOutput, "reshaping/basalt_alloy_drill_bit", 1,
                Ingredient.of(ModItems.BASALT_ALLOY.get()), null,
                new ItemStack(ModItems.BASALT_ALLOY_DRILL_BIT.get()),
                PLASMA_DURATION, PLASMA_ENERGY_PER_TICK);

        // 玄武岩 -> 玄武岩基底：注入配方的底材
        reshaping(recipeOutput, "reshaping/basalt_base_from_basalt", 1,
                Ingredient.of(Items.BASALT), null,
                new ItemStack(ModItems.BASALT_BASE.get()), SIMPLE_DURATION, SIMPLE_ENERGY_PER_TICK);

        // 强能核心 -> 发电核心：炉心由暖金转绿，碱性发电机的核心部件
        reshaping(recipeOutput, "reshaping/generation_core_from_powerful_core", 1,
                Ingredient.of(ModItems.POWERFUL_CORE.get()), null,
                new ItemStack(ModItems.GENERATION_CORE.get()),
                GENERATION_CORE_DURATION, GENERATION_CORE_ENERGY_PER_TICK);

        // 高能核心 -> 反应核心：炉心转红，RTG反应堆的核心部件
        reshaping(recipeOutput, "reshaping/reaction_core_from_high_energy_core", 1,
                Ingredient.of(ModItems.HIGH_ENERGY_CORE.get()), null,
                new ItemStack(ModItems.REACTION_CORE.get()),
                REACTION_CORE_DURATION, REACTION_CORE_ENERGY_PER_TICK);

        buildEquipmentRecycling(recipeOutput);
    }

    /** 盔甲与工具回炉：把成套装备重塑回它原来的材料。 */
    private void buildEquipmentRecycling(RecipeOutput recipeOutput) {
        List<EquipmentSet> sets = List.of(
                new EquipmentSet("leather", Items.LEATHER,
                        new Item[]{Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE,
                                Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS},
                        new Item[0]),
                new EquipmentSet("iron", Items.IRON_INGOT,
                        new Item[]{Items.IRON_HELMET, Items.IRON_CHESTPLATE,
                                Items.IRON_LEGGINGS, Items.IRON_BOOTS},
                        new Item[]{Items.IRON_SWORD, Items.IRON_PICKAXE, Items.IRON_AXE,
                                Items.IRON_SHOVEL, Items.IRON_HOE}),
                new EquipmentSet("gold", Items.GOLD_INGOT,
                        new Item[]{Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE,
                                Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS},
                        new Item[]{Items.GOLDEN_SWORD, Items.GOLDEN_PICKAXE, Items.GOLDEN_AXE,
                                Items.GOLDEN_SHOVEL, Items.GOLDEN_HOE}),
                new EquipmentSet("diamond", Items.DIAMOND,
                        new Item[]{Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE,
                                Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS},
                        new Item[]{Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE,
                                Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE}),
                // 下界合金装备由钻石装备锻造而来，这里只返还下界合金锭
                new EquipmentSet("netherite", Items.NETHERITE_INGOT,
                        new Item[]{Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE,
                                Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS},
                        new Item[]{Items.NETHERITE_SWORD, Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE,
                                Items.NETHERITE_SHOVEL, Items.NETHERITE_HOE}));

        for (EquipmentSet set : sets) {
            for (int i = 0; i < set.armor().length; i++) {
                recycle(recipeOutput, set, set.armor()[i], ARMOR_COST[i]);
            }
            for (int i = 0; i < set.tools().length; i++) {
                recycle(recipeOutput, set, set.tools()[i], TOOL_COST[i]);
            }
        }

        // 海龟壳不在成套系列里，单独处理
        recycle(recipeOutput, new EquipmentSet("turtle", Items.TURTLE_SCUTE, new Item[0], new Item[0]),
                Items.TURTLE_HELMET, 5);
    }

    private void recycle(RecipeOutput recipeOutput, EquipmentSet set, Item equipment, int count) {
        String path = BuiltInRegistries.ITEM.getKey(equipment).getPath();
        reshaping(recipeOutput, "reshaping/recycling/" + path,
                Ingredient.of(equipment), null,
                new ItemStack(set.material(), count), RECYCLE_DURATION, RECYCLE_ENERGY_PER_TICK);
    }

    /**
     * 写出单条重塑配方。
     *
     * @param secondInput 辅助材料，不需要时传 {@code null}
     */
    private void reshaping(RecipeOutput recipeOutput, String path, Ingredient input,
                           @Nullable Ingredient secondInput, ItemStack result,
                           int duration, int energyPerTick) {
        reshaping(recipeOutput, path, ReshapingRecipe.DEFAULT_INPUT_COUNT, input, secondInput, result,
                duration, energyPerTick);
    }

    /**
     * 写出单条重塑配方。
     *
     * @param inputCount  一次消耗的主材料数量
     * @param secondInput 辅助材料，不需要时传 {@code null}
     */
    private void reshaping(RecipeOutput recipeOutput, String path, int inputCount, Ingredient input,
                           @Nullable Ingredient secondInput, ItemStack result,
                           int duration, int energyPerTick) {
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, path),
                new ReshapingRecipe(input, Optional.ofNullable(secondInput), result, inputCount,
                        duration, energyPerTick),
                null);
    }

    /* ============================ 矿物扭曲配方 ============================ */

    /** 扭曲配方的档位：矿石越稀有，耗时与能耗越高。 */
    private record TwistingTier(int duration, int energyPerTick) {
    }

    private static final TwistingTier COMMON_ORE = new TwistingTier(400, 300);
    private static final TwistingTier RARE_ORE = new TwistingTier(600, 400);

    /**
     * 一种矿石的扭曲配方。
     *
     * @param name      用于生成配方 ID 的后缀
     * @param result    扭出的粗矿
     * @param baseCount 基准产量，实际产量再乘以等效时运的倍率
     * @param ores      该矿族的全部矿石方块（含深板岩变种）
     */
    private record OreSet(String name, Item result, int baseCount, TwistingTier tier, List<Item> ores) {
    }

    /**
     * 矿物扭曲机配方：矿石 -> 对应的粗矿。
     * <p>
     * 产出数量由方块实体按等效时运掷出，配方里只声明基准产量与等效时运等级。
     */
    private void buildTwistingRecipes(RecipeOutput recipeOutput) {
        List<OreSet> sets = List.of(
                new OreSet("coal", Items.COAL, 1, COMMON_ORE,
                        List.of(Items.COAL_ORE, Items.DEEPSLATE_COAL_ORE)),
                new OreSet("iron", Items.RAW_IRON, 1, COMMON_ORE,
                        List.of(Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE)),
                new OreSet("copper", Items.RAW_COPPER, 1, COMMON_ORE,
                        List.of(Items.COPPER_ORE, Items.DEEPSLATE_COPPER_ORE)),
                new OreSet("gold", Items.RAW_GOLD, 1, RARE_ORE,
                        List.of(Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE)),
                // 红石与青金石在原版就是成簇掉落，基准产量取原版下限
                new OreSet("redstone", Items.REDSTONE, 4, COMMON_ORE,
                        List.of(Items.REDSTONE_ORE, Items.DEEPSLATE_REDSTONE_ORE)),
                new OreSet("lapis", Items.LAPIS_LAZULI, 4, COMMON_ORE,
                        List.of(Items.LAPIS_ORE, Items.DEEPSLATE_LAPIS_ORE)),
                new OreSet("diamond", Items.DIAMOND, 1, RARE_ORE,
                        List.of(Items.DIAMOND_ORE, Items.DEEPSLATE_DIAMOND_ORE)),
                new OreSet("emerald", Items.EMERALD, 1, RARE_ORE,
                        List.of(Items.EMERALD_ORE, Items.DEEPSLATE_EMERALD_ORE)),
                new OreSet("nether_gold", Items.GOLD_NUGGET, 2, RARE_ORE,
                        List.of(Items.NETHER_GOLD_ORE)),
                new OreSet("nether_quartz", Items.QUARTZ, 1, COMMON_ORE,
                        List.of(Items.NETHER_QUARTZ_ORE)),
                new OreSet("cesium_salt", ModItems.RAW_CESIUM_SALT.get(), 1, COMMON_ORE,
                        List.of(ModBlocks.CESIUM_SALT_ORE.get().asItem())),
                // 钫盐矿：地狱上层稀有矿，产量基准同铯盐
                new OreSet("francium_salt", ModItems.RAW_FRANCIUM_SALT.get(), 1, COMMON_ORE,
                        List.of(ModBlocks.FRANCIUM_SALT_ORE.get().asItem())));

        for (OreSet set : sets) {
            recipeOutput.accept(
                    ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "twisting/" + set.name()),
                    new TwistingRecipe(Ingredient.of(set.ores().toArray(new Item[0])),
                            new ItemStack(set.result(), set.baseCount()),
                            TwistingRecipe.DEFAULT_FORTUNE_LEVEL,
                            set.tier().duration(), set.tier().energyPerTick()),
                    null);
        }
    }

    /* ============================ 灵感装配台配方 ============================ */

    /** 装配插件的耗时与能耗，用装配配方的默认值。 */
    private static final int ASSEMBLE_DURATION = AssemblingRecipe.DEFAULT_DURATION;
    private static final int ASSEMBLE_ENERGY_PER_TICK = AssemblingRecipe.DEFAULT_ENERGY_PER_TICK;

    /**
     * 机器插件配方：等离子锭 + 一种材料 -> 对应插件。
     * <p>
     * 三种插件共用同一个模板，将来加插件只要再列一条即可。
     */
    private void buildAssemblingRecipes(RecipeOutput recipeOutput) {
        assembling(recipeOutput, "assembling/capacitor_plugin",
                List.of(Ingredient.of(ModItems.PLASMA_INGOT.get()), Ingredient.of(ModItems.REDSTONE_INGOT.get())),
                new ItemStack(ModItems.CAPACITOR_PLUGIN.get()), ASSEMBLE_DURATION, ASSEMBLE_ENERGY_PER_TICK);

        assembling(recipeOutput, "assembling/inspiration_plugin",
                List.of(Ingredient.of(ModItems.PLASMA_INGOT.get()), Ingredient.of(ModItems.INSPIRATION.get())),
                new ItemStack(ModItems.INSPIRATION_PLUGIN.get()), ASSEMBLE_DURATION, ASSEMBLE_ENERGY_PER_TICK);

        assembling(recipeOutput, "assembling/speed_plugin",
                List.of(Ingredient.of(ModItems.PLASMA_INGOT.get()), Ingredient.of(Items.SUGAR)),
                new ItemStack(ModItems.SPEED_PLUGIN.get()), ASSEMBLE_DURATION, ASSEMBLE_ENERGY_PER_TICK);

        assembling(recipeOutput, "assembling/capacitor_downgrade_plugin",
                List.of(Ingredient.of(ModItems.PLASMA_INGOT.get()), Ingredient.of(Items.IRON_INGOT)),
                new ItemStack(ModItems.CAPACITOR_DOWNGRADE_PLUGIN.get()),
                ASSEMBLE_DURATION, ASSEMBLE_ENERGY_PER_TICK);

        // 高能核心：强能核心 + 钫盐 + 强化深板岩 —— 三样材料缺一不可
        assembling(recipeOutput, "assembling/high_energy_core",
                List.of(Ingredient.of(ModItems.POWERFUL_CORE.get()),
                        Ingredient.of(ModItems.FRANCIUM_SALT.get()),
                        Ingredient.of(Items.REINFORCED_DEEPSLATE)),
                new ItemStack(ModItems.HIGH_ENERGY_CORE.get()),
                ASSEMBLE_DURATION, ASSEMBLE_ENERGY_PER_TICK);

        // 空白数据：五种材料，四个输入槽的普通装配台放不下，只有高能机型做得了
        assembling(recipeOutput, "assembling/blank_data",
                List.of(Ingredient.of(Items.QUARTZ),
                        Ingredient.of(ModItems.PLASMA_INGOT.get()),
                        Ingredient.of(ModItems.MIND_MATRIX_INGOT.get()),
                        Ingredient.of(Items.GLASS),
                        Ingredient.of(ModItems.EPHEMERAL_MATERIAL.get())),
                new ItemStack(ModItems.BLANK_DATA.get()),
                ASSEMBLE_DURATION, ASSEMBLE_ENERGY_PER_TICK);

        // 沉船数据：空白数据 + 64 块木板 + 4 桶水 —— 第一条带流体的装配配方
        assembling(recipeOutput, "assembling/shipwreck_data",
                List.of(Ingredient.of(ModItems.BLANK_DATA.get()), Ingredient.of(ItemTags.PLANKS)),
                List.of(1, 64),
                List.of(new FluidRequirement(FluidIngredient.of(Fluids.WATER), 4000)),
                new ItemStack(ModItems.SHIPWRECK_DATA.get()),
                SHIPWRECK_DATA_DURATION, SHIPWRECK_DATA_ENERGY_PER_TICK);
    }

    /** 沉船数据比普通插件重一档，与高阶装配一致。 */
    private static final int SHIPWRECK_DATA_DURATION = 600;
    private static final int SHIPWRECK_DATA_ENERGY_PER_TICK = 400;

    /** 写出单条装配配方（不需要流体原料）。 */
    private void assembling(RecipeOutput recipeOutput, String path, List<Ingredient> ingredients,
                            ItemStack result, int duration, int energyPerTick) {
        assembling(recipeOutput, path, ingredients, List.of(), List.of(), result, duration, energyPerTick);
    }

    /**
     * 完整版：带流体原料，并且可以为每种材料声明「一次消耗几个」。
     * <p>
     * counts 与 ingredients 一一对应，缺省按 1 处理；沉船数据要一次吃掉 64 块木板就靠它。
     */
    private void assembling(RecipeOutput recipeOutput, String path, List<Ingredient> ingredients,
                            List<Integer> counts, List<FluidRequirement> fluids, ItemStack result,
                            int duration, int energyPerTick) {
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, path),
                new AssemblingRecipe(ingredients, counts, fluids, result, duration, energyPerTick),
                null);
    }

    /* ============================ 注入配方 ============================ */

    /**
     * 注入配方：基底物质在物质束缚器上，注入物质在注入枪上。
     * <p>
     * 能量窗口是本机制的核心参数 —— 放电量低于下限白费材料，高于上限直接爆炸，
     * 因此每条配方的上下限都要和「玩家可能拿到多少电」一起考虑。
     */
    private void buildInjectionRecipes(RecipeOutput recipeOutput) {
        // 翡翠钢锭 + 灵感 -> 注能翡翠钢（1 级：玄武岩合金块底座）
        injection(recipeOutput, "injection/energized_jade_steel",
                Ingredient.of(ModItems.JADE_STEEL_INGOT.get()),
                Ingredient.of(ModItems.INSPIRATION.get()),
                1, 50_000, 150_000,
                new ItemStack(ModItems.ENERGIZED_JADE_STEEL.get()));

        // 玄武岩基底 + 下界合金碎片 -> 3 玄武岩合金
        injection(recipeOutput, "injection/basalt_alloy",
                Ingredient.of(ModItems.BASALT_BASE.get()),
                Ingredient.of(Items.NETHERITE_SCRAP),
                1, 25_000, 75_000,
                new ItemStack(ModItems.BASALT_ALLOY.get(), 3));

        // 末地合金 + 无恒物质 -> 无恒合金
        // 这条不写死能量：窗口由存档种子随机决定（见 SeededEnergyWindow），JEI 里只显示 ???
        seededInjection(recipeOutput, "injection/ephemeral_alloy",
                Ingredient.of(ModItems.END_ALLOY.get()),
                Ingredient.of(ModItems.EPHEMERAL_MATERIAL.get()),
                1, new ItemStack(ModItems.EPHEMERAL_ALLOY.get()));

        // 打火石 + 玄武岩合金 -> 玄武岩合金打火石
        // 窗口很窄（25k~50k），逼玩家把注入枪精确充到这一档再用
        injection(recipeOutput, "injection/basalt_alloy_firestriker",
                Ingredient.of(Items.FLINT_AND_STEEL),
                Ingredient.of(ModItems.BASALT_ALLOY.get()),
                1, 25_000, 50_000,
                new ItemStack(ModItems.BASALT_ALLOY_FIRESTRIKER.get()));

        // 盾牌 + 末地合金 -> 末地合金盾牌：目前的最高档注入，窗口 500k~1500k
        injection(recipeOutput, "injection/end_alloy_shield",
                Ingredient.of(Items.SHIELD),
                Ingredient.of(ModItems.END_ALLOY.get()),
                1, 500_000, 1_500_000,
                new ItemStack(ModItems.END_ALLOY_SHIELD.get()));

        // 青金石锭 + 灵感 -> 注能青金石锭：窗口很窄（10k~15k），高能矿物扭曲机的材料
        injection(recipeOutput, "injection/energized_lapis_ingot",
                Ingredient.of(ModItems.LAPIS_INGOT.get()),
                Ingredient.of(ModItems.INSPIRATION.get()),
                1, 10_000, 15_000,
                new ItemStack(ModItems.ENERGIZED_LAPIS_INGOT.get()));

        // 等离子锭 + 灵感 -> 注能等离子锭：目前窗口最高的一条（1M~2M），
        // 必须靠电容插件把注入枪的容量堆上去才够得着
        injection(recipeOutput, "injection/energized_plasma_ingot",
                Ingredient.of(ModItems.PLASMA_INGOT.get()),
                Ingredient.of(ModItems.INSPIRATION.get()),
                1, 1_000_000, 2_000_000,
                new ItemStack(ModItems.ENERGIZED_PLASMA_INGOT.get()));
        // 注能红石锭：红石锭 ← 灵感（150k~300k FE），高能物质重塑机的材料
        injection(recipeOutput, "injection/energized_redstone_ingot",
                Ingredient.of(ModItems.REDSTONE_INGOT.get()),
                Ingredient.of(ModItems.INSPIRATION.get()),
                1, 150_000, 300_000,
                new ItemStack(ModItems.ENERGIZED_REDSTONE_INGOT.get()));

        // 铁锭 + 思维矩阵锭 -> 结构投影仪：窗口 50k~150k FE
        injection(recipeOutput, "injection/projector",
                Ingredient.of(Items.IRON_INGOT),
                Ingredient.of(ModItems.MIND_MATRIX_INGOT.get()),
                1, 50_000, 150_000,
                new ItemStack(ModItems.PROJECTOR.get()));

        // 翡翠钢升级模板 + 注能翡翠钢 -> 注能翡翠钢升级模板
        // 本条是**二级配方**：束缚器下方必须换成幽匿合金块，玄武岩合金底座做不了
        injection(recipeOutput, "injection/energized_jade_steel_upgrade_template",
                Ingredient.of(ModItems.JADE_STEEL_UPGRADE_TEMPLATE.get()),
                Ingredient.of(ModItems.ENERGIZED_JADE_STEEL.get()),
                2, 500_000, 1_000_000,
                new ItemStack(ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get()));
    }

    /* ============================ 钫盐系列 ============================ */

    /**
     * 钫盐的处理流程逐条对应铯盐（见 README 的铯盐链），差别只有两点：
     * 全流程产物都带辐射（数值挂在数据地图上），以及钫的碱性略强于铯。
     * <p>
     * 盖革计数器也在这里：它是「向铁锭注入铯锭」的产物，
     * 能量窗口 150k~250k FE —— 下限很高，逼玩家先把注入枪充满再动手。
     */
    private void buildFranciumRecipes(RecipeOutput recipeOutput) {
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

        // 钫盐块 + 还原粉（右键）-> 钫块，与铯同法
        RightClickBlockRecipeBuilder.recipe(ModBlocks.FRANCIUM_SALT_BLOCK.get(), ModBlocks.FRANCIUM_BLOCK.get(),
                        new ItemStack(ModItems.REDUCTION_POWDER.get()))
                .unlockedBy("has_reduction_powder", has(ModItems.REDUCTION_POWDER.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "francium_block_from_salt"));

        // 盖革计数器：铁锭（束缚器）注入铯锭（注入枪）
        injection(recipeOutput, "injection/geiger_counter",
                Ingredient.of(Items.IRON_INGOT),
                Ingredient.of(ModItems.CESIUM_INGOT.get()),
                1, 150_000, 250_000,
                new ItemStack(ModItems.GEIGER_COUNTER.get()));
    }

    private void injection(RecipeOutput recipeOutput, String path, Ingredient base, Ingredient inject,
                           int tier, int minEnergy, int maxEnergy, ItemStack result) {
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, path),
                new InjectionRecipe(base, inject, tier, minEnergy, maxEnergy,
                        result, InjectionRecipe.DEFAULT_DURATION),
                null);
    }

    /**
     * 种子随机窗口的注入配方：JSON 里不写 {@code min_energy} / {@code max_energy}，
     * 实际窗口由存档种子决定，玩家只能用世界数据探测器读。
     */
    private void seededInjection(RecipeOutput recipeOutput, String path, Ingredient base, Ingredient inject,
                                 int tier, ItemStack result) {
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, path),
                new InjectionRecipe(base, inject, result, tier, InjectionRecipe.DEFAULT_DURATION),
                null);
    }
}