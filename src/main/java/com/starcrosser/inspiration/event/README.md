# event 包：事件监听总清单

> 目的：以前 18 个监听类散在 12 个包里，想找「谁监听了右键方块」只能全局搜。
> 本表是**唯一权威清单** —— 想知道谁在监听什么，看这一页就够了。
> 每次新增 / 删除 / 移动监听器，顺手更新这张表。

## 1. 总表

「总线」一列由**事件类型**决定，不由注解决定：新版 FML 会按事件类型自己分派
（`IModBusEvent` 走 mod 总线，其余走游戏总线），所以**不要**写 `bus = Bus.MOD`
—— 该属性已标记待删除，写了会有告警。

### 1.1 本包（`event/`）—— 纯玩法监听器

放的是「只做一件事：响应某个事件」的类。它们的共同点是**不持有状态、不被别处引用**，
纯粹是「事件 → 玩法」。

| 类 | 事件 | 干什么 |
| --- | --- | --- |
| `EndAlloyShieldHandler` | `PlayerDestroyItemEvent` | 末地合金盾牌被击碎时，把周围敌对生物随机传送走 |
| `ExpMiningHandler` | `BlockEvent.BreakEvent` | 手持 `MiningExperienceTool` 挖掉方块时撒经验 |
| `HeadDropHandler` | `LivingDeathEvent` | 生物死亡时按映射表掉头颅 |
| `ParasiteHandler` | `LivingDeathEvent`、`PlayerInteractEvent.EntityInteract` | 寄生虫感染（右键村民 / 末影人）与掉脑：寄生村民 80%、机械寄生虫回收、末影人 100% 掉脑 |
| `RightClickBlockHandler` | `PlayerInteractEvent.RightClickBlock` | 手持物品右键方块 → 查 `RightClickBlockRecipe` 并转换方块 |

### 1.2 留在各自子系统里的监听器

这些**不搬**：它们的逻辑属于某个子系统，搬到 `event/` 会把它和同包的数据拆散
（辐射的 7 个事件就是辐射机制的主体，不是"外挂的监听"）。

| 类 | 总线 | 事件 | 干什么 |
| --- | --- | --- | --- |
| `radiation/RadiationEvents` | 游戏 | `BlockEvent.EntityPlaceEvent` / `BreakEvent` / `ChunkEvent.Load` / `ChunkEvent.Unload` / `PlayerEvent.Clone` / `LivingHealEvent` / `ServerStoppingEvent` | 区块辐射累积的增量维护与跨维度搬运 |
| `radiation/RadiationTicker` | 游戏 | `ServerTickEvent.Post` | 每刻限量推进辐射结算（每刻最多 2 个区块） |
| `alkali/AlkalinityDataMaps` | mod | `RegisterDataMapTypesEvent` | 注册 `inspiration:alkalinity` 数据地图类型 |
| `radiation/RadiationDataMaps` | mod | `RegisterDataMapTypesEvent` | 注册 `inspiration:radiation` 数据地图类型（item / block 两张） |

### 1.3 客户端监听器（P3 后全部收在 `client/` 下）

全部标了 `value = Dist.CLIENT`，专用服务器启动时不会被加载。
客户端类的完整清单见 `client/README.md`。

| 类 | 事件 | 干什么 |
| --- | --- | --- |
| `client/ClientExtensionsHandler` | `RegisterClientExtensionsEvent` | 注册物品 / 流体的客户端扩展 |
| `client/ClientModEvents` | `EntityRenderersEvent.RegisterRenderers` / `RegisterMenuScreensEvent` / `FMLClientSetupEvent` | 渲染器、界面、客户端初始化 |
| `client/tooltip/MachineTooltips` | `ItemTooltipEvent` | 机器 tooltip（一个键可写多行） |
| `client/tooltip/AlkalinityTooltips` | `ItemTooltipEvent` | 碱性值 tooltip |
| `client/tooltip/RadiationTooltips` | `ItemTooltipEvent` | 辐射值 tooltip |
| `client/extensions/ProjectorPreviewHandler` | `PlayerInteractEvent.RightClickBlock` | 投影仪右键 → 记录待显示的虚影 |
| `client/renderer/SimulatorPreviewRenderer` | `RenderLevelStageEvent` | 画结构虚影 |
| `client/InspirationClient` | `FMLClientSetupEvent` | 客户端入口 |

### 1.4 其余两处（不是玩法监听）

| 类 | 总线 | 事件 | 说明 |
| --- | --- | --- | --- |
| `ModDataGenerator` | mod | `GatherDataEvent` | 挂 datagen 的 provider，只在 `runData` 时存在 |
| `Inspiration`（入口类） | 游戏 | `ServerStartingEvent` | **手工注册**：`NeoForge.EVENT_BUS.register(this)`，因为它是实例方法 |

## 2. 两条硬规矩

1. **一个类只能被注册一次。** A8 那次踩过：`RightClickBlockHandler` 既有注解、
   又在 `Inspiration.java` 里被手工注册了一遍，导致每次右键跑两遍、刷 4 条日志。
   现在这个类**只有注解**，别再往 `Inspiration.java` 里加 `register(...)`。
2. **自检类必须带 `@EventBusSubscriber` 注解**，只写 `@SubscribeEvent` 挂不上总线
   （表现为服务器起来了但一行日志都没有，干等到超时）。

## 3. 新增监听器放哪

- 纯玩法、不被别处引用 → 放 `event/`，并加一行到 1.1。
- 属于某个子系统的机制 → 放那个子系统，并加一行到 1.2。
- 只在客户端跑 → 放该子系统的 `client/` 下并标 `value = Dist.CLIENT`，加一行到 1.3。
