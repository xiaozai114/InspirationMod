package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.material.ModMaterials;
import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.machine.recipe.ReshapingRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/** 物质重塑机的专属配方。 */
public final class ReshapingRecipes {

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
                new ItemStack(ModMaterials.REDSTONE.item().get()), SIMPLE_DURATION, SIMPLE_ENERGY_PER_TICK);

        // 3 青金石 -> 青金石锭
        reshaping(recipeOutput, "reshaping/lapis_ingot_from_lapis", 3,
                Ingredient.of(Items.LAPIS_LAZULI), null,
                new ItemStack(ModMaterials.LAPIS.item().get()), SIMPLE_DURATION, SIMPLE_ENERGY_PER_TICK);

        // 钻石 -> 等离子锭：重塑机最高档的单材料转化
        reshaping(recipeOutput, "reshaping/plasma_ingot_from_diamond", 1,
                Ingredient.of(Items.DIAMOND), null,
                new ItemStack(ModMaterials.PLASMA.item().get()), PLASMA_DURATION, PLASMA_ENERGY_PER_TICK);

        // 玄武岩合金 -> 玄武岩合金钻头：地核钻探者的耗材，与等离子锭同档
        reshaping(recipeOutput, "reshaping/basalt_alloy_drill_bit", 1,
                Ingredient.of(ModMaterials.BASALT_ALLOY.item().get()), null,
                new ItemStack(ModItems.BASALT_ALLOY_DRILL_BIT.get()),
                PLASMA_DURATION, PLASMA_ENERGY_PER_TICK);

        // 幽匿合金 + 玄武岩合金钻头 -> 幽匿合金钻头：挖到底会把基岩裂开
        reshaping(recipeOutput, "reshaping/sculk_alloy_drill_bit", 1,
                Ingredient.of(ModMaterials.SCULK_ALLOY.item().get()),
                Ingredient.of(ModItems.BASALT_ALLOY_DRILL_BIT.get()),
                new ItemStack(ModItems.SCULK_ALLOY_DRILL_BIT.get()),
                PLASMA_DURATION, PLASMA_ENERGY_PER_TICK);

        // 弱化核心 -> 零点模块：零点能发电机的核心部件
        reshaping(recipeOutput, "reshaping/null_point_module", 1,
                Ingredient.of(ModItems.WEAKENED_CORE.get()), null,
                new ItemStack(ModItems.NULL_POINT_MODULE.get()), PLASMA_DURATION, PLASMA_ENERGY_PER_TICK);

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
