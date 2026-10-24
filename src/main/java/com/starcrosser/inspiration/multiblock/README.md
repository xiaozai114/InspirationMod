# multiblock —— 结构模拟机

本模组第一台**多方块**机器：高能机器外壳围出 4 宽 × 3 高 × 3 长的中空长方体，
机身上嵌五种仓室，主方块推进度并结算战利品。

`blockentity/` 放控制器、`blockentity/part/` 放五种仓室（`part` 是「部件」的分类子包，
**不是**与 `blockentity` 平级的概念）。包内一律用 `blockentity` 而不是 `tile`。

## 只借 MBD2 的「结构层」

成型判定（字符图案摊到世界逐格比对 + 朝向 + 重复层）自己写不划算，1.21.1 上成熟的选择只有
Multiblocked2。但实现它的 `IMultiController` 会**连带**要求实现整套 `IMachine`
（朝向、配方逻辑、capability holder），那些正是要留在 Titanium 里的东西。

所以只用两样：`FactoryBlockPattern` / `BlockPattern` / `Predicates`（描述形状、逐格匹配）与
`MultiblockState`（匹配结果）。控制器**不实现** `IMultiController`，改调
`checkPatternAtWithoutController(state, facing)` 并自己遍历四个水平朝向（`facing` 要一个
「控制器正面」，我们的是 `subfacing`）。两库禁止 shade，故在 `neoforge.mods.toml` 声明 `required`
（用到了 LDLib2 的 `BlockInfo`）。

## 结构

形状只有 `pattern/SimulatorPattern.SHAPE` 一份数据源，MBD2 aisle 与客户端虚影都从它生成：

```
SHAPE[层][行]（行自下而上，层自后往前）
  后  CCCC / CCCC / CCCC
  中  CCCC / C  C / CCCC   ← 中间两格空腔
  前  CCCC / CMCC / CCCC   ← 正面中层左二那格是主方块（M）
```

36 格 = 外壳 / 部件 34 + 空腔 2。`C` 用**一个** `Predicates.custom` 同时接受高能机器外壳与
五种仓室 —— 「仓室替换外壳」就是这么来的。空腔用 `Predicates.any()`：留空、塞满外壳、放杂物
都算成型（最初写成 `air()`，逼玩家数格子，毫无意义）。

**坐标语义（容易踩）**：`FactoryBlockPattern.start()` 的方向是 `{LEFT, UP, FRONT}`；`aisle(...)` 的
**第一个字符串是最低那一行**，而调用顺序沿 `FRONT` 递增（后面的 aisle 更靠前）。`checkPatternAt` 里
`x = a - centerOffset[0]`、`y = b - centerOffset[1]`、`z = c - centerOffset[2]`，`centerOffset` 即主方块
那格的下标；预览要跟成型一致就得照抄这套公式（`SimulatorPattern#rotate` 是它的翻版）。

## 五种仓室

仓室都是本模组的 Titanium 方块实体，**自己不做任何事**，只提供能力：

| 仓室 | 基类 | 提供 | 界面 |
| --- | --- | --- | --- |
| 输入仓 | `InventoryHatchTile` | 9 格物品 | 要 |
| 输出仓 | `InventoryHatchTile` | 18 格物品 | 要 |
| 灵感提供仓 | `InventoryHatchTile` | 1 充能槽 + 128,000 mB 罐 | 要 |
| 数据仓 | `InventoryHatchTile` | 1 格，只收 `StructureDataItem` | 要 |
| 能量端口 | `PoweredTile` | 2,000,000 FE 缓冲 | 不要 |

继承链 `InventoryHatchTile` → `HatchTile` → `ActiveTile` → `BasicTile`：

- `HatchTile` 只做一件事：覆写 `onActivated` 调 `openGui`（**`ActiveTile` 以下没人调它**，
  直接继承的仓室右键毫无反应 —— 灵感提供仓原先就是这么打不开的）。
- `InventoryHatchTile` 管一个物品栏：`@Save` 字段、建 `InventoryComponent`、`addInventory`、
  `getInventory` 收在一处，子类只传「名字 / 位置 / 格数 / 排布 / 入槽过滤」。
