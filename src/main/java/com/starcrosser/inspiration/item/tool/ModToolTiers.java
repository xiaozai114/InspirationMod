package com.starcrosser.inspiration.item.tool;

import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/**
 * 本模组自定义的工具品质。
 * <p>
 * 品质用数据描述而不是写死枚举，将来新增材料只要在这里再挂一个常量即可。
 */
public final class ModToolTiers {

    /**
     * 给一件装备单独指定耐久，其余性能照搬原品质。
     * <p>
     * 因为 {@code TieredItem} 会用品质的 {@code getUses()} 覆盖
     * {@link net.minecraft.world.item.Item.Properties#durability}，想让同一品质下的
     * 剑 / 斧 / 镐各有各的耐久，只能从品质这一层改 —— 详见 {@link DurabilityTier}。
     * 各件装备具体用多少耐久见 {@link ToolDurability}。
     */
    public static Tier withDurability(Tier tier, int uses) {
        return new DurabilityTier(tier, uses);
    }

    /**
     * 翡翠钢：由翡翠钢升级模板锻造出的高档材料，定位与下界合金互有胜负。
     * <p>
     * 挖掘等级取下界合金档（{@link BlockTags#INCORRECT_FOR_NETHERITE_TOOL}），
     * 耐久与挖掘速度更高，攻击加成略低——单件工具的具体手感由各自的攻击力 / 攻速参数决定。
     */
    public static final Tier JADE_STEEL = new SimpleTier(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            2500,
            10.0F,
            3.5F,
            16,
            () -> Ingredient.of(ModItems.JADE_STEEL_INGOT.get()));

    /**
     * 注能翡翠钢：翡翠钢工具的注能升级版，用注能翡翠钢升级模板在锻造台上得到。
     * <p>
     * 挖掘等级同样取下界合金档；耐久、挖掘速度与攻击加成都在翡翠钢的基础上再抬一档，
     * 并且这一档工具额外带「长按右键消耗经验换增益」的能力（见 {@code ChargedBuff}）。
     */
    public static final Tier ENERGIZED_JADE_STEEL = new SimpleTier(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            4000,
            12.0F,
            5.0F,
            20,
            () -> Ingredient.of(ModItems.ENERGIZED_JADE_STEEL.get()));

    private ModToolTiers() {
    }

    /**
     * 数据驱动的品质实现。
     *
     * @param incorrectBlocksForDrops 用该品质的工具挖掘时不掉落物品的方块标签，
     *                                即「挖掘等级」的反面描述
     * @param uses                    耐久
     * @param speed                   挖掘速度倍率
     * @param attackDamageBonus       近战伤害加成
     * @param enchantmentValue        附魔价值
     * @param repairIngredient        铁砧修复材料，延迟构造，避免注册期引用物品
     */
    public record SimpleTier(TagKey<Block> incorrectBlocksForDrops,
                             int uses,
                             float speed,
                             float attackDamageBonus,
                             int enchantmentValue,
                             Supplier<Ingredient> repairIngredient) implements Tier {

        @Override
        public int getUses() {
            return uses;
        }

        @Override
        public float getSpeed() {
            return speed;
        }

        @Override
        public float getAttackDamageBonus() {
            return attackDamageBonus;
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return incorrectBlocksForDrops;
        }

        @Override
        public int getEnchantmentValue() {
            return enchantmentValue;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return repairIngredient.get();
        }
    }
}
