# 工具与武器（`item/tool` 包）

相关：[升级模板](../../machine/README.md) ・ [注能材料](../../injection/README.md) ・ 贴图脚本在仓库根 `tools/textures/`。
数值是**面板值**（组件值 +1 伤害 / +4 攻速），耐久即品质的 `uses`。

```
灵感系列（剑/镐/斧/锄/铲）
  └─ 翡翠钢升级模板 + 翡翠钢锭 ──► 翡翠钢系列
        └─ 注能翡翠钢升级模板 + 注能翡翠钢 ──► 注能翡翠钢系列
```

翡翠钢升级模板由物质重塑机从任意盔甲纹饰模板重塑（400 刻 / 200 FE·t⁻¹）；
注能版要把它放进束缚器做**二级注入**（500k~1M FE，底座必须是幽匿合金块）。两个模板由
`JadeSteelUpgradeTemplate` 的两个工厂产出，差别只在语言键前缀、适用范围（注能版多一把铲）与配色。

## 数值

| 工具 | 伤害 | 攻速（次/秒） | 耐久 | 经验/次 | 备注 |
| --- | --- | --- | --- | --- | --- |
| 灵感之剑 | 6.0 | 1.8 | 720 | 5 | |
| 灵感之斧（粘性斧） | 10.0 | 0.8 | 800 | 1 | |
| 灵感之镐 | 4.0 | 1.6 | 880 | 1 | 要挖黑曜石，用钻石品质 |
| 灵感之铲 | 4.0 | 1.0 | 880 | 1 | 灵感系列唯一合成品（锭/灵感/粘液球竖排） |
| 灵感之锄 | 1.0 | 4.0 | 1,000 | 1 | |
| 翡翠钢剑 | 7.5 | 2.0 | 2,250 | 12 | 对比下界合金：攻速 +0.4、单次伤害 -0.5 |
| 翡翠钢斧 | 11.5 | 0.8 | 2,500 | 3 | 伤害 +1.5、攻速 -0.2 |
| 翡翠钢镐 | 5.5 | 1.6 | 2,750 | 3 | 挖掘速度 10 | 
| 翡翠钢铲 | 6.5 | 1.0 | 2,750 | 3 | 耐久 +719 |
| 翡翠钢锄 | 1.0 | 4.0 | 3,100 | 3 | 其余持平 |
| 注能翡翠钢剑 | 10.0 | 2.1 | 3,600 | 18 | 蓄力 → 速度 I |
| 注能翡翠钢斧 | 14.0 | 1.0 | 4,000 | 5 | 力量 I |
| 注能翡翠钢镐 | 8.0 | 1.8 | 4,400 | 5 | 急迫 I |
| 注能翡翠钢铲 | 9.0 | 1.2 | 4,400 | 5 | 跳跃提升 I |
| 注能翡翠钢锄 | 3.5 | 4.0 | 5,000 | 5 | 幸运 I |

品质：翡翠钢挖掘速度 10 / 伤害加成 3.5 / 附魔 16，用翡翠钢锭修；注能 12 / 5.0 / 20，用注能翡翠钢修。
两档挖掘等级统一取下界合金（`incorrect_for_netherite_tool`），即能挖所有钻石镐级方块。

| 特殊武器 | 品质 | 耐久 | 说明 |
| --- | --- | --- | --- |
| 风暴之剑 / 斩首之斧 / 死神镰刀 | 钻石 | 1,800 | 风暴之剑右键发射风弹；镰刀本质是锄类 |
| 世界粉碎者 | 下界合金 | 999,999 | 右键移除基岩并掉落（不靠挖掘等级） |

## 耐久：按件区分

原版「同一品质共用一份耐久」是因为 `TieredItem` 构造里有 `properties.durability(tier.getUses())`，
会把 `Item.Properties#durability` 设的值**覆盖掉**（后写的赢）。

- 完全不用 `TieredItem` 可行但代价大：1.21 的挖掘等级与速度由 `DataComponents.TOOL` 驱动，
  真正的麻烦是 `useOn`（铲压土径、锄耕地、斧剥皮/刮铜/脱蜡），而我们正好依赖这三样，
  照抄约 350 行、抄漏一处就静默丢功能。
- 所以用**品质包装** `DurabilityTier`：只把 `getUses()` 换成指定值，其余全部代理原品质。
  用法 `ModToolTiers.withDurability(Tiers.IRON, ToolDurability.EXP_SWORD)`。
- 规则：**基数 × 类型系数**（剑 0.9 / 斧 1.0 / 镐 1.1 / 铲 1.1 / 锄 1.25，锄用途最窄给补偿）。
  基数：灵感 800、钻石档特殊武器 1,800、翡翠钢 2,500、注能 4,000。
- 不在 `TieredItem` 体系里的物品（打火石、钻头、盾牌）写 `durability()` 照常生效；
  「不可破坏」用 `durability(0)`（此时 `isDamageableItem()` 为假）。

## 特殊效果

- 剑命中掉经验、镐挖方块掉经验、斧剥皮掉木棍与经验、锄草方块掉草皮与经验；
- **铲把草方块压成土径**时掉草皮与经验（`ExperienceShovel`）：只在 `GRASS_BLOCK` 且原版逻辑真的
  压成功时才触发 —— 判据 `result.consumesAction()`，泥土 / 菌丝体等不触发。

经验量由 `ExperienceTool` / `MiningExperienceTool` 接口暴露，挖掘事件只认接口不认物品。

### 蓄力增益（仅注能系列）

`ChargedBuffTool#chargedEffect()` 声明效果，其余（蓄力 30 刻 / 耗 20 点经验 / 持续 600 刻 / I 级）
是默认值，共同实现是 `ChargedBuff`：经验不足（按 **`totalExperience` 总点数**，不是等级）直接
`FAIL`；`onUseTick` 每 5 刻冒附魔粒子；`finishUsingItem` 扣经验、上效果、播紫水晶叮声
（中途松手不会走到这里）。

⚠️ **接口的 `default` 打不过父类 `Item#use`**：五件工具都得自己覆写 `use` / `getUseDuration` /
`onUseTick` / `finishUsingItem` / `appendHoverText` 并转调 `ChargedBuff`。单继承无解，保持现状。

## 实现要点

- 品质集中在 `ModToolTiers`（`SimpleTier` 记录，数据驱动），耐久常量在 `ToolDurability`，
  包装实现在 `DurabilityTier`。
- `Item.Properties#durability` 对 `TieredItem` **无效**（见《耐久》），按件给耐久走 `withDurability`。
- 1.21 的挖掘等级由 `incorrect_for_<tier>_tool` 标签反着描述，而 diamond 与 netherite 两个标签
  **都是空的**，两者等价。
- 核对数值要读组件：`ATTACK_DAMAGE` +1、`ATTACK_SPEED` +4 才是面板值，另看 `MAX_DAMAGE`；
  挖掘等级用 `stack.isCorrectToolForDrops(state)`（剑恒为 false，别误判）。
- 自检里改经验**要调 `giveExperiencePoints`**：直接写 `totalExperience` 会被原版经验进度循环
  在扣费时把总量归零，读出假数据。
