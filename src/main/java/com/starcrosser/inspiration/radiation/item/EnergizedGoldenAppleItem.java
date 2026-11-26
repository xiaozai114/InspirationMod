package com.starcrosser.inspiration.radiation.item;

import com.starcrosser.inspiration.effect.ModEffects;
import com.starcrosser.inspiration.radiation.RadiationAttachments;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class EnergizedGoldenAppleItem extends Item {
    private final int doseRemoved;
    private final int minutes;

    private EnergizedGoldenAppleItem(Properties properties, int doseRemoved, int minutes) {
        super(properties);
        this.doseRemoved = doseRemoved;
        this.minutes = minutes;
    }

    public static EnergizedGoldenAppleItem ordinary() {
        return new EnergizedGoldenAppleItem(new Properties().rarity(Rarity.RARE).food(Foods.GOLDEN_APPLE), 1_000, 5);
    }

    public static EnergizedGoldenAppleItem enchanted() {
        return new EnergizedGoldenAppleItem(new Properties().rarity(Rarity.EPIC).food(Foods.ENCHANTED_GOLDEN_APPLE)
                .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true), 10_000, 30);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide && entity instanceof Player player) {
            RadiationAttachments.add(player, -doseRemoved);
            player.addEffect(new MobEffectInstance(ModEffects.RADIATION_RESISTANCE, minutes * 60 * 20));
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("tooltip.inspiration.radiation_apple", doseRemoved, minutes)
                .withStyle(ChatFormatting.GREEN));
    }
}
