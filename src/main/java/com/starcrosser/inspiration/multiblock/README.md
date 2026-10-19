# multiblock —— 结构模拟机

本模组第一台**多方块**机器：用高能机器外壳围出一个 4 宽 × 3 高 × 3 长的中空长方体，
机身上嵌五种仓室，主方块负责推进度、结算战利品。

## 为什么只借 Multiblocked2 的「结构层」

多方块成型判定（把一串字符图案摊到世界上逐格比对、还要处理朝向与重复层）自己写不划算，
1.21.1 上成熟的选择只有 **Multiblocked2（MBD2）**。但 MBD2 21.1.1 已经是一整套
「定义 + 蓝图 + 状态机」的机器框架：一旦实现它的 `IMultiController`，就**连带**要实现整套
`IMachine`（前后朝向、配方逻辑、capability holder……），而这些正是我们想留在 Titanium 里的东西。

所以这里只用它的两样东西：

| 只用 | 作用 |
| --- | --- |
| `FactoryBlockPattern` / `BlockPattern` / `Predicates` | 描述形状、逐格匹配 |
| `MultiblockState` | 匹配结果（成员位置缓存、错误位置） |

控制器**不实现** `IMultiController`，改成调
`BlockPattern#checkPatternAtWithoutController(state, facing)`，并自己遍历四个水平朝向
—— 因为那个方法的 `facing` 参数需要一个「控制器正面」，而我们的正面就是
`RotatableBlock` 的 `subfacing`。

MBD2 与 LDLib2 官方禁止 shade / jar-in-jar，所以两者都在 `neoforge.mods.toml` 里声明为
`required`（直接用到 `BlockInfo`，所以 LDLib2 也得显式声明）。

## 结构

形状只有 `pattern/SimulatorPattern.SHAPE` 一份数据源，MBD2 的 aisle 与客户端虚影都从它生成：

```
SHAPE[层][行]（行自下而上，层自后往前）
  后  CCCC / CCCC / CCCC
  中  CCCC / C  C / CCCC   ← 中间两格是空腔
  前  CCCC / CMCC / CCCC   ← 正面中层左二那格是主方块
```

36 格 = 外壳 / 部件 34 + 空腔 2。`C` 用**一个** `Predicates.custom` 谓词同时接受
高能机器外壳与五种仓室 —— 「仓室替换外壳」就是这么实现的，而不是拼一串 `blocks(...)`。

内部那两格用的是 `Predicates.any()`，**留空、塞满外壳、放别的杂物都算成型**。
最初写的是 `Predicates.air()`，结果玩家搭的时候得精确数着这两格别填 —— 而填不填对判定
没有任何意义，纯粹是难为人（实测「内部留空」与「内部塞满外壳」两种搭法都能成型）。

**坐标语义（容易踩）**：`FactoryBlockPattern.start()` 的三个相对方向是
`{LEFT, UP, FRONT}`，`BlockPattern#setActualRelativeOffset` 把它们逐个贴到世界的 X/Y/Z 上。
`aisle(String...)` 的**第一个字符串是最低那一行**（`stringDir = UP`），
而 `aisle()` 调用顺序沿 `FRONT` 递增 —— 也就是说后面的 aisle 更靠前。
`checkPatternAt` 里 `x = a - centerOffset[0]`、`y = b - centerOffset[1]`、
`z = c - centerOffset[2]`，其中 `centerOffset` 就是主方块那格在图案里的下标。
预览要摆得和实际成型一致，就必须照抄这套公式，`SimulatorPattern#rotate` 就是它的翻版。

## 五种仓室

仓室全是本模组自己的 Titanium 方块实体，控制器成型后按类型把它们收集起来统一调度。
它们**自己不做任何事**，只是提供能力：

| 仓室 | 方块实体基类 | 提供 | 界面的必要性 |
| --- | --- | --- | --- |
| 输入仓 | `HatchTile`（`ActiveTile`） | 9 格物品 | 要，`openGui` |
| 输出仓 | `HatchTile` | 18 格物品 | 要 |
| 能量端口 | `PoweredTile` | 2,000,000 FE 缓冲 | 不要（罐子靠线缆灌） |
| 灵感提供仓 | `ActiveTile` | 128,000 mB 充能罐 | 不要 |
| 数据仓 | `HatchTile` | 1 格，只收 `StructureDataItem` | 要 |

`HatchTile` 继承 `ActiveTile` 而不是 `BasicTile`：**只有 `ActiveTile` 带 `openGui`**。
能量端口与灵感提供仓不需要界面，所以走了别的基类 —— 能量缓冲只在 `PoweredTile` 那条链上有。

灵感提供仓的容量按「一份沉船数据跑完一整轮」定：12000 刻 × 8 mB = 96,000 mB，取 128,000。
罐体设成 `Action.FILL`，控制器抽的时候必须走 `drainForced`（普通 `drain` 会被动作校验拦掉）。

## 一轮模拟

