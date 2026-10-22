package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

/**
 * 末地合金盾牌。
 * <p>
 * 格挡行为完全交给原版 {@link ShieldItem}（NeoForge 用 {@code ItemAbilities.SHIELD_BLOCK}
 * 判定能否格挡，不认具体物品），这里只做一件事：把最大耐久抬到普通盾牌的 10 倍。
 * 「破盾时把周围敌对生物传送走」由 {@link com.starcrosser.inspiration.event.EndAlloyShieldHandler} 监听破盾事件实现 ——
 * 耐久扣减发生在 {@code ItemStack#hurtAndBreak} 里，物品自身没有「刚好被击碎」的回调。
 * <p>
 * 外观也不是这里管的：物品模型是 {@code builtin/entity}，由
 * {@code client.renderer.EndAlloyShieldRenderer} 用原版盾牌模型渲染，只是盾面换了贴图。
 */
public class EndAlloyShieldItem extends ShieldItem {

    /** 耐久：普通盾牌是 336，这里取 10 倍。 */
    public static final int MAX_DAMAGE = 336 * 10;

    public EndAlloyShieldItem(Item.Properties properties) {
        super(properties);
    }

    /** 铁砧修复：末地合金（以及原版盾牌认的木板）都能拿来补。 */
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.is(ModItems.END_ALLOY.get()) || super.isValidRepairItem(toRepair, repair);
    }
}
