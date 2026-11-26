# JEI 支持（`client/jei` 包）

相关：[客户端包](../README.md) ・ [机器体系](../../machine/README.md) ・ [注入](../../injection/README.md) ・
[语言键出处](../../datagen/README.md)。插件以 `@JeiPlugin` 声明由 JEI 自动发现；依赖 `compileOnly`，
运行时没有 JEI 也能正常游戏。**JEI 19.70.0.469 / MC 1.21.1**，升级时下面几条 API 约定要重新核对。

## 配方与用途

| 分类 | 催化剂 |
| --- | --- |
| 物质融合 | 物质融合器、强能物质融合机 |
| 物质重塑 | 物质重塑机 |
| 矿物扭曲 | 矿物扭曲机、高能矿物扭曲机 |
| 灵感装配 | 灵感装配台、高能灵感装配台 |
| 流体提取 | 流体提取机 |
| 原料精炼 | 原料精炼机 |
| 注入 | 物质束缚器、注入枪 |
| 右键转化 | —— |
| 多方块搭建 | 两种模拟机的控制器、外壳及示例仓室可查用途 |
| 结构模拟 / 矿物模拟 | 对应控制器、可重复使用的数据卡 |

上位机型与基础机型**共用一个分类**，只多挂一个催化剂；面板显示的是配方**基准值**，不含机型倍率
（高能 2 倍速、扭曲机时运 12 都不体现）。新增分类要动三处：`ModJeiRecipeTypes` 加常量、
新建 `category/XxxRecipeCategory`、在 `InspirationJeiPlugin` 里注册分类 / 灌配方 / 声明催化剂。

## 面板内容

- 搭建页用 LDLib2 的 `Scene` 和 JEI 适配器显示 MBD2 的实际结构坐标。旋转和切层只操作预览世界。
- 模拟生产页直接读取数据卡。矿石采用实际物品栈，可从产物反查；矿物概率按整张表的总权重计算。结构模拟显示每轮选取的战利品表，箱内掉落由服务器结算。

- 单方块机器（融合 / 重塑 / 扭曲 / 装配）**只给两行**：基准耗时与基准每刻耗电
  （`MachineRecipeText.baseLines`，键 `jei.inspiration.machine.duration|energy_per_tick`）。
  插件倍率、总能耗、充能消耗在机器界面里看得到，重复列只会把正文挤没。
- 矿物扭曲产量随机 → 区间与期望值挂在产物槽 tooltip（`addRichTooltipCallback`，
  旧的 `addTooltipCallback` 已过时）。
- 注入走自己的排版：两个输入各配「放哪台设备」的小图标与角色标签（紫 / 蓝），箭头指向产物；
  分隔线下写需求等级、最小（金）与最大（红）能量。种子随机窗口上下限都写 `???`。
- 融合里一次消耗多个的槽位用带数量的 `ItemStack`（`addItemStacks` + `copyWithCount`），
  JEI 会在角上画数字；只消耗 1 个时照旧 `addIngredients(Ingredient)`，保留标签原料的轮播。
- 流体提取的产物是流体：输出槽用 `setFluidRenderer(容量, false, 16, 16)` + `addFluidStack`，
  16×16 放不下数字，产物量单独写一行 `jei.inspiration.fluid_extracting.output`。
- 原料精炼三个输出槽竖排（`REFINING_OUTPUT_X=78`，y = 6 / 24 / 42）：最上一格与输入同高、
  箭头正对它，是主产物；下两格空着时表示无副产物，有副产物时各加一行 tooltip 说明。
  回收升级的翻倍是机器侧的事，面板只补一行提示（写进正文会让人误以为配方本身产那么多）。

## 布局常量（`JeiLayout`）

数值与 `assets/inspiration/textures/gui/jei/*.png` 的实际尺寸一致。

| 用途 | 常量 |
| --- | --- |
| 普通面板 | `WIDTH=104`、`HEIGHT=54`；输入 `8`/`30`、输出 `78`、`SLOT_Y=6`、`TEXT_Y=30` |
| 三材料（融合） | 面板高 `TALL_HEIGHT=74`，第三槽 `(19, 24)`，文字 `TALL_TEXT_Y=46` |
| 灵感装配 | 列 `8/30/52`、行 `6/24`，输出 `(98, 24)`，罐 `(8,44)`/`(30,44)`，`GRID_*` |
| 注入 | `INJECT_WIDTH=150`、`INJECT_HEIGHT=112`、`INJECT_TEXT_Y=56`（100 高会截掉最后一行） |
| 流体提取 | `WIDTH=104`、`TALL_HEIGHT=74`，罐 `(78, 6)`，文字 `TALL_TEXT_Y=46` |
| 原料精炼 | `WIDTH=104`、`REFINING_HEIGHT=86`，产物列 `78`，文字 `REFINING_TEXT_Y=60` |

## 必须避开的五个坑

1. **背景只能走 `IRecipeCategory#getBackground()`**：它从 19.19.0 起标了 `forRemoval`，
   但到 19.70.0.469 `RecipeLayout#drawRecipe` 仍在调它（`getWidth()` / `getHeight()` 也必须显式覆写，
   背景为 null 会抛异常）。绘制顺序是 背景 → 槽位 → 文字 → extras 里的贴图，
   背景放进 extras 会整块盖住内容。真删了就在 `draw()` 里自己画。
2. **贴图路径必须带 `.png`**：纹理管理器按完整路径找文件，少了就渲染成黑紫棋盘格
   （`JeiLayout#background(name)` 已统一处理）。
3. **独立小图必须 `drawableBuilder(loc,0,0,w,h).setTextureSize(w,h).build()`**：
   `createDrawable` 默认按 256×256 归一化取 UV，只能取到左上角一块再拉伸。
4. **往 extras 里放贴图用 `addDrawableWidget(drawable).setPosition(x, y)`**：
   `addDrawable(drawable, x, y)` 在 19.70 已标 `forRemoval`，留着会吃编译警告。
5. **`addText(lines, maxWidth, maxHeight)` 先按宽度折行**，超 `maxHeight` 就整段截断成省略号。
   排版按英文最长行估算：CJK 8px、空格 4px、其余 ASCII 5px、行高 9+2。

另外：取配方用原版 `RecipeType`（`net.minecraft.world.item.crafting`），别引 JEI 的同名类；
没有方块物品的方块 `asItem()` 返回 AIR，要退回 `fluidState.getType().getBucket()`。
