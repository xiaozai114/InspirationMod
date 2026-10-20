# Inspiration 重构方案

> **基线 commit**：`5194af5`（整体重构前的安全基线）—— 已推送到 `origin/master`。
> 本文所有行号/数字均以该 commit 为准。任何时候想回到起点：`git checkout 5194af5`。
>
> 环境：MC 1.21.1 / NeoForge 21.1.256 / Titanium 1.21-4.0.50 / JEI 19.57.0.451 /
> Multiblocked2 21.1.1 + LDLib2 2.2.39.a（只借结构层）。

---

## 0. 怎么读这份文档

| 如果你的目标是… | 直接看 |
| --- | --- |
| 想知道项目现在长什么样 | 第 1 节 |
| 想知道重构完应该长什么样 | 第 2 节 |
| 想知道"到底哪里有问题" | 第 3 节（12 个 bug + 7 个结构问题） |
| 想立刻动手 | 第 4 节（P0 → P3，每期都带步骤、验收、回滚点） |
| 怕改坏 | 第 5 节（必须保住的设计）、第 6 节（红线）、第 7 节（验证清单） |

**三条总原则**（后面所有决定都从这里推出来）：

1. **先止血，再整形，最后才是搬家。** 能改出 bug 的重构，收益再大也要排在修 bug 之后。
2. **每期结束必须能跑。** 每期一个 commit + 一个 tag，`runData` + `runServer` 全绿才进下一期。
3. **只改代码组织，不改游戏 ID。** 方块/物品/配方的 `ResourceLocation` 一个都不能变，否则老存档炸。

---

## 1. 现状基线

### 1.1 规模

| 指标 | 数值 |
| --- | --- |
| Java 文件 / 行数 | 202 个 / 18,653 行 |
| 包数量 | 41 个（最大 `item/custom` 30 个类、`machine/blockentity` 15 个） |
| 手写资源 / datagen 生成 | 180 / 538 |
| 内容量 | 83 物品、20 方块、11 台机器、6 个多方块方块、4 流体、4 实体、7 个 JEI 分类、6 章指导书 |
| 事件订阅类 | 18 个（散落在 12 个不同包里） |

### 1.2 六个子系统

| 子系统 | 职责 | 主要类（行数） | 健康度 |
| --- | --- | --- | --- |
| **机器** | 11 台：融合/重塑/扭曲/装配 + 各「高能版」、地核钻探、碱性发电机、RTG；插件槽改倍率；灵感充能驱动 | `InspiredMachineTile`(235)、`PowerGeneratorTile`(338)、11 个方块类(31~34 行/个)、15 个 BE、6 个 plugin、4 套 recipe | 基类设计好，但**高能版整段复制** |
| **注入** | 注入枪 + 物质束缚器；种子绑定的能量窗口决定成败；超上限爆炸 | `InjectionGunTile`(406)、`MatterBinderTile`(140)、`SeededEnergyWindow`(115)、`InjectionRecipe`(180) | 玩法设计是亮点，`InjectionGunTile` 一人扛 6 种职责 |
| **结构模拟机** | 4×3×3 外壳 + 5 种仓室；放数据 → 产战利品 | `StructureSimulatorTile`(459)、`SimulatorPattern`、5 个仓室(40~63 行)、`SimulationSpec` | 数据驱动好，但**仓室无共同抽象、坐标逻辑两份** |
| **辐射** | 物品/方块辐射值 → 区块累积 → 玩家剂量 → 效果 | `RadiationSavedData`、`RadiationBlockTracker`、`RadiationAttachments`、`RadiationEvents`(7 个订阅)…共 8 个类 | 性能设计好，**职责切成 8 份** |
| **物品与工具** | 30 个自定义物品；3 套机制：蓄力增益 / 经验掉落 / 耐久 Tier | 五件套各 58 行（其中约 24 行逐字相同）、`item/tool/` 9 个类 | 经验掉落最干净；五件套是复制粘贴重灾区 |
| **流体 / 碱性** | 思维流体遇水/岩浆生成方块；碱金属遇水爆炸 | `MindFluidReactions`、`Alkalinity` + DataMap | 碱性是全项目最干净的模块，可作重构参照 |

### 1.3 支撑层

- **datagen**：9 个 Provider，其中 `ModRecipesProvider` **1303 行**（占 datagen 总量一半）。
- **客户端**：渲染（盾牌/注入枪/虚影）、JEI 7 分类、指导书（空 Menu + 客户端 Screen）。
- **文档**：各包已有 `README.md`（`machine/`、`multiblock/`、`injection/`、`guide/`、`fluid/`、
  `item/tool/`、`jei/`、`recipe/`、`datagen/`、`block/`、`radiation/`）—— 这些要跟着重构一起更新。

---

## 2. 目标架构

### 2.1 分层原则（一句话版）

> **注册集中可遍历 → 逻辑按子系统成包 → 客户端代码物理隔离 → 事件集中注册 → 资源全部 datagen 化。**

### 2.2 目标包结构

