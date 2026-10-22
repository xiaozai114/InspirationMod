# client/ —— 纯客户端代码

P3 之后**所有**只在客户端存在的代码都在这一个包下面，包括 JEI 插件。
找客户端代码不用再满仓库翻；反过来，`client/` 之外的任何地方都不该出现客户端类。

## 1. 目录

| 子包 | 放什么 | 现有类 |
| --- | --- | --- |
| （根） | 客户端入口与两类注册 | `InspirationClient`（`@Mod(dist = CLIENT)` 入口）、`ClientModEvents`（渲染器 / 界面 / 客户端初始化）、`ClientExtensionsHandler`（物品与流体的客户端扩展） |
| `extensions/` | 挂给原版 / NeoForge / Titanium 的客户端扩展与 addon | `EndAlloyShieldClientExtensions`、`InspirationChargeClientExtensions`、`MindFluidClientExtensions`、`FacingLabelAddon`、`StatusAddon`、`ProjectorPreviewHandler` |
| `renderer/` | 渲染器 | `EndAlloyShieldRenderer`、`MatterBinderRenderer`、`SimulatorPreviewRenderer` |
| `tooltip/` | tooltip 监听（都是 `Dist.CLIENT` 的 `ItemTooltipEvent`） | `MachineTooltips`、`AlkalinityTooltips`、`RadiationTooltips` |
| `guide/` | 指导书界面 | `GuideBookScreen`（菜单在 `guide/GuideBookMenu`，服务端也要用） |
| `util/` | 只在客户端用的小工具 | `FacingLabels`（给朝向按钮挂标签，调用方必须标 `@OnlyIn`） |
| `jei/` | JEI 插件与七个配方分类 | `InspirationJeiPlugin`、`ModJeiRecipeTypes`、`JeiLayout`、`JeiSlots`、`MachineRecipeText`、`StructurePreview`、`category/*`（详见 `jei/README.md`） |

## 2. 三条规矩

1. **新写的客户端类一律放 `client/`**，不要再在 `machine/`、`multiblock/` 下面开 `client` 子包。
   之前散在四个地方（`machine/client`、`multiblock/client`、`alkali/client`、`radiation/client`），
   收拢后一眼就能看清边界。
2. **服务端代码要碰客户端类，必须标 `@OnlyIn(Dist.CLIENT)`**（或者整个类用
   `@EventBusSubscriber(value = Dist.CLIENT)` 只在客户端注册）。
   典型场景：Tile 给 Titanium 界面挂 addon（`initClient` / `addGuiAddonFactory`）。
   不标的后果是**本地永远测不出来**——客户端类在本地一定存在，只有上专用服务器才炸。
3. **`datagen/` 不在本包管辖内**：数据生成 API（`neoforge.client.model.generators`）本身就长在
   client 包下面，而 datagen 只在 `runData` 时存在，不会进服务器。

## 3. 自动体检

`util/ClientIsolationCheck` 会在**非生产环境**每次开服时扫一遍自己的 class：
读字节码看每个方法引用了哪些类型，凡是引用了客户端类、但没标 `@OnlyIn(CLIENT)` 的，
直接打 ERROR 日志列出类 + 方法 + 引用到的类。

- 「客户端类」的判定：内部名带 `/client/` 段（含本包），外加 `com/mojang/blaze3d/`、`mezz/jei/`。
- lambda / 合成方法拿不到注解，跟着「所在类有没有客户端方法」走，避免误报。
- 生产环境（`FMLLoader.isProduction()`）直接跳过，正式包里没有开销。

看到报错的处理方式二选一：给那个方法标 `@OnlyIn(Dist.CLIENT)`，或者把这段代码挪进 `client/`。
