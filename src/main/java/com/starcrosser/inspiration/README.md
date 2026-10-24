# 源码树导览与开发备忘

这一层放不属于某个子系统的东西：模组入口、注册入口、客户端事件、mixin。各子包的实现细节见各自 `README.md`：

| 文档 | 内容 |
| --- | --- |
| [`machine/README.md`](machine/README.md) | 机器一览、合成形状、Titanium 约定、界面网格 |
| [`machine/plugin/README.md`](machine/plugin/README.md) | 插件种类、倍率结算、非机器宿主 |
| [`machine/charge/README.md`](machine/charge/README.md) | 灵感充能：来源、换算、消耗 |
| [`material/README.md`](material/README.md) | 锭类材料与 data_maps |
| [`injection/README.md`](injection/README.md) | 注入合成：结构、绑定、触发、判定 |
| [`multiblock/README.md`](multiblock/README.md) | 结构模拟机：成型、仓室、模拟与虚影 |
| [`radiation/README.md`](radiation/README.md) | 辐射：区块累积与传播、中毒分层 |
| [`alkali/README.md`](alkali/README.md) | 碱性值：数据地图、口径与 tooltip |
| [`fluid/README.md`](fluid/README.md) | 思维流体反应与结晶方块 |
| [`item/tool/README.md`](item/tool/README.md) | 工具升级链与数值 |
| [`block/README.md`](block/README.md) | 方块登记点、朝向、黑紫块排查 |
| [`client/README.md`](client/README.md) | 客户端代码边界与自动体检 |
| [`client/jei/README.md`](client/jei/README.md) | JEI 分类与 API 注意事项 |
| [`recipe/README.md`](recipe/README.md) | 自定义配方三件套与序列化 |
| [`datagen/README.md`](datagen/README.md) | 数据生成 Provider |
| `tools/README.md`（仓库根） | 贴图生成脚本 |

## 构建

```bash
./gradlew build / compileJava / runData / runClient / runServer / jar
```

- **`build/libs/*.jar` 不随 `compileJava` 更新**，装进 mods 前先 `./gradlew jar`；查资源有没有进产物用 `unzip -l`。
- `runServer` 端口固定写在 `run/server.properties` 的 **25599**（25565 是用户常驻服务器，别占）。

## 没有客户端时怎么验证

- **机器 tick / 库存**：临时写自检类（`ServerStartedEvent` 放方块塞物品 → `ServerTickEvent.Post` 若干刻 → 读状态打日志），
  跑一次 `runServer`，**确认后删掉自检代码**、清方块、删 `run/crash-reports`。
- **配方有没有加载**：看日志 `Loaded N recipes`，改坏一份 JSON 会让数量下降并报错。
- **贴图 / 模型**：只能在客户端校验（服务端 resource manager 只加载 `data/`，查 `assets/` 必假阴性）。
  临时挂 `Dist.CLIENT` 订阅类，在 `FMLClientSetupEvent` 里 `getResource` + `NativeImage.read` 打尺寸；
  日志里的 `Unable to load model` 也是第一手线索。
- **没有发电 mod**：用 Titanium 的 `titanium:creative_generator` 当无限 FE 源。
- 改槽用 `getHandlerFromName("名字")`；改电用 `getEnergyStorage().setEnergyStored(v)`（绕过输入功率限制）。
- 无玩家时只有 force-load 区块会 tick 方块实体；造红石上升沿要先撤信号等几刻。

## 测试环境附加 mod

放 `run/mods`，不参与构建、不作为依赖：Powah! (Rearchitected) 6.2.10 + Cloth Config API 15.0.140 +
GuideME 21.1.19（后两个是 Powah! 的前置）。下载后 `unzip -t` 验一遍。

## 通用坑

| 症状 | 原因 |
| --- | --- |
| `Trying to access unbound value` | 忘了给某个 `DeferredRegister` 调 `register(bus)`。不报编译错，只在首次 `.get()` 抛。 |
| `Attempted to load class ... for invalid dist` | 服务端类引用了客户端类（**哪怕永不执行**，链接期就校验）→ 标 `@OnlyIn(Dist.CLIENT)`。 |
| `useWithoutItem` 逻辑执行两次 | 前一步返回 PASS 流程继续；返回 `CONSUME` 或加 `SKIP_DEFAULT_BLOCK_INTERACTION`。 |
| 一次扣多个材料的配方扣成负数 | `canProcess` 没校验数量；随机产量还要按上限预留输出槽。 |
| `Component.translatable(...)` 链式样式报错 | 静态类型是 `Component`（无 `withStyle`）→ 声明成 `MutableComponent`。 |
| 子类化原版实体拿到错的类型 | 原版构造函数常写死类型（`PrimedTnt` 内部硬编码 `EntityType.TNT`）；自存 owner 并覆盖 `getOwner()`。 |
| 子类化原版方块 codec 泛型报错 | codec 不变型，父类返回 `MapCodec<父类>`，子类不能收窄。 |

分模块的坑见各包文档（机器 / 插件 / 辐射 / 资源 / JEI / 工具 / 配方 / datagen）。

## 其它

- 静态字段里不要缓存 `DeferredBlock` / `DeferredItem`，也别在类初始化时 `.get()`（可能早于注册完成）。
- 爆炸事件在 `net.neoforged.neoforge.event.level.ExplosionEvent`（含 `Start` 与 `Detonate`）。
- 实体渲染器、方块实体渲染器都绑在 `client/ClientModEvents` 的 `EntityRenderersEvent`；多实体要**逐个**绑。
