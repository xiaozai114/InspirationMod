package com.starcrosser.inspiration.machine.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * 本模组所有「机器配方」的公共基类。
 * <p>
 * 四种机器配方（融合 / 重塑 / 扭曲 / 装配）原先各自重复了同一套样板：
 * 产物、耗时、每刻耗电三个字段加各自的 getter，以及 {@code assemble} /
 * {@code canCraftInDimensions} / {@code getResultItem} / {@code getToastSymbol}
 * 四个实现完全一样的方法。这里把它们收在一处，子类只留自己特有的输入匹配逻辑。
 * <p>
 * ⚠️ 子类仍需实现 {@code getSerializer()} 与 {@code getType()}：这两个返回的是各自的
 * 注册条目，类型不同，没法在基类里统一（强行统一会把四个配方类型混成一个）。
 *
 * @param <I> 该配方接受的配方输入类型
 */
public abstract class InspiredRecipe<I extends RecipeInput> implements Recipe<I> {

    private final ItemStack result;
    private final int duration;
    private final int energyPerTick;

    protected InspiredRecipe(ItemStack result, int duration, int energyPerTick) {
        this.result = result;
        this.duration = duration;
        this.energyPerTick = energyPerTick;
    }

    @Override
    public ItemStack assemble(I input, HolderLookup.Provider registries) {
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

    public ItemStack result() {
        return result;
    }

    /** 跑完一整次作业需要的刻数。 */
    public int duration() {
        return duration;
    }

    /** 作业期间每刻消耗的电量（FE）。 */
    public int energyPerTick() {
        return energyPerTick;
    }
}
