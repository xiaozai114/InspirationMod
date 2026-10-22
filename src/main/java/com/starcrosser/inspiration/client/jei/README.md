# JEI 支持（`client/jei` 包）

相关：[客户端包总览](../README.md) ・ [机器体系](../../machine/README.md) ・ [注入合成](../../injection/README.md) ・ [数据生成（语言键出处）](../../datagen/README.md)

插件类以 `@JeiPlugin` 注解声明，由 JEI 自动发现；依赖是 `compileOnly`，运行时没有 JEI 也能正常游戏。
P3 之后整个 JEI 包收在 `client/` 下（JEI 是纯客户端 mod），分类在 `category/` 子包。

依赖版本：**JEI 19.57.0.451 / MC 1.21.1**。升级时下面几条 API 约定要重新核对。

## 六个分类

| 分类 | 催化剂 |
| --- | --- |
| 物质融合 | 物质融合器、强能物质融合机 |
| 物质重塑 | 物质重塑机 |
| 矿物扭曲 | 矿物扭曲机、高能矿物扭曲机 |
| 灵感装配 | 灵感装配台、高能灵感装配台 |
| 右键转化 | —— |
| 注入 | 物质束缚器、注入枪 |

上位机型与基础机型**共用一个分类**（配方类型相同），只是多挂一个催化剂；
界面里显示的是配方**基准值**，不含机型倍率（高能机型的 2 倍速 / 2 倍耗电不体现在这里，
高能扭曲机的等效时运 12 也不体现 —— 面板用的是配方声明的时运）。

新增一个分类要动三处，缺一不可：

1. `ModJeiRecipeTypes` 加常量；
2. 新建 `category/XxxRecipeCategory`；
3. `InspirationJeiPlugin` 里注册分类、灌配方、声明催化剂。

## 面板内容

单方块机器（融合 / 重塑 / 扭曲 / 装配）的面板**只给两行**：基准耗时与基准每刻耗电。
插件倍率、总能耗、灵感充能消耗在机器界面里看得到，重复列出只会把正文挤到看不见。
这两行由 `MachineRecipeText.baseLines(duration, energyPerTick)` 生成，语言键是
`jei.inspiration.machine.duration` 与 `jei.inspiration.machine.energy_per_tick`。

矿物扭曲的产量是随机的，产量区间与期望值挂在产物槽的 tooltip 上
（`addRichTooltipCallback`；旧的 `addTooltipCallback` 已过时）。

注入（多方块）的信息更关键，不走这套：两个输入各配一个「该材料放哪台设备」的小图标与角色标签
（注入物质 / 基底物质，分别着紫色与蓝色），右侧箭头指向产物；分隔线下方写出需求等级、
最小能量（金色）与最大能量（红色）。种子随机窗口的配方把上下限都写成 `???`
（不再额外加说明行——想知道具体数值得用世界数据探测器）。

融合分类里一次要消耗多个的槽位，改用带数量的 `ItemStack` 灌进槽位（`addItemStacks` + `copyWithCount`），
JEI 会在图标角上画出数字；只消耗 1 个时照旧走 `addIngredients(Ingredient)`，保留标签类原料的轮播。

## 布局常量

都在 `JeiLayout`，数值与 `assets/inspiration/textures/gui/jei/*.png` 的实际尺寸保持一致。

| 用途 | 常量 |
| --- | --- |
| 普通面板（融合 / 重塑 / 扭曲 / 右键转化） | `WIDTH=104`、`HEIGHT=54` |
| 双输入槽 / 输出槽 | `FIRST_INPUT_X=8`、`SECOND_INPUT_X=30`、`OUTPUT_X=78`，`SLOT_Y=6` |
| 说明文字区 | `TEXT_Y=30` |
| 融合分类（含 3 材料配方） | 面板加高到 `TALL_HEIGHT=74`，第三槽 `(19, 24)`，说明文字 `TALL_TEXT_Y=46` |
| 灵感装配（最多 3×2 输入 + 2 流体罐） | 列 `8/30/52`、行 `6/24`，输出 `(98, 24)`；流体罐 `(8, 44)`、`(30, 44)`；面板 `GRID_WIDTH=122`、`GRID_HEIGHT=96`、`GRID_TEXT_Y=68` |
| 注入面板 | `INJECT_WIDTH=150`、`INJECT_HEIGHT=112`（说明区要塞下四行，100 会让最后一行被截断） |
| 注入图标 / 槽位 / 角色标签 | 见文件末尾那一组 `INJECT_*` |
| 注入说明文字区 | `INJECT_TEXT_Y=56` |

## 必须避开的四个坑

1. **背景只能走 `IRecipeCategory#getBackground()`**。它从 19.19.0 起就标了 `forRemoval`，
   但一直到 **19.57.0.451**，`RecipeLayout#drawRecipe` 仍在调用它（同时我们的 `getWidth()` /
   `getHeight()` 也必须是显式覆写，因为背景若为 null 会抛异常）。
   绘制顺序是 背景 → 槽位 → widgets（文字） → extras 的 `addDrawable`，
   把背景放进 extras 会整块盖住内容。哪天真被删了，官方推荐改在 `draw()` 里自己画。
2. **贴图路径必须带 `.png`**。MC 的纹理管理器按完整路径找文件，不补扩展名；
   少了它界面就渲染成缺失贴图的黑紫棋盘格。`JeiLayout#background(name)` 已经统一处理。
3. **独立小图必须 `drawableBuilder(loc,0,0,w,h).setTextureSize(w,h).build()`**。
   `createDrawable` 默认按 256×256 归一化取 UV，我们的背景是独立小图，
   那样只能取到左上角一块再拉伸。第 2、3 条是两个独立的坑，表现各不相同。
4. **`addText(lines, maxWidth, maxHeight)` 会先按宽度折行**，行数翻倍后超出 `maxHeight`
   就整段截断成省略号。排版按「英文最长行」估算：CJK 8px、空格 4px、其余 ASCII 5px、行高 9+2。

另外：取配方要用原版 `RecipeType`（`net.minecraft.world.item.crafting`），别引 JEI 的同名类；
没有方块物品的方块 `asItem()` 返回 AIR，要退回 `fluidState.getType().getBucket()`。
