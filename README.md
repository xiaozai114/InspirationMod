# Inspiration

Minecraft 1.21.1 / NeoForge 模组。主题围绕「灵感」这一资源展开：在原有矿物与工具体系之上，
新增九种用电机器，把灵感充能作为与电力并列的第二种消耗品，并提供可装入机器提升性能的插件；
思维流体与水、岩浆接触后会凝固成四种新方块，构成一条可再生的结晶产线；
灵感系列工具可经翡翠钢升级模板、注能翡翠钢升级模板两级锻造成更高数值的翡翠钢 / 注能翡翠钢工具；
注入合成则是一套多方块结构驱动的合成方式，用能量规模换取产物；
此外还有一套区块级**辐射**机制，由钫盐矿与钫系材料充当污染源。

详细的数值、实现约定与踩坑记录写在**源码包各自目录的 `README.md`** 里，按需查阅、改动时顺手更新。

## 环境与技术栈

| 项目 | 版本 |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.256 |
| Java | 21 |
| Titanium | 1.21-4.0.50（机器框架） |
| JEI | 19.57.0.451（`compileOnly`，运行时可选） |

机器统一基于 Titanium 实现：方块、方块实体、能量/物品/流体能力与 GUI 均由框架的组件系统拼装，
项目内不重复实现机器基类与 Screen。

## 详细文档

详细的数值、实现约定与踩坑记录写在**源码包各自目录的 `README.md`** 里，改动时顺手更新那份即可。
下面的路径均相对于 `src/main/java/com/starcrosser/inspiration/`。

| 文档 | 内容 |
| --- | --- |
| [`README.md`](src/main/java/com/starcrosser/inspiration/README.md) | 源码树导览：构建命令、没有客户端时怎么验证、通用坑清单 |
| [`machine/README.md`](src/main/java/com/starcrosser/inspiration/machine/README.md) | 四种机器、Titanium 约定与界面网格、各机器配方清单 |
| [`machine/plugin/README.md`](src/main/java/com/starcrosser/inspiration/machine/plugin/README.md) | 插件种类与倍率结算、非机器宿主如何复用槽位 |
| [`machine/charge/README.md`](src/main/java/com/starcrosser/inspiration/machine/charge/README.md) | 灵感充能：来源、换算与消耗规则 |
| [`injection/README.md`](src/main/java/com/starcrosser/inspiration/injection/README.md) | 注入合成：结构、绑定、触发、判定与配方 |
| [`multiblock/README.md`](src/main/java/com/starcrosser/inspiration/multiblock/README.md) | 结构模拟机：多方块成型（只借 MBD2 结构层）、五种仓室、模拟流程与虚影预览 |
| [`guide/README.md`](src/main/java/com/starcrosser/inspiration/guide/README.md) | 灵感指导书：章节内容、空菜单与书页界面 |
| [`radiation/README.md`](src/main/java/com/starcrosser/inspiration/radiation/README.md) | 辐射机制：区块累积与传播衰减、玩家中毒分层、钫盐系列与盖革计数器 |
| [`fluid/README.md`](src/main/java/com/starcrosser/inspiration/fluid/README.md) | 思维流体反应与四种结晶方块 |
| [`item/tool/README.md`](src/main/java/com/starcrosser/inspiration/item/tool/README.md) | 三条工具升级链、数值与注能系列的蓄力增益 |
| [`block/README.md`](src/main/java/com/starcrosser/inspiration/block/README.md) | 方块登记点、不完整方块与朝向、黑紫块排查 |
| [`client/README.md`](src/main/java/com/starcrosser/inspiration/client/README.md) | 纯客户端代码清单、三条边界规矩与自动体检 |
| [`client/jei/README.md`](src/main/java/com/starcrosser/inspiration/client/jei/README.md) | JEI 分类、面板内容与 API 注意事项 |
| [`recipe/README.md`](src/main/java/com/starcrosser/inspiration/recipe/README.md) | 自定义配方类型三件套与序列化约定 |
| [`datagen/README.md`](src/main/java/com/starcrosser/inspiration/datagen/README.md) | 数据生成 Provider 与注意事项 |
| [`tools/README.md`](tools/README.md) | 贴图生成脚本 |

