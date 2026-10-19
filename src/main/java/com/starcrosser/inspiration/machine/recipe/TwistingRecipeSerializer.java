package com.starcrosser.inspiration.machine.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * 矿物扭曲配方的序列化器：负责 JSON 解析与客户端同步。
 */
public class TwistingRecipeSerializer implements RecipeSerializer<TwistingRecipe> {

    @Override
    public MapCodec<TwistingRecipe> codec() {
        return TwistingRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, TwistingRecipe> streamCodec() {
        return StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, TwistingRecipe::input,
                ItemStack.STREAM_CODEC, TwistingRecipe::result,
                ByteBufCodecs.VAR_INT, TwistingRecipe::fortuneLevel,
                ByteBufCodecs.VAR_INT, TwistingRecipe::duration,
                ByteBufCodecs.VAR_INT, TwistingRecipe::energyPerTick,
                TwistingRecipe::new
        );
    }
}
