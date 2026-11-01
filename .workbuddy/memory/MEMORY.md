# 项目长期备忘（Inspiration，NeoForge 1.21.1）

> **必须 < 12KB**，超了先合并同类条目、把细则挪进包 README，别硬删。
> 细则在各包 `README.md`（根 `README.md` 有索引，构建/验证也在那）；本文件只放
> **踩坑与决策**。**改哪个包顺手更新那份**。

## 机器（Titanium）

- **P1.1 后一台机器 = 一份 `machine/spec/MachineSpec`**（等级 + 物品槽 + 流体槽 + 进度条色）。
  倍率 **2^(tier-1)** 作用于容量/功率/耗电/充能耗/罐容/速度；**槽数与等级独立**，同族两档共用
  一套 Tile+Block。
- ⚠️ **`createEnergyStorage()` 在 `PoweredTile` 构造期就被调用**（子类字段全 null）→ 容量按 tier1
  基准建，再由 `applyTier()` 补正；**`applyTier()` 必须在构造体最后一行**（否则 NPE 崩服）。
- ⚠️ **`@Save` 不认 `List`** → 可变数量组件手写 `saveAdditional`。**`@Save` 字段放基类没问题**
  （`NBTManager` 递归扫父类），**存盘键是字段名不是组件名**。
- 自检摆方块用 `getSharedSpawnPos()`（无玩家时只有出生点区块被 tick），或手动循环调
  `tile.serverTick(...)`。⚠️ 世界会在两次自检间存盘，开头清空槽位。
  `getHandlerFromName`/`serverTick` 在 **`ActiveTile`** 上，不在 `BasicTile`。
- **P2.1 后**注册走 `registry/MachineRegistry.register(registry,id,zhName,enName,factory)`，
  中英语言 / 方块状态 / 挖掘标签 / 战利品表全部遍历清单。**创造栏也自动了**：灵感页遍历
  `MachineRegistry`，末尾还有兜底循环（本模组注册、两页都没收的物品自动补上）→ 加东西不会再漏；
  **JEI 催化剂仍手工**。创造栏两页：材料页只放锭类材料（锭/块/粒/尘，遍历 `ModMaterials`），
  其余原料在灵感页末尾。**注册顺序 = 标签输出顺序 = 创造栏顺序**。
  `registerBlockWithTile` 第二参是 `Supplier<BasicTileBlock<?>>`（不变型）。
- **只有 `MachineTile#onActivated` 会 `openGui()`**；直接继承 `ActiveTile/PoweredTile` 的要自己
  覆写（仓室打不开就是栽在这）。GUI 坐标与侧栏标签限制见 `machine/README.md`。
- 插件倍率只在 `MachinePluginType#multiplier()` + `PluginEffects`（同类叠乘，speed 派生
  `energyUse=speed²`、`chargeUse=speed`）；缩容要截断。
  ⚠️ **回收升级 `BYPRODUCT` 是唯一加算的**（`byproduct += multiplier-1`，两个是 ×3），
  也只有 `RawRefinerTile` 读它。
- ⚠️ 罐体 `Action` 挡机器自身读写（`FILL`→`drainForced`、`DRAIN`→`fillForced`）；
  **多输出槽**（原料精炼机 3 格）先试放全部格子再扣材料、写入失败整批回滚。
- 发电类 `PowerGeneratorTile`：`maxReceive=0` + 每刻推六邻；燃料开工即扣、缓冲满时进度条打回 0
  且**不回调 onFinishWork**。

## 客户端隔离（P3 后）

- **纯客户端代码一律在 `client/`**；别处要碰就标 `@OnlyIn(Dist.CLIENT)`。
  `util/ClientIsolationCheck` 开服时扫 class 找漏标打 ERROR。

## 多方块（结构模拟机）

- **MBD2 + LDLib2 只借结构层**（控制器不实现 `IMultiController`，自遍历四朝向）；两库禁止 shade。
- **成型锚点就是控制器自己的位置**；虚影几何必须问 MBD2 的 `collectTrackedPositions`，
  不能另算一套（自研 `rotate()` 列方向是反的）。
- **P1.4 后仓室继承链**：`InventoryHatchTile` → `HatchTile` → `ActiveTile`（能量端口走 `PoweredTile`）。
  状态枚举**顺序即优先级**，存盘用 `id()`。多方块**不装插件**。

## 配方 / JEI

- **P1.2 后**四配方继承 `machine/recipe/InspiredRecipe`，**`getSerializer`/`getType` 仍在子类**；
  产物是**流体**时不继承它；副产物写成 List（`composite` 只到 6 通道）。
- ⚠️ **round-trip 校验不能直接比 tag**：`Ingredient.CONTENTS_STREAM_CODEC` 会把
  `tag:minecraft:planks` **展开成具体物品**，要按「接受哪些物品」语义比。
