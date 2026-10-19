# 碱性值（`alkali` 包）

> 相关：[辐射机制](../radiation/README.md) ・ [机器与碱性发电机](../machine/README.md) ・
> [配方与数据生成](../recipe/README.md)

与辐射值并列的第二套「物品属性」：只作用于物品，不参与区块结算，唯一用途是给碱性发电机
决定每刻产多少电。数值放在 NeoForge 数据地图里，因此任何物品（含别的模组的）只要在
`data/<命名空间>/data_maps/item/alkalinity.json` 里登记一行就能烧。

## 数值

| 物品 | 碱性值 |
| --- | --- |
| 钙粉 | 1 |
| 铯粉、铯锭 | 4 |
| 钫粉、钫锭 | 6 |

口径与 `AlkaliItem` 的爆炸半径一致（钙 0.1 → 1、铯 4 → 4、钫 6 → 6）：
碱性越强，遇水越炸，烧起来也越猛。

碱性的产物价值换算：碱性发电机一份燃料烧 12,000 刻，每刻 `100 × 碱性值` FE，
所以一份钙粉 1.2M FE、一份铯粉 4.8M FE、一份钫锭 7.2M FE。

## 文件职责

| 文件 | 职责 |
| --- | --- |
| `AlkalinityDataMaps` | 物品表 `data_maps/item/alkalinity.json` 的数据地图定义（`inspiration:alkalinity`，已 `.synced(...)`） |
| `Alkalinity` | 查询：`of(ItemStack)`，未登记返回 0 |
| `client/AlkalinityTooltips` | 在物品名字下面补一行「碱性值：x」（客户端限定） |

## 实现要点

- 与 `RadiationDataMaps` 同一套写法，**只有物品一张表**（不可能有「碱性方块」被烧）。
  两张数据地图都订阅同一个 `RegisterDataMapTypesEvent`，互不影响。
- 必须 `.synced(...)`，否则客户端读不到数值、tooltip 是空的。
- 数据地图文件在 `src/main/resources/data/inspiration/data_maps/item/alkalinity.json`，
  不走 datagen（与辐射表相同，手写维护）。
