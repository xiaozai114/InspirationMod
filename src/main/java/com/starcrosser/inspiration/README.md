# 源码树导览与开发备忘（`src/main/java/com/starcrosser/inspiration`）

这一层放不属于某个子系统的东西：模组入口、物品/方块注册入口、客户端事件、mixin 等。
下面是**包文档的索引**——每个子目录的实现细节写在自己的 `README.md` 里，改动时顺手更新那份即可。

| 文档 | 内容 |
| --- | --- |
| [`machine/README.md`](machine/README.md) | 九种加工机器 + 两种发电机、Titanium 约定与界面网格、各机器配方清单 |
| [`machine/plugin/README.md`](machine/plugin/README.md) | 插件种类与倍率结算、非机器宿主如何复用槽位 |
| [`machine/charge/README.md`](machine/charge/README.md) | 灵感充能：来源、换算与消耗规则 |
| [`injection/README.md`](injection/README.md) | 注入合成：结构、绑定、触发、判定与配方 |
| [`radiation/README.md`](radiation/README.md) | 辐射机制：区块累积与传播衰减、玩家中毒分层、钫盐系列与盖革计数器 |
| [`alkali/README.md`](alkali/README.md) | 碱性值：数据地图、数值口径与 tooltip |
| [`fluid/README.md`](fluid/README.md) | 思维流体反应与四种结晶方块 |
| [`item/tool/README.md`](item/tool/README.md) | 翡翠钢工具的升级路线与数值 |
| [`block/README.md`](block/README.md) | 方块登记点、不完整方块与朝向、黑紫块排查 |
| [`jei/README.md`](jei/README.md) | JEI 分类、面板内容与 API 注意事项 |
| [`recipe/README.md`](recipe/README.md) | 自定义配方类型三件套与序列化约定 |
| [`datagen/README.md`](datagen/README.md) | 数据生成 Provider 与注意事项 |
| `../../../tools/README.md`（仓库根） | 贴图生成脚本 |

---

## 构建命令

```bash
./gradlew build        # 完整构建
./gradlew compileJava  # 仅编译
./gradlew runData      # 运行数据生成
./gradlew runClient    # 启动客户端
./gradlew runServer    # 启动专用服务器
./gradlew jar          # 只打包 jar
```

注意两点：

- **`build/libs/*.jar` 不会随 `compileJava` 更新**。把产物放进 mods 目录前要先重跑 `./gradlew jar`。
  查某个资源有没有进产物：`unzip -l build/libs/<jar> | grep <文件名>`。
- `runServer` 的端口固定写在 `run/server.properties` 的 **25599**（25565 是用户自己的常驻服务器，不要占）。

## 没有客户端时怎么验证

- **机器 tick / 库存逻辑**：临时写一个自检类，`ServerStartedEvent` 里 `forceload` 并放方块塞物品，
  `ServerTickEvent.Post` 若干刻后读状态打日志，跑一次 `runServer` 看结果，**确认后删掉自检代码**，
  收尾把测试方块清成空气、删掉 `run/crash-reports`、自己 `stop`。
- **配方是否真的被加载**：看服务端日志的 `Loaded N recipes`。把某个 JSON 改坏会让数量下降并报错，
  据此反证加载路径有效。
- **贴图/模型是否存在**：**只能在客户端校验**。服务端 resource manager 只加载 `data/`，
  用它查 `assets/` 必然是假阴性。做法是临时挂一个 `Dist.CLIENT` 的 MOD 事件订阅类，
  在 `FMLClientSetupEvent` 里用 `Minecraft.getInstance().getResourceManager().getResource(loc)`
  + `NativeImage.read` 解码并打印尺寸。客户端日志里的 `Unable to load model` 也是第一手线索。
- **开发环境没装发电 mod 时**：用 Titanium 的创造模式发电机 `titanium:creative_generator` 作为无限 FE 源。
- **改槽位内容**：`tile.getHandlerFromName("名字")`；**改能量**：
  `PoweredTile#getEnergyStorage().setEnergyStored(v)`（绕过输入功率限制）。
- 没有玩家在线时只有 force-load 区块会 tick 方块实体；自检里要造红石上升沿，
  必须先撤信号等几刻再给。

本项目已经用自检类验证过的内容：思维流体在六个方向、源与流动两种状态下的全部反应；
注入合成的四个分支（成功、能量不足、等级不足、过载）；注入枪插件槽的容量叠乘与恢复。

## 测试环境附加 mod

放在 `run/mods`，不参与构建，也不作为依赖发布：

| mod | 用途 |
| --- | --- |
| Powah! (Rearchitected) 6.2.10 | 简单发电（熔炉、岩浆、热力、太阳能）、储能与线缆 |
| Cloth Config API 15.0.140 | Powah! 的必需依赖 |
| GuideME 21.1.19 | Powah! 的必需依赖 |

下载后务必 `unzip -t` 确认压缩包完整。

## 通用坑清单

按「报错表现」归类，遇到症状先来这里查：

| 症状 | 原因 |
| --- | --- |
| `Trying to access unbound value` | **忘了给某个 `DeferredRegister` 调 `register(modEventBus)`**。不会编译报错，只在首次 `.get()` 抛异常。客户端启动崩溃先看这里。 |
| `Attempted to load class GuiEventListener for invalid dist DEDICATED_SERVER` | 服务端类里引用了客户端类。**哪怕那个方法永远不会被执行**，JVM 链接期也会验证；给方法标 `@OnlyIn(Dist.CLIENT)`（`net.neoforged.api.distmarker`），dist cleaner 会在服务端剥掉。 |
| `useWithoutItem` 上的逻辑被执行两次 | 前一步返回了 PASS，流程继续往下走；要返回 `ItemInteractionResult.CONSUME`，或给物品加 `SKIP_DEFAULT_BLOCK_INTERACTION` 组件。 |
| 一次消耗多个材料的配方把材料扣成负数 | `canProcess` 里没校验数量够不够一次扣完；随机产量的输出槽还要按上限预留容量。 |
| `Component.translatable(...)` 链式样式报错 | 静态类型是 `Component`（没有 `withStyle`）；要链式改样式就声明成 `MutableComponent`。 |
| 子类化原版实体拿到错的类型 | 原版构造函数常把类型写死（如 `PrimedTnt` 内部硬编码 `EntityType.TNT`）。要照抄初始化只换类型，并自存 owner、覆盖 `getOwner()`。 |
| 子类化原版方块时 codec 泛型报错 | codec 是不变型的，父类返回 `MapCodec<父类>`，子类不能收窄（如 `SlimeBlock#codec()`）。 |

其余分模块的坑写在各自文档里：机器与 Titanium 见 [machine/README.md](machine/README.md)，
插件与非机器宿主见 [machine/plugin/README.md](machine/plugin/README.md)，
辐射见 [radiation/README.md](radiation/README.md)，资源见 [block/README.md](block/README.md)，
JEI 见 [jei/README.md](jei/README.md)，工具见 [item/tool/README.md](item/tool/README.md)，
配方与数据生成见 [recipe/README.md](recipe/README.md) 与 [datagen/README.md](datagen/README.md)。

## 其它

- 静态字段里不要缓存 `DeferredBlock` / `DeferredItem`，也不要在类初始化时 `.get()`，
  那可能早于注册完成。
- NeoForge 1.21.1 的爆炸事件在 `net.neoforged.neoforge.event.level.ExplosionEvent`
  （含 `Start` 与 `Detonate`）。
- 实体渲染器、方块实体渲染器都绑在 `client/ClientModEvents` 的 `EntityRenderersEvent` 上。
  自定义 TNT 这类多个实体要**逐个**绑定。
