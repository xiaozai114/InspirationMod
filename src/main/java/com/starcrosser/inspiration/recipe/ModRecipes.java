package com.starcrosser.inspiration.recipe;


import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.injection.recipe.InjectionRecipe;
import com.starcrosser.inspiration.injection.recipe.InjectionRecipeSerializer;
import com.starcrosser.inspiration.machine.recipe.AssemblingRecipe;
import com.starcrosser.inspiration.machine.recipe.AssemblingRecipeSerializer;
import com.starcrosser.inspiration.machine.recipe.FluidExtractingRecipe;
import com.starcrosser.inspiration.machine.recipe.FluidExtractingRecipeSerializer;
import com.starcrosser.inspiration.machine.recipe.FusionRecipe;
import com.starcrosser.inspiration.machine.recipe.FusionRecipeSerializer;
import com.starcrosser.inspiration.machine.recipe.RefiningRecipe;
import com.starcrosser.inspiration.machine.recipe.RefiningRecipeSerializer;
import com.starcrosser.inspiration.machine.recipe.ReshapingRecipe;
import com.starcrosser.inspiration.machine.recipe.ReshapingRecipeSerializer;
import com.starcrosser.inspiration.machine.recipe.TwistingRecipe;
import com.starcrosser.inspiration.machine.recipe.TwistingRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Inspiration.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Inspiration.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RightClickBlockRecipe>> RIGHT_CLICK_BLOCK_SERIALIZER =
            SERIALIZERS.register("right_click_block", RightClickBlockRecipeSerializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<RightClickBlockRecipe>> RIGHT_CLICK_BLOCK_TYPE =
            TYPES.register("right_click_block", () -> new RecipeType<>() {});

    // 物质融合器配方
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FusionRecipe>> FUSION_SERIALIZER =
            SERIALIZERS.register("fusion", FusionRecipeSerializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<FusionRecipe>> FUSION_TYPE =
            TYPES.register("fusion", () -> RecipeType.simple(
                    ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "fusion")));

    // 物质重塑机配方
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ReshapingRecipe>> RESHAPING_SERIALIZER =
            SERIALIZERS.register("reshaping", ReshapingRecipeSerializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<ReshapingRecipe>> RESHAPING_TYPE =
            TYPES.register("reshaping", () -> RecipeType.simple(
                    ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "reshaping")));

    // 矿物扭曲机配方
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TwistingRecipe>> TWISTING_SERIALIZER =
            SERIALIZERS.register("twisting", TwistingRecipeSerializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<TwistingRecipe>> TWISTING_TYPE =
            TYPES.register("twisting", () -> RecipeType.simple(
                    ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "twisting")));

    // 灵感装配台配方
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AssemblingRecipe>> ASSEMBLING_SERIALIZER =
            SERIALIZERS.register("assembling", AssemblingRecipeSerializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<AssemblingRecipe>> ASSEMBLING_TYPE =
            TYPES.register("assembling", () -> RecipeType.simple(
                    ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "assembling")));

    // 流体提取机配方
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FluidExtractingRecipe>>
            FLUID_EXTRACTING_SERIALIZER =
            SERIALIZERS.register("fluid_extracting", FluidExtractingRecipeSerializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<FluidExtractingRecipe>> FLUID_EXTRACTING_TYPE =
            TYPES.register("fluid_extracting", () -> RecipeType.simple(
                    ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "fluid_extracting")));

    // 原料精炼机配方
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RefiningRecipe>> REFINING_SERIALIZER =
            SERIALIZERS.register("refining", RefiningRecipeSerializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<RefiningRecipe>> REFINING_TYPE =
            TYPES.register("refining", () -> RecipeType.simple(
                    ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "refining")));

    // 注入配方（物质束缚器 + 注入枪）
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InjectionRecipe>> INJECTION_SERIALIZER =
            SERIALIZERS.register("injection", InjectionRecipeSerializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<InjectionRecipe>> INJECTION_TYPE =
            TYPES.register("injection", () -> RecipeType.simple(
                    ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "injection")));
}