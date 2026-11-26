package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.material.ModMaterials;
import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import static com.starcrosser.inspiration.datagen.recipe.RecipeUnlocks.getHasName;
import static com.starcrosser.inspiration.datagen.recipe.RecipeUnlocks.has;

/** 锻造配方：原版锻造台的转换配方，加上本 mod 的翡翠钢 / 注能翡翠钢升级。 */
public final class SmithingRecipes {

    private SmithingRecipes() {
    }

    public static void build(RecipeOutput recipeOutput) {
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
                        Ingredient.of(ModMaterials.EXTREME_ALLOY.item().get()),
                        RecipeCategory.TOOLS, ModItems.WORLD_BREAKER.get())
                .unlocks("has_extreme_alloy", has(ModMaterials.EXTREME_ALLOY.item().get()))
                .save(recipeOutput,"world_breaker_smithing");

        // ========== 翡翠钢升级：模板 + 灵感工具 + 翡翠钢锭 ==========
        buildJadeSteelUpgrades(recipeOutput);
        buildEnergizedJadeSteelUpgrades(recipeOutput);
        smithingUpgrade(recipeOutput, ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get(),
                ModMaterials.ENERGIZED_JADE_STEEL.item().get(), "energized_jade_steel_helmet",
                Items.NETHERITE_HELMET, ModItems.ENERGIZED_JADE_STEEL_HELMET.get(), RecipeCategory.COMBAT);
        smithingUpgrade(recipeOutput, ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get(),
                ModMaterials.ENERGIZED_JADE_STEEL.item().get(), "energized_jade_steel_chestplate",
                Items.NETHERITE_CHESTPLATE, ModItems.ENERGIZED_JADE_STEEL_CHESTPLATE.get(), RecipeCategory.COMBAT);
        smithingUpgrade(recipeOutput, ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get(),
                ModMaterials.ENERGIZED_JADE_STEEL.item().get(), "energized_jade_steel_leggings",
                Items.NETHERITE_LEGGINGS, ModItems.ENERGIZED_JADE_STEEL_LEGGINGS.get(), RecipeCategory.COMBAT);
        smithingUpgrade(recipeOutput, ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get(),
                ModMaterials.ENERGIZED_JADE_STEEL.item().get(), "energized_jade_steel_boots",
                Items.NETHERITE_BOOTS, ModItems.ENERGIZED_JADE_STEEL_BOOTS.get(), RecipeCategory.COMBAT);
    }

    /**
     * 四件灵感工具的翡翠钢升级。
     * <p>
     * 与下界合金升级同构：模板 + 底材工具 + 材料锭 → 升级后的工具。
     */
    private static void buildJadeSteelUpgrades(RecipeOutput recipeOutput) {
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
    private static void buildEnergizedJadeSteelUpgrades(RecipeOutput recipeOutput) {
        smithingUpgrade(recipeOutput, ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get(),
                ModMaterials.ENERGIZED_JADE_STEEL.item().get(), "energized_jade_steel_sword",
                ModItems.JADE_STEEL_SWORD.get(), ModItems.ENERGIZED_JADE_STEEL_SWORD.get(),
                RecipeCategory.COMBAT);
        smithingUpgrade(recipeOutput, ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get(),
                ModMaterials.ENERGIZED_JADE_STEEL.item().get(), "energized_jade_steel_axe",
                ModItems.JADE_STEEL_AXE.get(), ModItems.ENERGIZED_JADE_STEEL_AXE.get(),
                RecipeCategory.TOOLS);
        smithingUpgrade(recipeOutput, ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get(),
                ModMaterials.ENERGIZED_JADE_STEEL.item().get(), "energized_jade_steel_pickaxe",
                ModItems.JADE_STEEL_PICKAXE.get(), ModItems.ENERGIZED_JADE_STEEL_PICKAXE.get(),
                RecipeCategory.TOOLS);
        smithingUpgrade(recipeOutput, ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get(),
                ModMaterials.ENERGIZED_JADE_STEEL.item().get(), "energized_jade_steel_hoe",
                ModItems.JADE_STEEL_HOE.get(), ModItems.ENERGIZED_JADE_STEEL_HOE.get(),
                RecipeCategory.TOOLS);
        smithingUpgrade(recipeOutput, ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get(),
                ModMaterials.ENERGIZED_JADE_STEEL.item().get(), "energized_jade_steel_shovel",
                ModItems.JADE_STEEL_SHOVEL.get(), ModItems.ENERGIZED_JADE_STEEL_SHOVEL.get(),
                RecipeCategory.TOOLS);
    }

    /** 翡翠钢档的升级配方：模板与材料固定，进度条件挂在原工具上。 */
    private static void smithingUpgrade(RecipeOutput recipeOutput, String path, Item base, Item result,
                                        RecipeCategory category) {
        smithingUpgrade(recipeOutput, ModItems.JADE_STEEL_UPGRADE_TEMPLATE.get(),
                ModMaterials.JADE_STEEL.item().get(), path, base, result, category);
    }

    /** 写出一条锻造升级配方，进度条件挂在原工具上。 */
    private static void smithingUpgrade(RecipeOutput recipeOutput, Item template, Item material, String path,
                                        Item base, Item result, RecipeCategory category) {
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(template),
                        Ingredient.of(base),
                        Ingredient.of(material),
                        category, result)
                .unlocks("has_" + BuiltInRegistries.ITEM.getKey(base).getPath(), has(base))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, path));
    }
}