```
com.starcrosser.inspiration
├── Inspiration.java            只做：总线注册顺序 + 初始化，不放任何业务逻辑
├── config/                     Config（从根包搬进来）
├── registry/                   ★ 新：所有"可遍历清单"的唯一来源
│   ├── ModBlocks / ModItems / ModFluids / ModEntities / ModRecipes ...
│   ├── MachineRegistry         机器方块清单（解决 Titanium 注册不可遍历）
│   └── ContentSets             按"内容组"聚合：机器组、多方块组、工具组…
├── machine/
│   ├── block/ blockentity/ recipe/ plugin/ charge/
│   └── client/                 只放机器相关的客户端 addon / tooltip
├── multiblock/
│   ├── block/ blockentity/part/ pattern/ data/
│   └── client/
├── injection/  radiation/  alkali/  fluid/
├── item/{custom,tool}
├── recipe/  guide/  jei/
├── client/                     ★ 所有纯客户端代码收拢（renderer / extensions / guide screen）
├── event/                      ★ 新：所有事件处理器集中，一份清单看得全
├── datagen/                    按内容域拆分（ recipes/ 下再分 assembling / fusion / injection …）
└── util/
```

三个"★"是这次重构的骨架：

| 新增/调整 | 解决什么 |
| --- | --- |
| `registry/MachineRegistry` | Titanium 的 `DeferredRegistryHelper` 注册的方块**不在** `ModBlocks.BLOCKS` 里，无法遍历 → 才有"同一份清单手写 9 遍"。建一张自己的清单，后面全部循环生成。 |
| `event/` 集中 | 现在 18 个类散落 12 个包，`RightClickBlockHandler` 还被注册了两次。集中后一眼看出谁注册了什么。 |
| `client/` 收拢 + 强制隔离 | 现在靠人工纪律：忘写 `@OnlyIn(Dist.CLIENT)` **编译不报错**，只在专用服务器启动才炸。 |

### 2.3 明确的"不做"

- ❌ 不做大重写（rewrite from scratch）。18k 行代码的重写 = 必死。
- ❌ 不改任何 `ResourceLocation`（方块/物品/配方/流体 ID）。
- ❌ 不升级 NeoForge / Titanium 大版本（会引入新坑，和重构混在一起无法定位）。
- ❌ 不在同一期里既改结构又加新内容。

---

## 3. 问题清单

### 3.1 A 类：真实 bug（玩家能感知，P0 必修）

| # | 问题 | 位置 | 为什么严重 | 修法 |
| --- | --- | --- | --- | --- |
| A1 | `StormSword` 用**实例字段**存冷却时间 | `item/custom/StormSword.java:29-30` | `Item` 是单例，字段被所有玩家共享 → 多人服互相串冷却 | 存到 `player.getPersistentData()`（**没用** `getCooldowns()`：那会在物品上画出原版冷却遮罩，改变外观；而且这把剑左右键是两条独立冷却，原版冷却机制表达不了） |
| A2 | `BedrockPickaxe.useOn` 对**任何方块**生效 | `item/custom/BedrockPickaxe.java:33` 起 | 右键箱子/机器就毁掉它 | 只对 `Blocks.BEDROCK` 生效，其余走 `super.useOn(context)`（比白名单更窄，也正合这把镐的定位） |
| A3 | 注入中间态**没有落盘** | `injection/blockentity/InjectionGunTile.java` | 材料已扣、进度没存 → 中途卸载/退出，进度和材料一起没了 | 手写 `saveAdditional/loadAdditional`：存 `remainingTicks` + 结果枚举名 + **配方 id**。⚠️ 不能简单地用 `@Save`：`InjectionRecipe` 对象读档时拿不到（`RecipeManager` 在那时还没就绪），所以只存 id、在第一次 `serverTick` 里按 id 找回；找不到就作废这次注入（**不补触发结算**，否则会在玩家不知情时凭空爆炸） |
| A4 | `pendingOutput` / `facing` 没有落盘 | `multiblock/blockentity/StructureSimulatorTile.java` | 已产出的战利品丢失；重启后朝向重置 | 手写 `saveAdditional/loadAdditional`，`ItemStack` 用 `save/parseOptional` + `ListTag`（`@Save` 不支持 `List<ItemStack>`） |
| A5 | 投影仪虚影**几何与成型判定不同源** | `multiblock/pattern/SimulatorPattern.java`（自研 `rotate()`）、`multiblock/client/ProjectorPreviewHandler.java:83` | 玩家照虚影搭出来却永远成型不了（**这是最严重的一个**） | ① `previewCells()` 不再自己推几何，改为直接调 MBD2 的 `collectTrackedPositions(ZERO, facing)`，与成型判定同源；② `ProjectorPreviewHandler` 的 facing 取 `.getOpposite()`（结构沿 `-facing` 展开）；③ static 状态**不改**：它是纯客户端单例，且已有 `previewLevel` 维度校验，够用 |
| A6 | `getErrorPos()` 恒返回 null | `multiblock/blockentity/StructureSimulatorTile.java` | 玩家搭错了不知道错在哪一格，只能瞎试 | 加 `failedState` 字段留住「匹配格子最多的那次失败状态」，返回 `PatternError.getPos()`；另加 `errorPosPacked` + `getSyncedErrorPos()` 同步给客户端（服务端字段客户端读不到） |
| A7 | 英文指导书 3 处 `://n` | `datagen/ModEnUsLangProvider.java`（约 302、321、338） | 应为 `\n`，现在会显示成字面量 | 直接替换 |
| A8 | `RightClickBlockHandler` **被注册两次** | `recipe/RightClickBlockHandler.java` 的注解 + `Inspiration.java`（约第 89 行）手工注册 | 每次右键事件跑两遍、刷 4 条日志 | 删掉手工注册那一行，只留注解 |
| A9 | `world_breaker` 未进创造模式物品栏 | `item/ModCreativeModeTabs.java` | 有配方有模型，玩家却拿不到 | 加入创造栏列表 |
| A10 | 机器状态用 enum **ordinal** 存盘 | `multiblock/blockentity/StructureSimulatorTile.java` | 以后调整枚举顺序 → 老存档读成别的状态 | `SimulatorStatus` 加显式 `id()` + `fromId()`（未知 id 退化成 `NOT_FORMED`）。⚠️ 注意**状态枚举顺序即优先级**，别为了好看重排 |
| A11 | `Config` 在 `SPEC = BUILDER.build()` **之后**又定义了 18 项 | `Config.java` | 这 18 项不会生效，是个安静的坑 | 这 18 项是从别的 mod 抄来的 `CF_*`/`FLOOD_*`，全项目无引用 → **直接删掉**（不是移到 build 前），并加注释说明「所有 define 必须在 build() 之前」 |
| A12 | 调试配方进了游戏 | `src/main/resources/data/inspiration/recipe/testrecipe.json` | 玩家能看到一个 barrier→cobblestone 的测试配方 | `git rm` 删掉（它在 `src/main/resources`，是手写的，**不是** datagen 产物，所以不用改 datagen 代码） |

