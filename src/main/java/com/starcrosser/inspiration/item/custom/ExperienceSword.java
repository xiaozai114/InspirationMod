package com.starcrosser.inspiration.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;

public class ExperienceSword extends SwordItem {
    private int xp;
    public ExperienceSword(Tier tier, int attack_dmg, float attack_spd,int xp,Properties properties) {
        super(tier, properties.attributes(SwordItem.createAttributes(tier, attack_dmg, attack_spd)));
        this.xp = xp;
    }
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player) {
            Level level = target.level();
            ExperienceOrb orb = new ExperienceOrb(level ,target.getX(),target.getY()+target.getBbHeight()/2, target.getZ(),xp);
            level.addFreshEntity(orb);

        }

        return super.hurtEnemy(stack, target, attacker);
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 添加描述文本
        tooltip.add(Component.translatable("tooltip.inspiration.exp_sword.description"));
    }
}
