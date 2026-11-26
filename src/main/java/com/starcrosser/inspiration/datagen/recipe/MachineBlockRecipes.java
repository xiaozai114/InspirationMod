package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.voidenergy.ModVoidEnergy;
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
                .define('A', ModMaterials.BASALT_ALLOY.item().get())
                .define('P', ModItems.REINFORCED_CORE.get())
                .unlockedBy(getHasName(ModItems.REINFORCED_CORE.get()), has(ModItems.REINFORCED_CORE.get()))
                .save(recipeOutput);

        // ===== 机器本体：全部走 machine(...) 的统一形状（见方法注释） =====

        // 物质融合器：主要材料铁锭 + 次要材料红石锭 + 强化核心
        machine(recipeOutput, ModMachines.MATTER_FUSION_MACHINE.getBlock(),
                Items.IRON_INGOT, ModMaterials.REDSTONE.item().get(), ModItems.REINFORCED_CORE.get(),
                null, ModItems.REINFORCED_CORE.get());

        // 强能物质融合机：物质融合器升级，次要材料换成红石锭块
        machine(recipeOutput, ModMachines.ENERGIZED_MATTER_FUSION_MACHINE.getBlock(),
                ModMaterials.ENERGIZED_JADE_STEEL.item().get(), ModMaterials.REDSTONE.block().get(),
                ModItems.POWERFUL_CORE.get(), ModMachines.MATTER_FUSION_MACHINE.getBlock(),
                ModMaterials.ENERGIZED_JADE_STEEL.item().get());

        // 地核钻探者：翡翠钢 + 思维矩阵锭
        machine(recipeOutput, ModMachines.CORE_DRILLER.getBlock(),
                ModMaterials.JADE_STEEL.item().get(), ModMaterials.MIND_MATRIX.item().get(),
                ModItems.POWERFUL_CORE.get(), null, ModMaterials.MIND_MATRIX.item().get());

        // 物质重塑机：翡翠钢 + 红石锭块
        machine(recipeOutput, ModMachines.MATTER_RESHAPER.getBlock(),
                ModMaterials.JADE_STEEL.item().get(), ModMaterials.REDSTONE.block().get(),
                ModItems.POWERFUL_CORE.get(), null, ModItems.POWERFUL_CORE.get());

        // 矿物扭曲机：与物质重塑机同构，次要材料换成青金石锭块
        machine(recipeOutput, ModMachines.MINERAL_TWISTER.getBlock(),
                ModMaterials.JADE_STEEL.item().get(), ModMaterials.LAPIS.block().get(),
                ModItems.POWERFUL_CORE.get(), null, ModItems.POWERFUL_CORE.get());

        // 灵感装配台：翡翠钢 + 等离子锭
        machine(recipeOutput, ModMachines.INSPIRATION_ASSEMBLER.getBlock(),
                ModMaterials.JADE_STEEL.item().get(), ModMaterials.PLASMA.item().get(),
                ModItems.POWERFUL_CORE.get(), null, ModMaterials.PLASMA.item().get());

        // 高能矿物扭曲机：矿物扭曲机 + 高能核心 + 注能青金石锭
        machine(recipeOutput, ModMachines.HIGH_ENERGY_MINERAL_TWISTER.getBlock(),
                ModMaterials.EPHEMERAL_ALLOY.item().get(), ModMaterials.ENERGIZED_LAPIS.item().get(),
                ModItems.HIGH_ENERGY_CORE.get(), ModMachines.MINERAL_TWISTER.getBlock(),
                ModItems.HIGH_ENERGY_CORE.get());

        // 高能灵感装配台：灵感装配台 + 高能核心 + 注能等离子锭
        machine(recipeOutput, ModMachines.HIGH_ENERGY_INSPIRATION_ASSEMBLER.getBlock(),
                ModMaterials.EPHEMERAL_ALLOY.item().get(), ModMaterials.ENERGIZED_PLASMA.item().get(),
                ModItems.HIGH_ENERGY_CORE.get(), ModMachines.INSPIRATION_ASSEMBLER.getBlock(),
                ModItems.HIGH_ENERGY_CORE.get());

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModMachines.COMBUSTION_GENERATOR.getBlock())
                .pattern(" I ")
                .pattern("IFI")
                .pattern(" R ")
                .define('I', Items.IRON_INGOT)
                .define('F', Blocks.FURNACE)
                .define('R', Items.REDSTONE)
                .unlockedBy(getHasName(Items.FURNACE), has(Items.FURNACE))
                .save(recipeOutput);

        // 碱性发电机：翡翠钢 + 红石锭 + 发电核心（发电类机器，不跑配方）
        machine(recipeOutput, ModMachines.ALKALINE_REACTOR.getBlock(),
                ModMaterials.JADE_STEEL.item().get(), ModMaterials.REDSTONE.item().get(),
                ModItems.GENERATION_CORE.get(), null, ModItems.GENERATION_CORE.get());

        // RTG反应堆：无恒合金 + 注能红石锭 + 反应核心
        machine(recipeOutput, ModMachines.RTG_GENERATOR.getBlock(),
                ModMaterials.EPHEMERAL_ALLOY.item().get(), ModMaterials.ENERGIZED_REDSTONE.item().get(),
                ModItems.REACTION_CORE.get(), null, ModItems.REACTION_CORE.get());

        // 流体提取机：强能核心 + 翡翠钢 + 下界合金锭（与其它机器同构，四角翡翠钢、四边下界合金）
        machine(recipeOutput, ModMachines.FLUID_EXTRACTOR.getBlock(),
                ModMaterials.JADE_STEEL.item().get(), Items.NETHERITE_INGOT,
                ModItems.POWERFUL_CORE.get(), null, ModItems.POWERFUL_CORE.get());

        // 原料精炼机：3 钙锭 + 5 无恒合金 + 1 高能核心。
        // 刻意**不**用 machine(...) 的统一形状 —— 那套形状是「四角主材 + 四边次材 + 中心核心」，
        // 材料数固定 5/3/1；这里要的是 3/5/1，换成自己的 SCS / CHC / SSS。
        // 形状不同也有好处：玩家不会在放材料时误把两台高阶机器搞混。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModMachines.RAW_REFINER.getBlock())
                .pattern("SCS")
                .pattern("CHC")
                .pattern("SSS")
                .define('C', ModMaterials.CALCIUM.item().get())
                .define('H', ModItems.HIGH_ENERGY_CORE.get())
                .define('S', ModMaterials.EPHEMERAL_ALLOY.item().get())
                .unlockedBy(getHasName(ModItems.HIGH_ENERGY_CORE.get()), has(ModItems.HIGH_ENERGY_CORE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModMachines.CULTURE_TANK.getBlock())
                .pattern("GGG")
                .pattern("GJG")
                .pattern("GEG")
                .define('G', Items.GLASS_PANE)
                .define('J', ModMaterials.JADE_STEEL.item().get())
                .define('E', ModItems.EPHEMERAL_MATERIAL.get())
                .unlockedBy(getHasName(ModMaterials.JADE_STEEL.item().get()), has(ModMaterials.JADE_STEEL.item().get()))
                .save(recipeOutput);

        // 零点能发电机：4 虚空等离子锭 + 4 钚锭 + 1 零点模块
        machine(recipeOutput, ModMachines.ZERO_POINT_GENERATOR.getBlock(),
                ModItems.VOID_PLASMA_INGOT.get(), ModMaterials.PLUTONIUM.item().get(),
                ModItems.NULL_POINT_MODULE.get(), null, ModItems.NULL_POINT_MODULE.get());

        // 虚空注入枪：注入枪 + 8 不稳金属围一圈
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModInjection.VOID_INJECTION_GUN.getBlock())
                .pattern("SSS")
                .pattern("SGS")
                .pattern("SSS")
                .define('S', ModItems.UNSTABLE_METAL.get())
                .define('G', ModInjection.INJECTION_GUN.getBlock())
                .unlockedBy(getHasName(ModInjection.INJECTION_GUN.getBlock()),
                        has(ModInjection.INJECTION_GUN.getBlock()))
                .save(recipeOutput);

        // 高能物质融合器：4 无恒合金 + 4 压缩红石锭 + 1 高能核心，中间放强能物质融合机
        machine(recipeOutput, ModMachines.HIGH_ENERGY_MATTER_FUSION_MACHINE.getBlock(),
                ModMaterials.EPHEMERAL_ALLOY.item().get(), ModItems.COMPRESSED_REDSTONE_INGOT.get(),
                ModItems.HIGH_ENERGY_CORE.get(), ModMachines.ENERGIZED_MATTER_FUSION_MACHINE.getBlock(),
                ModItems.HIGH_ENERGY_CORE.get());

        // 虚空能量凝聚器：4 虚空等离子锭 + 4 铷锭 + 1 弱化核心
        machine(recipeOutput, ModMachines.VOID_CONDENSER.getBlock(),
                ModItems.VOID_PLASMA_INGOT.get(), ModMaterials.RUBIDIUM.item().get(),
                ModItems.WEAKENED_CORE.get(), null, ModItems.WEAKENED_CORE.get());

        machine(recipeOutput, ModMachines.RADIATION_ABSORBER.getBlock(),
                ModMaterials.EPHEMERAL_ALLOY.item().get(), ModMaterials.URANIUM.item().get(),
                ModItems.HIGH_ENERGY_CORE.get(), null, ModItems.HIGH_ENERGY_CORE.get());

        machine(recipeOutput, ModMachines.RESONANCE_CHAMBER.getBlock(),
                ModItems.VOID_PLASMA_INGOT.get(), ModMaterials.ACTINIUM.item().get(),
                ModItems.WEAKENED_CORE.get(), null, ModItems.WEAKENED_CORE.get());
        machine(recipeOutput, ModVoidEnergy.VOID_ENERGY_COLLECTOR.get(),
                ModItems.VOID_PLASMA_INGOT.get(), ModMaterials.FRANCIUM.item().get(),
                ModItems.WEAKENED_CORE.get(), null, ModItems.WEAKENED_CORE.get());

        // 灵感高炉：5 铁 + 3 镍 + 1 强化核心（镍是副材料：上面中间 + 中间两侧）
        machine(recipeOutput, ModMachines.INSPIRATION_FURNACE.getBlock(),
                Items.IRON_INGOT, ModMaterials.NICKEL.item().get(),
                ModItems.REINFORCED_CORE.get(), null, ModItems.REINFORCED_CORE.get());

        machine(recipeOutput, ModMachines.ENERGIZED_INSPIRATION_FURNACE.getBlock(),
                ModMaterials.ENERGIZED_JADE_STEEL.item().get(), ModMaterials.NICKEL.item().get(),
                ModItems.POWERFUL_CORE.get(), ModMachines.INSPIRATION_FURNACE.getBlock(),
                ModItems.POWERFUL_CORE.get());

        machine(recipeOutput, ModMachines.HIGH_ENERGY_INSPIRATION_FURNACE.getBlock(),
                ModMaterials.EPHEMERAL_ALLOY.item().get(), ModMaterials.NICKEL.item().get(),
                ModItems.HIGH_ENERGY_CORE.get(), ModMachines.ENERGIZED_INSPIRATION_FURNACE.getBlock(),
                ModItems.HIGH_ENERGY_CORE.get());


        // ==================== 结构模拟机（多方块） ====================

        // 结构模拟机：无恒合金 + 幽匿合金锭 + 高能核心，形状与其他机器一致
        machine(recipeOutput, ModMultiblocks.MINERAL_SIMULATOR.getBlock(),
                ModMaterials.EPHEMERAL_ALLOY.item().get(), ModMaterials.SPECTRUM_ALLOY.item().get(),
                ModItems.HIGH_ENERGY_CORE.get(), null, ModItems.HIGH_ENERGY_CORE.get());

        machine(recipeOutput, ModMultiblocks.STRUCTURE_SIMULATOR.getBlock(),
                ModMaterials.EPHEMERAL_ALLOY.item().get(), ModMaterials.SCULK_ALLOY.item().get(),
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
                ModMaterials.REDSTONE.item().get(), casing, casing);
        hatch(recipeOutput, ModMultiblocks.INSPIRATION_HATCH.getBlock(),
                ModItems.INSPIRATION.get(), casing, casing);
        hatch(recipeOutput, ModMultiblocks.DATA_HATCH.getBlock(),
                ModItems.BLANK_DATA.get(), casing, casing);

        // 高能物质重塑机：物质重塑机 + 高能核心 + 注能红石锭（除高能倍率外无额外效果）
        machine(recipeOutput, ModMachines.HIGH_ENERGY_MATTER_RESHAPER.getBlock(),
                ModMaterials.EPHEMERAL_ALLOY.item().get(), ModMaterials.ENERGIZED_REDSTONE.item().get(),
                ModItems.HIGH_ENERGY_CORE.get(), ModMachines.MATTER_RESHAPER.getBlock(),
                ModItems.HIGH_ENERGY_CORE.get());

        // 物质束缚器：8 等离子锭 + 1 不稳核心
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModInjection.MATTER_BINDER.getBlock())
                .pattern("PPP")
                .pattern("PCP")
                .pattern("PPP")
                .define('P', ModMaterials.PLASMA.item().get())
                .define('C', ModItems.UNSTABLE_CORE.get())
                .unlockedBy(getHasName(ModMaterials.PLASMA.item().get()), has(ModMaterials.PLASMA.item().get()))
                .save(recipeOutput);

        // 注入枪：发射器外圈围 4 个翡翠钢锭
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModInjection.INJECTION_GUN.getBlock())
                .pattern(" J ")
                .pattern("JDJ")
                .pattern(" J ")
                .define('J', ModMaterials.JADE_STEEL.item().get())
                .define('D', Items.DISPENSER)
                .unlockedBy(getHasName(ModMaterials.JADE_STEEL.item().get()), has(ModMaterials.JADE_STEEL.item().get()))
                .save(recipeOutput);

        // 绑定器：注入枪与束缚器之外的第三件工具，用翡翠钢 + 末影之眼
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BINDING_WAND.get())
                .pattern("  J")
                .pattern(" E ")
                .pattern("J  ")
                .define('J', ModMaterials.JADE_STEEL.item().get())
                .define('E', Items.ENDER_EYE)
                .unlockedBy(getHasName(ModMaterials.JADE_STEEL.item().get()), has(ModMaterials.JADE_STEEL.item().get()))
                .save(recipeOutput);

        // 玄武岩合金块（1 级底座）
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModMaterials.BASALT_ALLOY.block().get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModMaterials.BASALT_ALLOY.item().get())
                .unlockedBy(getHasName(ModMaterials.BASALT_ALLOY.item().get()), has(ModMaterials.BASALT_ALLOY.item().get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModMaterials.BASALT_ALLOY.item().get(), 9)
                .requires(ModMaterials.BASALT_ALLOY.block().get())
                .unlockedBy(getHasName(ModMaterials.BASALT_ALLOY.block().get()), has(ModMaterials.BASALT_ALLOY.block().get()))
                .save(recipeOutput, "basalt_alloy_from_block");

        // 幽匿合金块（2 级底座）
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModMaterials.SCULK_ALLOY.block().get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModMaterials.SCULK_ALLOY.item().get())
                .unlockedBy(getHasName(ModMaterials.SCULK_ALLOY.item().get()), has(ModMaterials.SCULK_ALLOY.item().get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModMaterials.SCULK_ALLOY.item().get(), 9)
                .requires(ModMaterials.SCULK_ALLOY.block().get())
                .unlockedBy(getHasName(ModMaterials.SCULK_ALLOY.block().get()), has(ModMaterials.SCULK_ALLOY.block().get()))
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
