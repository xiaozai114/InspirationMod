package com.starcrosser.inspiration.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class RightClickBlockRecipeSerializer implements RecipeSerializer<RightClickBlockRecipe> {

    // JSON 编解码器 (MapCodec)，用于解析 recipes/ 目录下的 JSON 文件
    // 它通过 RecordCodecBuilder 声明性地描述了如何将 JSON 字段映射到 Recipe 对象
    @Override
    public MapCodec<RightClickBlockRecipe> codec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("input_block").forGetter(RightClickBlockRecipe::inputBlock),
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("output_block").forGetter(RightClickBlockRecipe::outputBlock),
                ItemStack.CODEC.fieldOf("input_item").forGetter(RightClickBlockRecipe::inputItem),
                ItemStack.CODEC.optionalFieldOf("output_item", ItemStack.EMPTY).
                        forGetter(RightClickBlockRecipe::outputItem)
        ).apply(instance, RightClickBlockRecipe::new));
    }

    // 网络编解码器 (StreamCodec)，用于在网络中同步这个配方类型本身（如果需要的话）
    // 它同样声明性地描述了如何将对象数据写入/从 RegistryFriendlyByteBuf 读取
    @Override
    public StreamCodec<RegistryFriendlyByteBuf, RightClickBlockRecipe> streamCodec() {
        return StreamCodec.composite(
                ByteBufCodecs.registry(BuiltInRegistries.BLOCK.key()), RightClickBlockRecipe::inputBlock,
                ByteBufCodecs.registry(BuiltInRegistries.BLOCK.key()), RightClickBlockRecipe::outputBlock,
                ItemStack.STREAM_CODEC, RightClickBlockRecipe::inputItem,
                ByteBufCodecs.optional(ItemStack.STREAM_CODEC).map(
                        opt -> opt.orElse(ItemStack.EMPTY),
                        stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack)
                ), RightClickBlockRecipe::outputItem,
                RightClickBlockRecipe::new
        );
    }
}