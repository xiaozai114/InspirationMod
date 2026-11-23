package com.starcrosser.inspiration.multiblock.data;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

/**
 * 矿物模拟机的产出表：一组「物品 + 权重」，按权重随机抽一件。
 * <p>
 * 权重不必归一化，抽的时候按总和算概率；表本身是纯数据，加一种矿物只要往列表里加一行。
 * 维度数据物品各自持有一张表（{@code overworld} 现在已就绪，以后的维度照同样方式加）。
 */
public record MineralTable(String dimensionId, List<Ore> ores, int duration, int energyPerTick) {

    /**
     * 一种候选产物：物品栈的**惰性**供应商 + 抽取权重。
     * <p>
     * 必须是供应商：这张表在 {@code ModItems} 静态初始化期就被构造，
     * 那时材料的物品还没绑定到注册表，直接取 {@code ItemStack} 会抛「unbound value」。
     */
    public record Ore(Supplier<ItemStack> stack, int weight) {
    }

    public MineralTable {
        if (ores.isEmpty()) {
            throw new IllegalArgumentException("矿物表不能为空：" + dimensionId);
        }
        if (ores.stream().anyMatch(ore -> ore.weight() <= 0)) {
            throw new IllegalArgumentException("矿物权重必须为正：" + dimensionId);
        }
    }

    /** 权重总和，抽表时用来算概率。 */
    public int totalWeight() {
        return ores.stream().mapToInt(Ore::weight).sum();
    }

    /** 按权重抽一件；产出数量取自物品栈自身的 count。 */
    public ItemStack pick(RandomSource random) {
        int roll = random.nextInt(totalWeight());
        for (Ore ore : ores) {
            roll -= ore.weight();
            if (roll < 0) {
                return ore.stack().get().copy();
            }
        }
        return ores.getLast().stack().get().copy();
    }
}