- 一个自定义类型 = `XxxRecipe`（`MapCodec`）+ `XxxRecipeInput` + `XxxRecipeSerializer`；
  **≥7 字段必须手写 `StreamCodec.of(写,读)`**；`RecipeOutput#accept(id,recipe,null)`；
  **`save(output,"名字")` 落 `minecraft:` 命名空间**。
- **P2.2 后**配方按机制拆到 `datagen/recipe/`（13 个文件）。⚠️ `RecipeProvider#has`/
  `getHasName` 是 protected static，拆出去用不了 → 在 `RecipeUnlocks` 里原样复刻。
  ⚠️ 子目录靠 `ResourceLocation` 里带 `/`（如 `"refining/raw_iron"`），不是目录参数。
- JEI（现 `client/jei/`）三步：`ModJeiRecipeTypes` → `category/XxxRecipeCategory` →
  `InspirationJeiPlugin`。要点见 `client/jei/README.md`。

## 材料（`material/`，P2.5 后）

- **一个锭类材料 = `ModMaterials` 里一行声明**：锭 + 方块 + 方块物品 + 碱性/辐射 data_map
  一次注册完；语言/物品模型/方块状态/战利品表由各 Provider 遍历 `ALL` 生成。**加材料只需改一处**。
- **开关**：锭本体总是生成；`block`/`nugget`/`grit`/`molten` 各自一个开关，默认全关。
  `molten(true)` 会**抛异常**（尚未纳入材料声明）；液态等离子单独实现在 `fluid/MoltenPlasma`。
- **分数辐射 = 分子 + 周期两张表**：`radiation` 写分子（粒沿用锭的数）、
  `radiation_denominator` 写周期（锭 1 / 粒 9 / 尘 81），语义「每 N 秒释放 M 点」。
  ⚠️ **RTG 只收周期为 1 的燃料**。
- ⚠️ **ID 不带 `_ingot` 的材料必须显式 `.itemId(...)`**：`create("basalt_alloy")` 默认派生
  `basalt_alloy_ingot`，忘了覆盖就注册出新 ID，而且配方 JSON 会**静默**改用新 ID。
- 命名（用户拍板）：锭=`ingot`、粉=`dust`（4 个 `*_dust` 不动）、尘=`grit`（dust 已占用）、粒=`nugget`。
- 贴图：锭/粒/尘都由 `tools/textures` 按「模板换色」生成；**尘 = 粉等比缩到 0.6**（`dust_base.png`）。
- 碱性 `.alkalinity(int)` 与碱金属行为 `.alkaliRevert(...)` 是两件事。值 0 = 不写 data_map、
  不显示 tooltip、不能当燃料。有辐射且有方块时，方块与**方块物品**都要登记（值 ×9）。
- **data_maps 改由 `ModDataMapsProvider` 生成**（**手写与生成不能并存**，同名文件撞）；
  非锭材料（粉/盐/矿）在 Provider 里显式登记。

## 缸中之脑产线

- 培养缸继承 `PoweredTile`（不耗充能），按槽里的脑切产出（`BrainKind`）；村民脑的 `canIncrease`
  **故意不查营养液**，否则进度卡住、永远走不到「失活」那一格。
- 营养液 = 食物 →（饱食 + 饱和）× 100 mB；`FoodProperties#saturation()` 已是**绝对饱和度**。

## 灵感值

- 玩家 attachment（`player/InspirationValue`，上限 1000）：合成 / 吃食物 / 拿经验各 +1，100 点换 1 灵感
  （原来的两条合成配方已删）。**客户端缓存字段放在同一个 common 类里**（`synced`）—— payload handler
  一旦引客户端类，服务端加载时就会炸。
- 物品栏控件用 `ScreenEvent.Init.Post` 挂到 `InventoryScreen`；点击发 `ExtractInspirationPayload`。

## 灵感高炉 / 战利品

- **一台机器吃多种原版配方**：灵感高炉用 `SingleRecipeInput` 依次查 `SMELTING / BLASTING / SMOKING`，
  命中后缓存（输入变了才重查）；时长写死 100 刻、不给经验。
- **箱子战利品走 GLM**：修改器放 `data/inspiration/loot_modifiers/`，但**索引必须在
  `data/neoforge/loot_modifiers/global_loot_modifiers.json`**（放自己命名空间里会静默不生效）。

## 注入 / 材料 / 工具

- 注入：束缚器单槽、产物回同槽；右键放/取、潜行右键开 GUI，取放要返 `CONSUME`。
  超 `max_energy` 在束缚器处 `explode`——**刻意设计，别加保护**。
- 工具：**`Properties#durability` 对 `TieredItem` 无效** → 用 `ModToolTiers.withDurability`；
  蓄力 buff 的接口 `default` 打不过父类 `Item#use`（单继承无解，保持现状）。

## 事件