- 能量端口不在链上：缓冲只在 `PoweredTile` 上有，而它不提供 `openGui`。

⚠️ `@Save` 字段放基类**安全**：`NBTManager` 会沿 `getSuperclass()` 扫父类字段（存盘键是字段名）。

灵感提供仓容量按「跑完一整轮」定：12,000 刻 × 8 mB = 96,000，取 128,000。
可右键放灵感 / 碎片，也可管道灌；罐体 `Action.FILL` → 控制器抽取必须 `drainForced`。

## 一轮模拟

```
每 20 刻 checkStructure() → 否：NOT_FORMED；是：collectHatches()
每刻 pullEnergy()        从能量端口搬到控制器内部缓冲（缓冲对外 maxReceive/maxExtract = 0）
每刻 flushPendingOutput() 把上一轮没塞下的产物往输出仓倒
progressBar 的 canWork / onWorkTick / onWorkFinish 推进度
```

进度条回调是 Titanium 的三个 `Runnable`（`setOnStart` / `setOnTickWork` / `setOnFinishWork`，
**不是**带参函数引用），要读进度就自己存字段。

- 数据仓那份**不消耗**：每周期重读槽位，取不出就停机；`spec.duration()` 变化时同步 `setMaxProgress`。
- 跑完 `rollLoot` 从候选表挑一张 roll 进 `pendingOutput`，塞不下就留到下一刻（状态 `OUTPUT_FULL`）。

## 状态那一行

`SimulatorStatus` 的**枚举顺序即优先级**：`NOT_FORMED → NO_DATA → OUTPUT_FULL → NO_ENERGY →
NO_CHARGE → WORKING` —— 玩家看到的永远是最该先解决的那个。值存 `@Save int status`，
服务端 `markForUpdate()` 推给客户端，`client/extensions/StatusAddon` 在界面 `(8, 90)` 画一行
（玩家背包从 y=102 开始）。

## 为什么不装插件

电和充能都来自外部仓室，控制器自己只有 64,000 FE 周转缓冲；插件改的是机器自身容量与速度，
在这里要么没意义、要么破坏「仓室供能」的设计 —— 四个 augment 槽保持空置、不显示。

## 怎么让玩家知道该往哪放

1. **结构投影仪**（`item/ProjectorItem`）：右键主方块把结构投成虚影 30 秒、不消耗；
   由「铁锭 + 思维矩阵锭」注入得到（1 级，50k~150k FE）。投影期间**不开界面**
   （`onActivated` 见主手是投影仪就返回 `SUCCESS`，否则 GUI 挡住虚影）。
2. **客户端虚影**：`ProjectorPreviewHandler` 记「从哪格、哪个朝向、何时过期」（纯客户端，不同步），
   `SimulatorPreviewRenderer` 每帧画 —— 每格缩到 `0.8`、朝向取玩家水平朝向（结构朝远离玩家展开）、
   画在 `AFTER_TRANSLUCENT_BLOCKS`（能被地形遮挡），并用**独立的**
   `MultiBufferSource#immediate(ByteBufferBuilder)`（动共享缓冲的 `endBatch()` 会冲掉别人的批次）。
3. **JEI 结构预览**：**每层一条配方**，翻页即翻层。每页手绘 4×3 网格（`C` 外壳、`M` 控制器、
   空腔淡色底块），不用标准槽位 —— 结构要「格子挨着格子」的观感；`draw` 在槽位之后跑，
   底部分隔线下的小槽位要避开手绘区。背景由 `gen_jei_background.py` 生成，`shaft_x` 给负数跳过箭头。

## 加一份新数据

1. `item/ModItems` 注册一个 `StructureDataItem`，构造时给 `SimulationSpec`（时长 / 每刻耗电 / 候选战利品表）。
2. 装配配方产出它。
3. 语言键：物品名 + 可选 tooltip。三行参数（`tooltip.inspiration.structure_data.duration|energy|charge`）
   由基类统一挂。

机器代码一行都不用动 —— 数据是「参数包」，不是新方块。
