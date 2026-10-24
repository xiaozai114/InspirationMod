package com.starcrosser.inspiration.machine.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;

/**
 * 原料精炼配方的序列化器：JSON 解析与客户端同步。
 * <p>
 * 副产物是变长列表，网络上先写长度再逐个写元素。六个通道正好是
 * {@code StreamCodec.composite} 的最大重载数，再多一种字段就得手写 {@code of(写, 读)}。
 */
public class RefiningRecipeSerializer implements RecipeSerializer<RefiningRecipe> {

    @Override
    public MapCodec<RefiningRecipe> codec() {
        return RefiningRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, RefiningRecipe> streamCodec() {
        StreamCodec<RegistryFriendlyByteBuf, List<ItemStack>> byproducts =
                ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list());
        return StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, RefiningRecipe::input,
                ByteBufCodecs.VAR_INT, RefiningRecipe::inputCount,
                ItemStack.STREAM_CODEC, RefiningRecipe::result,
                byproducts, RefiningRecipe::byproducts,
                ByteBufCodecs.VAR_INT, RefiningRecipe::duration,
                ByteBufCodecs.VAR_INT, RefiningRecipe::energyPerTick,
                RefiningRecipe::new
        );
    }
}
