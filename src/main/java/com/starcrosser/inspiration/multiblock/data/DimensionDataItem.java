package com.starcrosser.inspiration.multiblock.data;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 维度数据：矿物模拟机的「矿脉档案」，本身**不被消耗**。
 * <p>
 * 一份数据 = 一张 {@link MineralTable}。加一个新维度就是再注册一个实例并写好权重表，
 * 机器的逻辑不用动。
 */
public class DimensionDataItem extends Item {

    private final MineralTable table;

    public DimensionDataItem(Item.Properties properties, MineralTable table) {
        super(properties);
        this.table = table;
    }

    public MineralTable table() {
        return table;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.inspiration.dimension_data.duration", table.duration()));
        tooltip.add(Component.translatable("tooltip.inspiration.dimension_data.energy", table.energyPerTick()));
        tooltip.add(Component.translatable("tooltip.inspiration.dimension_data.ores", table.ores().size())
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.inspiration.dimension_data.unconsumed")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
