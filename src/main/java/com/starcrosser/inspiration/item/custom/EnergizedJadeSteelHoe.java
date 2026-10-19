package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.item.tool.ChargedBuff;
import com.starcrosser.inspiration.item.tool.ChargedBuffTool;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

/** 注能翡翠钢锄：锄草方块掉草皮与经验，长按右键消耗经验获得<b>幸运</b>。 */
public class EnergizedJadeSteelHoe extends ExperienceHoe implements ChargedBuffTool {

    public EnergizedJadeSteelHoe(Tier tier, float attackDamage, float attackSpeed,
                                 int experiencePerAction, Properties properties) {
        super(tier, attackDamage, attackSpeed, experiencePerAction, properties);
    }

    @Override
    public Holder<MobEffect> chargedEffect() {
        return MobEffects.LUCK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ChargedBuff.use(level, player, hand, this);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return chargeTicks();
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        ChargedBuff.onUseTick(level, entity, stack, remainingUseDuration, this);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return ChargedBuff.finishUsingItem(stack, level, entity, this);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        ChargedBuff.appendHoverText(tooltip, flag, this);
    }
}
