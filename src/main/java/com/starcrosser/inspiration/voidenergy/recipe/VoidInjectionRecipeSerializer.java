package com.starcrosser.inspiration.voidenergy.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class VoidInjectionRecipeSerializer implements RecipeSerializer<VoidInjectionRecipe> {

    @Override
    public MapCodec<VoidInjectionRecipe> codec() {
        return VoidInjectionRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, VoidInjectionRecipe> streamCodec() {
        return VoidInjectionRecipe.STREAM_CODEC;
    }
}
