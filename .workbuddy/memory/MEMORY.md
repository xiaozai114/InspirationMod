# 项目长期备忘（Inspiration，NeoForge 1.21.1）

> **必须 < 12KB**，超了先合并同类条目、把细则挪进包 README，别硬删。
> 细则在各包 `README.md`（`machine/`、`multiblock/`、`injection/`、`guide/`、`fluid/`、
> `item/tool/`、`client/jei/`、`recipe/`、`datagen/`、`block/`、`radiation/`、`material/`、
> `client/`）；根 `README.md` 管构建/验证。**改哪个包顺手更新那份**。
> 本文件只放**踩坑与决策**。

## 机器（Titanium）

- **P1.1 后一台机器 = 一份 `machine/spec/MachineSpec`**（等级 + 物品槽 + 流体槽 + 进度条色）。
  倍率 **2^(tier-1)** 作用于容量/功率/耗电/充能耗/罐容/速度；**槽数与等级独立**。
  同族两档共用一套 Tile+Block。
- ⚠️ **`createEnergyStorage()` 在 `PoweredTile` 构造期就被调用**（子类字段全 null）→ 容量按 tier1
  基准建，再由 `applyTier()` 补正；**`applyTier()` 必须在构造体最后一行**（否则 NPE 崩服）。
- ⚠️ **`@Save` 不认 `List`** → 可变数量组件手写 `saveAdditional`。**`@Save` 字段放基类没问题**
  （`NBTManager` 递归扫父类），**存盘键是字段名不是组件名**。
- ⚠️ 自检摆方块必须 `getSharedSpawnPos()` + `setChunkForced`：无玩家时**只有出生点区块被 tick**。
- **P2.1 后**注册走 `registry/MachineRegistry.register(registry,id,zhName,enName,factory)`，
  中英语言 / 方块状态 / 挖掘标签 / 战利品表全部遍历清单。**创造栏与 JEI 映射仍手工**。
  **注册顺序 = 标签输出顺序**，别乱排。⚠️ `registerBlockWithTile` 第二参是
  `Supplier<BasicTileBlock<?>>`（**不变型**）。
- **只有 `MachineTile#onActivated` 会 `openGui()`**；直接继承 `ActiveTile/PoweredTile`（如仓室）
  要自己覆写 —— 灵感提供仓打不开就是栽在这。GUI 坐标与侧栏标签限制见 `machine/README.md`。
- 插件倍率只在 `MachinePluginType#multiplier()` + `PluginEffects`（同类叠乘，speed 派生
  `energyUse=speed²`、`chargeUse=speed`）；容量类只改容量、缩容要截断。
- 发电类 `PowerGeneratorTile`：`maxReceive=0` + 每刻推六邻；两坑：燃料开工即扣、
  缓冲满时进度条打回 0 且**不回调 onFinishWork**。
- `MachineTooltips`：**一个键可写多行**（值里 `\n`、代码 `split("\n")`）。

## 客户端隔离（P3 后）

- **所有纯客户端代码在 `client/`**：`extensions/`（扩展与 addon）、`renderer/`、`tooltip/`、
  `guide/`、`util/`，加 `jei/`（JEI 是纯客户端，搬进来后边界一眼可见）。
  `client/` 之外不该出现客户端类。
- 服务端要碰客户端类 → **必须 `@OnlyIn(Dist.CLIENT)`**（挂 Titanium addon 的那些
  `initClient` 已经是了）。不标的话本地永远测不出来，只有上专用服务器才炸。
- **`util/ClientIsolationCheck`** 是这道纪律的自动保险：非生产环境开服时扫自己的 class，
  按方法找「引用客户端类却没标 `@OnlyIn`」并打 ERROR。口径与两个豁免（datagen 整包、
  lambda 跟着类走）写在 `client/README.md`。
- `datagen/` 引用 `neoforge.client.model.generators` 是**正常的**（模型生成 API 就在那），
  不是漏标注解。

## 多方块（结构模拟机）

- **MBD2 21.1.1 + LDLib2 2.2.39.a，只借结构层**：控制器不实现 `IMultiController`，只用
  `FactoryBlockPattern`/`Predicates`/`MultiblockState` + `checkPatternAtWithoutController`
  自遍历四朝向。两库 `implementation`、禁止 shade。
- **成型锚点就是控制器自己的位置**；虚影几何必须问 MBD2 的 `collectTrackedPositions`，
  不能另算一套（自研 `rotate()` 列方向是反的 —— "照虚影搭却成型不了"的根因）。
- **P1.4 后仓室继承链**：`InventoryHatchTile` → `HatchTile` → `ActiveTile`。`InventoryHatchTile`
  管「一个物品栏」，子类只传名字/位置/格数/排布/入槽过滤。能量端口仍走 `PoweredTile`。
- 状态枚举**顺序即优先级**（`NOT_FORMED→…→WORKING`），存盘用 `id()`。多方块**不装插件**。

