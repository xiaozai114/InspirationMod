package com.starcrosser.inspiration.recipe;


import com.starcrosser.inspiration.Inspiration;
import net.minecraft.core.registries.Registries;
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
}