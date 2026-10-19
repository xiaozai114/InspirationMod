package com.starcrosser.inspiration.machine.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * 物质融合配方的序列化器：负责 JSON 解析与客户端同步。
 * <p>
 * 字段有 9 个，超过了 {@code StreamCodec.composite} 支持的上限，因此手写编解码。
 * 编码顺序必须与 {@link FusionRecipe} 的构造参数顺序一一对应。
 */
public class FusionRecipeSerializer implements RecipeSerializer<FusionRecipe> {

    @Override
    public MapCodec<FusionRecipe> codec() {
        return FusionRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FusionRecipe> streamCodec() {
        return StreamCodec.of(FusionRecipeSerializer::encode, FusionRecipeSerializer::decode);
    }

    private static void encode(RegistryFriendlyByteBuf buffer, FusionRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.firstInput());
        buffer.writeVarInt(recipe.firstCount());
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.secondInput());
        buffer.writeVarInt(recipe.secondCount());
        ItemStack.STREAM_CODEC.encode(buffer, recipe.result());
        buffer.writeVarInt(recipe.duration());
        buffer.writeVarInt(recipe.energyPerTick());
        // 第三个输入槽给强能物质融合机用；没有写 third_input 时同步 Ingredient.EMPTY
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.thirdInput());
        buffer.writeVarInt(recipe.thirdCount());
    }

    private static FusionRecipe decode(RegistryFriendlyByteBuf buffer) {
        Ingredient firstInput = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        int firstCount = buffer.readVarInt();
        Ingredient secondInput = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        int secondCount = buffer.readVarInt();
        ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
        int duration = buffer.readVarInt();
        int energyPerTick = buffer.readVarInt();
        Ingredient thirdInput = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        int thirdCount = buffer.readVarInt();
        return new FusionRecipe(firstInput, firstCount, secondInput, secondCount,
                result, duration, energyPerTick, thirdInput, thirdCount);
    }
}
