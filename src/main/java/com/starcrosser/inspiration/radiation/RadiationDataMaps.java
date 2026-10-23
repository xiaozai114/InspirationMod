package com.starcrosser.inspiration.radiation;

import com.mojang.serialization.Codec;
import com.starcrosser.inspiration.Inspiration;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

/**
 * 辐射值的数据地图定义。
 * <p>
 * 辐射值不写死在 Java 里，而是挂在 NeoForge 的数据地图上：物品与方块各一张表，
 * 值就是「每秒向所在区块注入的辐射点数」。这样将来给任何物品/方块配辐射值，
 * 只需要在 {@code data/<命名空间>/data_maps/item|block/radiation.json} 里加一行 ——
 * 包括别的模组的东西。
 * <p>
 * 两张表同名（{@code inspiration:radiation}）但分属不同注册表，
 * 因此数据文件路径分别是 {@code data_maps/item/radiation.json} 与
 * {@code data_maps/block/radiation.json}。
 * <p>
 * 另外还有一张只在物品上的 {@link #ITEM_RADIATION_DENOMINATOR}（周期）：
 * 值 {@code M} 配周期 {@code N} 表示「每 N 秒释放 M 点」，用来表达粒与尘这种
 * 比锭小得多的形态 —— 9 粒 = 1 锭，所以粒写成 {@code 值/9 秒}。
 */
// 不写 bus = Bus.MOD：该属性已被标记待删除。新版 FML 会看事件类型自己分派 ——
// RegisterDataMapTypesEvent 实现了 IModBusEvent，因此这一整类都会挂到 mod 总线。
@EventBusSubscriber(modid = Inspiration.MODID)
public final class RadiationDataMaps {

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "radiation");

    /** 辐射周期表的 ID：与 {@link #ID} 配套，只在物品表上有。 */
    public static final ResourceLocation DENOMINATOR_ID =
            ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "radiation_denominator");

    /**
     * 物品辐射值（分子）：物品在玩家背包里时，每 {@link #ITEM_RADIATION_DENOMINATOR} 秒
     * 向所在区块注入「值 × 数量」点辐射。
     */
    public static final DataMapType<Item, Integer> ITEM_RADIATION = DataMapType
            .builder(ID, Registries.ITEM, Codec.intRange(0, Integer.MAX_VALUE))
            .synced(Codec.INT, false)
            .build();

    /**
     * 物品辐射的周期（分母）：没登记就是 1，即「每秒注入」。
     * <p>
     * 存在的原因是 9 枚粒才等于 1 枚锭：铀锭每秒 2 点，铀粒就该是「每 9 秒 2 点」——
     * 分子与锭相同、分母是 9，于是 9 枚粒的平均速率正好回到 1 枚锭。
     * 值必须 {@code >= 1}；{@code 1} 与不登记等价。
     */
    public static final DataMapType<Item, Integer> ITEM_RADIATION_DENOMINATOR = DataMapType
            .builder(DENOMINATOR_ID, Registries.ITEM, Codec.intRange(1, Integer.MAX_VALUE))
            .synced(Codec.INT, false)
            .build();

    /** 方块辐射值：方块只要有任意一面接触空气，每秒向所在区块注入该值点辐射。 */
    public static final DataMapType<Block, Integer> BLOCK_RADIATION = DataMapType
            .builder(ID, Registries.BLOCK, Codec.intRange(0, Integer.MAX_VALUE))
            .synced(Codec.INT, false)
            .build();

    private RadiationDataMaps() {
    }

    @SubscribeEvent
    public static void register(RegisterDataMapTypesEvent event) {
        event.register(ITEM_RADIATION);
        event.register(ITEM_RADIATION_DENOMINATOR);
        event.register(BLOCK_RADIATION);
    }
}
