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

import java.util.List;

/**
 * 原料精炼机配方：一份原料 → 一份主产物 + 最多两份副产物。
 *
 * <pre>
 * {
 *   "type": "inspiration:refining",
 *   "input": { "item": "minecraft:raw_iron" },
 *   "count": 1,                                    // 一次消耗几个，默认 1
 *   "result": { "id": "minecraft:iron_ingot", "count": 1 },
 *   "byproducts": [                                // 可省略；按顺序填进第 2、3 个输出槽
 *     { "id": "inspiration:nickel_nugget", "count": 1 }
 *   ],
 *   "duration": 160,                               // 可选
 *   "energy_per_tick": 160                         // 可选
 * }
 * </pre>
 * <p>
 * 副产物写成**列表**而不是两个独立字段：一来空列表就表示「没有副产物」，
 * 不用塞两个 {@code ItemStack.EMPTY}；二来 {@code StreamCodec.composite} 最多六个通道，
 * 主产物 + 两个副产物 + 输入 + 消耗量 + 耗时 + 耗电正好七个字段，写成列表才能压到六个。
 * <p>
 * 主产物走 {@link InspiredRecipe} 的 {@code result}，所以 {@code assemble} /
 * {@code getResultItem} / {@code getToastSymbol} 都不用重写。
 */
public class RefiningRecipe extends InspiredRecipe<RefiningRecipeInput> {

    public static final int DEFAULT_DURATION = 160;
    public static final int DEFAULT_ENERGY_PER_TICK = 160;

    public static final MapCodec<RefiningRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(RefiningRecipe::input),
            Codec.INT.optionalFieldOf("count", 1).forGetter(RefiningRecipe::inputCount),
            ItemStack.CODEC.fieldOf("result").forGetter(RefiningRecipe::result),
            ItemStack.CODEC.listOf().optionalFieldOf("byproducts", List.of())
                    .forGetter(RefiningRecipe::byproducts),
            Codec.INT.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(RefiningRecipe::duration),
            Codec.INT.optionalFieldOf("energy_per_tick", DEFAULT_ENERGY_PER_TICK)
                    .forGetter(RefiningRecipe::energyPerTick)
    ).apply(instance, RefiningRecipe::new));

    /** 一个配方最多给两份副产物，对应机器的第 2、3 个输出槽。 */
    public static final int MAX_BYPRODUCTS = 2;

    private final Ingredient input;
    private final int inputCount;
    private final List<ItemStack> byproducts;

    public RefiningRecipe(Ingredient input, int inputCount, ItemStack result, List<ItemStack> byproducts,
                          int duration, int energyPerTick) {
        super(result, duration, energyPerTick);
        this.input = input;
        this.inputCount = Math.max(1, inputCount);
        this.byproducts = byproducts.stream().limit(MAX_BYPRODUCTS).toList();
    }

    /** 一进一出的便捷构造（没有副产物）。 */
    public RefiningRecipe(Ingredient input, ItemStack result) {
        this(input, 1, result, List.of(), DEFAULT_DURATION, DEFAULT_ENERGY_PER_TICK);
    }

    @Override
    public boolean matches(RefiningRecipeInput input, Level level) {
        return !input.stack().isEmpty()
                && input.stack().getCount() >= inputCount
                && this.input.test(input.stack());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.REFINING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.REFINING_TYPE.get();
    }

    public Ingredient input() {
        return input;
    }

    public int inputCount() {
        return inputCount;
    }

    /** 全部副产物，长度 0~{@link #MAX_BYPRODUCTS}。 */
    public List<ItemStack> byproducts() {
        return byproducts;
    }

    /** 第 {@code index} 份副产物（从 0 数），没有就给空栈。 */
    public ItemStack byproduct(int index) {
        return index < byproducts.size() ? byproducts.get(index) : ItemStack.EMPTY;
    }
}