## 目录结构

```
src/main/java/com/starcrosser/inspiration/
├── block/           方块注册，custom/ 为自定义方块（含思维流体方块）
├── item/            物品注册，custom/ 为带行为的物品，tool/ 为工具品质与经验接口
├── fluid/           流体注册（思维流体、灵感充能）与流体反应规则表
├── machine/
│   ├── block/       机器方块
│   ├── blockentity/ 机器方块实体（含充能机器公共基类 InspiredMachineTile）
│   ├── charge/      灵感充能的定义与换算
│   ├── plugin/      插件种类、倍率结算、插件槽与可扩容能量存储
│   └── recipe/      四种机器配方类型及其序列化器
├── injection/       注入合成：结构判定、等级标签、配方与两个方块实体
│   ├── block/       物质束缚器与注入枪方块
│   ├── blockentity/ 两个结构的方块实体
│   ├── item/        绑定器
│   └── recipe/      注入配方、输入与序列化器
├── multiblock/      结构模拟机：多方块成型、五种仓室、结构数据与虚影预览
│   ├── block/       高能机器外壳、仓室与控制器方块
│   ├── blockentity/ 控制器（含 MBD2 结构校验）与 part/ 下的五种仓室
│   ├── data/        结构数据物品与模拟参数
│   └── pattern/     4×3×3 结构定义与虚影坐标
├── guide/           灵感指导书：章节表、空菜单与客户端的书页界面
├── radiation/       辐射机制：数据地图、区块辐射存档、玩家辐射、方块索引与结算
│   └── item/        盖革计数器
├── entity/          自定义 TNT 实体
├── recipe/          右键转化配方与全局配方注册
├── datagen/         数据生成（配方、战利品表、方块状态、语言文件等）
├── config/          配置项（NeoForge ModConfigSpec）
├── client/          纯客户端代码，client/ 之外不该出现客户端类
│   ├── extensions/  客户端扩展与 addon（物品/流体扩展、按钮标签、状态行、虚影状态）
│   ├── renderer/    三个渲染器
│   ├── tooltip/     三种 tooltip 监听
│   ├── guide/       指导书界面
│   ├── util/        客户端小工具
│   └── jei/         JEI 插件，category/ 为各配方分类
└── util/            通用工具（含客户端隔离体检 ClientIsolationCheck）
```

资源与数据在 `src/main/resources`；由数据生成产出的文件写入 `src/generated/resources`，不应手工编辑。
贴图生成脚本集中在 `tools/textures/`，输出路径相对仓库根目录。

## 构建与常用命令

```bash
./gradlew build        # 完整构建
./gradlew compileJava  # 仅编译
./gradlew runData      # 运行数据生成
./gradlew runClient    # 启动客户端（含 JEI 时需一并放入 mods 目录）
./gradlew runServer    # 启动专用服务器
```

`runServer` 默认端口为 25565，与已有服务器冲突时可临时修改 `run/server.properties`。
验证手法与常见坑见 [源码树 README.md](src/main/java/com/starcrosser/inspiration/README.md)。

## 内容速览

### 机器

