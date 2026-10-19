package com.starcrosser.inspiration.alkali;

import com.mojang.serialization.Codec;
import com.starcrosser.inspiration.Inspiration;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

/**
 * 碱性值的数据地图定义。
 * <p>
 * 与辐射值同一套思路（见 {@code radiation/RadiationDataMaps}）：数值不写死在 Java 里，
 * 而是挂在 NeoForge 的数据地图上，文件在
 * {@code data/<命名空间>/data_maps/item/alkalinity.json}。碱性值只属于物品，
 * 所以只有物品表一张 —— 碱金属放进碱性发电机时，每刻产电 {@code 100 × 碱性值} FE。
 * <p>
 * 加了 {@code .synced(...)}，客户端才能读到数值来画 tooltip。
 */
// 不写 bus = Bus.MOD：该属性已被标记待删除，新版 FML 会按事件类型自己分派。
@EventBusSubscriber(modid = Inspiration.MODID)
public final class AlkalinityDataMaps {

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "alkalinity");

    /** 物品碱性值：碱性发电机燃烧该物品时，每刻产电「100 × 值」FE。 */
    public static final DataMapType<Item, Integer> ITEM_ALKALINITY = DataMapType
            .builder(ID, Registries.ITEM, Codec.intRange(0, Integer.MAX_VALUE))
            .synced(Codec.INT, false)
            .build();

    private AlkalinityDataMaps() {
    }

    @SubscribeEvent
    public static void register(RegisterDataMapTypesEvent event) {
        event.register(ITEM_ALKALINITY);
    }
}
