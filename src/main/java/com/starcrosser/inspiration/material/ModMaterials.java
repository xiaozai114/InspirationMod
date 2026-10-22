package com.starcrosser.inspiration.material;

import com.starcrosser.inspiration.item.ModItems;

import java.util.ArrayList;
import java.util.List;

/**
 * 本模组的锭类材料清单。
 * <p>
 * 这里只放「声明」：每个材料一行调用链，把物品、方块、data_maps 条目一次说清。
 * 配套资源（中英语言、物品模型、方块状态、方块模型、战利品表、挖掘标签、创造栏）
 * 由各 datagen Provider 遍历 {@link #ALL} 自动生成，加材料不用再逐个补。
 * <p>
 * 见 {@code material/README.md} 里的开关说明与「什么不该放进这里」。
 */
public final class ModMaterials {

    /** 全部材料，按声明顺序；datagen 遍历它生成配套资源。 */
    public static final List<InspirationMaterial> ALL = new ArrayList<>();

    /**
     * 铯：铯粉高炉冶炼得到，遇水炸开并变回铯盐（半径 4 格），碱性 4。
     * 方块是铯块（9 枚锭压一块，硬度 2.5/6.0，镐挖）。
     */
    public static final InspirationMaterial CESIUM = InspirationMaterial.create("cesium")
            .name("铯", "Cesium")
            .alkalinity(4)
            .alkaliRevert(() -> ModItems.CESIUM_SALT.get(), 4f)
            .block(true)
            .register();

    private ModMaterials() {
    }

    /**
     * 触发静态初始化（把所有材料注册进物品表与方块表）。
     * 必须在 {@code ModItems.register(bus)} 与 {@code ModBlocks.register(bus)} 之前调用，
     * 否则注册表已经冻结，再注册会抛异常。
     */
    public static void init() {
    }
}
