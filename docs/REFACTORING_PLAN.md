# 重构纪要（P0–P3 已完成）

基线 `5194af5`，P3 收尾打 `p3-done`。四期全部完成，本文件只留**结论、决策与红线**；
各模块现在的做法与坑写在对应包的 `README.md` 里（索引见仓库根 `README.md`），本文件不再重复。

## 起点与目标

起点是「同一份机器清单手写 9 遍」：加一台机器要改创造栏、两种语言、方块状态、两处标签、
两处战利品表、配方，漏一处就是黑紫方块或缺名。目标是**一处声明、Provider 遍历生成**，
顺带把客户端代码从服务端路径上物理隔离出去。

## 现在的分层

| 层 | 包 |
| --- | --- |
| 内容 | `block` `item` `fluid` `material` `machine` `injection` `multiblock` `radiation` `alkali` `recipe` `entity` `guide` |
| 支撑 | `datagen` `config` `client`（纯客户端） `util` `event`（纯玩法监听） |

`event/` 只放**不持状态、不被引用**的玩法监听器；机制主体（辐射、数据地图）留在自己的包里。
`client/` 之外出现客户端类必须标 `@OnlyIn(Dist.CLIENT)`，`util/ClientIsolationCheck` 在开服时自动扫一遍。

## 各期结果

| 期 | 做了什么 | 关键 commit |
| --- | --- | --- |
| P0 止血 | 修玩家能感知的 bug、清死代码 | — |
| P1.1 | 机器等级化：一台机器 = 一份 `MachineSpec`，同族两档共用 Tile+Block | `5184ad2` `e45e830` |
| P1.2 | 四套配方抽 `InspiredRecipe` 基类（产物是流体的不继承） | — |
| P1.3 | 五件套工厂化**放弃**（单继承下无解，见下） | — |
| P1.4 | 仓室抽 `InventoryHatchTile → HatchTile → ActiveTile` | — |
| P2.1 | `registry/MachineRegistry`：注册即生成语言 / 状态 / 标签 / 战利品 | `b1c0ed6` |
| P2.2 | 拆 `ModRecipesProvider`（1303 行 → `datagen/recipe/` 十余个文件） | `eaa1e08` |
| P2.3 | 事件集中到 `event/`，`event/README.md` 成为订阅类唯一清单 | `a8a9e61` |
| P2.5 | 材料统一：`ModMaterials` 一行声明生成锭/块/粒/尘 + data_maps | `cce9506` `02daec3` `ef46f07` |
| P3 | 客户端隔离（`client/` 五子包 + `jei/` + 自动体检）、命名统一 | `p3-start` → `p3-done` |

## 关键决策（含与原方案的偏离）

- **五件套工厂化不做**：`TieredItem` 与带行为的物品只能单继承，抽工厂反而更绕，保持现状。
- **材料系统的三处偏离**：注能三锭也进 `ModMaterials`（来源不影响注册）；幽匿 / 玄武岩合金块也进
  （底座身份只影响标签，标签本来就显式写）；`molten` 开关先抛异常，等流体提取机实现。
- **创造栏**：从一页拆成两页 —— 材料页只放**锭类材料及附属（锭 / 粒 / 块 / 尘）**，
  其余原料（矿、粉、盐、晶、煤、核心部件、废料）放灵感页末尾。分页后两页仍**逐个显式写**，
  因为展示顺序是刻意排的；唯一遍历清单的是锭类那一组。
- **注册顺序 = 标签 JSON 输出顺序**，机器清单的排列有语义，别随手重排。
- **创造栏全自动**：灵感页遍历 `MachineRegistry`，末尾再加一层兜底循环（本模组注册、两页都没收的
  物品自动补上）；只有 **JEI 催化剂**仍需手工补一行。

## 红线

1. **不动任何 `ResourceLocation`**（方块、物品、配方、标签、数据地图的 ID 都算）。
2. 同一个 commit 不混「改结构」与「加内容」。
3. 机器方块不在 `ModBlocks.BLOCKS` 里 → 绕过 `MachineRegistry` 手工注册时，必须自己补
   战利品表、方块标签、方块状态与语言。
4. 清单类数据（机器、材料）只有一处权威来源；新增内容改那一处，不要另写一份。

## 验收（每期照跑）

```bash
rm -f src/generated/resources/.cache/* && ./gradlew runData --offline   # 改代码后必须先清缓存
./gradlew compileJava --offline
./gradlew jar --offline && unzip -l build/libs/*.jar | grep -i selftest  # 确认自检类没进产物
```

改行为时补一次服务端自检（临时类 + `server.halt(false)`，跑完删掉、删 `run/crash-reports`）；
贴图与模型**只能在客户端**校验。