| 机器 | 配方类型 | 能量缓冲 / 输入功率 | 灵感充能 |
| --- | --- | --- | --- |
| 物质融合器 | 物质融合 | 60,000 FE / 1,000 FE·t⁻¹ | 1 mB/刻 |
| 强能物质融合机 | 物质融合 | 120,000 FE / 2,000 FE·t⁻¹ | 2 mB/刻 |
| 物质重塑机 | 物质重塑、右键转化 | 200,000 FE / 4,000 FE·t⁻¹ | 2 mB/刻 |
| 矿物扭曲机 | 矿物扭曲 | 400,000 FE / 8,000 FE·t⁻¹ | 2 mB/刻 |
| 灵感装配台 | 灵感装配 | 300,000 FE / 6,000 FE·t⁻¹ | 2 mB/刻 |
| 地核钻探者 | 无（向下掏竖井） | 4,000,000 FE / 20,000 FE·t⁻¹ | 16,000 mB/次 |
| 高能物质重塑机 | 物质重塑、右键转化 | 400,000 FE / 8,000 FE·t⁻¹ | 4 mB/刻 |
| 高能矿物扭曲机 | 矿物扭曲（时运 12） | 800,000 FE / 16,000 FE·t⁻¹ | 4 mB/刻 |
| 高能灵感装配台 | 灵感装配 | 600,000 FE / 12,000 FE·t⁻¹ | 4 mB/刻 |

九台机器均为只进不出的用电设备，能量只能被自身消耗，界面右侧都有四个插件槽。
强能物质融合机是物质融合器的上位机型（三个输入槽、两倍速、两倍消耗与容量），配方共用一套。
**高能机型**（紫色外壳）是下一阶段的上位机型：电容、灵感充能容量与处理速度都是上一阶段的
两倍，每刻耗电与每刻充能消耗同样翻倍（一次作业的总耗电不变，只是快一倍）；主色调取无恒合金
的淡紫。高能矿物扭曲机额外把等效时运从 6 抬到 12；高能灵感装配台有六个物品输入槽与两个
流体输入槽，是本阶段最重要的合成机器 —— 五种以上材料、或需要两种流体原料的装配配方只有它能做。
地核钻探者不跑配方：装满电与钻头后把正下方掏成竖井直到基岩，掉落按精准采集结算，
并收进机器上方的容器里。各自的配方清单见 [machine/README.md](src/main/java/com/starcrosser/inspiration/machine/README.md)。

**机器本体的合成形状是统一的**：主要材料占四角与底中，次要材料占上中与两侧，核心坐正中，
升级机型把底中换成下级机器 —— 见文档里的选材表。

### 结构模拟机（多方块）

第一台**多方块**机器：用高能机器外壳（铁块 + 无恒物质）围出 4 宽 × 3 高 × 3 长的中空长方体，
正面中层左二那格放主方块，机身上嵌**输入仓 / 输出仓 / 能量端口 / 灵感提供仓 / 数据仓**
五种仓室（都可以顶替外壳的位置）。数据仓里放一份**结构数据**（不消耗），
机器按数据里的参数耗电、耗灵感充能（固定 8 mB/刻），跑完从对应的战利品表里摸一次奖。

成型判定只借用 Multiblocked2 的**结构层**（`BlockPattern` / `Predicates`），
控制器自己仍是 Titanium 方块实体 —— 界面、能量、充能都不受那个框架约束。
多方块目前**不能装插件**：电和充能都来自仓室，插件改容量没有意义。
详见 [multiblock/README.md](src/main/java/com/starcrosser/inspiration/multiblock/README.md)。

### 灵感

灵感是**玩家思维力量的具体化** —— 设定上如此，机制上它是本模组的基石资源：
既是合成材料，也是机器的第二种消耗品。

| 配方 | 产物 |
| --- | --- |
| 4 铜锭 + 1 下界石英 | 1 灵感 |
| 4 金锭 + 1 紫水晶碎片 | 4 灵感 |

思维流体凝固出的浓缩灵感结晶也是来源之一。

游戏内的入门说明写在**灵感指导书**里（**书 + 灵感**无序合成，右键翻开）：
第一章讲灵感是什么，后面几章把六台机器、能量与充能、插件、注入结构、辐射各交代一遍。
章节内容按语言键写在语言文件里（改文案不用碰代码），实现见
[guide/README.md](src/main/java/com/starcrosser/inspiration/guide/README.md)。

### 灵感充能

