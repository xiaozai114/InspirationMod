# 数据生成（`datagen` 包）

`datagen/` 下的 Provider 产出全部资源与数据包。产出目录 `src/generated/resources`
**不应手工编辑** —— 改动要去改对应的 Provider，再跑 `./gradlew runData` 重新生成。

相关：[配方体系](../recipe/README.md) ・ [方块（模型/状态）](../block/README.md) ・ [JEI 的语言键](../jei/README.md)

## Provider 一览

| Provider | 产出 |
| --- | --- |
| `ModRecipesProvider` | 全部配方 JSON（含右键转化、机器配方、数据表驱动的批量配方） |
| `ModBlockStatesProvider` | 方块状态与方块模型 |
| `ModItemModelsProvider` | 物品模型 |
| `ModBlockLootTablesProvider` | 方块战利品表 |
| `ModBlockTagsProvider` | 方块标签（含注入等级标签） |
| `ModEnUsLangProvider` / `ModZhCnLangProvider` | 中英文语言文件 |
| `ModCreativeModeTabs` | 创造模式物品栏（不是 Provider，但同为数据侧） |

## 机器配方

机器本体一律走 `ModRecipesProvider#machine(...)`，形状为 `MSM / SCS / MXM`
（X 为下级机器，基础机型填主要材料），选材表见 [机器文档](../machine/README.md) 的
《机器本体的合成形状》。以后新增机器直接调这个方法，不要再手写 pattern。

## 已知事项

- `ModBlockLootTablesProvider#getKnownBlocks` 只认 `ModBlocks.BLOCKS`，
  **机器方块必须手动加进去**，否则掉不出东西。
- 数据生成时，进度/能耗字段等于默认值会被 codec 省略，读回来自动填默认值，属正常行为，
  不要为此改默认值。
- 不完整方块的模型要手写 `elements`，六向方块的 variant 也要手写（见 [方块文档](../block/README.md)）。
