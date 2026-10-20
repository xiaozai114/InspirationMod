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
 * 物质融合器配方：多种材料融合成一种产物。
 * <p>
 * 输入槽的顺序不影响匹配，玩家可以从任意槽位放入材料。
 * {@code third_input} 是给**强能物质融合机**留的第三个槽位：老机型只有两个输入槽，
 * 素材数量为 2 与 3 的配方互不通用，因此 3 材料配方天然只有三槽机能做。
 * <p>
 * 每种材料还可以声明消耗数量（{@code first_count} / {@code second_count} / {@code third_count}，
 * 默认 1）：槽里必须一次攒够数量才匹配，作业结束时按各自数量扣除。
 * 数量只是**同一槽的堆叠**，不占用额外槽位 —— 三槽机型因此照样能做 4 + 1 + 4 这种偏重的配方。
 */
public class FusionRecipe extends InspiredRecipe<FusionRecipeInput> {

    public static final int DEFAULT_DURATION = 200;
    public static final int DEFAULT_ENERGY_PER_TICK = 40;

    /** 单种材料一次最多消耗多少个，兼作 JSON 的取值范围校验。 */
    public static final int MAX_COUNT = 64;

    public static final MapCodec<FusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("first_input").forGetter(FusionRecipe::firstInput),
            Codec.intRange(1, MAX_COUNT).optionalFieldOf("first_count", 1).forGetter(FusionRecipe::firstCount),
            Ingredient.CODEC_NONEMPTY.fieldOf("second_input").forGetter(FusionRecipe::secondInput),
            Codec.intRange(1, MAX_COUNT).optionalFieldOf("second_count", 1).forGetter(FusionRecipe::secondCount),
            ItemStack.CODEC.fieldOf("result").forGetter(FusionRecipe::result),
            Codec.INT.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(FusionRecipe::duration),
            Codec.INT.optionalFieldOf("energy_per_tick", DEFAULT_ENERGY_PER_TICK).forGetter(FusionRecipe::energyPerTick),
            Ingredient.CODEC.optionalFieldOf("third_input", Ingredient.EMPTY).forGetter(FusionRecipe::thirdInput),
            Codec.intRange(1, MAX_COUNT).optionalFieldOf("third_count", 1).forGetter(FusionRecipe::thirdCount)
    ).apply(instance, FusionRecipe::new));

    private final Ingredient firstInput;
    private final int firstCount;
    private final Ingredient secondInput;
    private final int secondCount;
    private final Ingredient thirdInput;
    private final int thirdCount;

    /** 全参构造：参数顺序与 {@link #CODEC} 的字段顺序一一对应。 */
    public FusionRecipe(Ingredient firstInput, int firstCount, Ingredient secondInput, int secondCount,
                        ItemStack result, int duration, int energyPerTick,
                        Ingredient thirdInput, int thirdCount) {
        super(result, duration, energyPerTick);
        if (firstCount < 1 || secondCount < 1 || thirdCount < 1) {
            throw new IllegalArgumentException(
                    "融合配方的材料数量至少为 1：" + firstCount + " / " + secondCount + " / " + thirdCount);
        }
        this.firstInput = firstInput;
        this.firstCount = firstCount;
        this.secondInput = secondInput;
        this.secondCount = secondCount;
        this.thirdInput = thirdInput;
        this.thirdCount = thirdCount;
    }

    /** 三种材料、各自声明消耗数量。 */
    public FusionRecipe(Ingredient firstInput, int firstCount, Ingredient secondInput, int secondCount,
                        Ingredient thirdInput, int thirdCount, ItemStack result,
                        int duration, int energyPerTick) {
        this(firstInput, firstCount, secondInput, secondCount, result, duration, energyPerTick,
                thirdInput, thirdCount);
    }

    /** 三种材料各消耗 1 个。 */
    public FusionRecipe(Ingredient firstInput, Ingredient secondInput, Ingredient thirdInput,
                        ItemStack result, int duration, int energyPerTick) {
        this(firstInput, 1, secondInput, 1, result, duration, energyPerTick, thirdInput, 1);
    }

    /** 两种材料各消耗 1 个（强能机型也能跑，只是用不满三个槽）。 */
    public FusionRecipe(Ingredient firstInput, Ingredient secondInput, ItemStack result,
                        int duration, int energyPerTick, Ingredient thirdInput) {
        this(firstInput, 1, secondInput, 1, result, duration, energyPerTick, thirdInput, 1);
    }

    public FusionRecipe(Ingredient firstInput, Ingredient secondInput, ItemStack result,
                        int duration, int energyPerTick) {
        this(firstInput, 1, secondInput, 1, result, duration, energyPerTick, Ingredient.EMPTY, 1);
    }

    public FusionRecipe(Ingredient firstInput, Ingredient secondInput, ItemStack result) {
        this(firstInput, 1, secondInput, 1, result, DEFAULT_DURATION, DEFAULT_ENERGY_PER_TICK,
                Ingredient.EMPTY, 1);
    }

    /** 本配方要求的材料清单；第三项为可选。 */
    public List<Ingredient> ingredients() {
        return hasThirdInput()
                ? List.of(firstInput, secondInput, thirdInput)
                : List.of(firstInput, secondInput);
    }

    /** 与 {@link #ingredients()} 一一对应的消耗数量。 */
    public int[] counts() {
        return hasThirdInput()
                ? new int[]{firstCount, secondCount, thirdCount}
                : new int[]{firstCount, secondCount};
    }

    /** 第 {@code materialIndex} 种材料要消耗多少个；越界返回 1。 */
    public int countFor(int materialIndex) {
        int[] all = counts();
        return materialIndex >= 0 && materialIndex < all.length ? all[materialIndex] : 1;
    }

    public boolean hasThirdInput() {
        return !thirdInput.isEmpty();
    }

    /**
     * 顺序无关的槽位指派：返回「材料下标 → 输入槽下标」，找不到就返回 {@code null}。
     * <p>
     * 槽位不足、非空槽位数量与材料数不一致（例如三槽机里两个槽有东西、配方却要三种材料）、
     * 或者某个槽的堆叠数量攒不够该材料所需个数，都算不匹配，
     * 避免出现「只扣一部分材料却产出整份产物」。
     */
    public int[] resolveSlots(List<ItemStack> slots) {
        List<Ingredient> required = ingredients();
        if (slots == null || slots.size() < required.size()) {
            return null;
        }
        long filled = slots.stream().filter(stack -> !stack.isEmpty()).count();
        if (filled != required.size()) {
            return null;
        }
        int[] counts = counts();
        return RecipeSlotMatcher.assign(required.size(), slots.size(),
                (material, slot) -> {
                    ItemStack stack = slots.get(slot);
                    return !stack.isEmpty()
                            && stack.getCount() >= counts[material]
                            && required.get(material).test(stack);
                });
    }

    @Override
    public boolean matches(FusionRecipeInput input, Level level) {
        return resolveSlots(input.items()) != null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FUSION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.FUSION_TYPE.get();
    }

    public Ingredient firstInput() {
        return firstInput;
    }

    public int firstCount() {
        return firstCount;
    }

    public Ingredient secondInput() {
        return secondInput;
    }

    public int secondCount() {
        return secondCount;
    }

    public Ingredient thirdInput() {
        return thirdInput;
    }

    public int thirdCount() {
        return thirdCount;
    }
}
