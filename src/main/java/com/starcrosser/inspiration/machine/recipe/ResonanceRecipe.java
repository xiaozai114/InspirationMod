package com.starcrosser.inspiration.machine.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.starcrosser.inspiration.recipe.ModRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class ResonanceRecipe extends InspiredRecipe<SingleRecipeInput> {
    public static final int DURATION = 200;
    public static final int FE_PER_TICK = 500;
    public static final int VE_PER_TICK = 2;
    public static final MapCodec<ResonanceRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(ResonanceRecipe::input),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ResonanceRecipe::result),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("duration", DURATION).forGetter(ResonanceRecipe::duration),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("energy_per_tick", FE_PER_TICK).forGetter(ResonanceRecipe::energyPerTick)
    ).apply(instance, ResonanceRecipe::new));

    private final Ingredient input;

    public ResonanceRecipe(Ingredient input, ItemStack result, int duration, int energyPerTick) {
        super(result, duration, energyPerTick);
        this.input = input;
    }

    public Ingredient input() {
        return input;
    }

    @Override
    public boolean matches(SingleRecipeInput slot, Level level) {
        return input.test(slot.item());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.RESONANCE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RESONANCE_TYPE.get();
    }
}
