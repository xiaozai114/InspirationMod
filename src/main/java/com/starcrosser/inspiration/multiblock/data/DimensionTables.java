package com.starcrosser.inspiration.multiblock.data;

import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.material.ModMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

/**
 * 维度数据的具体内容（权重表）。权重是相对值，只影响比例，不必凑整。
 */
public final class DimensionTables {

    private DimensionTables() {
    }

    /**
     * 主世界：以铁矿与铜矿为大宗，钻石/绿宝石是稀有条目。
     * 时长 1200 刻（一分钟）、每刻 200 FE —— 与结构模拟机同档的「慢但稳定」。
     */
    public static final MineralTable OVERWORLD = new MineralTable("minecraft:overworld",
            List.of(
                    new MineralTable.Ore(() -> new ItemStack(Items.RAW_IRON, 2), 24),
                    new MineralTable.Ore(() -> new ItemStack(Items.RAW_COPPER, 3), 22),
                    new MineralTable.Ore(() -> new ItemStack(Items.COAL, 3), 20),
                    new MineralTable.Ore(() -> new ItemStack(Items.RAW_GOLD, 2), 10),
                    new MineralTable.Ore(() -> new ItemStack(Items.REDSTONE, 4), 10),
                    new MineralTable.Ore(() -> new ItemStack(Items.LAPIS_LAZULI, 4), 6),
                    new MineralTable.Ore(() -> new ItemStack(Items.DIAMOND), 3),
                    new MineralTable.Ore(() -> new ItemStack(Items.EMERALD), 2),
                    new MineralTable.Ore(() -> new ItemStack(ModMaterials.NICKEL.item().get(), 2), 6),
                    new MineralTable.Ore(() -> new ItemStack(ModMaterials.URANIUM.item().get()), 3)),
            1_200, 200);
}
