package com.starcrosser.inspiration.machine.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;

/**
 * 装配配方的序列化器：负责 JSON 解析与客户端同步。
 * <p>
 * 材料与流体原料都是可变长列表，网络上先写长度再逐个写元素；
 * 流体原料每条是「种类 + 用量」两个字段，由 {@link FluidRequirement#STREAM_CODEC} 承载。
 */
public class AssemblingRecipeSerializer implements RecipeSerializer<AssemblingRecipe> {

    @Override
    public MapCodec<AssemblingRecipe> codec() {
        return AssemblingRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AssemblingRecipe> streamCodec() {
        StreamCodec<RegistryFriendlyByteBuf, List<Ingredient>> ingredients =
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list());
        StreamCodec<RegistryFriendlyByteBuf, List<FluidRequirement>> fluids =
                FluidRequirement.STREAM_CODEC.apply(ByteBufCodecs.list());
        // ByteBufCodecs.VAR_INT 的 buf 类型是 ByteBuf，而这里整条链是 RegistryFriendlyByteBuf，
        // 泛型不变型不能直接套用，所以先包一层
        StreamCodec<RegistryFriendlyByteBuf, Integer> varInt = StreamCodec.of(
                (buf, value) -> buf.writeVarInt(value), FriendlyByteBuf::readVarInt);
        StreamCodec<RegistryFriendlyByteBuf, List<Integer>> counts = varInt.apply(ByteBufCodecs.list());
        // 六种字段正好是 StreamCodec.composite 的最大重载数，再多一种就要手写 of(写, 读)
        return StreamCodec.composite(
                ingredients, AssemblingRecipe::ingredients,
                counts, AssemblingRecipe::counts,
                fluids, AssemblingRecipe::fluids,
                ItemStack.STREAM_CODEC, AssemblingRecipe::result,
                ByteBufCodecs.VAR_INT, AssemblingRecipe::duration,
                ByteBufCodecs.VAR_INT, AssemblingRecipe::energyPerTick,
                AssemblingRecipe::new
        );
    }
}