### 3.2 B 类：结构性问题（按影响排序）

| # | 问题 | 证据 | 影响 | 建议在 |
| --- | --- | --- | --- | --- |
| **B1** | **同一份清单被手写 9 遍** | 创造栏、中英语言×2、方块状态、标签、战利品表×2、配方里各列一遍 11 台机器 | 加一台机器要改 9 处，漏一处就是一个黑紫块或缺失翻译 | P2 |
| **B2** | **高能机型 = 复制粘贴** | 4 组普通/高能 BE 相似度 74%~83%，约 490 行重复（如 `InspirationAssemblerTile` 306 行 vs `HighEnergyInspirationAssemblerTile` 353 行） | 改一个逻辑要改两处，迟早漏 | P1 |
| **B3** | **四套配方无公共基类** | `FusionRecipe`(243) / `AssemblingRecipe`(222) / `ReshapingRecipe`(137) / `TwistingRecipe`(135)，约 160 行样板；回溯匹配算法写了 3 遍；`FusionRecipe` 9 个字段手写 `encode/decode` | 加字段漏改一处 = **静默数据错位**（不报错，存档悄悄坏） | P1 |
| **B4** | 五件套工具各 58 行，其中约 24 行逐字相同 | `EnergizedJadeSteel{Axe,Hoe,Pickaxe,Shovel,Sword}.java` 各 58 行 | Java 单继承的无奈，但可以用工厂/委托减少 | P1 |
| **B5** | 事件处理器散落 12 个包 | 18 个类含 `@SubscribeEvent`，分布在根包、`alkali/`、`client/`、`item/custom/`、`item/tool/`、`machine/client/`、`multiblock/client/`、`radiation/`、`recipe/` | 想找"谁监听了右键"要全局搜 | P2 |
| **B6** | 客户端隔离靠人工纪律 | 忘写 `@OnlyIn(Dist.CLIENT)` 编译不报错；`control/VecHelper` 还 import 了 `Minecraft`/`Camera` | 只在专用服务器启动才炸，本地测试永远发现不了 | P3 |
| **B7** | 命名/包结构三套并存 | `blockentity` vs `part`；`recipe` vs `machine/recipe` vs `injection/recipe`；`block/` 根下散落类；`Config` 在根包 | 找文件靠猜 | P3 |

### 3.3 C 类：死代码（P0 顺手清）

| 项 | 说明 |
| --- | --- |
| `control/VecHelper.java` | 355 行，**只有 1 个方法在用**；且混入了客户端 import |
| `item/custom/ModFuelItem.java` | 已从未注册状态（无引用） |
| `datagen/ModItemTagsProvider.java` | 空实现 |
| `block/ModBlocks.FLUID_BLOCKS` | 死寄存器（流体方块应用 `registerBlockOnly`） |

> 清理原则：**先 `git rm`，编译 + 跑一遍 `runServer` 自检确认无引用**，不要边删边改。

---

## 4. 分期执行方案

