package com.starcrosser.inspiration.multiblock.data;

import com.starcrosser.inspiration.block.ModBlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

/**
 * 维度数据的具体内容（权重表）。权重是相对值，只影响比例，不必凑整。
 */
public final class DimensionTables {

    private DimensionTables() {
    }

    public static final int STANDARD_DURATION = 120;

    /**
     * 主世界：以铁矿与铜矿为大宗，钻石/绿宝石是稀有条目。
     */
    public static final MineralTable OVERWORLD = new MineralTable("minecraft:overworld",
            List.of(
                    new MineralTable.Ore(() -> new ItemStack(Items.IRON_ORE, 2), 24),
                    new MineralTable.Ore(() -> new ItemStack(Items.COPPER_ORE, 3), 22),
                    new MineralTable.Ore(() -> new ItemStack(Items.COAL_ORE, 3), 20),
                    new MineralTable.Ore(() -> new ItemStack(Items.GOLD_ORE, 2), 10),
                    new MineralTable.Ore(() -> new ItemStack(Items.REDSTONE_ORE, 4), 12),
                    new MineralTable.Ore(() -> new ItemStack(Items.LAPIS_ORE, 4), 6),
                    new MineralTable.Ore(() -> new ItemStack(Items.DIAMOND_ORE), 3),
                    new MineralTable.Ore(() -> new ItemStack(Items.EMERALD_ORE), 2)),
            STANDARD_DURATION, 200);

    public static final MineralTable NETHER = new MineralTable("minecraft:the_nether",
            List.of(
                    new MineralTable.Ore(() -> new ItemStack(Items.NETHER_GOLD_ORE, 2), 34),
                    new MineralTable.Ore(() -> new ItemStack(Items.GILDED_BLACKSTONE), 12),
                    new MineralTable.Ore(() -> new ItemStack(Items.NETHER_QUARTZ_ORE, 3), 40),
                    new MineralTable.Ore(() -> new ItemStack(ModBlocks.FRANCIUM_SALT_ORE.get()), 12),
                    new MineralTable.Ore(() -> new ItemStack(Items.ANCIENT_DEBRIS), 2)),
            STANDARD_DURATION, 400);

    public static final MineralTable END = new MineralTable("minecraft:the_end",
            List.of(
                    new MineralTable.Ore(() -> new ItemStack(ModBlocks.EPHEMERAL_ORE.get()), 75),
                    new MineralTable.Ore(() -> new ItemStack(ModBlocks.TUNGSTEN_ORE.get()), 25)),
            1_200, 1200);
}