机器运行的第二种消耗品：只存在于机器罐体中（无流体方块与桶），灵感 = 1,000 mB、
灵感碎片 = 100 mB，罐体容量 16,000 mB。充能不足时进度条暂停，不会扣除材料。
详见 [machine/charge/README.md](src/main/java/com/starcrosser/inspiration/machine/charge/README.md)。

### 思维流体

倒出后接触水或岩浆会凝固，产物取决于接触对象与它是源还是流动：

| 接触 | 源 | 流动 |
| --- | --- | --- |
| 水 | 浓缩灵感结晶 | 稀释灵感结晶 |
| 岩浆 | 思维水晶岩 | 灵感凝胶 |

反应只替换思维流体那一格，水与岩浆不受影响，可反复量产。详见 [fluid/README.md](src/main/java/com/starcrosser/inspiration/fluid/README.md)。

### 机器插件

界面右侧四个槽装入即生效，同类叠乘，不同种类之间乘算：

| 插件 | 效果（每个） | 代价 |
| --- | --- | --- |
| 电容插件 | 能量缓冲容量 ×2 | 无 |
| 电容降级插件 | 能量缓冲容量 ×0.67 | 无 |
| 灵感插件 | 灵感充能上限 ×2 | 无 |
| 速度插件 | 运行速度 ×2 | 每刻耗电 ×4、每刻充能消耗 ×2 |

详见 [machine/plugin/README.md](src/main/java/com/starcrosser/inspiration/machine/plugin/README.md)。

### 注入合成

中心物质束缚器 + 正下方等级底座（方块标签 `inspiration:injection_tier_N`）+ 周围 5×5×5 内的注入枪。
枪由红石上升沿触发，**只有被触发的那一支放电**；能量低于配方下限则材料损失，高于上限则在束缚器处爆炸
（刻意设计，不提供规避手段）。详见 [injection/README.md](src/main/java/com/starcrosser/inspiration/injection/README.md)。

### 下一阶段主材：末地合金、无恒合金与末地合金盾牌

**末地合金** = 4 黑曜石 + 1 末影珍珠 + 4 紫珀块在强能物质融合机里融合（800 刻 / 600 FE·t⁻¹）；
把**末地合金**注入**盾牌**（500k~1500k FE）得到**末地合金盾牌**：外观就是原版那面盾，只是盾面换成了
末地石材质（物品模型走 `builtin/entity` + 自定义渲染器，举盾姿态与格挡行为都沿用原版），
耐久是普通盾牌的 10 倍，被击碎时会把周围 12 格内的敌对生物随机甩到 16~32 格之外。
把**无恒物质**注入末地合金得到**无恒合金**。这条注入配方的能量窗口不写死在数据里，
而是每个存档按种子随机一次：`i` 按 50% / 25% / 12.5% / 6.25% / 6.25% 取 `[0,4]`，
`j` 在 `[0, 4-i]` 上均匀取，窗口 = `[2^i × (2/3)^(j+1) × 100K, 2^(i+1) × (2/3)^(j+1) × 100K]`
（最低约 13K，最高约 107 万）。JEI 里一律显示 `???`，
要读出本存档的实际窗口得用**世界数据探测器**（仅创造模式可得，右键打印）。

**高能核心** = 强能核心 + 钫盐 + 强化深板岩在灵感装配台合成，是强能核心之上的一档能量部件。

### 高能机器与注能锭

高能机器统一用**无恒合金**做外壳、用**高能核心**做中枢，外加一种由灵感注能得到的材料：

| 注能锭 | 注入配方 | 能量窗口 |
| --- | --- | --- |
| 注能青金石锭 | 青金石锭 ← 灵感 | 10k~15k FE |
| 注能红石锭 | 红石锭 ← 灵感 | 150k~300k FE |
| 注能等离子锭 | 等离子锭 ← 灵感 | 1M~2M FE |

