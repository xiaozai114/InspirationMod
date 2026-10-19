package com.starcrosser.inspiration.machine.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.starcrosser.inspiration.recipe.ModRecipes;
import com.starcrosser.inspiration.util.OreFortuneBonus;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * 矿物扭曲机配方：把一块矿石扭出它对应的粗矿。
 * <p>
 * {@code result} 中的数量是基准产量，实际产出时再乘以按 {@code fortune_level}
 * 掷出的倍率（见 {@link OreFortuneBonus}），因此同一块矿石每次产出的数量并不固定。
 */
public class TwistingRecipe implements Recipe<TwistingRecipeInput> {

    public static final int DEFAULT_DURATION = 400;
    public static final int DEFAULT_ENERGY_PER_TICK = 300;

    /** 等效时运等级：默认 6。 */
    public static final int DEFAULT_FORTUNE_LEVEL = 6;

    public static final MapCodec<TwistingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(TwistingRecipe::input),
            ItemStack.CODEC.fieldOf("result").forGetter(TwistingRecipe::result),
            Codec.INT.optionalFieldOf("fortune_level", DEFAULT_FORTUNE_LEVEL).forGetter(TwistingRecipe::fortuneLevel),
            Codec.INT.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(TwistingRecipe::duration),
            Codec.INT.optionalFieldOf("energy_per_tick", DEFAULT_ENERGY_PER_TICK).forGetter(TwistingRecipe::energyPerTick)
    ).apply(instance, TwistingRecipe::new));

    private final Ingredient input;
    private final ItemStack result;
    private final int fortuneLevel;
    private final int duration;
    private final int energyPerTick;

    public TwistingRecipe(Ingredient input, ItemStack result, int fortuneLevel, int duration, int energyPerTick) {
        this.input = input;
        this.result = result;
        this.fortuneLevel = fortuneLevel;
        this.duration = duration;
        this.energyPerTick = energyPerTick;
    }

    /** 使用默认等效时运与默认耗时 / 能耗的便捷构造。 */
    public TwistingRecipe(Ingredient input, ItemStack result) {
        this(input, result, DEFAULT_FORTUNE_LEVEL, DEFAULT_DURATION, DEFAULT_ENERGY_PER_TICK);
    }

    @Override
    public boolean matches(TwistingRecipeInput input, Level level) {
        return !input.stack().isEmpty() && this.input.test(input.stack());
    }

    @Override
    public ItemStack assemble(TwistingRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public ItemStack getToastSymbol() {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TWISTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.TWISTING_TYPE.get();
    }

    public Ingredient input() {
        return input;
    }

    /** 基准产物，数量为 1 次扭曲的保底产量。 */
    public ItemStack result() {
        return result;
    }

    public int fortuneLevel() {
        return fortuneLevel;
    }

    public int duration() {
        return duration;
    }

    public int energyPerTick() {
        return energyPerTick;
    }

    /* ============================ 产量区间 ============================ */

    /** 一次扭曲的保底产量。 */
    public int minResultCount() {
        return result.getCount() * OreFortuneBonus.minMultiplier(fortuneLevel);
    }

    /** 一次扭曲的上限产量，同时用于判断输出槽能否容纳。 */
    public int maxResultCount() {
        return result.getCount() * OreFortuneBonus.maxMultiplier(fortuneLevel);
    }

    /** 一次扭曲的期望产量，仅用于界面展示。 */
    public double averageResultCount() {
        return result.getCount() * OreFortuneBonus.averageMultiplier(fortuneLevel);
    }

    /** 按当前等效时运掷出本次产出数量。 */
    public int rollResultCount(RandomSource random) {
        return result.getCount() * OreFortuneBonus.rollMultiplier(random, fortuneLevel);
    }
}
