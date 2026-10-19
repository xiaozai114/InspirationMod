package com.starcrosser.inspiration.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 钻头：地核钻探者的耗材。
 * <p>
 * 钻头本身没有任何挖掘功能，它只是一张「允许机器开工」的许可证 —— 机器每完成一次作业
 * 会消耗 1 点耐久，耐久耗尽时 {@code hurtAndBreak} 会把这一格钻头直接抽走（报废）。
 * 将来要做更耐用的钻头，只需要注册一个耐久更高的 {@code DrillBitItem}，
 * 机器的识别条件是 {@code instanceof DrillBitItem}，不需要改动槽位过滤器。
 */
public class DrillBitItem extends Item {

    public DrillBitItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.inspiration.drill_bit.description")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