## 配方 / JEI

- **P1.2 后**四配方继承 `machine/recipe/InspiredRecipe`（result/duration/energyPerTick + 四个模板方法），
  **`getSerializer`/`getType` 仍在子类**。槽位回溯统一在 `RecipeSlotMatcher`。
- ⚠️ **round-trip 校验不能直接比 tag**：`Ingredient.CONTENTS_STREAM_CODEC` 发到客户端时把
  `tag:minecraft:planks` **展开成具体物品**。材料按「接受哪些物品」语义比。
- 一个自定义类型 = `XxxRecipe`（`MapCodec`）+ `XxxRecipeInput` + `XxxRecipeSerializer`；
  **≥7 字段必须手写 `StreamCodec.of(写,读)`**；`RecipeOutput#accept(id,recipe,null)`；
  **`save(output,"名字")` 落 `minecraft:` 命名空间**。
- **P2.2 后**配方按机制拆到 `datagen/recipe/`（11 个文件）。⚠️ `RecipeProvider#has`/
  `getHasName` 是 protected static，拆出去用不了 → 在 `RecipeUnlocks` 里原样复刻。
- JEI（现 `client/jei/`）三步：`ModJeiRecipeTypes` → `category/XxxRecipeCategory` →
  `InspirationJeiPlugin`。要点见 `client/jei/README.md`。

## 材料（`material/`，P2.5 后）

- **一个锭类材料 = `ModMaterials` 里一行声明**：锭 + 方块 + 方块物品 + 碱性/辐射
  data_map 条目一次注册完；中英语言/物品模型/方块状态/战利品表由各 Provider
  遍历 `ModMaterials.ALL` 生成。**加材料只需改一处**。
- **开关**：锭本体总是生成；`block` / `nugget` / `grit` / `molten` 各自一个开关，**默认全关**。
  `molten(true)` 现在会**抛异常**（流体提取机那期才实现），避免开关存在却看不出没生效。
- ⚠️ **ID 不带 `_ingot` 的材料必须显式 `.itemId(...)`**：`create("basalt_alloy")` 默认派生
  `basalt_alloy_ingot`，忘了覆盖就注册出新 ID，而且配方 JSON 会**静默**改用新 ID。
- 命名（用户拍板）：锭=`ingot`、粉=`dust`（**现有 4 个 `*_dust` ID 不动**）、尘=`grit`
  （dust 已被粉占用）、粒=`nugget`。尘比粉粗（破碎矿石），粉是化学产物。
- 碱性 `.alkalinity(int)` 与碱金属行为 `.alkaliRevert(target,radius)` **是两件事**
  （钙粉碱性 1、半径 0.1f）。值 0 = 不写 data_map、不显示 tooltip、不能当燃料。
- 有辐射且有方块时，方块与**方块物品**都要登记，值 = 物品值 × 9。
- **data_maps 改由 `ModDataMapsProvider` 生成**，手写三份已删；**手写与生成不能并存**（同名文件撞），
  不属于锭材料的条目（粉/盐/矿）在 Provider 里显式登记。

## 注入 / 材料 / 工具

- 注入：束缚器单槽、产物回同槽；右键放/取、**潜行右键开 GUI**；失败返回 `CONSUME`。
  超 `max_energy` 在束缚器处 `explode`——**刻意设计，别加保护**。
- 辐射值要在 **item 与 block 两表各登记一份**。幽匿合金 = 回响碎片 + 强化深板岩融合**得 2 个**。
- 工具：**`Properties#durability` 对 `TieredItem` 无效** → 用 `ModToolTiers.withDurability`。
  蓄力 buff 的接口 `default` **打不过父类 `Item#use`**（P1.3 结论：保持现状，单继承无解）。

## 事件

- **P2.3 后** `event/` 放**纯玩法监听器**（不持状态、不被引用）：碎盾传送 / 挖掘掉经验 /
  掉头颅 / 右键方块转换。辐射与数据地图注册**留在原子系统**（是机制主体）。
- **`event/README.md` 是订阅类的唯一权威清单**（类 / 总线 / 事件 / 干什么）。
- ⚠️ **新版 FML 按事件类型自己分派总线**（`IModBusEvent` → mod 总线），**别写 `bus = Bus.MOD`**
  （已标记待删除）；游戏总线注解照样收得到 `RegisterDataMapTypesEvent`。
- ⚠️ 一个类只能注册一次（A8：`RightClickBlockHandler` 曾被注解 + 手工各注册一遍）。

## 构建与验证

- ⚠️ **`sed -i` 会把 CRLF 改成 LF**（本机 `core.autocrlf=true`）：批量替换后 `git status`
  一片 M 但 `git diff --numstat` 是空的。提交前把 `src/main/java` 下的 `.java`/`.md`
  统一回 CRLF，工作区才干净。
