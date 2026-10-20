# 项目长期备忘（Inspiration，NeoForge 1.21.1）

> 日志蒸馏版，**必须 < 10KB**。细则在源码包各自 `README.md`（`machine/`、`multiblock/`、
> `injection/`、`guide/`、`fluid/`、`item/tool/`、`jei/`、`recipe/`、`datagen/`、`block/`、
> `radiation/`）；根 `README.md` 管构建/验证，贴图脚本在 `tools/README.md`。**改哪个包顺手更新那份**。
> 新增机器/工具的清单在项目技能 `inspiration-add-machine|add-tool/`。本文件只放**踩坑与决策**。

## 机器（Titanium）

- **机器等级化（P1.1 后）**：一台机器 = 一份 `machine/spec/MachineSpec`（等级 + 物品槽数 +
  流体槽数 + 进度条颜色）。倍率 **2^(tier-1)**，统一作用于容量/输入功率/耗电/充能/罐容量/速度
  六项；**槽数与等级独立**。同族两档共用一套 Tile+Block 类，差异全在注册处。
  加新机型 = 加一条 `ModMachines` 注册，不用写新类。
- ⚠️ **`createEnergyStorage()` 在 `PoweredTile` 构造期就被调用**（javap 字节码可见），
  子类字段全是 null → 容量只能按 tier 1 基准建，再由 `applyTier()` 补正；
  **`applyTier()` 必须在构造体最后一行**（放 super() 后立刻调会因 `chargeTank` 为 null 而 NPE 崩服）。
- ⚠️ **Titanium `@Save` 不认 `List`**：NBT 处理器只有 BlockPos/数值/枚举/ItemStack/Tank/
  INBTSerializable。可变数量的组件要手写 `saveAdditional`/`loadAdditional`。
- ⚠️ 自检摆方块必须 `level.getSharedSpawnPos()` + `setChunkForced`：无玩家时**只有出生点
  区块被 tick**，别处摆的方块实体一个 tick 都拿不到（表现为"永远不成型"的假失败）。

- 继承 `InspiredMachineTile`（`BasicTile→ActiveTile→PoweredTile→MachineTile`），GUI 自动画
  （176×184：能量条 (8,14)、输入槽 (44,30)、输出槽 (116,39)、侧栏按钮 y=84、充能罐 (150,14)、
  充能槽 (150,74)）。`registerBlockWithTile` 一次注册方块/物品/BE/能力；**机器方块不在
  `ModBlocks.BLOCKS`**，战利品表与标签要单独补。
- 侧向按钮标签 `machine/client/FacingLabelAddon`：只能画在按钮**上方**（按钮 y=84、Titanium 玩家
  背包从 **y=102** 起，下方只剩 4px），间隔 18px → 标签 ≤ 两个汉字。颜色取 `getColor()` 压暗 62%。
- 只有 `MachineTile#onActivated` 会 `openGui()`；直接继承 `ActiveTile/PoweredTile` 要自己覆写。
- 插件倍率只在 `MachinePluginType#multiplier()` + `PluginEffects`（同类叠乘，speed 派生
  `energyUse=speed²`、`chargeUse=speed`）；容量类只改容量、缩容要截断。
- **上位机型**：电容/充能容量/速度 ×2，每刻耗电与充能耗 ×2（总耗电不变、快一倍），倍率写在各自 Tile。
- **发电类** `PowerGeneratorTile`：`maxReceive=0` + 每刻推六邻。两坑：燃料开工即扣、缓冲满时
  进度条被打回 0 且**不回调 onFinishWork**（烧完靠自己数 `burnedTicks`）。
- **机器 tooltip** `machine/client/MachineTooltips`：**一个键可写多行**，值里 `\n` 分隔、代码
  `split("\n")` 逐行挂（MC 不会自己折行）。显示名 `alkaline_reactor`=碱性发电机、
  `rtg_generator`=RTG反应堆（**ID 与语言键不改**）。

## 多方块（结构模拟机）

- **Multiblocked2 21.1.1 + LDLib2 2.2.39.a，只借结构层**：控制器不实现 `IMultiController`
  （连带要整套 `IMachine`），只用 `FactoryBlockPattern`/`Predicates`/`MultiblockState` +
  `checkPatternAtWithoutController(state,facing)` 自遍历四朝向。两库 `implementation`、禁止 shade，
  `neoforge.mods.toml` 里 `required`（**写死版本号**，模板只展开固定几个键）。
