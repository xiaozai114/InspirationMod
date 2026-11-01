# Inspiration

Minecraft 1.21.1 / NeoForge 模组。在原有矿物与工具体系之上加了一条「灵感」产线：灵感既是合成材料、
也是机器运行的第二种消耗品（与电力并列）；此外有 17 台机器、一套多方块注入合成、区块级辐射，
以及可装入机器改变性能的插件。

数值、实现约定与踩坑记录写在**各源码包目录的 `README.md`** 里（索引见下），改动时顺手更新那一份。

| 项目 | 版本 |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.265 |
| Java | 21 |
| Titanium | 1.21-4.0.51（机器框架：机器基类、组件、GUI 全部来自它） |
| JEI | 19.70.0.469（`compileOnly`，运行时可选） |
| Multiblocked2 / LDLib2 | 21.1.2 / 2.2.45（只借结构层） |

## 文档索引

路径相对 `src/main/java/com/starcrosser/inspiration/`。

| 文档 | 内容 |
| --- | --- |
| [`README.md`](src/main/java/com/starcrosser/inspiration/README.md) | 构建命令、无客户端时怎么验证、通用坑 |
| [`machine/README.md`](src/main/java/com/starcrosser/inspiration/machine/README.md) | 机器一览、合成形状、Titanium 约定、界面网格 |
| [`machine/plugin/README.md`](src/main/java/com/starcrosser/inspiration/machine/plugin/README.md) | 插件种类、倍率结算、非机器宿主 |
| [`machine/charge/README.md`](src/main/java/com/starcrosser/inspiration/machine/charge/README.md) | 灵感充能：来源、换算、消耗 |
| [`material/README.md`](src/main/java/com/starcrosser/inspiration/material/README.md) | 锭类材料：一处声明生成全部资源；分数辐射 |
| [`injection/README.md`](src/main/java/com/starcrosser/inspiration/injection/README.md) | 注入合成：结构、绑定、触发、判定 |
| [`multiblock/README.md`](src/main/java/com/starcrosser/inspiration/multiblock/README.md) | 结构模拟机：成型、五种仓室、模拟与虚影 |
| [`radiation/README.md`](src/main/java/com/starcrosser/inspiration/radiation/README.md) | 辐射：区块累积与传播、中毒分层 |
| [`fluid/README.md`](src/main/java/com/starcrosser/inspiration/fluid/README.md) | 思维流体反应与四种结晶方块 |
| [`item/tool/README.md`](src/main/java/com/starcrosser/inspiration/item/tool/README.md) | 三条工具升级链、注能系列蓄力增益 |
| [`guide/README.md`](src/main/java/com/starcrosser/inspiration/guide/README.md) | 灵感指导书 |
| [`block/README.md`](src/main/java/com/starcrosser/inspiration/block/README.md) | 方块登记点、不完整方块与朝向 |
| [`recipe/README.md`](src/main/java/com/starcrosser/inspiration/recipe/README.md) | 自定义配方三件套与序列化约定 |
| [`datagen/README.md`](src/main/java/com/starcrosser/inspiration/datagen/README.md) | 数据生成 Provider 与注意事项 |
| [`client/README.md`](src/main/java/com/starcrosser/inspiration/client/README.md) | 客户端代码边界与自动体检 |
| [`client/jei/README.md`](src/main/java/com/starcrosser/inspiration/client/jei/README.md) | JEI 分类、面板与 API 注意事项 |
| [`tools/README.md`](tools/README.md) | 贴图生成脚本 |

## 目录结构

```
src/main/java/com/starcrosser/inspiration/
├── block/       方块注册，custom/ 为自定义方块
├── item/        物品注册，custom/ 有行为的物品，tool/ 工具品质与经验接口
├── fluid/       流体注册（思维流体、灵感充能、液态等离子、营养液）与反应规则
├── material/    锭类材料：一处声明生成锭/块/粒/尘与 data_maps
├── machine/     block/ · blockentity/ · charge/ · plugin/ · recipe/
├── injection/   注入合成：结构判定、等级标签、配方与两个方块实体
├── multiblock/  结构模拟机：block/ · blockentity/part/ · data/ · pattern/
├── guide/       指导书章节表与客户端书页
├── radiation/   数据地图、区块辐射存档、玩家辐射、方块索引与结算
├── entity/      自定义 TNT 实体
├── recipe/      右键转化配方与全局注册
├── datagen/     数据生成（配方、战利品、方块状态、语言、模型）
├── config/      配置项
├── player/      灵感值（玩家 attachment）与提取逻辑
├── network/     自定义数据包（灵感值同步 / 提取）
├── client/      extensions/ · renderer/ · tooltip/ · guide/ · gui/ · util/ · jei/
└── util/        通用工具（含客户端隔离体检 ClientIsolationCheck）
```

