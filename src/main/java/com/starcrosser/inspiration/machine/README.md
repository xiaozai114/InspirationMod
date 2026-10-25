# 机器体系（`machine` 包）

相关：[插件](plugin/README.md) ・ [充能](charge/README.md) ・ [配方](../recipe/README.md) ・
[JEI](../client/jei/README.md)。**12 台用电机器 + 2 台发电机**，Titanium 组件拼装；用电机器
右侧四个插件槽、能量**只进不出**，发电机相反（`maxReceive = 0`，每刻推六邻）。

| 机器 | 配方 | 缓冲 / 功率（FE，FE·t⁻¹） | 充能 |
| --- | --- | --- | --- |
| 物质融合器 | 物质融合 | 60k / 1k | 1 mB/刻 |
| 强能物质融合机 | 物质融合 | 120k / 2k | 2 mB/刻 |
| 物质重塑机 | 物质重塑、右键转化 | 200k / 4k | 2 mB/刻 |
| 高能物质重塑机 | 物质重塑、右键转化 | 400k / 8k | 4 mB/刻 |
| 矿物扭曲机 | 矿物扭曲 | 400k / 8k | 2 mB/刻 |
| 高能矿物扭曲机 | 矿物扭曲（时运 12） | 800k / 16k | 4 mB/刻 |
| 灵感装配台 | 灵感装配 | 300k / 6k | 2 mB/刻 |
| 高能灵感装配台 | 灵感装配 | 600k / 12k | 4 mB/刻 |
| 流体提取机 | 流体提取 | 400k / 8k | 2 mB/刻 |
| 原料精炼机 | 原料精炼 | 300k / 6k | 2 mB/刻 |
| 培养缸 | 无（按脑类型固定产出） | 400k / 20k | 无 |
| 地核钻探者 | 无（掏竖井） | 4M / 20k | 16,000 mB/次 |
| 碱性发电机 | 无（烧碱性物质） | 1M / 输出 8k | 无 |
| RTG反应堆 | 无（烧放射性物质） | 4M / 输出 64k | 无 |

## 通用规则

- **高能机型**（紫壳）= 上一档 ×2：容量 / 速度 / 每刻耗电 / 每刻耗充能，总耗电不变、只是快一倍；
  配方共用一套，靠槽数天然区分。倍率写在各自实体的 `baseSpeed()` / `baseChargeCapacity()` /
  `energyCost()`，基类不用改。
- 高能矿物扭曲机时运抬到 12、输出槽按 13 倍预留；高能灵感装配台物品槽 4→6、流体槽 1→2。
- **发电**：碱性 = 12,000 刻 ×（100 × 碱性值）FE；RTG = 24,000 刻 ×（1000 × 辐射值）FE + x 个核废料。
  ⚠️ 燃料**开工瞬间扣掉**（`canBurn` 先认 `burningValue` 再回查槽位）；缓冲满时进度条被打回 0 且
  **不回调** `onFinishWork` → 收尾靠自己的 `burnedTicks`。速度插件 = 功率翻倍、时长缩短，
  总发电量与副作用次数不变。RTG 每 1200 燃烧单位放 `(x-1)^3` 辐射，x > 9 再按 `(x-9)/1000` 概率爆炸。

## 合成形状

`machine(...)` 统一为 `M S M / S C S / M X M`：M 主要材料（5 份，按**用量**定）、S 次要（3 份）、
C 核心、X 下级机器（此时 M 只剩四角）。低阶 M = 翡翠钢、C = 强能核心；高能机型 M = 无恒合金、
C = 高能核心，S 换对应注能锭；发电机 C = 发电 / 反应核心。逐台见 `MachineBlockRecipes`。

**例外**：原料精炼机要 3 钙锭 / 5 无恒合金 / 1 高能核心（5/3/1 套不上），自己写 `SCS / CHC / SSS`；
核心本体与注入结构件也不走 —— 材料不足三种。

## 后加的几台机器

**流体提取机**：1 输入槽 + 16,000 mB 输出罐（`Action.DRAIN`）。一锭 = **144 mB**（9 的倍数 →
粒 16、块 1296）。产物是流体，所以不复用 `InspiredRecipe`，`getToastSymbol` 顶个桶。

**原料精炼机**：1 输入 → 1 主产物 + 2 副产物。三格是**同一个库存组件**、竖排 x=116
（y=20/38/56；横摆会撞 x=150 的充能罐），第 0 格主产物。

- ⚠️ 先确认三格都塞得下再扣材料（`canAcceptOutputs`）；`finishCraft` 留快照，任一格失败整批回滚。
- 副产物 = 基准 × `PluginEffects#byproductCount`，由**回收升级**给**加算 +100%**（装两个是 ×3 不是 ×4）。
  唯一加算的插件，也只有这台机器读它。
