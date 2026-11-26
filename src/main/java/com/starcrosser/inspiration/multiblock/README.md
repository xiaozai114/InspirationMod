# 多方块模拟机

结构模拟机为 4×3×3，读取结构数据并抽取战利品。矿物模拟机为 3×5×3，读取维度数据并按权重产出一组矿石。
两台机器的空腔均允许留空或填充。可运行的结构机示例使用 29 个外壳、1 个控制器及数据仓、能量端口、灵感提供仓、输出仓各 1 个。
矿物机示例使用 38 个外壳、1 个控制器及数据仓、能量端口、输出仓各 1 个。仓室均可与其他外壳格对调。

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
那格的下标。客户端使用 MBD2 的 `collectTrackedPositions` 取得实际参与匹配的位置。

## 五种仓室

仓室都是本模组的 Titanium 方块实体，**自己不做任何事**，只提供能力：

| 仓室 | 基类 | 提供 | 界面 |
| --- | --- | --- | --- |
| 输入仓 | `InventoryHatchTile` | 9 格物品 | 要 |
| 输出仓 | `InventoryHatchTile` | 18 格物品 | 要 |
| 灵感提供仓 | `InventoryHatchTile` | 1 充能槽 + 128,000 mB 罐 | 要 |
| 数据仓 | `InventoryHatchTile` | 1 格，接受结构数据或维度数据；控制器读取相应类型 | 要 |
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

- 数据卡可重复使用。拔卡暂停，放回同种卡续作，换成其他种类重新计时。存档同时保存进度与卡的注册名。
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
3. **JEI 搭建预览**：每台机器有一个可运行的示例，拖动旋转，箭头切换完整结构和水平楼层。材料清单与场景共用同一组方块。
4. **JEI 生产页**：从注册的数据卡读取耗时、耗电和产出。结构卡列出各战利品表的抽取概率；维度卡列出每组矿石的数量和权重占比。

## 模拟数据

矿物数据在 `data/DimensionTables.java`。主世界和下界每轮 120 刻，末地 1,200 刻，每轮抽一组。权重与数量同时供控制器和 JEI 读取。末地钨矿只从这张表产出，用镐可回收本体。

结构数据在 `data/StructureSite.java`。每轮按列出的权重选择一个原版战利品表，由服务器用当前数据包结算。村庄包含职业和民居箱，古迹废墟区分普通和稀有考古。试炼密室的容器、普通宝库、不祥宝库分为三张卡。宝库调用顶层奖励表，由原版决定内部奖励档次。

维度卡和结构卡用灵感装配台制作。三种维度卡各需要 16 B 对应流体，由高能装配台的单个原料罐供齐。下界使用熔岩，末地使用虚空流体，每颗虚空露珠可提取 1 B。新增卡的样本清单见 `datagen/recipe/SimulatorCardRecipes.java`。

### 结构卡配方与模拟消耗

每张卡装配时还需空白数据 ×1，基准装配时间 600 刻、400 FE/刻。下表后两列为结构模拟机的运行数值。所有卡运行时另耗灵感充能 8 mB/刻，每轮结算一张战利品表。

| 结构数据 | 装配样本 | 模拟耗时（刻） | 模拟耗电（FE/刻） |
| --- | --- | ---: | ---: |
| 沉船 | 任意木板 ×64、水 4 B | 12,000 | 1,000 |
| 废弃矿井 | 铁轨 ×32、橡木栅栏 ×16、蜘蛛网 ×4 | 4,800 | 500 |
| 地牢 | 苔石 ×32、骨头 ×16、腐肉 ×16 | 4,800 | 500 |
| 沙漠神殿 | 切制砂岩 ×32、橙色陶瓦 ×16、TNT ×4、刷子 ×1 | 7,200 | 800 |
| 沙漠水井 | 砂岩 ×32、砂岩台阶 ×16、刷子 ×1 | 2,400 | 300 |
| 丛林神庙 | 苔石 ×32、绊线钩 ×4、发射器 ×2 | 7,200 | 800 |
| 雪屋 | 雪块 ×32、白色床 ×1、金苹果 ×1 | 3,600 | 400 |
| 海底废墟 | 沙砾 ×32、沙子 ×32、海晶石 ×16、刷子 ×1 | 4,800 | 600 |
| 古迹废墟 | 陶瓦 ×32、泥砖 ×32、沙砾 ×16、刷子 ×1 | 6,000 | 650 |
| 埋藏的宝藏 | 沙子 ×64、箱子 ×1、海洋之心 ×1 | 9,600 | 1,000 |
| 村庄 | 干草捆 ×16、绿宝石 ×8、钟 ×1 | 3,600 | 400 |
| 掠夺者前哨站 | 深色橡木原木 ×32、弩 ×1、白色旗帜 ×1 | 6,000 | 650 |
| 要塞 | 石砖 ×64、书架 ×16、末影之眼 ×4 | 9,600 | 1,200 |
| 林地府邸 | 深色橡木木板 ×64、书架 ×16、不死图腾 ×1 | 12,000 | 1,400 |
| 远古城市 | 深板岩瓦 ×64、幽匿块 ×32、回响碎片 ×2 | 14,400 | 2,000 |
| 废弃传送门 | 黑曜石 ×16、哭泣的黑曜石 ×4、金锭 ×8 | 4,800 | 600 |
| 下界要塞 | 下界砖块 ×64、烈焰棒 ×8、下界疣 ×16 | 8,400 | 1,000 |
| 堡垒遗迹 | 磨制黑石砖 ×64、镶金黑石 ×8、金块 ×4 | 12,000 | 1,600 |
| 末地城 | 紫珀块 ×64、末地烛 ×16、潜影壳 ×2 | 14,400 | 2,200 |
| 试炼密室 | 凝灰岩砖 ×64、铜格栅 ×16、旋风棒 ×2 | 6,000 | 800 |
| 普通宝库 | 凝灰岩砖 ×32、雕纹铜块 ×8、试炼钥匙 ×1 | 9,600 | 1,400 |
| 不祥宝库 | 磨制凝灰岩 ×32、雕纹铜块 ×8、不祥试炼钥匙 ×1 | 16,000 | 2,400 |