- `aisle(...)` **第一个字符串是最低那行**，调用顺序沿 FRONT 递增；虚影要照抄
  `setActualRelativeOffset`（`SimulatorPattern#rotate`），否则与成型对不上。
- 仓室基类各走一条：要界面 → `HatchTile extends ActiveTile`（**只有它带 `openGui`**）；
  能量端口 → `PoweredTile`；灵感罐 → `ActiveTile`。数据每周期重读、**不消耗**。
- 状态枚举**顺序即优先级**（`NOT_FORMED→NO_DATA→OUTPUT_FULL→NO_ENERGY→NO_CHARGE→WORKING`）。
  多方块**不装插件**（电/充能来自仓室）。
- 虚影用 `AFTER_TRANSLUCENT_BLOCKS` + **独立的** `MultiBufferSource.immediate(...)`
  （共享缓冲 `endBatch()` 会冲掉别人的批次），每格缩 0.8。

## 配方 / JEI

- 一个自定义类型 = `XxxRecipe`（`MapCodec`）+ `XxxRecipeInput` + `XxxRecipeSerializer`；
  **≥7 字段必须手写 `StreamCodec.of(写,读)`**（`composite` 只重载到 6）。
- `RecipeOutput#accept(id,recipe,null)`；**`save(output,"名字")` 落 `minecraft:` 命名空间**，
  新写的一律传 `ResourceLocation.fromNamespaceAndPath(MODID,...)`。
- JEI 三步：`ModJeiRecipeTypes` → `category/XxxRecipeCategory` → `InspirationJeiPlugin`。要点：
  背景尺寸**必须等于 `getWidth()/getHeight()`**（放 extras 会盖掉内容）；贴图路径**必须带 `.png`**；
  `addText` 超 maxHeight 整段截断。**`MapCodec` 没有 `listOf()`**，先 `CODEC.codec().listOf()`。
  自绘用 `draw(...)`（在槽位之后执行，别和槽位区域重叠）；要翻页就把「页」做成多条配方。

## 注入 / 材料 / 工具（只列易错点）

- 注入：束缚器单槽、产物回同槽；右键放/取、**潜行右键开 GUI**；失败返回 `CONSUME`。
  超 `max_energy` 在束缚器处 `explode`——**爆炸是刻意设计，别加保护**。
- 辐射值要在 **item 与 block 两表各登记一份**。**幽匿合金** = 回响碎片 + 强化深板岩融合**得 2 个**。
- 工具：**`Properties#durability` 对 `TieredItem` 无效**（被 `tier.getUses()` 覆盖），按件给耐久走
  `ModToolTiers.withDurability(Tier,int)`。蓄力 buff 走 `ChargedBuffTool` 接口，但
  **接口 `default` 打不过父类 `Item#use`**，5 件工具都要显式覆写 5 个方法转调。

## 构建与验证

- MC 1.21.1 / **NeoForge 21.1.256** / ModDevGradle **2.0.148** / Titanium 1.21-4.0.50 /
  **JEI 19.57.0.451**。依赖升级后 `runData` 报 files 数变化属正常（HashCache 未命中）。
  JEI 是 `compileOnly + localRuntime`，**服务端也会加载它**，故 `runServer` 能验插件注册。
- **`@EventBusSubscriber(bus = Bus.MOD)` 已 @Deprecated(forRemoval)**，`bus = ...` 直接删掉。
- 新方块要补 blockstates / loot / tags / `blockItem` / 创造页 / 中英文语言，**物品本体别忘了
  `basicItem`**；半透明加 `renderType("translucent")`；**流体方块用 `registerBlockOnly`**。
- **jar 不随 compileJava 更新**（交付前重跑 `./gradlew jar` 并 `unzip -l|-p` 核对）；
  **`assets/` 只能在客户端校验**（临时挂 `FMLClientSetupEvent` + `NativeImage.read`）。
- 自检写成临时类 + `server.halt(false)`，跑完**删掉**、删 `run/crash-reports`。25565 是用户自己的
  服务器，永远别占：`run/server.properties` 的 `server-port` 固定 **25599**。
