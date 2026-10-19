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
| A1 | `StormSword` 用**实例字段**存冷却时间 | `item/custom/StormSword.java:29-30` | `Item` 是单例，字段被所有玩家共享 → 多人服互相串冷却 | 改用 `player.getCooldowns().addCooldown(...)`（原版机制，单人多人都对） |
| A2 | `BedrockPickaxe.useOn` 对**任何方块**生效 | `item/custom/BedrockPickaxe.java:33` 起 | 右键箱子/机器就毁掉它 | 加方块白名单（tag 或硬编码列表），非白名单直接 `PASS` |
| A3 | 注入中间态**没有 `@Save`** | `injection/blockentity/InjectionGunTile.java`（仅 76、79 两处 `@Save`） | 材料已扣、进度没存 → 中途卸载/退出，进度和材料一起没了 | 给进度、当前配方、已注入能量等字段补 `@Save` |
| A4 | `pendingOutput` 没有 `@Save` | `multiblock/blockentity/StructureSimulatorTile.java`（仅 87、100 两处 `@Save`） | 已产出的战利品丢失 | 补 `@Save`（注意 `ItemStack` 列表要自己写序列化） |
| A5 | 投影仪虚影朝向存疑 + 用 **static** 字段存状态 | `multiblock/client/ProjectorPreviewHandler.java:33-35`、`facing = event.getEntity().getDirection()`（第 81 行） | ① 可能与实际成型方向相反，玩家照着虚影搭却成型不了；② static 状态在切维度/多人下不可靠 | 与 `SimulatorPattern` 对齐后确认是否要 `.getOpposite()`；状态改为按维度 + 玩家的实例数据 |
| A6 | `getErrorPos()` 恒返回 null | `multiblock/blockentity/StructureSimulatorTile.java`（约 174-188、434-437） | 玩家搭错了不知道错在哪一格，只能瞎试 | MBD2 的 `checkPatternAt` 能拿到不匹配的位置，把第一个不匹配的格子返回出去 |
| A7 | 英文指导书 3 处 `://n` | `datagen/ModEnUsLangProvider.java`（约 302、321、338） | 应为 `\n`，现在会显示成字面量 | 直接替换 |
| A8 | `RightClickBlockHandler` **被注册两次** | `recipe/RightClickBlockHandler.java` 的注解 + `Inspiration.java`（约第 89 行）手工注册 | 每次右键事件跑两遍、刷 4 条日志 | 删掉手工注册那一行，只留注解 |
| A9 | `world_breaker` 未进创造模式物品栏 | `item/ModCreativeModeTabs.java` | 有配方有模型，玩家却拿不到 | 加入创造栏列表 |
| A10 | 机器状态用 enum **ordinal** 存盘 | `multiblock/blockentity/StructureSimulatorTile.java`（约 100-101） | 以后调整枚举顺序 → 老存档读成别的状态 | 存 `name()` 或显式 int id |
| A11 | `Config` 在 `SPEC = BUILDER.build()` **之后**又定义了 18 项 | `Config.java`（约 38 行 vs 44-61 行） | 这 18 项不会生效，是个安静的坑 | 全部移到 `build()` 之前 |
| A12 | 调试配方进了游戏 | `src/generated/resources/data/inspiration/recipe/testrecipe.json` | 玩家能看到一个测试配方 | 从 datagen 里删掉生成代码 + 删除文件 |

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

### P1 — 消除重复（收益最大的一期）

> 这一期动的是**游戏逻辑**，风险中等。必须改完立刻跑 `runData` 对照生成文件。

#### P1.1 高能机型合并（B2）

**问题根因**：基类 `InspiredMachineTile` 已经提供了 `baseSpeed()`、`baseChargeCapacity()` 这类钩子，
**唯独缺"耗电倍率"钩子**，于是 4 个高能版各自写了一个私有 `energyCost()`，连带把整段逻辑复制了一遍。

**改法**：

```java
// 在 InspiredMachineTile 里补齐这组钩子（默认值 = 1.0）
protected double speedMultiplier()     { return 1.0; }
protected double capacityMultiplier()  { return 1.0; }
protected double energyUseMultiplier() { return 1.0; }   // ★ 新增：这就是缺的那一个
```

然后让高能版只覆写这三个方法（各 ×2），其余逻辑全部继承。
**目标形态**：4 组 BE 从「两份 300+ 行」变成「一份基类 + 4 个 30 行子类」，净减约 400 行。

**验收**：`runServer` 自检里对比每台机器「放同一组插件 → 每刻耗电、速度、容量」与重构前一致。

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
