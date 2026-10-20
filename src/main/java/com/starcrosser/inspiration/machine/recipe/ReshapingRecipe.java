package com.starcrosser.inspiration.machine.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.starcrosser.inspiration.recipe.ModRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 物质重塑机配方：把一种物质重塑成另一种。
 * <p>
 * 主输入必填，辅助材料可选；配方按槽位固定匹配，不做顺序交换，
 * 以免「主材料 / 辅助材料」的语义在运行时变得不确定。
 * <p>
 * 一次消耗的主材料数量由 {@code input_count} 决定（默认 1），
 * 用于表达「3 个红石 -> 1 个红石锭」这类需要堆叠数量的转化。
 */
public class ReshapingRecipe extends InspiredRecipe<ReshapingRecipeInput> {

    public static final int DEFAULT_DURATION = 300;
    public static final int DEFAULT_ENERGY_PER_TICK = 120;

    /** 默认每次只消耗一个主材料。 */
    public static final int DEFAULT_INPUT_COUNT = 1;

    public static final MapCodec<ReshapingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(ReshapingRecipe::input),
            Ingredient.CODEC_NONEMPTY.optionalFieldOf("second_input").forGetter(ReshapingRecipe::secondInput),
            ItemStack.CODEC.fieldOf("result").forGetter(ReshapingRecipe::result),
            Codec.INT.optionalFieldOf("input_count", DEFAULT_INPUT_COUNT).forGetter(ReshapingRecipe::inputCount),
            Codec.INT.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(ReshapingRecipe::duration),
            Codec.INT.optionalFieldOf("energy_per_tick", DEFAULT_ENERGY_PER_TICK).forGetter(ReshapingRecipe::energyPerTick)
    ).apply(instance, ReshapingRecipe::new));

    private final Ingredient input;
    @Nullable
    private final Ingredient secondInput;
    private final int inputCount;

    public ReshapingRecipe(Ingredient input, Optional<Ingredient> secondInput, ItemStack result,
                           int inputCount, int duration, int energyPerTick) {
        super(result, duration, energyPerTick);
        this.input = input;
        this.secondInput = secondInput.orElse(null);
        this.inputCount = Math.max(inputCount, DEFAULT_INPUT_COUNT);
    }

    /** 单输入配方的便捷构造。 */
    public ReshapingRecipe(Ingredient input, ItemStack result) {
        this(input, Optional.empty(), result, DEFAULT_INPUT_COUNT, DEFAULT_DURATION, DEFAULT_ENERGY_PER_TICK);
    }

    @Override
    public boolean matches(ReshapingRecipeInput input, Level level) {
        if (input.first().isEmpty() || !this.input.test(input.first())) {
            return false;
        }
        if (secondInput == null) {
            // 单输入配方忽略第二个槽位
            return true;
        }
        return !input.second().isEmpty() && secondInput.test(input.second());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.RESHAPING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RESHAPING_TYPE.get();
    }

    public Ingredient input() {
        return input;
    }

    public Optional<Ingredient> secondInput() {
        return Optional.ofNullable(secondInput);
    }

    /** 配方是否需要辅助材料。 */
    public boolean hasSecondInput() {
        return secondInput != null;
    }

    public int inputCount() {
        return inputCount;
    }
}