- ⚠️ **搬包要按「文件组」提交时，用默认（mixed）的 `git reset`**，别用 `--soft`
  （soft 保留索引，后面按路径 add 再 commit 会把**全部**改动一起提进去）。
  另外**旧路径的删除也要 stage**，只 add 新路径会漏。
- **datagen 哈希缓存会漏判内容变化**：核对前删 `src/generated/resources/.cache/*`（不必删
  整个目录，删了要重新生成 500+ 文件）再 `runData`，否则看到假的「零变化」。
- MC 1.21.1 / **NeoForge 21.1.256** / ModDevGradle **2.0.148** / Titanium 1.21-4.0.50 /
  **JEI 19.57.0.451**（`compileOnly + localRuntime`，**服务端也加载** → `runServer` 能验插件注册）。
- 新方块要补 blockstates / loot / tags / `blockItem` / 创造页 / 中英语言（**物品本体别忘了
  `basicItem`**）；半透明加 `renderType("translucent")`；**流体方块用 `registerBlockOnly`**。
- **jar 不随 compileJava 更新**；**`assets/` 只能在客户端校验**。
- 自检 = 临时类 + `server.halt(false)`，跑完删掉、删 `run/crash-reports`。`server-port` 固定 **25599**。
- **Gradle**：`gradle.properties` 写死 `org.gradle.java.home=...jdk-21`；**日常一律 `--offline`**
  （maven.neoforged.net 不通，解析 classpath 会联网卡死）。
- **`build.gradle` 两处补丁别删**：① `force 'org.jetbrains:annotations:26.1.0'` ② 禁用
  `compileTestJava`/`test`（否则 IDEA 同步卡死 → 满屏 import 报红）。

## 坑

1. 有 DAMAGE 且 MAX_STACK_SIZE>1 会抛异常。强化深板岩**可破坏但掉落表为空**，必须单独特判。
2. Titanium 罐体设 `Action.FILL` 后自身扣减必须 `drainForced`；**不同步容量**（只同步存量）。
3. `tile.getItemHandler(null)` 刚放下时可能为 null → 走
   `level.getCapability(Capabilities.ItemHandler.BLOCK,pos,side)`。
4. 子类化原版方块注意 codec 泛型不变型；`BooleanProperty` 默认 true、`DirectionProperty` 默认
   `DOWN`。**忘了给 `DeferredRegister` 调 `register(bus)` 不报编译错**，只在首次 `.get()` 抛
   unbound value；配方一次消耗多个材料时 `canProcess` 要校验数量。
5. **服务端引用客户端类会炸** → 给方法标 `@OnlyIn(Dist.CLIENT)`（P3 后有自动体检，见上）。
6. 自检用 **`FakePlayer`**（`openMenu` 返回 empty）、**改经验调 `giveExperiencePoints`**；
   **自检类必须带 `@EventBusSubscriber` 注解**，只写 `@SubscribeEvent` 不会挂上总线；
   **不能复用被测代码的坐标计算**（否则只证明「自己和自己一致」）。
7. **`testCompileClasspath` 继承 `implementation`**：禁用 `compileTestJava` **不够**
   （配置缓存仍序列化它的 classpath），必须 `force`（见构建节）。
8. **Titanium 组件构造参数顺序互不相同**：`InventoryComponent(name,x,y,slots)`、
   `FluidTankComponent(name,capacity,x,y)`、`EnergyStorageComponent(cap,maxReceive,maxExtract,x,y)`。
   写错**不报编译错**。
9. **物品栏黑紫棋盘格 = 缺 `models/item/<方块>.json`**（`ModItemModelsProvider` 补 `blockItem`）。
10. **配置缓存按「任务集」分别存条目**；动仓库 `content`/`metadataSources` 会让已缓存解析失效。
11. **Git 远程** `origin = https://github.com/xiaozai114/InspirationMod`（私有），代理对
    **github.com:443 间歇 502**。持续失败走 API 兜底：把本地 commit 元数据原样发给 API
    （`git cat-file commit <sha>` → POST `/git/commits` → PATCH ref 带 `force:true`）。
    坑：`date` 要用**该时区本地时间**再贴偏移（取 UTC 再贴 `+08:00` 会差 8 小时 → sha 对不上）。
12. **datagen 哈希缓存会漏判内容变化** → 假的「零变化」，见构建节。

## 重构

- 方案 **`docs/REFACTORING_PLAN.md`**，基线 **`5194af5`**。P0 止血 → P1（机器等级化 ✅ /
  配方基类 ✅ / 五件套 ❌ 保持现状 / 仓室基类 ✅）→ P2（MachineRegistry ✅ / 拆 Provider ✅ /
  事件集中 ✅ / P2.5 材料系统统一）→ P3 客户端隔离与命名。
- 红线：**不动任何 `ResourceLocation`**（老存档靠 ID）；**同 commit 不混「改结构」与「加内容」**。