每期格式统一：**目标 → 涉及文件 → 步骤 → 验收标准 → 回滚点**。

### P0 — 止血（修 bug + 清死代码）

**目标**：不改动任何结构，只把 A 类 12 个 bug 修掉、C 类死代码删掉。风险最低、立刻见效。

**步骤（建议按此顺序，每个 bug 一个 commit）**

1. A1 `StormSword` → 改 `player.getCooldowns()`
2. A2 `BedrockPickaxe` → 加方块白名单
3. A3 `InjectionGunTile` → 补齐 `@Save`
4. A4 `StructureSimulatorTile` → 补齐 `@Save`（含 `pendingOutput` 的 ItemStack 列表序列化）
5. A5 `ProjectorPreviewHandler` → 核对方向后决定 `.getOpposite()`；static 改实例
6. A6 `getErrorPos()` → 真正返回第一个不匹配的位置
7. A7 英文指导书 `://n` → `\n`
8. A8 删掉 `Inspiration.java` 里对手工注册 `RightClickBlockHandler` 的那一行
9. A9 `world_breaker` 进创造栏
10. A10 enum ordinal → `name()`
11. A11 `Config` 的定义移到 `build()` 之前
12. A12 删调试配方
13. C 类死代码：逐项删除（每删一项编译一次）

**验收标准**

- `./gradlew --offline compileJava --console=plain` 通过
- `./gradlew --offline runData --console=plain` 通过，且生成文件数**只减少 1**（`testrecipe.json`）
- `./gradlew --offline runServer --console=plain` 自检通过（用临时自检类，跑完删掉）
- 客户端实测 4 项：虚影朝向与成型一致、StormSword 冷却不串、注入中途退出不丢材料、搭错时能看到错误位置提示

**回滚点**：`git tag p0-start` → 完成后 `git tag p0-done`

---

### P0 执行结果（实测，已全部做完并验证）

A1~A12 **全部改完**（13 个文件，+235/−78）。C 类死代码**本轮不动**——删除要逐项 `git rm` 后重跑
`runServer` 确认无引用，成本不低且收益是「代码干净」，与"止血"无关，留给 P2/P3 顺手做。

| 项 | 验证方式 | 结果 |
| --- | --- | --- |
| A5 虚影几何 / 成型 | 服务端自检：按 `previewCells(facing)` 摆 4 座结构 | ✅ north/east/south/west **全部成型**，且 Tile 识别到的朝向与摆放朝向一致 |
| A6 错误位置 | 同上场景里的「孤零零一个控制器」 | ✅ 不成型，`getErrorPos()` 返回了具体坐标（不再是 null） |
| A10 状态 id | `SimulatorStatus` 全部枚举 `id()` → `fromId()` round-trip，外加未知 id=999 | ✅ 一致，且 999 退化成 `NOT_FORMED` |
| A1 / A2 / A3 / A4 / A7 / A8 / A9 / A11 / A12 | 静态审查 + `compileJava` + `runData` | ✅ 编译通过；`runData` 生成资源**只变 1 个文件**（en_us.json 的 3 处 `://n`），符合预期 |
| 客户端 4 项目视 | 需要进游戏看 | ⚠️ **未做**：虚影朝向、StormSword 冷却、注入中途退出、错误位置提示的界面显示 |

#### ⚠️ 自检踩到的坑（下次写自检前先读这段）

**没有玩家在线时，服务器只 tick 出生点区块**。自检第一版把结构摆在 `(0,100,0)`，
结果 5 座结构**一座都没成型**，我一度以为是成型逻辑错了，反复改了 3 版判定代码——
实际那个世界的出生点是 `(-112, 75, -336)`，`(0,100,0)` 所在区块压根没被 tick，
`serverTick` 一次都没进（加一行无条件日志才看出来）。

正确做法（已写进 `P0SelfCheck` 的注释）：

```java
BlockPos spawn = level.getSharedSpawnPos();          // 别硬编码 (0, y, 0)
...
level.setChunkForced(base.getX() >> 4, base.getZ() >> 4, true);  // 强制加载，否则不 tick
```

判定顺序也别搞反：**先确认 `serverTick` 有被调用，再看成型结果**。跳过第一步就会像我一样
在错误的方向上改代码。

#### 另两条实测确认的事实（写进代码注释了）

1. **成型锚点就是控制器自己的位置**。MBD2 的 `centerOffset` 只有在图案里存在 `controller`
   谓词时才会被填成控制器格的图案坐标；`checkPatternAtWithoutController(state, facing)`
   内部用的锚点就是 `state.controllerPos`。别改成「图案原点」，否则整体平移。
2. **虚影几何必须问 MBD2**（`collectTrackedPositions`），不能另算一套。之前自己按
   `structureDir = {LEFT, UP, FRONT}` 推的 `rotate()` 与 MBD2 实际方向对不上（列方向是反的），
   正是"照着虚影搭却成型不了"的根因。

---

### P1 — 消除重复（收益最大的一期）

> 这一期动的是**游戏逻辑**，风险中等。必须改完立刻跑 `runData` 对照生成文件。

