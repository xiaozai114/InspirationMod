package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.voidenergy.ModVoidEnergy;
import com.starcrosser.inspiration.material.ModMaterials;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import static com.starcrosser.inspiration.datagen.recipe.RecipeUnlocks.getHasName;
import static com.starcrosser.inspiration.datagen.recipe.RecipeUnlocks.has;

/** 工作台配方：有序合成 + 无序合成。 */
public final class CraftingRecipes {

    private CraftingRecipes() {
    }

    public static void build(RecipeOutput recipeOutput) {
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
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModMaterials.CESIUM.block().get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModMaterials.CESIUM.item().get())
                .unlockedBy(getHasName(ModMaterials.CESIUM.item().get()), has(ModMaterials.CESIUM.item().get()))
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

        // 灵感不再有合成配方：改由玩家身上的「灵感值」提取（合成 / 吃东西 / 拿经验积累）

        // 指导书：书 + 灵感（书的装帧由灵感驱动，所以是无序的）
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GUIDE_BOOK.get())
                .requires(Items.BOOK)
                .requires(ModItems.INSPIRATION.get())
                .unlockedBy(getHasName(ModItems.INSPIRATION.get()), has(ModItems.INSPIRATION.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "guide_book"));

        // 红石锭（默认 ID "redstone_ingot"）
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModMaterials.REDSTONE.item().get(), 2)
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
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModMaterials.LAPIS.item().get(), 2)
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
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModMaterials.REDSTONE.block().get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModMaterials.REDSTONE.item().get())
                .unlockedBy(getHasName(ModMaterials.REDSTONE.item().get()), has(ModMaterials.REDSTONE.item().get()))
                .save(recipeOutput);

        // 青金石锭块（默认 ID "lapis_ingot_block"）
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModMaterials.LAPIS.block().get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModMaterials.LAPIS.item().get())
                .unlockedBy(getHasName(ModMaterials.LAPIS.item().get()), has(ModMaterials.LAPIS.item().get()))
                .save(recipeOutput);

        // 极限合金（默认 ID "extreme_alloy"）
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModMaterials.EXTREME_ALLOY.item().get())
                .pattern("###")
                .pattern("#YZ")
                .pattern("ZZZ")
                .define('#', ModMaterials.REDSTONE.block().get())
                .define('Y', ModItems.INSPIRATION.get())
                .define('Z', ModMaterials.LAPIS.block().get())
                .unlockedBy(getHasName(ModMaterials.REDSTONE.block().get()), has(ModMaterials.REDSTONE.block().get()))
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

        // 不稳核心 + 8 铁锭 -> 8 不稳金属：虚空能量提取器与虚空注入枪的主料
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.UNSTABLE_METAL.get(), 8)
                .requires(ModItems.UNSTABLE_CORE.get())
                .requires(Items.IRON_INGOT, 8)
                .unlockedBy(getHasName(ModItems.UNSTABLE_CORE.get()), has(ModItems.UNSTABLE_CORE.get()))
                .save(recipeOutput, "unstable_metal_from_unstable_core");

        // 虚空能量提取器：4 铁锭 + 4 不稳金属 + 强化核心
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModVoidEnergy.VOID_ENERGY_EXTRACTOR.get())
                .pattern("MSM")
                .pattern("SCS")
                .pattern("MSM")
                .define('M', Items.IRON_INGOT)
                .define('S', ModItems.UNSTABLE_METAL.get())
                .define('C', ModItems.REINFORCED_CORE.get())
                .unlockedBy(getHasName(ModItems.REINFORCED_CORE.get()), has(ModItems.REINFORCED_CORE.get()))
                .save(recipeOutput);

        // 虚空能量中继器：5 铁锭 + 3 幽匿合金 + 1 强化核心（与其它机器同形状）
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModVoidEnergy.VOID_ENERGY_RELAY.get())
                .pattern("MSM")
                .pattern("SCS")
                .pattern("MXM")
                .define('M', Items.IRON_INGOT)
                .define('S', ModMaterials.SCULK_ALLOY.item().get())
                .define('C', ModItems.REINFORCED_CORE.get())
                .define('X', Items.IRON_INGOT)
                .unlockedBy(getHasName(ModItems.REINFORCED_CORE.get()), has(ModItems.REINFORCED_CORE.get()))
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
                .define('W', ModMaterials.REDSTONE.item().get())
                .define('Z', ModMaterials.LAPIS.item().get())
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
                .define('J', ModMaterials.JADE_STEEL.item().get())
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
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModMaterials.CESIUM.item().get(), 9)
                .requires(ModMaterials.CESIUM.block().get())
                .unlockedBy(getHasName(ModMaterials.CESIUM.block().get()), has(ModMaterials.CESIUM.block().get()))
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
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModMaterials.REDSTONE.item().get(), 9)
                .group("redstone_ingot")
                .requires(ModMaterials.REDSTONE.block().get())
                .unlockedBy(getHasName(ModMaterials.REDSTONE.block().get()), has(ModMaterials.REDSTONE.block().get()))
                .save(recipeOutput, "redstone_ingot_from_redstone_ingot_block");

        // 青金石锭块分解，需要指定 ID
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModMaterials.LAPIS.item().get(), 9)
                .group("lapis_ingot")
                .requires(ModMaterials.LAPIS.block().get())
                .unlockedBy(getHasName(ModMaterials.LAPIS.block().get()), has(ModMaterials.LAPIS.block().get()))
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
    }
}