- **Gradle**：`JAVA_HOME=D:\1` 是 **Java 25**，但 `gradle.properties` 已写死
  `org.gradle.java.home=C:\Program Files\Java\jdk-21`（否则命令行与 IDEA 各起一套 daemon）；
  **游戏用 toolchain 的 JDK 21**。内存已提到 `-Xmx3G`（1G 会被 OS 杀）。
- **日常构建一律加 `--offline`**：本机 maven.neoforged.net 不通（直连超时、代理 502），
  而写配置缓存时会解析 classpath → 联网 → 无声卡死。已在 gradle.properties 加
  15s/30s 的 HTTP 超时兜底（忘了 --offline 也会 1 分钟内报错，不再死等）。
  IDEA 侧：运行配置的 Arguments 填 `--offline --console=plain`，且**自己新建**一个配置，
  别改 MDG 自动生成的（同步可能被覆盖）。
- **`build.gradle` 两处补丁别删**：① `configurations.configureEach { force
  'org.jetbrains:annotations:26.1.0' }` ② 禁用 `compileTestJava`/`test`。原因见坑 15。

## 坑

1. `getKnownBlocks` 只认 `ModBlocks.BLOCKS`，机器方块要手动加；等于默认值的字段会被 codec 省略。
2. 有 DAMAGE 且 MAX_STACK_SIZE>1 会抛异常。挖掘等级由 `incorrect_for_<tier>_tool` 反向描述；
   强化深板岩**可破坏但掉落表为空**，必须单独特判。
3. Titanium 罐体设 `Action.FILL` 后自身扣减必须 `drainForced`；**不同步容量**（只同步存量），
   见 `PluginEnergyStorage`/`PluginFluidTank`。`@Save` 可直接标 `FluidTankComponent`。
4. `tile.getItemHandler(null)` 刚放下时可能为 null → 走
   `level.getCapability(Capabilities.ItemHandler.BLOCK,pos,side)`。
5. 给基类加抽象方法前先查子类同名方法；`Predicate<T>` 方法引用形参**必须带 `T`**；静态字段别缓存
   `DeferredBlock/DeferredItem`。
6. 子类化原版方块注意 codec 泛型不变型；子类化原版实体别用带坐标的构造（写死 `EntityType.TNT`）。
   `BooleanProperty` 默认 true、`DirectionProperty` 默认 `DOWN`，都要 `registerDefaultState` 改。
7. **忘了给 `DeferredRegister` 调 `register(modEventBus)` 不报编译错**，只在首次 `.get()` 抛
   unbound value。配方一次消耗多个材料时 `canProcess` 要校验数量、随机产量按上限预留输出槽。
8. **服务端引用客户端类会炸**（链接期验证），给方法标 `@OnlyIn(Dist.CLIENT)`。
   `RegisterDataMapTypesEvent` 是 **IModBusEvent**。`TooltipContext` 是 `Item` 的嵌套类。
9. **`PlayerEvent.Clone` 构造是 `(新玩家,旧玩家,wasDeath)`**；上效果先 `removeEffect` 再 `addEffect`。
   `RadiationSavedData` 落盘 `run/world/data/inspiration_radiation.dat`，残留会污染下次自检。
10. **1.21 格挡判定走 `ItemAbilities.SHIELD_BLOCK`**，自定义盾不用注册标签。三维渲染写死
    `stack.is(Items.SHIELD)`，但**有解**：`builtin/entity` + `IClientItemExtensions#getCustomRenderer`
    + 注册 `inspiration:blocking` 属性。「刚好被击碎」用 `PlayerDestroyItemEvent`。
11. `isValidRepairItem` 是 **`Item`** 的方法；`AABB#inflate` 返回 `AABB`，别再包一层。
12. 自检用 **`FakePlayer`**；它 `openMenu` 返回 empty，验菜单要自己 `createMenu`。
    **改经验要调 `giveExperiencePoints`**（直接写 `totalExperience` 会被进度循环归零）。
    `getHandlerFromName("x")` 返回 `IFacingComponent`，要显式转 `SidedInventoryComponent<?>`。
13. **自检类必须带 `@EventBusSubscriber` 注解**，只写 `@SubscribeEvent` 不会挂上总线
    （表现为服务器起来了但一行日志都没有，干等到超时）。