#### P1.1 机器等级化（B2）—— **已完成（commit `5184ad2` + `e45e830`）**

> 做法比原计划更进一步：不是"给基类补几个倍率钩子、让高能版继承"，
> 而是把「高能」这个概念**彻底从事物里拿掉**，换成注册时的一份配置。

**问题根因**：基类 `InspiredMachineTile` 已经提供了 `baseSpeed()`、`baseChargeCapacity()` 这类钩子，
**唯独缺"耗电倍率"钩子**，于是 4 个高能版各自写了一个私有 `energyCost()`，连带把整段逻辑复制了一遍。

**最终形态**：一台机器 = 一份 {@link MachineSpec}（`machine/spec/MachineSpec.java`）

```java
MachineSpec FUSION_T1 = MachineSpec.of(1, 2, DyeColor.LIGHT_BLUE);  // 等级 1，2 物品槽
MachineSpec FUSION_T2 = MachineSpec.of(2, 3, DyeColor.LIGHT_BLUE);  // 等级 2，3 物品槽
```

- **等级只决定数值倍率**：`multiplier() = 2^(tier-1)`（tier 1=×1、2=×2、3=×4），
  统一作用于能量容量、输入功率、每刻耗电、充能消耗、灵感罐容量、运行速度这六项；
  矿物扭曲机的等效时运下限（6 → 12）也走同一套。
- **槽数只决定能吃几条材料**：`itemInputSlots` / `fluidInputSlots`，与等级**互相独立**，
  所以将来可以有「等级 3 但仍是 2 槽」的机型。
- 加一台新机器 = 在 `ModMachines` 里加一条注册，**不用写新的方块实体类**。

**结果**：4 组 BE 从「两份 300+ 行」合并为 4 个通用类（融合 / 重塑 / 扭曲 / 装配），
删除 8 个文件（4 个 Tile + 4 个 Block），**净减 778 行**。方块 id 全部不变，老存档不受影响。

**验收（已做）**：`runServer` 自检把 9 台机器摆进世界 tick，逐台比对
能量容量 / 输入功率 / 灵感罐容量 / 每刻充能 / 运行速度 —— **与重构前完全一致，全部通过**。
`runData` 生成资源**零变化**。

#### ⚠️ P1.1 踩到的三个坑（改机器前必读）

1. **`createEnergyStorage()` 在 `PoweredTile` 的构造期就被调用**（javap 看字节码确认：
   第 16 行 `invokevirtual createEnergyStorage`），那时所有子类字段都是 null。
   → 能量缓冲只能先按 tier 1 基准值创建，再在构造末尾补正（新增 `PluginEnergyStorage#setMaxReceive`）。
2. **补正必须放在构造体最后一行**。我一开始写在 `super()` 之后立刻调用，
   结果 `chargeTank`（本类自己的字段，在后面才创建）还是 null → 一放机器就 NPE 崩服。
   这个 bug 是自检抓出来的，不是读代码发现的。
3. **Titanium 的 `@Save` 不认 `List`**：它的 NBT 处理器只有 BlockPos / 数值 / 枚举 /
   ItemStack / Tank / INBTSerializable 这些，**没有 List**。装配台的流体罐数量可变，
   所以改成手写 `saveAdditional` / `loadAdditional`（罐体本身实现了 `INBTSerializable`）。

#### P1.1 的两个遗留项

- **配方类型还没进 `MachineSpec`**。四个族的配方 input 类型各不相同
  （`FusionRecipeInput` / `AssemblingRecipeInput` / `ReshapingRecipe` 走 ReshaperTask / `TwistingRecipeInput`），
  泛型上收不进同一份 spec。等 **P1.2 抽出公共配方基类** 之后，再把 `RecipeType` 收进去。
- **物资重塑机两档的进度条颜色不同**（绿 / 紫）。经你确认按原样保留，
  于是 `MachineSpec` 里多了一个 `barColor` 字段 —— 这是 spec 里唯一纯外观的字段，属于历史遗留。

#### P1.2 四套配方抽公共基类（B3）

**改法**：抽 `InspiredRecipe`（抽象基类），收拢这四处逐字相同的样板：

- `assemble(...)`、`canCraftInDimensions(...)`、`getResultItem(...)`、`getToastSymbol()` 等
- **回溯匹配算法**（现在写了 3 遍）统一到基类一个 `match(...)` 方法
- `FusionRecipe` 的 9 字段手写 `encode/decode` → 改用统一的字段描述表（或至少加一条单元测试式自检，
  编解码跑一遍 round-trip，防止字段顺序错位）

**验收（关键）**：`runData` 后 `git diff --stat src/generated/resources/data/inspiration/recipe/` **必须为空**。
只要有一个字节变化，就说明 codec 改错了 → 立刻回滚这一小步。

#### P1.3 五件套工厂化（B4）

`ChargedBuffTool` 接口已有 `default` 方法，但**打不过父类 `Item#use`**（Java 规则：类优先于接口），
所以 5 个类各自写了 5 个转调方法。

