package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.datagen.recipe.AssemblingRecipes;
import com.starcrosser.inspiration.datagen.recipe.CookingRecipes;
import com.starcrosser.inspiration.datagen.recipe.CraftingRecipes;
import com.starcrosser.inspiration.datagen.recipe.VoidInjectionRecipes;
import com.starcrosser.inspiration.datagen.recipe.FluidExtractorRecipes;
import com.starcrosser.inspiration.datagen.recipe.FranciumRecipes;
import com.starcrosser.inspiration.datagen.recipe.FusionRecipes;
import com.starcrosser.inspiration.datagen.recipe.InjectionRecipes;
import com.starcrosser.inspiration.datagen.recipe.MachineBlockRecipes;
import com.starcrosser.inspiration.datagen.recipe.MaterialRecipes;
import com.starcrosser.inspiration.datagen.recipe.RefiningRecipes;
import com.starcrosser.inspiration.datagen.recipe.ReshapingRecipes;
import com.starcrosser.inspiration.datagen.recipe.ResonanceRecipes;
import com.starcrosser.inspiration.datagen.recipe.RightClickRecipes;
import com.starcrosser.inspiration.datagen.recipe.SimulatorCardRecipes;
import com.starcrosser.inspiration.datagen.recipe.SmithingRecipes;
import com.starcrosser.inspiration.datagen.recipe.TwistingRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

/**
 * 配方生成的总入口，**只负责把活派下去**。
 * <p>
 * 原先 1300 行全堆在这里，加一条配方要在长文件里找位置。现在按机制拆到
 * {@code datagen/recipe/} 下，每个文件管一类配方：
 *
 * <ul>
 *   <li>{@link CookingRecipes} 熔炉 / 高炉 / 烟熏 / 营火</li>
 *   <li>{@link CraftingRecipes} 工作台的有序与无序合成</li>
 *   <li>{@link MaterialRecipes} 锭 / 粒 / 尘三档之间的打包与拆包（表驱动）</li>
 *   <li>{@link SmithingRecipes} 锻造转换与翡翠钢升级</li>
 *   <li>{@link RightClickRecipes} 手持物品右键方块转换</li>
 *   <li>{@link MachineBlockRecipes} 机器与多方块结构方块本身</li>
 *   <li>{@link FusionRecipes} / {@link ReshapingRecipes} / {@link TwistingRecipes} /
 *       {@link AssemblingRecipes} 四台配方机器各自的配方</li>
 *   <li>{@link InjectionRecipes} 注入</li>
 *   <li>{@link FranciumRecipes} 钫盐系列</li>
 *   <li>{@link FluidExtractorRecipes} 流体提取机：材料 → 液态金属</li>
 *   <li>{@link RefiningRecipes} 原料精炼机：原料 → 主产物 + 副产物</li>
 * </ul>
 *
 * 加一条新配方：找到对应那个文件往里写就行；要加一整类机制，就在 {@code datagen/recipe/}
 * 下新建文件，然后在这里补一行。
 */
public class ModRecipesProvider extends RecipeProvider {

    public ModRecipesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // 原版机制
        CookingRecipes.build(recipeOutput);
        CraftingRecipes.build(recipeOutput);
        MaterialRecipes.build(recipeOutput);
        SmithingRecipes.build(recipeOutput);
        RightClickRecipes.build(recipeOutput);
        FranciumRecipes.build(recipeOutput);

        // 机器本体与四类机器配方
        MachineBlockRecipes.build(recipeOutput);
        FusionRecipes.build(recipeOutput);
        ReshapingRecipes.build(recipeOutput);
        ResonanceRecipes.build(recipeOutput);
        TwistingRecipes.build(recipeOutput);
        AssemblingRecipes.build(recipeOutput);
        SimulatorCardRecipes.build(recipeOutput);

        // 注入
        InjectionRecipes.build(recipeOutput);
        VoidInjectionRecipes.build(recipeOutput);

        // 流体提取机与原料精炼机
        FluidExtractorRecipes.build(recipeOutput);
        RefiningRecipes.build(recipeOutput);
    }
}
