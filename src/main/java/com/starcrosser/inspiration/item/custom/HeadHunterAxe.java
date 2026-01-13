package com.starcrosser.inspiration.item.custom;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Tier;

public class HeadHunterAxe extends AxeItem {
    public HeadHunterAxe(Tier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(tier, properties.attributes(AxeItem.createAttributes(tier, attackDamage, attackSpeed)));
    }


}
