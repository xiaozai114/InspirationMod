package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.machine.recipe.ReshapingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/** 物质重塑机的专属配方，含「装备回炉成材料」那一大组。 */
public final class ReshapingRecipes {

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

    private ReshapingRecipes() {
    }

    public static void build(RecipeOutput recipeOutput) {
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
    private static void buildEquipmentRecycling(RecipeOutput recipeOutput) {
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

    private static void recycle(RecipeOutput recipeOutput, EquipmentSet set, Item equipment, int count) {
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
    private static void reshaping(RecipeOutput recipeOutput, String path, Ingredient input,
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
    private static void reshaping(RecipeOutput recipeOutput, String path, int inputCount, Ingredient input,
                                  @Nullable Ingredient secondInput, ItemStack result,
                                  int duration, int energyPerTick) {
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, path),
                new ReshapingRecipe(input, Optional.ofNullable(secondInput), result, inputCount,
                        duration, energyPerTick),
                null);
    }
}