- **P2.3 后** `event/` 放**纯玩法监听器**（不持状态、不被引用）；辐射与数据地图注册留在原子系统。
  **`event/README.md` 是订阅类的唯一权威清单**。
- ⚠️ **新版 FML 按事件类型自己分派总线**（`IModBusEvent` → mod 总线），**别写 `bus = Bus.MOD`**；
  一个类只能注册一次（注解 + 手工各注册一遍会重复触发）。

## 构建与验证

- ⚠️ **`sed -i` 会把 CRLF 改成 LF**（本机 `core.autocrlf=true`）：status 一片 M 但 numstat 为空
  → 提交前把 `.java`/`.md` 统一回 CRLF。
- ⚠️ 按「文件组」提交用默认（mixed）`git reset`，别用 `--soft`（索引残留会把全部改动一起提）；
  旧路径的删除也要 stage。
- **datagen 哈希缓存会漏判内容变化**：核对前删 `src/generated/resources/.cache/*` 再 `runData`，
  否则看到假的「零变化」。
- MC 1.21.1 / **NeoForge 21.1.265** / ModDevGradle **2.0.148** / Titanium 1.21-4.0.51 /
  **JEI 19.70.0.469**（`compileOnly + localRuntime`，服务端也加载 → `runServer` 能验插件注册）/ MBD2 21.1.2 + LDLib2 2.2.45。
- 新方块要补 blockstates / loot / tags / `blockItem` / 创造页 / 中英语言（物品本体别忘了
  `basicItem`）；半透明 `renderType("translucent")`；流体方块 `registerBlockOnly`。
- **jar 不随 compileJava 更新**；**`assets/` 只能在客户端校验**；打包后 `unzip -l` 查有无自检残留。
- 自检 = 临时类 + `server.halt(false)`，跑完删掉、删 `run/crash-reports`。`server-port` 固定 **25599**。
- **Gradle**：`gradle.properties` 写死 `org.gradle.java.home=...jdk-21`；日常一律 `--offline`。
- **`build.gradle` 两处补丁别删**：① `force 'org.jetbrains:annotations:26.1.0'` ② 禁用
  `compileTestJava`/`test`（否则 IDEA 同步卡死 → 满屏 import 报红）；单禁用不够，
  配置缓存仍会序列化 `testCompileClasspath`，还要 `force`。

## 坑

1. 有 DAMAGE 且 MAX_STACK_SIZE>1 会抛异常。强化深板岩**可破坏但掉落表为空**，必须单独特判。
2. 罐体**不同步容量**（只同步存量，容量要自己挂 DataSlot）。
3. `tile.getItemHandler(null)` 刚放下时可能为 null → 走
   `level.getCapability(Capabilities.ItemHandler.BLOCK,pos,side)`。
4. 子类化原版方块注意 codec 泛型不变型；`BooleanProperty` 默认 true、`DirectionProperty` 默认 `DOWN`。
   **忘了给 `DeferredRegister` 调 `register(bus)` 不报编译错**，只在首次 `.get()` 抛 unbound value。
5. **服务端引用客户端类会炸** → 给方法标 `@OnlyIn(Dist.CLIENT)`（P3 后有自动体检，见上）。
6. 自检用 **`FakePlayer`**（`openMenu` 返回 empty）、**改经验调 `giveExperiencePoints`**；
   **自检类必须带 `@EventBusSubscriber`**（只写 `@SubscribeEvent` 挂不上总线）；
   **不能复用被测代码的坐标计算**。
8. **Titanium 组件构造参数顺序互不相同**：`InventoryComponent(name,x,y,slots)`、
   `FluidTankComponent(name,capacity,x,y)`、`EnergyStorageComponent(cap,maxReceive,maxExtract,x,y)`。
   写错**不报编译错**。
9. 物品栏黑紫棋盘格 = 缺 `models/item/<方块>.json`。
10. 配置缓存按任务集存条目；动仓库 content/metadataSources 会让已缓存解析失效。
11. **Git 远程** `origin = .../InspirationMod`（私有），**github.com:443 间歇 502**（api 通常通）。
    兜底走 REST，顺序 **blob → tree → commit → ref**：树的 entries 用 `sha:null` 删、
    **二进制必须先 `POST /git/blobs`**（trees 不认 `encoding`），`diff-tree A B` 的 A 是旧、B 是新。

## 重构

- **P0–P3 全部完成**（基线 `5194af5`）；`docs/REFACTORING_PLAN.md` 已改写成《重构纪要》，
  只留结论 / 决策 / 红线 / 验收。
- 红线：**不动任何 `ResourceLocation`**（老存档靠 ID）；同 commit 不混「改结构」与「加内容」。
- **文档要短**（用户要求）：每份包 README 6~7KB，只写规则 / 清单 / 坑，不写过程叙述；
  新增内容优先改已有表格。
