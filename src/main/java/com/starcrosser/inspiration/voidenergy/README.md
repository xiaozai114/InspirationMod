# 虚空能量（`voidenergy` 包）

相关：[机器体系](../machine/README.md) ・ [注入](../injection/README.md) ・ [配方](../recipe/README.md)

虚空能量提取器从开裂基岩中生成团簇，供虚空注入、凝聚和谐振使用。

## 1. 一条链

```
基岩 ──幽匿合金钻头（地核钻探者）──> 开裂的基岩
                                        │ 放上
                                   虚空能量提取器（+1 ve/刻 给它上方那一格）
                                        │ 生成/充能
                                   虚空能量团簇（实体）
                                        │ 用
              虚空能量中继器（必须与团簇同格）──> 5×5×5 内的机器
```

- **钻头**：`DrillBitItem#cracksBedrock()` 是唯一开关，幽匿合金钻头为 true。
  钩子在 `CoreDrillerTile#drillStep` 尾部（竖井挖到 `total` 之后），只把紧贴底部的
  **原版基岩**换成开裂基岩，重复调用幂等。
- **开裂的基岩**：硬度 -1（和基岩一样不可破坏），掉落表为空，不会被刷成可携带物品。
- **提取器**：只认「脚下是开裂基岩」；上方是空气就生成团簇，被占住就什么也不做。
  **需要电力**：每产 1 ve 扣 50 FE（`ENERGY_PER_TICK`），没电时既不产 ve 也不啃电；
  它不是 Titanium 机器，能量接口由 `VoidEnergyCapabilities` 挂到 `Capabilities.EnergyStorage.BLOCK`。
- **中继器**：自身不存能量，必须放在**团簇所在的那一格**（团簇是实体，可与方块重叠），
  范围以它为中心 ±2（5×5×5）。查找入口只有 `VoidEnergyNetwork#findCluster`。

## 2. 阶段表

`VoidClusterStage` 用左闭右开区间，新增阶段只加一行。每刻效果全部在服务端结算：

| 区间（ve） | 阶段 | 每刻 |
| --- | --- | --- |
| [0, 1000) | 虚弱 | — |
| [1000, 2000) | 攀升 | −2 |
| [2000, 4000) | 高原 | — |
| [4000, 8000) | 震荡 | 随机 −60~+50 |
| [8000, 32000) | 山巅 | — |
| [32000, 36000) | 危险 | 1%/刻 自爆（威力 6） |
| [36000, ∞) | 天界 | — |

能量归零后**持续 200 刻**才消散（`EMPTY_LIFETIME`），提取器重新供能会立刻把它拉回来。
上限按 int 收口在 `MAX_ENERGY = 1e9`。

## 3. 渲染

团簇是一张始终朝向摄像机的发光面片（`VoidClusterRenderer`），颜色取阶段色、尺寸随能量
在 0.45~0.8 之间长大。不开剔除、两面都画 —— 面片会随视角翻转，靠背面剔除会周期性闪没。

## 4. 虚空注入（已完成，含 JEI）

**只吃 ve，不吃 FE**：虚空注入枪没有能量缓冲，配方也只有 ve 窗口。虚空合金例外 ——
它走**普通注入**（末地合金 + 虚空露珠，tier 2，FE 窗口 `{1}`），不消耗 ve。

- 配方：`VoidInjectionRecipe`（base 必填、inject 可空、tier、veMin、veMax、result、duration）。
- 判定：`cost = max(1, ve/10)`，**拿 cost 比窗口**：`cost > veMax` 过载爆炸、`cost < veMin` 不生效
  （材料照扣，与普通注入一致）、否则成功并扣 `cost`。
- 枪：`VoidInjectionGunTile extends InjectionGunTile`，只重写
  `startInjection` / `hasResolvedRecipe` / `ensureRecipeResolved` / `pendingResult`；
  父类已把这四个方法与 `activeRecipe` 等字段开放为 `protected`，流程（绑定、红石上升沿、
  进度、激光、爆炸）完全复用。
- 中继器接入：`VoidEnergyNetwork.findCluster(level, gunPos)`，同格团簇或半径 2 内的中继器。
- 虚空能量凝聚器：最多 16 ve/刻，每 1024 ve 产 1 虚空露珠。
- 零点能发电机：long 能量缓冲（128 GFE），单槽最多 64 个增幅器，每分钟产 `1 + n×1.2G` FE。
### 凝聚器与发电机（已完成）

- **虚空能量凝聚器**：`VoidCondenserTile extends PoweredTile`（不耗灵感，所以没有充能槽），
  每刻 16 ve + 500 FE，1024 ve（64 刻）产 1 虚空露珠；ve 同样走 `VoidEnergyNetwork`。
- **零点能发电机**：`ZeroPointGeneratorTile extends ActiveTile`，**不用 Titanium 的能量组件**
  （那是 int 的）：缓冲是 long 的 `stored`（128 GFE），界面那根「电量条」是百分比（0~1000）。
  单槽按 64 堆叠放增幅器，每 1200 刻产出 `1 + n×1.2G` FE，每刻往六邻推送（单面 ≤200k）。
- 配方：凝聚器 = 4 虚空等离子锭 + 4 铷锭 + 弱化核心；发电机 = 4 虚空等离子锭 + 4 钚锭 + 零点模块；
  零点模块 = 弱化核心重塑；增幅器 = 虚空合金 + 4 注能红石锭 + 576 mB 液态金属（装配）。

增幅器用的是 **576 mB 液态无恒合金**（`inspiration:molten_ephemeral_alloy`），
由材料的 `.molten(true)` 自动登记（流体类型 / 源 / 流动 / 桶 + 提取配方），
专属贴图放在 `block/molten_ephemeral_alloy_*`，生成脚本不会覆盖它。

## 5. 汇流与谐振

虚空能量汇流器以自身为中心搜索 5×5×5 方块，每刻从每个来源团簇收取最多 16 VE，存入正上方的团簇。输出格可留空，也可放谐振腔或中继器。输出受阻或已满时，能量保留在来源团簇中。

谐振腔可直接从同格团簇取能，也可经两格以内的中继器取能。四条初始配方都运行 200 刻，每刻 500 FE、2 VE。第一刻能量备齐后才取一份输入。之后单独缺电会暂停，断供 VE 会清空进度并损失该份输入。

谐振灵感每个可提供 4,000 mB 灵感充能。用谐振末地合金作注入基底，可在原配方的能量窗口内制得两个虚空合金或两个无恒合金。
