package com.starcrosser.inspiration.client.jei;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.client.jei.category.AssemblingRecipeCategory;
import com.starcrosser.inspiration.client.jei.category.FusionRecipeCategory;
import com.starcrosser.inspiration.client.jei.category.InjectionRecipeCategory;
import com.starcrosser.inspiration.client.jei.category.ReshapingRecipeCategory;
import com.starcrosser.inspiration.client.jei.category.RightClickBlockRecipeCategory;
import com.starcrosser.inspiration.client.jei.category.StructurePreviewCategory;
import com.starcrosser.inspiration.client.jei.category.TwistingRecipeCategory;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import com.starcrosser.inspiration.multiblock.pattern.SimulatorPattern;
import com.starcrosser.inspiration.recipe.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;
import java.util.stream.IntStream;

/**
 * JEI 插件入口。
 * <p>
 * 只负责把本模组的自定义配方类型接到 JEI 上：注册分类、灌入配方、声明催化剂。
 * 具体界面绘制交给 {@code category} 子包下的各个分类。
 */
@JeiPlugin
public class InspirationJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new AssemblingRecipeCategory(guiHelper),
                new FusionRecipeCategory(guiHelper),
                new InjectionRecipeCategory(guiHelper),
                new ReshapingRecipeCategory(guiHelper),
                new RightClickBlockRecipeCategory(guiHelper),
                new StructurePreviewCategory(guiHelper),
                new TwistingRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        RecipeManager recipeManager = level.getRecipeManager();
        registration.addRecipes(ModJeiRecipeTypes.FUSION,
                collect(recipeManager, ModRecipes.FUSION_TYPE.get()));
        registration.addRecipes(ModJeiRecipeTypes.RESHAPING,
                collect(recipeManager, ModRecipes.RESHAPING_TYPE.get()));
        registration.addRecipes(ModJeiRecipeTypes.RIGHT_CLICK_BLOCK,
                collect(recipeManager, ModRecipes.RIGHT_CLICK_BLOCK_TYPE.get()));
        registration.addRecipes(ModJeiRecipeTypes.TWISTING,
                collect(recipeManager, ModRecipes.TWISTING_TYPE.get()));
        registration.addRecipes(ModJeiRecipeTypes.ASSEMBLING,
                collect(recipeManager, ModRecipes.ASSEMBLING_TYPE.get()));
        registration.addRecipes(ModJeiRecipeTypes.INJECTION,
                collect(recipeManager, ModRecipes.INJECTION_TYPE.get()));
        // 结构预览不是数据包配方：层数直接由形状常量决定，一层一条，翻页即翻层
        registration.addRecipes(ModJeiRecipeTypes.STRUCTURE_PREVIEW,
                IntStream.range(0, SimulatorPattern.depth())
                        .mapToObj(StructurePreview::new)
                        .toList());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModMachines.MATTER_FUSION_MACHINE, ModJeiRecipeTypes.FUSION);
        // 强能机型能做全部融合配方，三材料的那几条只有它能执行
        registration.addRecipeCatalyst(ModMachines.ENERGIZED_MATTER_FUSION_MACHINE, ModJeiRecipeTypes.FUSION);
        registration.addRecipeCatalyst(ModMachines.MATTER_RESHAPER, ModJeiRecipeTypes.RESHAPING);
        // 物质重塑机同样能执行全部右键转化配方
        registration.addRecipeCatalyst(ModMachines.MATTER_RESHAPER, ModJeiRecipeTypes.RIGHT_CLICK_BLOCK);
        // 高能机型能做全部重塑 / 右键转化配方，界面里显示的仍是配方基准值
        registration.addRecipeCatalyst(ModMachines.HIGH_ENERGY_MATTER_RESHAPER, ModJeiRecipeTypes.RESHAPING);
        registration.addRecipeCatalyst(ModMachines.HIGH_ENERGY_MATTER_RESHAPER, ModJeiRecipeTypes.RIGHT_CLICK_BLOCK);
        registration.addRecipeCatalyst(ModMachines.MINERAL_TWISTER, ModJeiRecipeTypes.TWISTING);
        // 高能机型能做全部扭曲配方，但等效时运是 12（界面里显示的是配方基准值 6）
        registration.addRecipeCatalyst(ModMachines.HIGH_ENERGY_MINERAL_TWISTER, ModJeiRecipeTypes.TWISTING);
        registration.addRecipeCatalyst(ModMachines.INSPIRATION_ASSEMBLER, ModJeiRecipeTypes.ASSEMBLING);
        // 高能装配台能做全部装配配方，五种以上材料 / 两种流体的那几条只有它能执行
        registration.addRecipeCatalyst(ModMachines.HIGH_ENERGY_INSPIRATION_ASSEMBLER,
                ModJeiRecipeTypes.ASSEMBLING);
        // 注入的催化剂同时给出束缚器与注入枪，缺一不可
        registration.addRecipeCatalyst(ModInjection.MATTER_BINDER, ModJeiRecipeTypes.INJECTION);
        registration.addRecipeCatalyst(ModInjection.INJECTION_GUN, ModJeiRecipeTypes.INJECTION);
        // 结构预览挂在结构模拟机的用途里：在 JEI 里对着控制器按 U 就能看到结构
        registration.addRecipeCatalyst(ModMultiblocks.STRUCTURE_SIMULATOR,
                ModJeiRecipeTypes.STRUCTURE_PREVIEW);
    }

    /** 从配方管理器取出某一类型的全部配方（去掉 RecipeHolder 外壳）。 */
    private static <I extends RecipeInput, T extends Recipe<I>> List<T> collect(
            RecipeManager recipeManager, RecipeType<T> type) {
        return recipeManager.getAllRecipesFor(type).stream()
                .map(RecipeHolder::value)
                .toList();
    }
}
