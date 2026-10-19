package com.starcrosser.inspiration.multiblock.data;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 结构数据：结构模拟机的「蓝图」物品，本身**不被消耗**。
 * <p>
 * 每种数据就是一个物品实例（沉船数据、以后的林地府邸数据……），参数直接写在
 * {@link SimulationSpec} 里，因此加一种新结构 = 注册一个新物品，不需要动机器代码。
 */
public class StructureDataItem extends Item {

    private final SimulationSpec spec;

    public StructureDataItem(Item.Properties properties, SimulationSpec spec) {
        super(properties);
        this.spec = spec;
    }

    public SimulationSpec spec() {
        return spec;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.inspiration.structure_data.duration", spec.duration()));
        tooltip.add(Component.translatable("tooltip.inspiration.structure_data.energy", spec.energyPerTick()));
        tooltip.add(Component.translatable("tooltip.inspiration.structure_data.charge",
                com.starcrosser.inspiration.multiblock.blockentity.StructureSimulatorTile.CHARGE_PER_TICK));
        tooltip.add(Component.translatable("tooltip.inspiration.structure_data.unconsumed")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
