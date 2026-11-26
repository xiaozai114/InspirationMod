package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.machine.charge.InspirationCharge;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/** Displays how much charge one item provides in a machine or inspiration hatch. */
public class InspirationChargeItem extends Item {

    public InspirationChargeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.inspiration.inspiration_shard.description",
                        InspirationCharge.chargeValue(stack))
                .withStyle(ChatFormatting.GRAY));
    }
}
