package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import com.starcrosser.inspiration.material.ModMaterials;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import static com.starcrosser.inspiration.datagen.recipe.RecipeUnlocks.getHasName;
import static com.starcrosser.inspiration.datagen.recipe.RecipeUnlocks.has;

/** 机器与多方块结构方块的工作台配方（含它们的核心材料）。 */
public final class MachineBlockRecipes {

    private MachineBlockRecipes() {
    }

    public static void build(RecipeOutput recipeOutput) {
        // 强化核心：5 铯盐（四角 + 中心）+ 4 黑曜石（四边）
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
                .define('C', ModMaterials.CESIUM.item().get())
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
    private static void machine(RecipeOutput recipeOutput, ItemLike result, ItemLike main, ItemLike secondary,
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
    private static void hatch(RecipeOutput recipeOutput, ItemLike result, ItemLike top, ItemLike bottom,
                              ItemLike unlock) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
                .pattern("T")
                .pattern("B")
                .define('T', top)
                .define('B', bottom)
                .unlockedBy(getHasName(unlock), has(unlock))
                .save(recipeOutput);
    }
}
