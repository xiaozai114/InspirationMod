package com.starcrosser.inspiration.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 幽匿合金钻头：地核钻探者的上位耗材。
 * <p>
 * 与基础钻头的唯一区别是「挖到底时能把那一格基岩裂开」，裂开的基岩才能放虚空能量提取器。
 * 行为开关在 {@link DrillBitItem#cracksBedrock()}，机器只认这个接口，不认具体类型。
 */
public class SculkAlloyDrillBit extends DrillBitItem {

    public SculkAlloyDrillBit(Properties properties) {
        super(properties);
    }

    @Override
    public boolean cracksBedrock() {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.inspiration.sculk_drill_bit.cracks")
                .withStyle(ChatFormatting.DARK_PURPLE));
    }
}
