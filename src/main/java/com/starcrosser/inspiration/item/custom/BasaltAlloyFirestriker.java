package com.starcrosser.inspiration.item.custom;

import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;

/**
 * 玄武岩合金打火石。
 * <p>
 * 行为与打火石完全一致，但注册时把最大耐久设为 0，使其不可破坏、永不损耗。
 */
public class BasaltAlloyFirestriker extends FlintAndSteelItem {

    public BasaltAlloyFirestriker(Item.Properties properties) {
        super(properties);
    }
}
