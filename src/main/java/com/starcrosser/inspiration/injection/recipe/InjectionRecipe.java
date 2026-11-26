package com.starcrosser.inspiration.injection.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.starcrosser.inspiration.injection.InjectionOutcome;
import com.starcrosser.inspiration.recipe.ModRecipes;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * 注入配方：把注入枪上的物质打进束缚器上的基底里。
 * <p>
 * 配方本身不声明耗时之外的任何机器参数，只声明一条「能量窗口」：
 * 注入枪放电量必须落在 {@code [min_energy, max_energy]} 之间才成功。
 * 低于下限配方不生效且材料照损，高于上限在束缚器处爆炸，
 * 具体判定见 {@link InjectionOutcome}。
 */
public class InjectionRecipe implements Recipe<InjectionRecipeInput> {

    /** 默认等级：玄武岩合金块底座的等级。 */
    public static final int DEFAULT_TIER = 1;

    /** 注入过程非常快，8 刻（0.4 秒）留出激光动画的时间。 */
    public static final int DEFAULT_DURATION = 8;

    /**
     * 种子随机配方的能量占位值：写进 JSON 也没关系，{@link #seeded()} 为真时一律忽略，
     * 真正的窗口由 {@code SeededEnergyWindow} 按存档种子算。
     */
    public static final int SEEDED_PLACEHOLDER = -1;

    public static final MapCodec<InjectionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("base").forGetter(InjectionRecipe::base),
            Ingredient.CODEC_NONEMPTY.fieldOf("inject").forGetter(InjectionRecipe::inject),
            Codec.INT.optionalFieldOf("tier", DEFAULT_TIER).forGetter(InjectionRecipe::tier),
            Codec.INT.optionalFieldOf("min_energy", SEEDED_PLACEHOLDER).forGetter(InjectionRecipe::minEnergy),
            Codec.INT.optionalFieldOf("max_energy", SEEDED_PLACEHOLDER).forGetter(InjectionRecipe::maxEnergy),
            ItemStack.CODEC.fieldOf("result").forGetter(InjectionRecipe::result),
            Codec.INT.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(InjectionRecipe::duration),
            Codec.BOOL.optionalFieldOf("seeded_energy", false).forGetter(InjectionRecipe::seeded),
            ResourceLocation.CODEC.optionalFieldOf("window_source").forGetter(InjectionRecipe::windowSource)
    ).apply(instance, InjectionRecipe::new));

    private final Ingredient base;
    private final Ingredient inject;
    private final int tier;
    private final int minEnergy;
    private final int maxEnergy;
    private final ItemStack result;
    private final int duration;
    private final boolean seeded;
    private final Optional<ResourceLocation> windowSource;

    /**
     * 全参构造，参数顺序与 {@link #CODEC} 的字段顺序一致。
     * <p>
     * {@code seeded} 为真时 {@code minEnergy / maxEnergy} 只是占位，允许为负；
     * 为假时必须给出合法的窗口。
     */
    public InjectionRecipe(Ingredient base, Ingredient inject, int tier, int minEnergy, int maxEnergy,
                           ItemStack result, int duration, boolean seeded, Optional<ResourceLocation> windowSource) {
        if (!seeded) {
            if (minEnergy < 0 || maxEnergy < 0) {
                throw new IllegalArgumentException(
                        "注入配方要么声明 min_energy / max_energy，要么标记 seeded_energy=true");
            }
            if (maxEnergy < minEnergy) {
                throw new IllegalArgumentException(
                        "注入配方的最大 FE 不能小于最小 FE：" + minEnergy + " ~ " + maxEnergy);
            }
        }
        if (tier < 1) {
            throw new IllegalArgumentException("注入等级至少为 1，实际为 " + tier);
        }
        this.base = base;
        this.inject = inject;
        this.tier = tier;
        this.minEnergy = minEnergy;
        this.maxEnergy = maxEnergy;
        this.result = result;
        this.duration = duration;
        this.seeded = seeded;
        this.windowSource = windowSource;
    }

    public InjectionRecipe(Ingredient base, Ingredient inject, int tier, int minEnergy, int maxEnergy,
                           ItemStack result, int duration, boolean seeded) {
        this(base, inject, tier, minEnergy, maxEnergy, result, duration, seeded, Optional.empty());
    }

    /** 固定能量窗口的配方。 */
    public InjectionRecipe(Ingredient base, Ingredient inject, int tier, int minEnergy, int maxEnergy,
                           ItemStack result, int duration) {
        this(base, inject, tier, minEnergy, maxEnergy, result, duration, false, Optional.empty());
    }

    /** 能量窗口由世界种子决定的配方，JSON 里不必写 min_energy / max_energy。 */
    public InjectionRecipe(Ingredient base, Ingredient inject, ItemStack result, int tier, int duration) {
        this(base, inject, tier, SEEDED_PLACEHOLDER, SEEDED_PLACEHOLDER, result, duration, true, Optional.empty());
    }

    @Override
    public boolean matches(InjectionRecipeInput input, Level level) {
        return base.test(input.base()) && inject.test(input.inject());
    }

    /** 按放电量判定这一次注入的结局。种子随机配方要先解析出窗口再用另一个重载。 */
    public InjectionOutcome outcomeFor(int energy) {
        return InjectionOutcome.of(energy, minEnergy, maxEnergy);
    }

    /** 按放电量与给定窗口判定结局。 */
    public static InjectionOutcome outcomeFor(int energy, int minEnergy, int maxEnergy) {
        return InjectionOutcome.of(energy, minEnergy, maxEnergy);
    }

    @Override
    public ItemStack assemble(InjectionRecipeInput input, HolderLookup.Provider registries) {
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
        return ModRecipes.INJECTION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.INJECTION_TYPE.get();
    }

    /** 基底物质，放在物质束缚器上。 */
    public Ingredient base() {
        return base;
    }

    /** 注入物质，放在注入枪上。 */
    public Ingredient inject() {
        return inject;
    }

    /** 配方等级，不能超过底座方块提供的等级。 */
    public int tier() {
        return tier;
    }

    public int minEnergy() {
        return minEnergy;
    }

    public int maxEnergy() {
        return maxEnergy;
    }

    public ItemStack result() {
        return result;
    }

    public int duration() {
        return duration;
    }

    /** 能量窗口是否由世界种子随机决定；为真时 {@link #minEnergy()} 与 {@link #maxEnergy()} 无意义。 */
    public boolean seeded() {
        return seeded;
    }

    public Optional<ResourceLocation> windowSource() {
        return windowSource;
    }

    /** A different base uses the same injection window, including in existing worlds. */
    public InjectionRecipe withBaseAndYield(Ingredient variant, int multiplier, ResourceLocation originalId) {
        return new InjectionRecipe(variant, inject, tier, minEnergy, maxEnergy,
                result.copyWithCount(result.getCount() * multiplier), duration, seeded,
                seeded ? Optional.of(originalId) : Optional.empty());
    }
}
