package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.fluid.ModFluids;
import com.starcrosser.inspiration.material.ModMaterials;
import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.machine.recipe.AssemblingRecipe;
import com.starcrosser.inspiration.machine.recipe.FluidExtractingRecipe;
import com.starcrosser.inspiration.machine.recipe.FluidRequirement;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.List;

/** 灵感装配台的专属配方。 */
public final class AssemblingRecipes {

    /** 装配插件的耗时与能耗，用装配配方的默认值。 */
    private static final int ASSEMBLE_DURATION = AssemblingRecipe.DEFAULT_DURATION;
    private static final int ASSEMBLE_ENERGY_PER_TICK = AssemblingRecipe.DEFAULT_ENERGY_PER_TICK;

    /** 沉船数据比普通插件重一档，与高阶装配一致。 */
    private static final int SHIPWRECK_DATA_DURATION = 600;
    private static final int SHIPWRECK_DATA_ENERGY_PER_TICK = 400;

    private AssemblingRecipes() {
    }

    public static void build(RecipeOutput recipeOutput) {
        assembling(recipeOutput, "assembling/capacitor_plugin",
                List.of(Ingredient.of(ModMaterials.PLASMA.item().get()), Ingredient.of(ModMaterials.REDSTONE.item().get())),
                new ItemStack(ModItems.CAPACITOR_PLUGIN.get()), ASSEMBLE_DURATION, ASSEMBLE_ENERGY_PER_TICK);

        assembling(recipeOutput, "assembling/inspiration_plugin",
                List.of(Ingredient.of(ModMaterials.PLASMA.item().get()), Ingredient.of(ModItems.INSPIRATION.get())),
                new ItemStack(ModItems.INSPIRATION_PLUGIN.get()), ASSEMBLE_DURATION, ASSEMBLE_ENERGY_PER_TICK);

        assembling(recipeOutput, "assembling/speed_plugin",
                List.of(Ingredient.of(ModMaterials.PLASMA.item().get()), Ingredient.of(Items.SUGAR)),
                new ItemStack(ModItems.SPEED_PLUGIN.get()), ASSEMBLE_DURATION, ASSEMBLE_ENERGY_PER_TICK);

        assembling(recipeOutput, "assembling/capacitor_downgrade_plugin",
                List.of(Ingredient.of(ModMaterials.PLASMA.item().get()), Ingredient.of(Items.IRON_INGOT)),
                new ItemStack(ModItems.CAPACITOR_DOWNGRADE_PLUGIN.get()),
                ASSEMBLE_DURATION, ASSEMBLE_ENERGY_PER_TICK);

        // 回收升级：思维矩阵锭 + 液态等离子 —— 第一份产物流回到装配台里的配方，
        // 也是流体提取机存在的理由（等离子得先熔成流体才能上装配台）
        assembling(recipeOutput, "assembling/recycling_plugin",
                List.of(Ingredient.of(ModMaterials.MIND_MATRIX.item().get())),
                List.of(),
                List.of(new FluidRequirement(
                        FluidIngredient.of(ModFluids.Fluids.MOLTEN_PLASMA.get()),
                        FluidExtractingRecipe.INGOT_AMOUNT)),
                new ItemStack(ModItems.RECYCLING_PLUGIN.get()),
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
                        Ingredient.of(ModMaterials.PLASMA.item().get()),
                        Ingredient.of(ModMaterials.MIND_MATRIX.item().get()),
                        Ingredient.of(Items.GLASS),
                        Ingredient.of(ModItems.EPHEMERAL_MATERIAL.get())),
                new ItemStack(ModItems.BLANK_DATA.get()),
                ASSEMBLE_DURATION, ASSEMBLE_ENERGY_PER_TICK);

        // 沉船数据：空白数据 + 64 块木板 + 4 桶水 —— 第一条带流体的装配配方
        assembling(recipeOutput, "assembling/shipwreck_data",
                List.of(Ingredient.of(ModItems.BLANK_DATA.get()), Ingredient.of(ItemTags.PLANKS)),
                List.of(1, 64),
                List.of(new FluidRequirement(FluidIngredient.of(net.minecraft.world.level.material.Fluids.WATER), 4000)),
                new ItemStack(ModItems.SHIPWRECK_DATA.get()),
                SHIPWRECK_DATA_DURATION, SHIPWRECK_DATA_ENERGY_PER_TICK);
    }

    /** 写出单条装配配方（不需要流体原料）。 */
    private static void assembling(RecipeOutput recipeOutput, String path, List<Ingredient> ingredients,
                                   ItemStack result, int duration, int energyPerTick) {
        assembling(recipeOutput, path, ingredients, List.of(), List.of(), result, duration, energyPerTick);
    }

    /**
     * 完整版：带流体原料，并且可以为每种材料声明「一次消耗几个」。
     * <p>
     * counts 与 ingredients 一一对应，缺省按 1 处理；沉船数据要一次吃掉 64 块木板就靠它。
     */
    private static void assembling(RecipeOutput recipeOutput, String path, List<Ingredient> ingredients,
                                   List<Integer> counts, List<FluidRequirement> fluids, ItemStack result,
                                   int duration, int energyPerTick) {
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, path),
                new AssemblingRecipe(ingredients, counts, fluids, result, duration, energyPerTick),
                null);
    }
}