14. 自建界面（指导书）= `MenuType` + 空 `AbstractContainerMenu` + `RegisterMenuScreensEvent`，
    三处回调签名各不相同（见 `guide/README.md`）；`Screen` 没有 `isHovering`。
    语言文件同一个 key 加两次会让 `runData` 抛 `Duplicate translation key`。
15. **`testCompileClasspath` 继承 `implementation`**：kotlin-stdlib → `org.jetbrains:annotations:13.0`
    没下载过 → `build --offline` 失败、**IDEA 同步卡住 → 满屏 import 报红**。禁用 `compileTestJava`
    **不够**（配置缓存仍序列化它的 `classpath`），必须 `force` 到已缓存的 26.1.0。
16. **Titanium 组件构造参数顺序互不相同**：`InventoryComponent(name,x,y,slots)`、
    `FluidTankComponent(name,**capacity**,x,y)`、`EnergyStorageComponent(capacity,maxReceive,
    maxExtract,x,y)`。写错**不报编译错**，只表现为容量/槽数离谱（`(name,44,20,128000)` → 容量 44）。
17. **物品栏里的黑紫棋盘格 = 缺 `models/item/<方块>.json`**，不是贴图坏了。世界里有 blockstate +
    block model 就正常，只有物品栏黑 —— `ModItemModelsProvider` 里补 `blockItem(...)`。新增方块最易漏。
18. **自检不能复用被测代码的坐标计算**，否则只证明「自己和自己一致」。虚影与成型这种成对实现，
    自检要照库的原始公式另写一套，两边都过才算对。
19. **配置缓存是按「任务集」分别存条目的**：跑通 `help` 只帮到 `help`，`runClient`/`build`
    各自要成功写一次。**动仓库的 `content` / `metadataSources` 会让已缓存的解析结果失效**
    （离线连本地明明有的 parchment zip 都解析不了），别为了绕网络去改仓库配置。
20. **Git 远程**：`origin = https://github.com/xiaozai114/InspirationMod`（私有）。
    旧远程 `Asp4rtatE/inspiration-1.21.1` 已 **404**（账号弃用）。代理 `http://127.0.0.1:19204`
    对 **github.com:443 的 CONNECT 间歇性/持续性 502**，而 `api.github.com` 始终 200。
    `git push` 报 502 时先重试几次；**持续失败就走 API 兜底**：`git credential fill` 取 token →
    POST `/repos/{r}/git/trees`（带 `base_tree`）→ POST `/git/commits` → PATCH `/git/refs/heads/master`。
    **改进版（推荐，能省掉对齐这一步）**：别自己拼 tree，**把本地 commit 对象的元数据原样发给
    API** —— `git cat-file commit <sha>` 拆出 tree/parent/author/committer/message，POST
    `/git/commits` 时一并传 `author`/`committer`，再 PATCH ref（带 `force:true`）。
    这样远端生成的 **sha 与本地完全相同**，本地什么都不用 reset。
    两个坑：① `date` 要用**该时区的本地时间**再贴偏移（`fromtimestamp(ts, timezone(±delta))`），
    取 UTC 时间再贴 `+08:00` 会让绝对时间差 8 小时 → sha 对不上；
    ② `git fetch` 同样会被 502 拦，`update-ref` 又因本地没有该对象而失败，所以别指望 fetch 兜底。
    SSH over 443 **不通**（代理切非 TLS 流量）。

## 重构

- 方案文档：**`docs/REFACTORING_PLAN.md`**，基线 commit **`5194af5`**。分 P0 止血（12 个真实 bug +
  死代码）→ P1 消除重复（高能版钩子、四套配方基类、五件套工厂、仓室基类）→ P2 注册与资源统一
  （`MachineRegistry` 解决「手写 9 遍」、拆 `ModRecipesProvider`、事件集中）→ P3 客户端隔离与命名统一。
- **高能版复制粘贴的根因**：`InspiredMachineTile` 有 `baseSpeed()`/`baseChargeCapacity()`，
  **唯独缺耗电倍率钩子** → 4 个高能版各写私有 `energyCost()`。补 `energyUseMultiplier()` 即可收敛。
- 重构红线：**不动任何 `ResourceLocation`**（老存档靠 ID），同 commit 不混「改结构」与「加内容」。