**改法**：抽一个 `ChargedToolItem` 中间基类（extends `Item` implements `ChargedBuffTool`），
在基类里一次性覆写那 5 个方法并转调接口；5 个具体类只保留各自的 Tier / 数值 / 行为差异。

#### P1.4 仓室抽公共基类

5 个仓室 40~63 行，各自实现了一遍「找控制器 → 上报」。
抽一个 `AbstractHatchTile`，把「向上报控制器、断线清理」收进去。

**回滚点**：`git tag p1-start` → 每小步一个 commit → `git tag p1-done`

---

### P2 — 注册与资源统一（解决"手写 9 遍"）

#### P2.1 建 `registry/MachineRegistry`（B1 的根因修复）

Titanium 的 `DeferredRegistryHelper.registerBlockWithTile(...)` 注册的方块**不会进入** `ModBlocks.BLOCKS`，
所以 datagen 里遍历不到它们 —— 这才是"11 台机器被手写 9 遍"的总根源。

**改法**：

```java
public final class MachineRegistry {
    // 注册机器时同时登记到这里
    public static final List<DeferredBlock<? extends Block>> MACHINES = new ArrayList<>();
}
```

然后创造栏 / 中英语言 / 方块状态 / 标签 / 战利品表**全部改成遍历这份清单**。

**收益**：以后加一台机器，只需要在注册处写一次，9 处配套资源自动生成。
**风险**：必须逐个核对生成的 json 与旧文件逐字节相同（同样用 `git diff --stat` 验证）。

#### P2.2 拆 `ModRecipesProvider`（1303 行）

按配方类型拆成 `datagen/recipe/` 下的多个类（`AssemblingRecipes`、`FusionRecipes`、
`InjectionRecipes`、`MultiblockRecipes`…），主 Provider 只做组合。

#### P2.3 事件集中到 `event/`（B5）

把 18 个订阅类里纯事件处理的逻辑抽出，或至少建立 `event/` 包 + 一份清单文档，
保证"谁监听了什么"一眼可见。同时**确保没有任何处理器被注册两次**（A8 的教训）。

**回滚点**：`git tag p2-start` → `git tag p2-done`

---

### P2.5 — 材料系统统一：`InspirationMaterial`（新增需求）

> 用户诉求：把**碱性和辐射收进同一个类**，锭类材料统一由 `InspirationMaterial` 注册，
> 注册时填好碱性/辐射（**0 = 不显示、不触发**），自动注册**方块**和**流体形态**，
> 并用两个可选开关决定是否生成**粒（nugget）**与**尘埃（dust）**。

#### 为什么现在必须做（可数的现状）

| 事实 | 数字 |
| --- | --- |
| 锭类材料 | **16 个**（10 个 `_ingot` + 6 个合金） |
| 每个锭的物品注册 | 2 行，样式完全一致（只有 `rarity` / 是否 `AlkaliItem` 的差别） |
| **注册一个「带方块的锭」要动** | **10 个文件 / 15 条语句 / 2 张贴图 = 17 处** |
| 若还要碱性 + 辐射 | 再手写 3 个 `data_maps` JSON → **20 处** |
| 注册一个「不带方块的锭」 | 6 个文件 + 1 张贴图 = 7 处 |
| data_maps | **100% 手写**，不走 datagen（`alkalinity.json` 5 条、`radiation.json` item 9 条 / block 3 条） |
| 碱性消费点 | 1 处（`AlkalineReactorTile`），另有一套**平行的** `AlkaliItem` 遇水爆炸参数 |
| 辐射消费点 | 6 处（RTG、`RadiationTicker`、`RadiationBlockTracker`、`RadiationEvents`、tooltip、盖革计数器） |
| 现有 nugget / molten 流体 | **0 个 / 0 个**（体系里的空白） |
| 现有 dust | 4 个（`calcium_dust`、`calcium_hydroxide_dust`、`cesium_dust`、`francium_dust`）+ `reduction_powder` |

另外 `AlkalinityTooltips` 与 `RadiationTooltips` 是**逐字同构**的两份代码（连注释都互相引用），
是本次合并的天然目标。

#### 目标形态：一个材料 = 一次声明

```java
public static final InspirationMaterial CESIUM = InspirationMaterial.create("cesium")
        .alkalinity(4)          // 0 = 无碱性：不进 data_map、不显示 tooltip、不能当碱性发电机燃料
        .radiation(0)           // 0 = 无辐射：不进 data_map、不显示 tooltip、不能当 RTG 燃料
        .nugget(true)           // 生成 cesium_nugget
        .dust(true)             // 生成 cesium_dust
        .color(0xC8C8D0)        // 贴图脚本按颜色生成块/流体贴图
        .hardness(2.5f, 6.0f)
        .rarity(Rarity.UNCOMMON)
        .register();            // 一次性把下面所有东西都注册掉
```

**`register()` 自动生成的清单**：

