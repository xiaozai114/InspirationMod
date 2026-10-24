package com.starcrosser.inspiration.machine.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.recipe.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * 流体提取机配方：把材料熔成对应的液态金属。
 *
 * <pre>
 * {
 *   "type": "inspiration:fluid_extracting",
 *   "input": { "item": "inspiration:plasma_ingot" },
 *   "count": 1,                                  // 一次熔几个，默认 1
 *   "result": { "fluid": "inspiration:molten_plasma", "amount": 144 },
 *   "duration": 200,                             // 可选
 *   "energy_per_tick": 200                       // 可选
 * }
 * </pre>
 * <p>
 * 比例按「一锭 = 144 mB」记：144 是 9 的倍数，所以九粒（16 mB）凑一锭、一块（1296 mB）拆成九锭，
 * 与材料系统里 9 粒 = 1 锭、9 锭 = 1 块的换算一致。
 * <p>
 * 不复用 {@link InspiredRecipe}：那个基类的产物是 {@link ItemStack}，而这里的产物是流体，
 * 强行塞一个空物品栈进去会让 {@code getToastSymbol} 这类方法失去意义。
 */
public class FluidExtractingRecipe implements Recipe<FluidExtractingRecipeInput> {

    public static final int DEFAULT_DURATION = 200;
    public static final int DEFAULT_ENERGY_PER_TICK = 200;

    /** 一锭对应的流体量（mB）。 */
    public static final int INGOT_AMOUNT = 144;

    /** 一粒对应的流体量（mB）。 */
    public static final int NUGGET_AMOUNT = INGOT_AMOUNT / 9;

    /** 一块对应的流体量（mB）。 */
    public static final int BLOCK_AMOUNT = INGOT_AMOUNT * 9;

    public static final MapCodec<FluidExtractingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(FluidExtractingRecipe::input),
            Codec.INT.optionalFieldOf("count", 1).forGetter(FluidExtractingRecipe::inputCount),
            FluidStack.CODEC.fieldOf("result").forGetter(FluidExtractingRecipe::result),
            Codec.INT.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(FluidExtractingRecipe::duration),
            Codec.INT.optionalFieldOf("energy_per_tick", DEFAULT_ENERGY_PER_TICK)
                    .forGetter(FluidExtractingRecipe::energyPerTick)
    ).apply(instance, FluidExtractingRecipe::new));

    private final Ingredient input;
    private final int inputCount;
    private final FluidStack result;
    private final int duration;
    private final int energyPerTick;

    public FluidExtractingRecipe(Ingredient input, int inputCount, FluidStack result,
                                 int duration, int energyPerTick) {
        this.input = input;
        this.inputCount = Math.max(1, inputCount);
        this.result = result;
        this.duration = duration;
        this.energyPerTick = energyPerTick;
    }

    /** 一次熔一个、用默认耗时与能耗的便捷构造。 */
    public FluidExtractingRecipe(Ingredient input, FluidStack result) {
        this(input, 1, result, DEFAULT_DURATION, DEFAULT_ENERGY_PER_TICK);
    }

    @Override
    public boolean matches(FluidExtractingRecipeInput input, Level level) {
        return !input.stack().isEmpty()
                && input.stack().getCount() >= inputCount
                && this.input.test(input.stack());
    }

    /** 流体配方不产出物品，界面里的「结果物品」留空。 */
    @Override
    public ItemStack assemble(FluidExtractingRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    /** 配方书里的图标只能给物品，用桶顶着。 */
    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModItems.MOLTEN_PLASMA_BUCKET.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FLUID_EXTRACTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.FLUID_EXTRACTING_TYPE.get();
    }

    public Ingredient input() {
        return input;
    }

    public int inputCount() {
        return inputCount;
    }

    public FluidStack result() {
        return result;
    }

    public int duration() {
        return duration;
    }

    public int energyPerTick() {
        return energyPerTick;
    }
}
