# 灵感充能（`machine/charge` 包）

相关：[机器体系](../README.md) ・ [灵感插件](../plugin/README.md) ・ [JEI 显示什么](../../jei/README.md)

机器运行除电力外还要消耗灵感充能，这是本模组的第二消耗品。

## 规则

- 灵感充能是一种只存在于机器罐体中的流体（`inspiration_charge`），**没有对应的流体方块与桶**。
- 机器 GUI 右侧设有充能槽：每个「灵感」转化 1,000 mB、「灵感碎片」转化 100 mB；罐体容量 16,000 mB。
- 消耗速率按机器定：物质融合机每运行一刻 1 mB，物质重塑机与矿物扭曲机每刻 2 mB。
- 罐体只允许灌入灵感充能，且只进不出；充能不足时进度条暂停，**不会扣除材料**。

## 实现

充能槽与罐体由 `InspiredMachineTile` 统一提供（详见 [机器体系](../README.md)），
具体机器只声明 `chargePerTick()`，并在配方校验时判断余量、在结算时扣减。

换算与常量集中在 `machine/charge/`。

## 相关约定

- 罐体只支持 `Action.FILL`，所以程序内部扣减要用 `drainForced`，普通 `drain` 会被拦。
- `InspirationCharge.TANK_CAPACITY` 是基准容量，实际容量 = 基准 × 灵感插件倍率。
- JEI 面板不再列灵感充能消耗（详见 [JEI 支持](../../jei/README.md)），数值写在机器界面上。
