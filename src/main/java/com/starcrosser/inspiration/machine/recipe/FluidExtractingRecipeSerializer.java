package com.starcrosser.inspiration.machine.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

/** 流体提取配方的序列化器：JSON 解析与客户端同步。 */
public class FluidExtractingRecipeSerializer implements RecipeSerializer<FluidExtractingRecipe> {

    @Override
    public MapCodec<FluidExtractingRecipe> codec() {
        return FluidExtractingRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FluidExtractingRecipe> streamCodec() {
        return StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, FluidExtractingRecipe::input,
                ByteBufCodecs.VAR_INT, FluidExtractingRecipe::inputCount,
                FluidStack.STREAM_CODEC, FluidExtractingRecipe::result,
                ByteBufCodecs.VAR_INT, FluidExtractingRecipe::duration,
                ByteBufCodecs.VAR_INT, FluidExtractingRecipe::energyPerTick,
                FluidExtractingRecipe::new
        );
    }
}
