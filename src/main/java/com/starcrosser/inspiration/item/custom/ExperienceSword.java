package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.item.tool.ExperienceDrops;
import com.starcrosser.inspiration.item.tool.ExperienceTool;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;

import java.util.List;

/** 命中敌人时掉落经验的剑。翡翠钢升级版沿用本类，仅经验量与数值不同。 */
public class ExperienceSword extends SwordItem implements ExperienceTool {

    private final int experiencePerAction;

    public ExperienceSword(Tier tier, int attackDamage, float attackSpeed,
                           int experiencePerAction, Properties properties) {
        super(tier, properties.attributes(SwordItem.createAttributes(tier, attackDamage, attackSpeed)));
        this.experiencePerAction = experiencePerAction;
    }

    @Override
    public int experiencePerAction() {
        return experiencePerAction;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player) {
            ExperienceDrops.spawnExperience(target.level(),
                    target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                    experiencePerAction);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.inspiration.exp_sword.description", experiencePerAction));
    }
}
