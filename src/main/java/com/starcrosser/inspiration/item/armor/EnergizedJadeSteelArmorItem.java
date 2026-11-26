package com.starcrosser.inspiration.item.armor;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class EnergizedJadeSteelArmorItem extends ArmorItem {
    public static final int RESISTANCE_PERCENT = 20;

    public EnergizedJadeSteelArmorItem(Type type) {
        super(ModArmorMaterials.ENERGIZED_JADE_STEEL, type,
                new Properties().fireResistant().durability(type.getDurability(37)));
    }

    public static int resistancePercent(Player player) {
        int percent = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (player.getItemBySlot(slot).getItem() instanceof EnergizedJadeSteelArmorItem piece
                    && piece.getEquipmentSlot() == slot) {
                percent += RESISTANCE_PERCENT;
            }
        }
        return percent;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("tooltip.inspiration.radiation_armor", RESISTANCE_PERCENT)
                .withStyle(ChatFormatting.GREEN));
    }
}
