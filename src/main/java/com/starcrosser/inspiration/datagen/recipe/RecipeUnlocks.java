package com.starcrosser.inspiration.datagen.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Optional;

/**
 * 配方解锁条件的两个小工具。
 * <p>
 * 原版的 {@code RecipeProvider#has} 与 {@code RecipeProvider#getHasName} 是 **protected static**，
 * 只有 {@code RecipeProvider} 的子类用得了。配方拆成多个类之后（见本包），
 * 那些类不再是 {@code RecipeProvider} 的子类，于是把它们原样复刻到这里
 * —— 条件名与触发器的构造方式与 {@code RecipeProvider#inventoryTrigger} 逐字一致，
 * 保证生成的进度 JSON 不变。
 */
public final class RecipeUnlocks {

    private RecipeUnlocks() {
    }

    /** 解锁条件的名字：{@code has_<物品路径>}。 */
    public static String getHasName(ItemLike item) {
        return "has_" + BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }

    /** 「背包里有这件物品」这个解锁条件。 */
    public static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item) {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(
                new InventoryChangeTrigger.TriggerInstance(Optional.empty(),
                        InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                        List.of(ItemPredicate.Builder.item().of(item).build())));
    }
}
