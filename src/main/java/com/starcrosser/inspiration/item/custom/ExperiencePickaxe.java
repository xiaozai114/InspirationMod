package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.item.tool.MiningExperienceTool;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

import java.util.List;

/**
 * 挖掘方块时掉落经验的镐。
 * <p>
 * 经验的实际产出发生在 {@code item/tool/ExpMiningHandler}，本类只负责携带数值。
 */
public class ExperiencePickaxe extends PickaxeItem implements MiningExperienceTool {

    private final int experiencePerAction;

    public ExperiencePickaxe(Tier tier, float attackDamage, float attackSpeed,
                             int experiencePerAction, Properties properties) {
        super(tier, properties.attributes(DiggerItem.createAttributes(tier, attackDamage, attackSpeed)));
        this.experiencePerAction = experiencePerAction;
    }

    @Override
    public int experiencePerAction() {
        return experiencePerAction;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.inspiration.exp_pickaxe.description", experiencePerAction));
    }
}
