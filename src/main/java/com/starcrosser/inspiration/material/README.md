# 材料（`material` 包）

相关：[碱性值](../alkali/README.md) ・ [辐射值](../radiation/README.md) ・ [创造栏](../item/README.md)

一个「锭类材料」= 一次声明。声明完调 `register()`，物品、方块、data_maps 条目一次性注册掉；
配套资源（中英语言、物品模型、方块状态、方块模型、战利品表、挖掘标签）由各 datagen Provider
遍历 `ModMaterials.ALL` 自动生成。

## 怎么用

```java
public static final InspirationMaterial CESIUM = InspirationMaterial.create("cesium")
        .name("铯", "Cesium")                          // 派生出「铯锭 / 铯块 / 铯粒 / 铯尘」
        .alkalinity(4)                                 // > 0 才写进 data_map
        .alkaliRevert(() -> ModItems.CESIUM_SALT.get(), 4f)  // 用 AlkaliItem：遇水炸开变回铯盐
        .block(true)                                   // 生成铯块 + 方块物品
        .register();
```

`create("cesium")` 派生的 ID：`cesium_ingot` / `cesium_block` / `cesium_nugget` /
`cesium_grit` / `molten_cesium`。名字派生：「铯锭 / 铯块 / 铯粒 / 铯尘」。
不一致时用 `itemId(...)`、`blockId(...)`、`itemName(...)`、`blockName(...)` 单独覆盖
（例如玄武岩合金的物品 ID 是 `basalt_alloy` 而不是 `basalt_alloy_ingot`）。

## 开关

锭本体总是生成，其余每一项一个开关，**默认全关**：

| 开关 | 生成 | 现状 |
| --- | --- | --- |
| `block(boolean)` | `<base>_block` + 方块物品 | 有块的材料开 |
| `nugget(boolean)` | `<base>_nugget`（粒） | 关：粒尚未纳入玩法 |
| `grit(boolean)` | `<base>_grit`（尘） | 关：尘要等流体提取机那一期 |
| `molten(boolean)` | `molten_<base>` 熔融流体 | 关：尚未实现，打开会抛异常 |

关着的开关不产生任何东西，所以「加材料」和「加内容」是两件可以分开提交的事。
`molten` 现在故意抛异常而不是静默什么都不做 —— 免得开关存在却看不出它没生效。

## 命名约定

| 中文 | 英文 | 说明 |
| --- | --- | --- |
| 锭 | `ingot` | 材料本体 |
| 粉 | `dust` | 化学产物。**现有 4 个物品 ID 已经是 `*_dust`，不动**（老存档靠 ID） |
| 尘 | `grit` | 比粉更粗的碎屑，由破碎矿石得到。粉的 ID 已被占用，所以尘改用 `grit` |
| 粒 | `nugget` | 9 粒 = 1 锭 |

## 「0」的语义

`alkalinity = 0` / `radiation = 0` 表示**没有这个属性**：不写 data_map 条目、不显示 tooltip、
不能当对应发电机的燃料。不要在表里塞一堆 0。

有辐射且有方块时，方块与方块物品的辐射值取「物品值 × 9」（9 枚锭压一块）。

## 配套资源从哪里来

| 资源 | 位置 | 方式 |
| --- | --- | --- |
| 中英语言 | `ModZhCnLangProvider` / `ModEnUsLangProvider` | 遍历（语言文件按 key 排序输出，顺序不影响结果） |
| 物品模型 | `ModItemModelsProvider` | 遍历：锭 + 粒 + 尘 |
| 方块物品模型 | `ModItemModelsProvider` | 遍历：`blockItem(...)` |
| 方块状态/模型 | `ModBlockStatesProvider` | 遍历：`simpleBlock(..., cubeAll(...))` |
| 战利品表 | `ModBlockLootTablesProvider` | 遍历：`dropSelf(...)`；已知方块列表自动含它们 |
| 碱性/辐射 data_map | `ModDataMapsProvider` | `InspirationMaterial#addDataMaps` |
| 挖掘标签 | `ModBlockTagsProvider` | **逐个显式写**（标签 JSON 按 `.add()` 顺序输出，遍历会打乱排列） |
| 创造栏 | `ModCreativeModeTabs` | **逐个显式写**（材料与同期物品穿插排列，是刻意排的） |

## 什么不该放进这里

| 例外 | 原因 |
| --- | --- |
| 核废料、还原粉、各种粉 | 不是锭类材料，只是带化学/辐射属性的物品 |
| 注能三锭（`energized_*`） | 来源是注入而非熔炼，且没有对应方块 |
| 产量 ≠ 1 的合金 | 融合配方要指定 count，统一模板得先留 `productCount` 字段 |
| 原版材料链（铁锭、钻石…） | 不是本模组注册的，但可以在 data_maps 里登记辐射/碱性 |
| 幽匿合金块 / 玄武岩合金块 | 除是材料块外还是注入结构的底座，标签要额外照顾 |

## 已知事项

- 粒与尘现在没有贴图，打开开关会先缺图。贴图脚本在 `tools/textures/`，做那一期时补。
- 熔融流体的注册与贴图要配合「流体提取机」，重构全部做完后单独开一期。