- 副产物写成列表：两个平铺字段会到 7 个通道，超过 `StreamCodec.composite` 上限。

**培养缸**：不走配方表，按槽里的脑决定产出（`BrainKind` 枚举）。村民脑 100 FE + 1 mB/刻、
200 刻 1 灵感；末影人脑 1000 FE + 10 mB/刻、400 刻 1 思维晶体。脑**不消耗**。
⚠️ 营养液见底时村民脑会变成失活大脑、进度归零（末影人脑不会），所以村民脑的 `canIncrease`
**故意不查营养液** —— 让 `onTickWork` 还有机会跑到那一格去失活；写反了就成了「进度卡住、脑永远不失活」。
继承 `PoweredTile` 而不是 `InspiredMachineTile`（它不耗灵感充能，也就不该有充能槽）。
方块右键用 `FluidUtil.interactWithFluidHandler` 处理桶进桶出。

## 各配方类型

- **融合**：顺序无关，可选 `third_input`，材料数须等于非空槽数（3 材料只有强能机型能做）；
  `*_count` 表示一次消耗几个，同一槽堆叠。
- **重塑**：右键转化 + 重塑配方（可选第二输入、`input_count`）由 `ReshaperTask` 统一描述；装备回炉 41 条走数据表。
- **扭曲**：单槽，产量按时运掷出，公式同原版 `ApplyBonusCount.OreDrops`（6 → 3.625 倍、12 → 7.75 倍）；
  远古残骸与无恒矿石不在配方里。
- **装配**：4 物品槽 + 1 流体槽（高能 6 + 2），材料与槽位不必一一对应，`resolveSlots` / `resolveFluids`
  回溯指派，流体最多两条；流体槽**不收灵感充能**。
- **地核钻探**：不跑配方，一次作业结清（满电 + 16,000 mB + 1 点耐久）；进度 = 竖井格数 × 2；
  统一按精准采集结算（刷怪笼不掉自身、强化深板岩掉自身）；产物塞进机器**正上方**的容器。

## Titanium 约定

- 机器统一继承 `InspiredMachineTile`（充能槽 + 16,000 mB 罐 + 每刻刷新插件倍率）；子类实现
  `chargePerTick()` / `baseEnergyCapacity()` / `getProgressBar()`，可覆写 `baseSpeed()` / `baseChargeCapacity()`。
- ⚠️ **只有 `MachineTile#onActivated` 会 `openGui()`**，继承 `ActiveTile` / `PoweredTile` 的要自己覆写。
- `registerBlockWithTile` 一次注册方块 / 方块物品 / 方块实体 / 能力；**机器方块不进 `ModBlocks.BLOCKS`**
  → 战利品表与方块标签单独补。
- 自动化用 `level.getCapability(Capabilities.ItemHandler.BLOCK, pos, side)`；改槽用 `getHandlerFromName("名字")`；
  给电用 `getEnergyStorage().setEnergyStored(v)`。
- ⚠️ 罐体 `Action` 会挡住机器自己的读写：`FILL` 罐扣减要 `drainForced`、`DRAIN` 罐灌入要 `fillForced`，
  普通 `fill` / `drain` **静默返回 0**（表现：原料被吃掉、罐里空的）。

## 界面网格（176×184）

能量条 (8,14) 12×50 ・ 输入槽 (44,30) ・ 输出槽 (116,39) ・ 充能槽 (150,74) / 充能罐 (150,14) 18×56 ・
侧向按钮 y=84（x 从 8 起、间隔 18）・ 插件底板 (175,4)、槽位 (180,11)。原料精炼机三格竖排 (116,20/38/56)；
灵感装配台罐 (26,14) + 输入 (52,25) 2×2，高能机型两罐 (26,14)/(44,14) + 输入 (66,16) 2×3。
注意 `SidedFluidTankComponent(name, 容量, x, y, 序号)` **容量在第 2 位**。

**侧向标签**：`client/extensions/FacingLabelAddon` 在按钮上方（`y = 按钮y − 10`）画
`gui.inspiration.facing.<组件名>`，间隔 18 像素 → 限两个汉字。`initClient()` **必须标 `@OnlyIn(Dist.CLIENT)`**。

## 机器 tooltip

`client/tooltip/MachineTooltips` 按物品 ID 挂 `tooltip.inspiration.machine.<机器名>`，语言值里的 `\n`
由它手动切成多行；新增机器往 `MACHINES` 加一行 + 补双语。ID ≠ 显示名：`alkaline_reactor` = 碱性发电机、
`rtg_generator` = RTG反应堆。
