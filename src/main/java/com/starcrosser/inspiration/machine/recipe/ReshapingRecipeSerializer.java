package com.starcrosser.inspiration.machine.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Optional;

/**
 * 物质重塑配方的序列化器：负责 JSON 解析与客户端同步。
 * <p>
 * 辅助材料在网络上用「是否存在 + 内容」两段表示，避免 {@code Ingredient} 没有空值编码的问题。
 */
public class ReshapingRecipeSerializer implements RecipeSerializer<ReshapingRecipe> {

    @Override
    public MapCodec<ReshapingRecipe> codec() {
        return ReshapingRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ReshapingRecipe> streamCodec() {
        StreamCodec<RegistryFriendlyByteBuf, Optional<Ingredient>> optionalSecond =
                ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC);
        return StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, ReshapingRecipe::input,
                optionalSecond, ReshapingRecipe::secondInput,
                ItemStack.STREAM_CODEC, ReshapingRecipe::result,
                ByteBufCodecs.VAR_INT, ReshapingRecipe::inputCount,
                ByteBufCodecs.VAR_INT, ReshapingRecipe::duration,
                ByteBufCodecs.VAR_INT, ReshapingRecipe::energyPerTick,
                ReshapingRecipe::new
        );
    }
}
