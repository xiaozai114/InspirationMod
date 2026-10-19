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
 */
// 不写 bus = Bus.MOD：该属性已被标记待删除。新版 FML 会看事件类型自己分派 ——
// RegisterDataMapTypesEvent 实现了 IModBusEvent，因此这一整类都会挂到 mod 总线。
@EventBusSubscriber(modid = Inspiration.MODID)
public final class RadiationDataMaps {

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "radiation");

    /** 物品辐射值：物品在玩家背包里时，每秒向所在区块注入「值 × 数量」点辐射。 */
    public static final DataMapType<Item, Integer> ITEM_RADIATION = DataMapType
            .builder(ID, Registries.ITEM, Codec.intRange(0, Integer.MAX_VALUE))
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
        event.register(BLOCK_RADIATION);
    }
}
