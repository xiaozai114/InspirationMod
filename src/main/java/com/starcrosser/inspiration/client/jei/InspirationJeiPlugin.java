package com.starcrosser.inspiration.client.jei;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.client.jei.category.AssemblingRecipeCategory;
import com.starcrosser.inspiration.client.jei.category.FluidExtractingRecipeCategory;
import com.starcrosser.inspiration.client.jei.category.FusionRecipeCategory;
import com.starcrosser.inspiration.client.jei.category.InjectionRecipeCategory;
import com.starcrosser.inspiration.client.jei.category.RefiningRecipeCategory;
import com.starcrosser.inspiration.client.jei.category.ReshapingRecipeCategory;
import com.starcrosser.inspiration.client.jei.category.ResonanceCategory;
import com.starcrosser.inspiration.client.jei.category.RadiationAbsorptionCategory;
import com.starcrosser.inspiration.client.jei.category.RightClickBlockRecipeCategory;
import com.starcrosser.inspiration.client.jei.category.StructurePreviewCategory;
import com.starcrosser.inspiration.client.jei.category.StructureProductionCategory;
import com.starcrosser.inspiration.client.jei.category.MineralProductionCategory;
import com.starcrosser.inspiration.client.jei.category.TwistingRecipeCategory;
import com.starcrosser.inspiration.client.jei.category.VoidCondensationCategory;
import com.starcrosser.inspiration.client.jei.category.VoidInjectionCategory;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import com.starcrosser.inspiration.multiblock.MultiblockKind;
import com.starcrosser.inspiration.multiblock.data.DimensionDataItem;
import com.starcrosser.inspiration.multiblock.data.StructureDataItem;
import com.starcrosser.inspiration.recipe.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;
import java.util.Arrays;

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
                new FluidExtractingRecipeCategory(guiHelper),
                new FusionRecipeCategory(guiHelper),
                new InjectionRecipeCategory(guiHelper),
                new RefiningRecipeCategory(guiHelper),
                new ReshapingRecipeCategory(guiHelper),
                new ResonanceCategory(guiHelper),
                new RadiationAbsorptionCategory(guiHelper),
                new RightClickBlockRecipeCategory(guiHelper),
                new StructurePreviewCategory(guiHelper),
                new StructureProductionCategory(guiHelper),
                new MineralProductionCategory(guiHelper),
                new TwistingRecipeCategory(guiHelper),
                new VoidInjectionCategory(guiHelper),
                new VoidCondensationCategory(guiHelper));
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
        registration.addRecipes(ModJeiRecipeTypes.RADIATION_ABSORPTION, List.of(
                ModItems.NUCLEAR_WASTE.get().getDefaultInstance()));
        registration.addRecipes(ModJeiRecipeTypes.RESONANCE, collect(recipeManager, ModRecipes.RESONANCE_TYPE.get()));
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
        registration.addRecipes(ModJeiRecipeTypes.FLUID_EXTRACTING,
                collect(recipeManager, ModRecipes.FLUID_EXTRACTING_TYPE.get()));
        registration.addRecipes(ModJeiRecipeTypes.REFINING,
                collect(recipeManager, ModRecipes.REFINING_TYPE.get()));
        registration.addRecipes(ModJeiRecipeTypes.VOID_INJECTION,
                collect(recipeManager, ModRecipes.VOID_INJECTION_TYPE.get()));
        // 虚空凝聚没有数据包配方：凝聚器是固定行为，用一条静态记录展示数值
        registration.addRecipes(ModJeiRecipeTypes.VOID_CONDENSATION,
                List.of(new com.starcrosser.inspiration.voidenergy.recipe.VoidCondensationInfo(
                        new net.minecraft.world.item.ItemStack(
                                com.starcrosser.inspiration.item.ModItems.VOID_DEW.get()),
                        com.starcrosser.inspiration.machine.blockentity.VoidCondenserTile.VE_PER_TICK,
                        com.starcrosser.inspiration.machine.blockentity.VoidCondenserTile.VE_PER_DEW,
                        com.starcrosser.inspiration.machine.blockentity.VoidCondenserTile.ENERGY_PER_TICK)));
        registration.addRecipes(ModJeiRecipeTypes.STRUCTURE_PREVIEW,
                Arrays.stream(MultiblockKind.values()).map(StructurePreview::of).toList());
        registration.addRecipes(ModJeiRecipeTypes.STRUCTURE_PRODUCTION,
                BuiltInRegistries.ITEM.stream().filter(StructureDataItem.class::isInstance)
                        .map(StructureDataItem.class::cast).toList());
        registration.addRecipes(ModJeiRecipeTypes.MINERAL_PRODUCTION,
                BuiltInRegistries.ITEM.stream().filter(DimensionDataItem.class::isInstance)
                        .map(DimensionDataItem.class::cast).flatMap(card -> MineralProductionPage.forCard(card).stream())
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
        registration.addRecipeCatalyst(ModMachines.FLUID_EXTRACTOR, ModJeiRecipeTypes.FLUID_EXTRACTING);
        registration.addRecipeCatalyst(ModMachines.RAW_REFINER, ModJeiRecipeTypes.REFINING);
        registration.addRecipeCatalyst(ModMultiblocks.STRUCTURE_SIMULATOR,
                ModJeiRecipeTypes.STRUCTURE_PRODUCTION);
        registration.addRecipeCatalyst(ModMultiblocks.MINERAL_SIMULATOR,
                ModJeiRecipeTypes.MINERAL_PRODUCTION);
        // 虚空注入同样要「束缚器 + 枪」两件套，只是枪换成虚空版
        registration.addRecipeCatalyst(ModInjection.MATTER_BINDER, ModJeiRecipeTypes.VOID_INJECTION);
        registration.addRecipeCatalyst(ModInjection.VOID_INJECTION_GUN, ModJeiRecipeTypes.VOID_INJECTION);
        registration.addRecipeCatalyst(ModMachines.RADIATION_ABSORBER, ModJeiRecipeTypes.RADIATION_ABSORPTION);
        registration.addRecipeCatalyst(ModMachines.RESONANCE_CHAMBER, ModJeiRecipeTypes.RESONANCE);
        registration.addRecipeCatalyst(ModMachines.VOID_CONDENSER, ModJeiRecipeTypes.VOID_CONDENSATION);
    }

    /** 从配方管理器取出某一类型的全部配方（去掉 RecipeHolder 外壳）。 */
    private static <I extends RecipeInput, T extends Recipe<I>> List<T> collect(
            RecipeManager recipeManager, RecipeType<T> type) {
        return recipeManager.getAllRecipesFor(type).stream()
                .map(RecipeHolder::value)
                .toList();
    }
}
