package com.starcrosser.inspiration.item.tool;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/**
 * 只改耐久、其余全部照搬的品质包装。
 * <p>
 * 存在的理由：{@code TieredItem} 的构造里有一句 {@code properties.durability(tier.getUses())}，
 * 会把 {@link net.minecraft.world.item.Item.Properties#durability} 设的值**覆盖掉**，
 * 于是同一品质下的剑 / 斧 / 镐 只能共用 {@code uses} 这一个耐久。
 * 原版正是这么干的（钻石全套 1561、下界合金全套 2031），但本模组想让每件装备有各自的耐久。
 * <p>
 * 完整的替代方案是连 {@code TieredItem} 一起不用、自己写工具基类 —— 那要照抄原版的
 * 压土径、耕地、剥皮等 {@code useOn} 逻辑，风险高得多（见 {@code item/tool/README.md}）。
 * 这里换个等价但零风险的做法：把品质包一层，只把 {@link #getUses()} 换成指定值，
 * 挖掘速度、伤害加成、挖掘等级、附魔价值、修复材料全部代理给原品质。
 * 这样 {@code TieredItem} 写进组件的 {@code MAX_DAMAGE} 就是我们要的数。
 *
 * @param delegate 被包装的原品质（可以是原版 {@code Tiers} 里的，也可以是 {@link ModToolTiers} 的）
 * @param uses     这件装备自己的耐久
 */
public record DurabilityTier(Tier delegate, int uses) implements Tier {

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return delegate.getSpeed();
    }

    @Override
    public float getAttackDamageBonus() {
        return delegate.getAttackDamageBonus();
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return delegate.getIncorrectBlocksForDrops();
    }

    @Override
    public int getEnchantmentValue() {
        return delegate.getEnchantmentValue();
    }

    @Override
    public Ingredient getRepairIngredient() {
        return delegate.getRepairIngredient();
    }
}
