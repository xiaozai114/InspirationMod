package com.starcrosser.inspiration.injection.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * 注入配方的序列化器。
 * <p>
 * 字段有 8 个，超过了 {@code StreamCodec.composite} 支持的上限，因此手写编解码。
 * 编码顺序必须与 {@link InjectionRecipe} 的构造参数顺序一一对应。
 */
public class InjectionRecipeSerializer implements RecipeSerializer<InjectionRecipe> {

    @Override
    public MapCodec<InjectionRecipe> codec() {
        return InjectionRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, InjectionRecipe> streamCodec() {
        return StreamCodec.of(InjectionRecipeSerializer::encode, InjectionRecipeSerializer::decode);
    }

    private static void encode(RegistryFriendlyByteBuf buffer, InjectionRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.base());
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.inject());
        buffer.writeVarInt(recipe.tier());
        buffer.writeVarInt(recipe.minEnergy());
        buffer.writeVarInt(recipe.maxEnergy());
        ItemStack.STREAM_CODEC.encode(buffer, recipe.result());
        buffer.writeVarInt(recipe.duration());
        buffer.writeBoolean(recipe.seeded());
    }

    private static InjectionRecipe decode(RegistryFriendlyByteBuf buffer) {
        Ingredient base = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        Ingredient inject = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        int tier = buffer.readVarInt();
        int minEnergy = buffer.readVarInt();
        int maxEnergy = buffer.readVarInt();
        ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
        int duration = buffer.readVarInt();
        boolean seeded = buffer.readBoolean();
        return new InjectionRecipe(base, inject, tier, minEnergy, maxEnergy, result, duration, seeded);
    }
}
