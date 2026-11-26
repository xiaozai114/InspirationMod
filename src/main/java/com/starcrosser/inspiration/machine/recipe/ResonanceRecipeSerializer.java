package com.starcrosser.inspiration.machine.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ResonanceRecipeSerializer implements RecipeSerializer<ResonanceRecipe> {
    private static final StreamCodec<RegistryFriendlyByteBuf, ResonanceRecipe> STREAM = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, ResonanceRecipe::input,
            ItemStack.STREAM_CODEC, ResonanceRecipe::result,
            ByteBufCodecs.VAR_INT, ResonanceRecipe::duration,
            ByteBufCodecs.VAR_INT, ResonanceRecipe::energyPerTick,
            ResonanceRecipe::new);

    @Override
    public MapCodec<ResonanceRecipe> codec() {
        return ResonanceRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ResonanceRecipe> streamCodec() {
        return STREAM;
    }
}