| 高能机器 | 合成（都是同样的形状） | 额外效果 |
| --- | --- | --- |
| 高能物质重塑机 | 物质重塑机 + 高能核心 + 4 无恒合金 + 3 注能红石锭 | 无 |
| 高能矿物扭曲机 | 矿物扭曲机 + 高能核心 + 4 无恒合金 + 3 注能青金石锭 | 等效时运 12 |
| 高能灵感装配台 | 灵感装配台 + 高能核心 + 4 无恒合金 + 3 注能等离子锭 | 6 物品槽 / 2 流体槽 |

注能等离子锭的窗口是全部注入配方里最高的一档（1M~2M FE），得靠电容插件把注入枪的容量堆上去。

**幽匿合金** = 回响碎片 + 强化深板岩在物质融合器里融合得到 2 个（400 刻 / 200 FE·t⁻¹）。
它的方块是注入结构的 **2 级底座**（1 级是玄武岩合金块），目前唯一一条 2 级配方是
「翡翠钢升级模板 ← 注能翡翠钢」（500k~1M FE）→ 注能翡翠钢升级模板。

### 辐射

暴露在空气中的辐射方块、以及玩家背包里的辐射物品，每秒向所在区块注入辐射；
区块每小时向四个正方向中辐射更低的邻区块传播 `⌊x/100⌋` 并额外衰减 `⌊x/10⌋`。
玩家站在辐射区块里每秒吸收一次区块值，超过 10K / 50K / 100K / 500K / 1M 逐层叠加负面效果
（最高层：凋零、饥饿 II、缓慢 II、挖掘疲劳 III、虚弱 III），死亡后体内辐射减半。

| 来源 | 辐射值 |
| --- | --- |
| 钫盐矿、粗钫盐、钫盐 | 1 |
| 钫粉、钫锭 | 3 |
| 钫盐块 / 钫块 | 9 / 27 |

钫盐矿、钫盐块、钫块三个方块物品拿在身上同样计辐射（物品表与方块表各登记一份）。

钫盐矿在地狱 Y 95~124 稀有生成且不露天；盖革计数器由**铁锭注入铯锭**（150k~250k FE）得到，
手持右键读出所在区块与自身的辐射值。详见 [radiation/README.md](src/main/java/com/starcrosser/inspiration/radiation/README.md)。

### 工具与武器

```
翡翠钢升级模板 + 灵感系列工具 + 翡翠钢锭 → 对应翡翠钢工具
注能翡翠钢升级模板 + 翡翠钢工具 + 注能翡翠钢 → 对应注能翡翠钢工具
```

灵感系列共五件（剑 / 镐 / 斧 / 锄 / 铲，铲由翡翠钢锭 + 灵感 + 粘液球合成，压土径掉草皮）。
翡翠钢系列挖掘等级对齐下界合金，经验掉落效果全部保留并按新数值放大。
注能翡翠钢系列挖掘速度 12，且**长按右键 1.5 秒消耗 20 点经验换取 30 秒增益**：

两档的耐久都**按件区分**（剑最少、锄最多：翡翠钢 2,250~3,100、注能 3,600~5,000），
因为它们绕开了 `TieredItem` 用品质耐久覆盖 `Properties` 的行为（见工具文档）。
剑=速度、镐=急迫、斧=力量、锄=幸运、铲=跳跃提升。
详见 [item/tool/README.md](src/main/java/com/starcrosser/inspiration/item/tool/README.md)。

### JEI

提供六个分类：物质融合、物质重塑、矿物扭曲、灵感装配、右键转化、注入。
单方块机器面板只显示基准耗时与基准每刻耗电两行（融合分类因为有 3 材料配方而高一格，
需要多个的输入槽会直接画出数量），注入面板保留完整的等级与能量窗口信息，
种子随机的配方则把上下限写成 `???` 并加一行说明。
详见 [client/jei/README.md](src/main/java/com/starcrosser/inspiration/client/jei/README.md)。

## 许可

All Rights Reserved。项目基于 NeoForge MDK 模板创建。
