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
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 灵感装配台配方：把若干材料（外加可选的流体原料）装配成一个成品。
 * <p>
 * 材料与槽位不要求一一对应：只要每种材料都能在机器的输入槽里各找到一份即可，
 * 结算时按实际分配到的槽位扣除。流体原料同样与罐位无关，最多两条
 * （普通装配台只有一个流体槽，因此两条流体的配方只有高能机型能做）。
 */
public class AssemblingRecipe extends InspiredRecipe<AssemblingRecipeInput> {

    public static final int DEFAULT_DURATION = 300;
    public static final int DEFAULT_ENERGY_PER_TICK = 200;

    /** 物品输入槽的数量上限，同时是配方允许的材料种类上限。 */
    public static final int MAX_INGREDIENTS = 6;

    /** 流体原料的条数上限：与高能灵感装配台的流体输入槽数量一致。 */
    public static final int MAX_FLUIDS = 2;

    public static final MapCodec<AssemblingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(AssemblingRecipe::ingredients),
            // 每种材料一次消耗几个，与 ingredients 一一对应；缺省全是 1
            Codec.INT.listOf().optionalFieldOf("counts", List.<Integer>of()).forGetter(AssemblingRecipe::counts),
            // MapCodec 没有 listOf，先转回 Codec 再取列表
            FluidRequirement.CODEC.codec().listOf().optionalFieldOf("fluids", List.of())
                    .forGetter(AssemblingRecipe::fluids),
            ItemStack.CODEC.fieldOf("result").forGetter(AssemblingRecipe::result),
            Codec.INT.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(AssemblingRecipe::duration),
            Codec.INT.optionalFieldOf("energy_per_tick", DEFAULT_ENERGY_PER_TICK)
                    .forGetter(AssemblingRecipe::energyPerTick)
    ).apply(instance, AssemblingRecipe::new));

    private final List<Ingredient> ingredients;
    /** 与 {@link #ingredients} 等长的每槽消耗量，缺省是 1。 */
    private final List<Integer> counts;
    private final List<FluidRequirement> fluids;

    public AssemblingRecipe(List<Ingredient> ingredients, List<Integer> counts,
                            List<FluidRequirement> fluids, ItemStack result,
                            int duration, int energyPerTick) {
        super(result, duration, energyPerTick);
        if (ingredients.isEmpty() || ingredients.size() > MAX_INGREDIENTS) {
            throw new IllegalArgumentException(
                    "装配配方需要 1~" + MAX_INGREDIENTS + " 种材料，实际为 " + ingredients.size());
        }
        if (fluids.size() > MAX_FLUIDS) {
            throw new IllegalArgumentException(
                    "装配配方最多 " + MAX_FLUIDS + " 种流体原料，实际为 " + fluids.size());
        }
        this.ingredients = List.copyOf(ingredients);
        this.counts = normalizeCounts(counts, ingredients.size());
        this.fluids = List.copyOf(fluids);
    }

    /** 把数量列表补齐/截断到材料条数，缺省与越界都按 1 处理。 */
    private static List<Integer> normalizeCounts(List<Integer> counts, int size) {
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            int value = i < counts.size() ? counts.get(i) : 1;
            values[i] = Math.max(1, value);
        }
        return java.util.Arrays.stream(values).boxed().toList();
    }

    /** 每种材料一次消耗几个，与 {@link #ingredients} 等长。 */
    public List<Integer> counts() {
        return counts;
    }

    /** 第 index 种材料一次消耗几个。 */
    public int countOf(int index) {
        return index >= 0 && index < counts.size() ? counts.get(index) : 1;
    }

    /** 不需要流体的便捷构造。 */
    public AssemblingRecipe(List<Ingredient> ingredients, ItemStack result, int duration, int energyPerTick) {
        this(ingredients, List.<Integer>of(), List.<FluidRequirement>of(), result, duration, energyPerTick);
    }

    @Override
    public boolean matches(AssemblingRecipeInput input, Level level) {
        return resolveSlots(input.items()) != null && resolveFluids(input.fluids()) != null;
    }

    /**
     * 为每种材料分配一个输入槽。
     *
     * @return 与 {@link #ingredients()} 等长的槽位下标表；材料凑不齐时返回 {@code null}
     */
    @Nullable
    public int[] resolveSlots(List<ItemStack> stacks) {
        return resolveSlots(stacks, 1);
    }

    @Nullable
    public int[] resolveSlots(List<ItemStack> stacks, int batches) {
        return RecipeSlotMatcher.assign(ingredients.size(), stacks.size(), (material, slot) -> {
            ItemStack stack = stacks.get(slot);
            return !stack.isEmpty()
                    && stack.getCount() >= (long) countOf(material) * batches
                    && ingredients.get(material).test(stack);
        });
    }

    /**
     * 为每条流体原料分配一个输入罐，其中每一项都必须是 {@link FluidRequirement#matches}。
     *
     * @return 与 {@link #fluids()} 等长的罐位下标表；流体凑不齐时返回 {@code null}
     */
    @Nullable
    public int[] resolveFluids(List<FluidStack> tanks) {
        return resolveFluids(tanks, 1);
    }

    @Nullable
    public int[] resolveFluids(List<FluidStack> tanks, int batches) {
        if (fluids.isEmpty()) {
            return new int[0];
        }
        return RecipeSlotMatcher.assign(fluids.size(), tanks.size(),
                (requirement, tank) -> fluids.get(requirement).matches(tanks.get(tank))
                        && tanks.get(tank).getAmount() >= (long) fluids.get(requirement).amount() * batches);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ASSEMBLING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ASSEMBLING_TYPE.get();
    }

    public List<Ingredient> ingredients() {
        return ingredients;
    }

    /** 流体原料，不需要时为空列表。 */
    public List<FluidRequirement> fluids() {
        return fluids;
    }
}