```
每 20 刻 checkStructure() ──► formed?
        │                        │
        └─ 否：状态 = NOT_FORMED  └─ 是：collectHatches() 收集四类仓室位置
                                  │
每刻 pullEnergy()  从能量端口搬电到控制器内部缓冲（缓冲 maxReceive/maxExtract = 0，不接外部线缆）
每刻 flushPendingOutput()  把上一轮没塞下的产物往输出仓倒
     progressBar 的 canWork / onWorkTick / onWorkFinish 推进度
```

进度条回调是 Titanium 的三个 `Runnable`（**不是**带参函数引用）：
`setOnStart` / `setOnTickWork` / `setOnFinishWork`，要读进度条就自己存字段。

- 数据仓里那份数据**不被消耗**：每个进度周期重读槽位，取出来就停机。
- `spec.duration()` 每次变化都同步到 `progressBar.setMaxProgress`，
  换一份数据才不会按上一份的时长走。
- 跑完 `rollLoot` 在数据的候选战利品表里随机挑一张 roll 一次，塞进 `pendingOutput`；
  塞不进输出仓就留着，下一刻继续倒，此时状态是 `OUTPUT_FULL`。

## 状态那一行

`SimulatorStatus` 的**枚举顺序就是优先级**，`updateStatus()` 按顺序挑第一个成立的：

`NOT_FORMED` → `NO_DATA` → `OUTPUT_FULL` → `NO_ENERGY` → `NO_CHARGE` → `WORKING`

玩家看到的永远是最该先解决的那个问题（结构没搭好比没电更重要）。
值存在 `@Save int status` 里，服务端改动后 `markForUpdate()` 推给客户端；
`client/StatusAddon` 在界面 `(8, 90)` 画一行 —— 176×184 的界面里，玩家背包从 y=102 开始。

## 为什么不给多方块装插件

多方块的电和充能都来自外部仓室，控制器自己只有一个 64,000 FE 的周转缓冲。
插件改写的是**机器自身的容量与速度**，在这里要么没意义（容量），要么会破坏
「仓室供能」这个设计（速度）。所以 `MachineTile` 的四个 augment 槽保持空置，界面上也不显示。

## 怎么让玩家知道该往哪放

结构是 4×3×3、主方块在**正面中层左二**，光看 tooltip 根本搭不出来。所以做了三件事：

### 1. 结构投影仪（`item/ProjectorItem`）

手持右键主方块，就在原地把整座结构投成虚影，持续 30 秒，不消耗物品。
由「铁锭 + 思维矩阵锭」注入得到（1 级底座，能量窗口 50k~150k FE）。

投影期间**不打开界面**：`StructureSimulatorTile#onActivated` 见到主手是投影仪就返回
`ItemInteractionResult.SUCCESS`，否则一开 GUI 就把刚投出来的虚影挡住了。

### 2. 客户端虚影（`client/ProjectorPreviewHandler` + `client/SimulatorPreviewRenderer`）

状态拆成两半：`ProjectorPreviewHandler` 记「从哪一格、按哪个朝向、什么时候过期」
（纯客户端，不做网络同步 —— 投影只是视觉辅助），渲染器每帧去画。

- 每格缩到 `0.8`（和 MBD2 自己的结构预览同一个做法），缩小的方块与四周实体方块之间留缝，
  一眼能看出是预览而不是真摆出来的。
- 朝向取玩家的水平朝向，于是结构从主方块**朝远离玩家的方向**展开，正面那层正对玩家。
- 画在 `AFTER_TRANSLUCENT_BLOCKS`：不透明方块已写完深度，虚影能正常被地形遮挡。
- 用**独立的** `MultiBufferSource#immediate(ByteBufferBuilder)`，不去动 Minecraft 那份共享缓冲
  —— 调 `endBatch()` 会把别人还没画完的批次提前冲掉。

### 3. JEI 结构预览（`jei/category/StructurePreviewCategory`）

逐层看最清楚，所以**每一层注册成一条配方**，JEI 自带的翻页就是翻层（`StructurePreview` 只带层下标）。
每页手绘 4×3 网格：`C` 位置放外壳图标、`M` 放控制器，内部两格画一层淡色底块表示「这里不用放」。

网格是 `draw` 里手绘而不是用槽位 —— 结构要的是「格子挨着格子」的观感，
JEI 标准槽位之间有间隙，摆出来更像零件清单而不是结构。代价是 `draw` 在槽位之后执行，
所以底部分隔线以下那个显示控制器的小槽位必须避开手绘区域。
背景贴图 `structure_preview.png` 由 `tools/textures/gen_jei_background.py` 生成，
`shaft_x` 给负数让脚本跳过画箭头（这里没有「输入→输出」的方向）。

## 加一份新数据

1. `item/ModItems` 里注册一个 `StructureDataItem`，构造时给一份 `SimulationSpec`
   （时长 / 每刻耗电 / 候选战利品表）。
2. 装配配方里产出它。
3. 语言键：物品名 + 可选的专属 tooltip。三行参数
   （`tooltip.inspiration.structure_data.duration|energy|charge`）由基类统一挂，不用重写。

机器代码一行都不用动 —— 数据是「参数包」，不是新方块。
