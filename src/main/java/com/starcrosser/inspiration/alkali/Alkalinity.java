package com.starcrosser.inspiration.alkali;

import net.minecraft.world.item.ItemStack;

/**
 * 碱性值的查询。
 * <p>
 * 数值本身来自 {@link AlkalinityDataMaps}，这里只负责「取出来」：
 * 单个物品取单件值，与堆里有多少件无关（碱性发电机一次只烧一件）。
 */
public final class Alkalinity {

    private Alkalinity() {
    }

    /** 单个物品的碱性值；没有登记过就是 0。 */
    public static int of(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        Integer value = stack.getItemHolder().getData(AlkalinityDataMaps.ITEM_ALKALINITY);
        return value == null ? 0 : value;
    }
}
