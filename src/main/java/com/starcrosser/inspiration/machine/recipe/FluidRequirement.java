package com.starcrosser.inspiration.machine.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

/**
 * 装配配方里的一条流体原料：一种流体 {@link FluidIngredient} 加一个用量（mB）。
 * <p>
 * 单独成记录而不是在配方里平铺两个字段，是为了让「两种流体原料」能写成列表 ——
 * 高能灵感装配台有两个流体输入槽，列表长度就是需要的罐数。
 */
public record FluidRequirement(FluidIngredient ingredient, int amount) {

    public static final MapCodec<FluidRequirement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FluidIngredient.CODEC.fieldOf("ingredient").forGetter(FluidRequirement::ingredient),
            Codec.INT.optionalFieldOf("amount", 0).forGetter(FluidRequirement::amount)
    ).apply(instance, FluidRequirement::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidRequirement> STREAM_CODEC = StreamCodec.composite(
            FluidIngredient.STREAM_CODEC, FluidRequirement::ingredient,
            ByteBufCodecs.VAR_INT, FluidRequirement::amount,
            FluidRequirement::new
    );

    /** 这条需求是否被罐内的流体满足：种类要对得上，量要够扣。 */
    public boolean matches(FluidStack stack) {
        if (amount <= 0 || stack.isEmpty()) {
            return false;
        }
        return ingredient.test(stack) && stack.getAmount() >= amount;
    }
}
