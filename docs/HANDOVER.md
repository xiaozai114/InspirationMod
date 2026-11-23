# 交接文档（Inspiration，NeoForge 1.21.1）

> 面向接手的人：这份只写「现状 + 怎么继续」，机制的细则在各源码包的 `README.md`，
> 踩坑记录在 `.workbuddy/memory/`。本文与代码同步更新于 2026-11-23。

## 1. 环境

| 项目 | 版本 |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.265 |
| ModDevGradle | 2.0.148 |
| Titanium（机器框架） | 1.21-4.0.51 |
| JEI | 19.70.0.469（`compileOnly` + `localRuntime`） |
| Multiblocked2 / LDLib2 | 21.1.2 / 2.2.45（只借结构层） |
| Java | 21（`gradle.properties` 里固定了 `org.gradle.java.home`） |

## 2. 代码结构

路径相对 `src/main/java/com/starcrosser/inspiration/`。

| 目录 | 职责 |
| --- | --- |
| `machine/` | Titanium 机器（规格、方块、方块实体、插件、GUI 组件）与机器配方 |
| `material/` | 锭类材料的「一处声明生成全部资源」，含 `.molten(true)` 熔融流体 |
| `fluid/` | 流体类型与流体；`MoltenMetals` 是熔融金属的统一登记入口 |
| `multiblock/` | 多方块：控制器、仓室、`MultiblockKind`、维度数据与权重表 |
| `injection/` | 注入结构（束缚器 + 注入枪）与注入配方 |
| `voidenergy/` | 虚空能量：团簇实体、阶段、提取器、中继器、注入枪、凝聚器、注入配方 |
| `recipe/` `datagen/` | 自定义配方类型与全部数据生成（方块状态、模型、语言、战利品、标签、配方） |
| `client/` | 纯客户端：渲染器、界面、JEI 分类、tooltip、投影仪 |
| `event/` | 纯玩法监听器（不持状态） |

## 3. 本轮工作内容

### 3.1 依赖升级

NeoForge 21.1.256 → 21.1.265，Titanium 4.0.50 → 4.0.51，JEI 19.57 → 19.70，
MBD2 21.1.1 → 21.1.2，LDLib2 2.2.39.a → 2.2.45。唯一需要改代码的地方是
JEI 把 `addDrawable(drawable, x, y)` 标成 `forRemoval`，已改为
`addDrawableWidget(drawable).setPosition(x, y)`。

### 3.2 虚空能量体系（`voidenergy/`）

```
基岩 ──幽匿合金钻头（地核钻探者）──> 开裂的基岩
        → 虚空能量提取器（50 FE/ve）→ 上方生成/充能虚空能量团簇（实体）
        → 中继器（必须与团簇同格）→ 5×5×5 内的机器可用
```

- 团簇七个阶段全部实现（攀升 −2/刻、震荡 −60~+50、危险 1%/刻自爆）。
- 虚空注入只吃团簇能量：`cost = max(1, ve/10)`，与配方窗口比较；
  超出上限过载爆炸、低于下限不生效。
- 虚空能量凝聚器：16 ve + 500 FE/刻，1024 ve 产 1 露珠（不耗灵感）。
- 零点能发电机：**long** 缓冲 128 GFE，单槽 64 个增幅器，每分钟 `1 + n×1.2G` FE。
- 虚空护目镜：本模组第一件护甲（`item/armor/`），+2 护甲，戴着能看见团簇 ve。

### 3.3 其它内容

压缩红石锭（注能红石**块** + 铀锭注入）、光谱合金（四材料融合，辐射 1）、
高能物质融合器（四输入槽，属性 ×4）、矿物模拟机（3×5×3 多方块，按维度数据权重产矿）、
`MultiblockKind`（JEI 结构预览与投影仪统一按它查表）。

## 4. 构建与验证

```bash
./gradlew compileJava --offline     # 日常编译；改依赖后第一次去掉 --offline
./gradlew runData --offline         # 数据生成（先删 src/generated/resources/.cache/* 再跑）
./gradlew runServer --offline       # 服务端冒烟：JEI 插件也会被加载，能验注册
./gradlew assemble                  # 打包，产物在 build/libs
```

自检惯例：写一个临时的 `@EventBusSubscriber` 类，在 `ServerStartedEvent` 里搭方块、
调 `serverTick`、打日志，最后 `server.halt(false)`；跑完删掉临时类与 `run/crash-reports`。

## 5. 必须知道的坑（详见各包 README）

1. **Titanium 机器 Tile 的构造器应允许传入自己的 block/type** —— 否则「复用方块 + 子类实体」会在放置时抛 `Invalid block entity`。
2. `@Save` 不认 `List`；`createEnergyStorage()` 在构造期就被调用，子类字段此时还是 null。
3. 动数据生成前先删 `.cache/*`，否则会看到假的「零变化」。
4. `save(output, "名字")` 的配方会落到 **`minecraft`** 命名空间。
5. 多方块的 `Predicates.controller` 不能省，否则 MBD2 的 centerOffset 全 0、结构永远不成型。
6. 静态初始化期不要取 `ItemStack`（物品还没绑定），用惰性 `Supplier`。
7. 服务端不能引用客户端类；`client/` 之外要用就标 `@OnlyIn(Dist.CLIENT)`。

## 6. 未完成 / 待办

- 矿物模拟机的权重表 JEI 展示（结构预览已有，权重还没有专门分类）。
- 下界、末地的维度数据（`DimensionTables` 里只做了主世界）。
- 虚空注入枪的贴图仍与普通注入枪相同。
- `MoltenMetals` 的贴图生成脚本需按材料色相登记（`tools/textures/gen_molten_metals.py` 的 `TINTS`）。
- 矿物表里暂时没有镍、铀矿（本模组没有这两种矿石方块）。

## 7. 已知偏差

- `DimensionTables` 的水量按需求写成 **16 B（16000 mB）**。
- 空白数据目前**没有工作台配方**（按需求移除），只能通过其它途径获得。