| 类别 | 产出 | 条件 |
| --- | --- | --- |
| 物品 | `<id>_ingot` | 总是 |
| 物品 | `<id>_nugget` | `nugget(true)` |
| 物品 | `<id>_dust` | `dust(true)` |
| 方块 | `<id>_block` + 方块物品 | 总是（特殊材料可 `.skipBlock()`） |
| 流体 | `molten_<id>`（类型 + 源 + 流动） | 总是（方块/桶默认**不**生成，见下方取舍） |
| data_map | `alkalinity` | **仅当 `alkalinity > 0`** |
| data_map | `radiation`（item 表 + block 表） | **仅当 `radiation > 0`** |
| 配方 | 9 尘 → 1 粒、9 粒 → 1 锭、9 锭 ↔ 1 块 | 按开关各自生成 |
| 资源 | 中英语言、item model、blockstate、block model、战利品表、挖掘等级标签、创造栏 | 总是 |
| 贴图 | 锭 / 块 / 流体（still + flowing） | 由 `tools/textures/` 下的 python 脚本按 `color` 生成 |

#### 「0」的语义（关键约定）

| 值 | 是否写进 data_map | tooltip | 发电机 |
| --- | --- | --- | --- |
| `alkalinity = 0` | **不写条目**（`getData` 返回 null → `Alkalinity.of` = 0） | 不显示 | `AlkalineReactorTile.isFuel` = false |
| `radiation = 0` | **不写条目** | 不显示 | `RtgGeneratorTile.isFuel` = false |

> 好处：不用在 data_map 里塞一堆 0，也不用改运行时的判断逻辑
> （现有的 `value <= 0 → return` 与 `> 0` 判断天然成立）。

#### 运行时 API 保持不变

`Alkalinity.of(...)` / `Radiation.of(...)` / `Radiation.ofStack(...)` **签名与语义全部不动**，
6 个辐射消费点和 1 个碱性消费点**一行都不用改**。变的只是数据来源：

```
现在：手写 alkalinity.json / radiation.json  →  DataMap
改后：InspirationMaterial 清单  →  datagen 生成同样的 json  →  DataMap
```

**验收**：迁移后 `git diff` 那三个 json 必须**只多不少、值完全相同**（除了新增材料的条目）。

#### 三个取舍（需要你拍板）

1. **流体要不要带方块和桶？**
   一个"完整流体"（类型+源+流动+方块+桶+贴图+语言+客户端扩展）要动约 10 处。
   16 个材料全量生成 = 16 套流体，体量和注册上限都不划算。
   **建议**：默认只生成「熔融流体本体」（类型 + 源 + 流动），**不生成流体方块和桶**；
   确实需要的材料再单独开 `.withFluidBucket()`。
2. **粒 / 尘埃的换算链**：你说的是「9 尘埃 → 1 粒」。那「粒 → 锭」按 9:1 还是别的值？
   **默认按 9 尘 = 1 粒、9 粒 = 1 锭**（与原版金粒/铁粒一致），若你要别的比例告诉我。
   另外：现有 4 个 dust（钙/氢氧化钙/铯/钫）是否要**并入**新体系、还是保持现状？
3. **贴图**：项目已有 `tools/textures/gen_*.py` 的脚本惯例（如 `gen_ingot_textures.py`）。
   新材料的锭/块/流体贴图建议**脚本按 `color` 统一生成**，而不是手画 16 套。

#### 迁移步骤（建议分三步，别一次全迁）

1. **P2.5a**：建 `InspirationMaterial` 类 + 一个可遍历的材料清单，先只做「物品 + 方块 + data_maps + 语言 + model」，
   **暂不**生成粒/尘/流体。挑 **铯**（有碱性、有块、有尘）做试点，验证生成的 json 与手写版一致。
2. **P2.5b**：开粒 / 尘 / 流体开关，补齐配方与贴图脚本。
3. **P2.5c**：把剩下 15 个锭类材料逐个迁进来，删除手写 `data_maps` 里的对应条目。

#### 边界：这些**不**进 `InspirationMaterial`

| 例外 | 原因 |
| --- | --- |
| 核废料 `nuclear_waste`、`reduction_powder` | 不是锭类材料，只是带辐射/化学属性的物品 |
| 注能三锭（`energized_*`） | 来源是**注入**而非熔炼，且没有对应方块；能量窗口各不相同 |
| 产量 ≠ 1 的合金（幽匿 ×2、翡翠钢 ×2、玄武岩合金 ×2、褐煤 ×2、末地合金 4/1/4） | 需要在融合配方里指定 count，统一模板要留 `productCount` 字段 |
| `AlkaliItem`（铯锭/钫锭/两种尘） | 多了一整套「遇水爆炸并变回某物品」的行为，需要 `.alkaliBehavior(target, range)` 钩子 |
| 原版材料链（铁锭、钻石、绿宝石、远古残骸、回响碎片、强化深板岩） | 不是本 mod 注册的，走不了统一注册，但可以在 data_maps 里登记辐射/碱性 |
| 幽匿合金块 / 玄武岩合金块 | 除了是材料块，还是**注入结构的 1/2 级底座**，标签要额外照顾 |

