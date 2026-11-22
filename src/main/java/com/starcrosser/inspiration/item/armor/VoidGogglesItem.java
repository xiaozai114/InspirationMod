package com.starcrosser.inspiration.item.armor;

import net.minecraft.world.item.ArmorItem;

/**
 * 虚空护目镜：戴在头上时 +2 护甲，并让玩家看见团簇里的具体 ve 数。
 * <p>
 * 「能看见 ve」靠类型标记实现：客户端渲染器直接判断头部槽位是不是这个物品
 * （见 {@code VoidClusterRenderer}），不需要额外的组件或同步字段。
 */
public class VoidGogglesItem extends ArmorItem {

    /** 耐久倍率，与铁甲同级。 */
    private static final int DURABILITY_MULTIPLIER = 15;

    public VoidGogglesItem() {
        super(ModArmorMaterials.VOID_GOGGLES, ArmorItem.Type.HELMET,
                new Properties().durability(ArmorItem.Type.HELMET.getDurability(DURABILITY_MULTIPLIER)));
    }
}
