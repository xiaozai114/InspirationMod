# 插件体系（`machine/plugin` 包）

相关：[机器体系](../README.md) ・ [灵感充能](../charge/README.md) ・ [注入枪用了同一套槽位](../../injection/README.md)

界面右侧的四个槽就是插件槽：装入 `MachinePluginItem` 即生效，同类插件可叠乘，取下即恢复，
不同种类之间也是乘算。

相关代码在 `machine/plugin/`：

| 类 | 职责 |
| --- | --- |
| `MachinePluginType` | 插件种类枚举，**每类自带 `multiplier()`** |
| `PluginEffects` | 读槽位内容算总倍率；speed 派生 `energyUse = speed²`、`chargeUse = speed` |
| `PluginInventory` | 创建与配置四个槽 |
| `PluginHost` | 槽位内容指纹 + 变化时的结算回调 |
| `PluginEnergyStorage` | 容量可变的能量缓冲，替换 Titanium 默认的固定容量存储 |
| `PluginFluidTank` | 容量可变的罐体（灵感充能罐用），同样带容量同步 |

## 效果表

| 插件 | 效果（每个） | 代价 |
| --- | --- | --- |
| 电容插件 | 能量缓冲容量 ×2 | 无 |
| 电容降级插件 | 能量缓冲容量 ×0.67 | 无 |
| 灵感插件 | 灵感充能上限 ×2 | 无 |
| 速度插件 | 运行速度 ×2（进度条每刻推进量翻倍） | 每刻耗电 ×4、每刻充能消耗 ×2 |

调平衡只需要改 `MachinePluginType` 枚举里的倍率数值，`PluginEffects` 会自动折算：
速度插件「更快」的同时总能耗约为原来的 2 倍。

容量类插件只改容量，不带来额外消耗；容量变小（比如取下插件）时旧存量会被截断。

## 槽位创建与结算

`PluginInventory` 负责把四个槽配成插件槽：只收插件物品、每槽限 1 个、`disableFacingAddon()`，
并把六个侧面的 `FaceMode` 全设为 `NONE`（照抄 Titanium 自己的做法，防止被管道/漏斗改装）。
位置沿用 Titanium 机器的 `(180, 11)`。

`PluginHost` 每刻比对插件槽的内容指纹，只在变化时重算倍率并回调宿主：

1. 读四个槽的内容，拼成「物品 id:数量」指纹串；
2. 与上次不同则用 `PluginEffects.from(stacks)` 重算；
3. 宿主把容量写回 `PluginEnergyStorage`（能量）与充能罐（充能）。

## 容量必须同步到客户端

改完服务端的容量还不够 —— **GUI 是客户端 tile 在画**，而 Titanium 的同步项里没有容量：

- `EnergyStorageComponent` 只把「当前存量」注册成 `DataSlot`；
- `FluidTankComponent` 只同步「流体种类 + 存量」；
- 容量是构造值，客户端那一份永远是初始值。

于是界面上会出现两个症状：能量条 tooltip 的上限卡在原始值（注入枪无论装什么都显示 10 万），
以及灵感插件让实际存量超过旧容量后，液位按 `存量 / 旧容量` 算，直接画到槽位外面去。

解法是两个子类的 `getContainerAddons()`：在父类提供的同步项之后再挂一个
`IntReferenceHolderAddon(new FunctionReferenceHolder(写容量, 读容量))`。
菜单每 tick 广播一次，客户端收到就写回自己那份缓冲区，上限与液位比例随之修正。
`PluginHost` 的结算只在服务端跑，客户端不需要也不应该重算。

## 非机器宿主：注入枪

Titanium 的 augment 槽只存在于 `MachineTile`，所以要在别处方块实体上复用这套逻辑时，
还得自己补两处：

- 构造里用 `PluginInventory` 建槽；`initClient` 里手动加插件背景底板
  `new AssetScreenAddon(AssetTypes.AUGMENT_BACKGROUND, BACKGROUND_X, BACKGROUND_Y, true)`
  ——`MachineTile#initClient` 才会自动加，其它方块实体没有。
- **该方法必须标 `@OnlyIn(Dist.CLIENT)`**：不是因为它会被服务端执行，而是 JVM 在链接期
  就验证所有方法（含返回类型的父类加载），专用服务器会直接报
  `Attempted to load class GuiEventListener for invalid dist DEDICATED_SERVER`。
  Titanium 自己的客户端回调也是这么标的。

注入枪同样有这四个槽，但它只结算「容量」这一项 —— 压低上限就等于压低一次注入的放电量，
这正是电容降级插件的用途。