`src/main/resources` 是手写资源；`src/generated/resources` 由 datagen 产出，不要手工编辑。
贴图脚本在 `tools/textures/`。

## 构建

`./gradlew build` / `compileJava` / `runData`（改配方、语言、模型后必跑）/ `runClient` / `runServer`。
验证手法与常见坑见[源码树 README](src/main/java/com/starcrosser/inspiration/README.md)。

## 内容速览

- **机器**：15 台用电 + 2 台发电（碱性发电机、RTG反应堆）。用电机器只进不出、右侧四个插件槽；
  发电类只烧燃料往外送电。逐台数值见 [machine/README.md](src/main/java/com/starcrosser/inspiration/machine/README.md)。
- **插件**：电容（容量 ×2）、电容降级（×0.67）、灵感（充能上限 ×2）、速度（速度 ×2，耗电 ×4、耗充能 ×2）、
  回收升级（原料精炼机副产物**加算** +100%）。同类叠乘、异类乘算。
- **灵感与充能**：合成、吃东西、获得经验各攒 1 点灵感值（上限 1000）；物品栏右侧的进度条 + 按钮
  每 100 点换 1 枚灵感（浓缩灵感结晶也是来源）。
  充能只存在于机器罐中（无方块无桶），1 灵感 = 1,000 mB、碎片 = 100 mB，罐容 16,000 mB，
  不足时进度条暂停、不扣材料。
- **灵感高炉**：一台顶熔炉 / 高炉 / 烟熏三家，统一 5 秒、不给经验；三档（镍 + 核心 / 注能翡翠钢 / 无恒合金）。
  镍锭可以在**地牢、要塞、下界要塞**的箱子里开出来。
- **思维流体**：接触水凝固为浓缩（源）/稀释（流动）灵感结晶，接触岩浆凝固为思维水晶岩 / 灵感凝胶。
- **注入合成**：物质束缚器 + 下方等级底座（`inspiration:injection_tier_N`）+ 5×5×5 内的注入枪，
  枪由红石上升沿触发；低于能量下限损失材料，高于上限在束缚器处爆炸（刻意设计）。
- **结构模拟机**：多方块（4×3×3 中空，外壳为高能机器外壳）嵌五种仓室，只借 MBD2 结构层、
  控制器仍是 Titanium 方块实体，**不装插件**。
- **辐射**：辐射方块与背包里的辐射物品每秒向区块注入；区块每小时向四个正方向传播 `⌊x/100⌋`
  并衰减 `⌊x/10⌋`；玩家每秒吸收区块值，10K/50K/100K/500K/1M 逐层叠负面效果，死亡减半。
  来源为钫盐系列（1 / 3 / 盐块 9 / 钫块 27），盖革计数器 = 铁锭 ← 铯锭（150k~250k FE）。
- **缸中之脑**：斩首者或寄生村民取脑，腐肉 / 失活大脑在原料精炼机里孵出寄生虫；
  食物进流体提取机出营养液；培养缸用村民脑产灵感、末影人脑产思维晶体（末影脑靠机械寄生虫感染末影人取得）。
- **工具**：灵感 → 翡翠钢 → 注能翡翠钢两级升级（模板 + 工具 + 金属锭）；注能系列挖掘速度 12，
  长按右键 1.5 秒换 30 秒增益（剑速度 / 镐急迫 / 斧力量 / 锄幸运 / 铲跳跃）。
- **JEI**：八个分类（融合 / 重塑 / 扭曲 / 装配 / 右键转化 / 注入 / 流体提取 / 原料精炼），
  面板只显示基准耗时与耗电；种子随机的配方显示 `???`。

## 许可

All Rights Reserved。项目基于 NeoForge MDK 模板创建。