**回滚点**：`git tag p2.5-start` → `git tag p2.5-done`

> ⚠️ 这一期会**新增游戏内容**（nugget / dust / molten 流体），与红线「不同 commit 混改结构和加内容」
> 冲突 → 拆成 a/b/c 三个 commit，**每个 commit 只做一件事**，且都在 `p2.5` 这个 tag 区间内。

---

### P3 — 客户端隔离与命名统一

#### P3.1 客户端代码物理收拢（B6）

- 所有纯客户端类移入 `client/`（含 `renderer/`、`extensions/`、`guide/screen/`）
- 补 `@OnlyIn(Dist.CLIENT)` 或统一走 `DistExecutor`
- **加一道保险**：在 `runServer` 自检里遍历所有类，检查是否有服务端路径引用到客户端类
  （这类问题编译期发现不了，只能靠运行时自检）

#### P3.2 命名统一（B7）

- `blockentity` vs `part` → 统一为 `blockentity`（多方块仓室放 `multiblock/blockentity/part/` 保持不变也行，但要在 README 里说明约定）
- `Config` 移入 `config/`
- `block/` 根下散落的类归入 `block/custom/`

**回滚点**：`git tag p3-start` → `git tag p3-done`

---

## 5. 重构时必须"保住"的设计

下面这些是实测过、设计正确的东西，**不要顺手推翻**：

1. **`PluginEffects` 的倍率公式**：耗电 = 速度²、耗充能 = 速度。全局只有一处定义，改错会让所有机器失衡。
2. **`PluginHost` 的指纹比对**：避免每刻重算插件效果，是性能关键点。
3. **`SimulationSpec` 数据驱动**：加一种结构 = 注册一个新物品，机器代码零改动。这个扩展性很值钱。
4. **只借 MBD2 结构层**：控制器不实现 `IMultiController`（否则要拖进整套 `IMachine`）。
   成型判定用 `checkPatternAtWithoutController` + 自遍历四朝向。
5. **`RadiationBlockTracker` 的性能设计**：事件增量维护 + 区块调色板预筛 + 每刻限流 2 区块。
   是实测过的设计，别改成"每次全扫"。
6. **指导书「空 Menu + 客户端 Screen」**：绕开原版成书限制的取舍，干净有效。

---

## 6. 红线（碰到就停）

| 红线 | 原因 |
| --- | --- |
| 不改变任何 `ResourceLocation` | 老存档靠 ID 索引，改了就废档 |
| 不在 P1 之后才补 `@Save` | 存档格式一旦发布就难改；P0 先补完 |
| 不在线跑 Gradle | 本机 `maven.neoforged.net` 不通（直连超时、代理 502）。**日常一律 `--offline`** |
| 不在同一 commit 里既改结构又加内容 | 出问题无法定位是哪一半导致的 |
| 不动 `build.gradle` 的两处补丁 | ① `force 'org.jetbrains:annotations:26.1.0'` ② 禁用 `compileTestJava`/`test`。删了 IDEA 同步会卡死 |

---

## 7. 每期验收清单（复制即用）

```bash
# 0) 开工前打点
git tag pN-start

# 1) 编译
./gradlew --offline compileJava --console=plain

# 2) 数据生成（关键：对比生成结果）
./gradlew --offline runData --console=plain
git status --short src/generated/resources     # P1/P2 之后应为空或只有预期变化
git diff --stat src/generated/resources        # 逐字节核对

# 3) 服务端自检（临时自检类 + server.halt(false)，跑完删掉）
./gradlew --offline runServer --console=plain

# 4) 打 jar 并核对内容（jar 不随 compileJava 更新！）
./gradlew --offline jar --console=plain
unzip -l build/libs/*.jar | head -30

# 5) 收尾
rm -rf run/crash-reports            # 残留的崩溃报告
git tag pN-done
```

**另外两条本机约定**（别踩）：

- `run/server.properties` 的 `server-port` 固定 **25599**（25565 是你自己的服务器，永远别占）。
- 自检残留的 `run/world/data/inspiration_radiation.dat` 会污染下次自检，跑完清掉。

---

## 8. 附录：各包现状速查

| 包 | 类数 | 备注 |
| --- | --- | --- |
| `item/custom` | 30 | 最大包，含五件套、经验四件套 |
| `machine/blockentity` | 15 | 3369 行，高能版重复重灾区 |
| `machine/block` | 11 | 每个 31~34 行，非常规整 |
| `machine/recipe` | 13 | 1071 行，四套配方 |
| `injection/**` | ~12 | 1645 行 |
| `multiblock/**` | ~20 | 仓室 5 个 + 控制器 459 行 |
| `radiation/**` | 8 | 含 `client/`、`item/` 子包 |
| `datagen` | 9 | `ModRecipesProvider` 1303 行 |
| `jei/**` | 9 | 7 个分类 |

---

**文档维护**：每完成一期，回来更新本文的「现状」与附录，并在
`.workbuddy/memory/YYYY-MM-DD.md` 记一笔（做了什么、踩了什么坑）。
