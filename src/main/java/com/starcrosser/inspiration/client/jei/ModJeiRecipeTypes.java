package com.starcrosser.inspiration.client.jei;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.injection.recipe.InjectionRecipe;
import com.starcrosser.inspiration.machine.recipe.AssemblingRecipe;
import com.starcrosser.inspiration.machine.recipe.FluidExtractingRecipe;
import com.starcrosser.inspiration.machine.recipe.FusionRecipe;
import com.starcrosser.inspiration.machine.recipe.RefiningRecipe;
import com.starcrosser.inspiration.machine.recipe.ReshapingRecipe;
import com.starcrosser.inspiration.machine.recipe.TwistingRecipe;
import com.starcrosser.inspiration.recipe.RightClickBlockRecipe;
import com.starcrosser.inspiration.voidenergy.recipe.VoidCondensationInfo;
import com.starcrosser.inspiration.voidenergy.recipe.VoidInjectionRecipe;
import mezz.jei.api.recipe.RecipeType;

/**
 * 本模组在 JEI 中注册的配方分类标识。
 * <p>
 * 每新增一种自定义配方类型，在这里加一个常量即可。
 */
public final class ModJeiRecipeTypes {

    /** 物质融合器。 */
    public static final RecipeType<FusionRecipe> FUSION =
            RecipeType.create(Inspiration.MODID, "fusion", FusionRecipe.class);

    /** 物质重塑机。 */
    public static final RecipeType<ReshapingRecipe> RESHAPING =
            RecipeType.create(Inspiration.MODID, "reshaping", ReshapingRecipe.class);

    /** 手持物品右键点击方块。 */
    public static final RecipeType<RightClickBlockRecipe> RIGHT_CLICK_BLOCK =
            RecipeType.create(Inspiration.MODID, "right_click_block", RightClickBlockRecipe.class);

    /** 矿物扭曲机。 */
    public static final RecipeType<TwistingRecipe> TWISTING =
            RecipeType.create(Inspiration.MODID, "twisting", TwistingRecipe.class);

    /** 灵感装配台。 */
    public static final RecipeType<AssemblingRecipe> ASSEMBLING =
            RecipeType.create(Inspiration.MODID, "assembling", AssemblingRecipe.class);

    /** 注入（物质束缚器 + 注入枪）。 */
    public static final RecipeType<InjectionRecipe> INJECTION =
            RecipeType.create(Inspiration.MODID, "injection", InjectionRecipe.class);

    /** 流体提取机。 */
    public static final RecipeType<FluidExtractingRecipe> FLUID_EXTRACTING =
            RecipeType.create(Inspiration.MODID, "fluid_extracting", FluidExtractingRecipe.class);

    /** 原料精炼机。 */
    public static final RecipeType<RefiningRecipe> REFINING =
            RecipeType.create(Inspiration.MODID, "refining", RefiningRecipe.class);

    /** 结构模拟机的结构预览：每一层一条，翻页即翻层。不是真配方，只是给 JEI 的展示载体。 */
    public static final RecipeType<StructurePreview> STRUCTURE_PREVIEW =
            RecipeType.create(Inspiration.MODID, "structure_preview", StructurePreview.class);

    private ModJeiRecipeTypes() {
    }

    /** 虚空注入：只消耗团簇能量。 */
    public static final RecipeType<VoidInjectionRecipe> VOID_INJECTION =
            RecipeType.create(Inspiration.MODID, "void_injection", VoidInjectionRecipe.class);

    /** 虚空能量凝聚：固定行为，只有一条展示用记录。 */
    public static final RecipeType<VoidCondensationInfo> VOID_CONDENSATION =
            RecipeType.create(Inspiration.MODID, "void_condensation", VoidCondensationInfo.class);
}
