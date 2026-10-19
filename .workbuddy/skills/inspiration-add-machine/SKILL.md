---
name: inspiration-add-machine
description: This skill should be used when adding content to the Inspiration mod (NeoForge 1.21.1, modid inspiration) at the workspace root — machines, machine recipe types, custom blocks (including fluid-reaction blocks and translucent blocks), items, and their JEI / datagen / texture / loot-table integration. Trigger it for requests like 新增一台机器、给机器加配方、新增方块、新增物品、接入 JEI、加流体反应、生成贴图、调整机器能耗耗时, or any change touching the machine/, block/, or fluid/ package.
agent_created: true
---

# 在 Inspiration 模组中新增机器 / 方块 / 配方类型

> 目录名保留 `inspiration-add-machine` 只是为避免改动引用；本技能实际覆盖三类内容：
> **机器**（第 1~6 节）、**普通方块与自定义方块**（第 7 节）、**自定义配方类型**（第 4 节）。

本模组的所有用电机器都建立在 **Titanium 4.0.50**（`com.hrznstudio.titanium`）之上，
不要在 `MachineTile` 之外自造机器基类。GUI、能量与物品能力由库自动生成，
因此一台新机器通常只需「注册 + 方块 + 方块实体」三个文件，外加若干数据生成挂钩。

## 触发场景

- 增加一台新机器（输入输出槽、能量缓冲、进度条、按面 IO）。
- 增加一种新的机器配方类型（`RecipeType` + `RecipeSerializer` + `RecipeInput`）。
- 把新配方类型接入 JEI，或在 JEI 里展示概率性 / 多材料产物。
- 调整机器参数、生成机器方块贴图或 JEI 背景贴图。
- 增加普通方块或带行为的自定义方块（第 7 节）。

**不覆盖**工具 / 武器 / 品质（`Tier`）/ 锻造升级链——那部分见项目技能
`.workbuddy/skills/inspiration-add-tool/`（含成套工具换色脚本与运行时属性自检的写法）。

## 工作流

### 1. 先读现有实现，再动手

按顺序读这三台机器的对应文件，复制其结构而不是重新设计：

| 机器 | 方块 | 方块实体 | 特点 |
| --- | --- | --- | --- |
| 物质融合器 | `machine/block/MatterFusionMachineBlock.java` | `machine/blockentity/MatterFusionMachineTile.java` | 两输入顺序无关、60k FE |
| 强能物质融合机 | `machine/block/EnergizedMatterFusionMachineBlock.java` | `machine/blockentity/EnergizedMatterFusionMachineTile.java` | 上位机型：三输入、两倍速/耗电/耗充能/容量，覆写 `baseSpeed()` 与 `baseChargeCapacity()` |
| 物质重塑机 | `machine/block/MatterReshaperMachineBlock.java` | `machine/blockentity/{MatterReshaperMachineTile,ReshaperTask,ReshaperTaskResolver}.java` | 双输入、任务解析与机器解耦 |
| 矿物扭曲机 | `machine/block/MineralTwisterBlock.java` | `machine/blockentity/MineralTwisterTile.java` | 单输入 + 随机产量 |
| 灵感装配台 | `machine/block/InspirationAssemblerBlock.java` | `machine/blockentity/InspirationAssemblerTile.java` | 2×2 输入 + 独立流体输入槽 |
| 地核钻探者 | `machine/block/CoreDrillerBlock.java` | `machine/blockentity/CoreDrillerTile.java` | 无配方：向下掏竖井，进度条由现场扫描决定上限 |

六台机器都继承 `InspiredMachineTile`，因此都自带插件槽（见下节）。

配方类型集中在 `recipe/ModRecipes.java` 注册，配方类放在 `machine/recipe/`。

### 2. 逐项落地（缺一项都会在运行期暴露）

1. `machine/recipe/`：`XxxRecipe`（`MapCodec` + 可选默认值字段）、`XxxRecipeSerializer`（`MapCodec` +
   `StreamCodec.composite`，**字段顺序必须与构造函数形参顺序一致**）、`XxxRecipeInput`。
2. `recipe/ModRecipes.java`：注册 `XXX_SERIALIZER` 与 `XXX_TYPE`（用 `RecipeType.simple(...)`）。
3. `machine/block/XxxBlock.java`：`RotatableBlock<Tile>` + `RotationType.FOUR_WAY`。
4. `machine/blockentity/XxxTile.java`：用 `addInventory` / `addProgressBar` /
   `createEnergyStorage` 组装；缓存配方，避免每刻查表。
   **基类选 `InspiredMachineTile`**（不是 `MachineTile`）：它自带灵感充能槽与罐体、插件槽与
   插件倍率结算，子类实现 `chargePerTick()`、`baseEnergyCapacity()`、`getProgressBar()`，
   并在 `canProcess` 里用 `effectiveEnergyPerTick(...)` / `hasCharge(effectiveChargePerTick())`、
   在每刻结算里用 `effectiveEnergyPerTick(...)` 与 `consumeCharge(effectiveChargePerTick())`。
   基础值：融合机 1 mB/刻、强能融合机 2 mB/刻，另三台 2 mB/刻。
   想做「同配方但更快更贵」的上位机型，覆写 `baseSpeed()`（进度条每刻推进量）与
   `baseChargeCapacity()`，配方侧的耗电倍率写在机器自己的 `energyCost(...)` 里。
   能量存储用 `PluginEnergyStorage`（普通 `EnergyStorageComponent` 无法被插件改容量）。
5. `machine/ModMachines.java`：`registry.registerBlockWithTile("xxx", XxxBlock::new, null)`。
6. 数据生成挂钩（**容易漏**）：`ModRecipesProvider`（机器本体合成 + 配方）、
   `ModBlockStatesProvider#machineBlock`、`ModBlockLootTablesProvider`（`dropSelf` **与**
   `getKnownBlocks` 各加一行）、`ModBlockTagsProvider`、中英文 `LangProvider`、`ModCreativeModeTabs`。
   机器物品模型由方块状态提供器生成，`ModItemModelsProvider` 不需要改动。
7. JEI：`jei/ModJeiRecipeTypes` 加常量，新增 `jei/category/XxxRecipeCategory`，并在
   `jei/InspirationJeiPlugin` 的 `registerCategories` / `registerRecipes` / `registerRecipeCatalysts`
   三处注册。
8. 贴图：用 `tools/textures/` 下的生成脚本，不要手搓二进制。
9. 验证：`./gradlew compileJava` → `./gradlew runData` → `./gradlew runServer`。

### 3. 机器插件（复用 Titanium 的 augment 槽）

机器界面右侧那四个空槽就是 Titanium 的 augment 槽，本模组把它当作插件槽：

- `InspiredMachineTile` 的构造函数已经用
  `getAugmentInventory().setInputFilter((stack, i) -> stack.getItem() instanceof MachinePluginItem)`
  把过滤条件换成了插件，**新机器不需要再写一遍**，也不需要新增 GUI 槽位。
- 效果结算集中在 `machine/plugin/PluginEffects`：只保留一个 `speed` 变量，
  `energyUse = speed²`、`chargeUse = speed`（速度插件每个 ×2 → 2 倍速 / 每刻耗电 ×4 / 每刻充能 ×2）。
  容量类插件（`PluginEnergyStorage#setCapacity`、`setChargeCapacity`）只翻容量、无额外代价，
  缩容时超出部分会被截断。改数值只改 `MachinePluginType.MULTIPLIER` 与 `PluginEffects` 两处。
- 刷新用「插件内容指纹」：`InspiredMachineTile` 每刻比对槽内物品组成的字符串，
  变了才重算倍率并套用，避免每刻遍历。

### 4. 需要流体 / 乱序原料的配方类型

参考 `assembling`（灵感装配）：

- 记录类 `AssemblingRecipeInput(List<ItemStack>, FluidStack)`，配方字段为 1~4 条
  `Ingredient` + 可选 `FluidIngredient` + `fluid_amount`。
- **原料顺序无关**靠回溯指派（`AssemblingRecipe#resolveSlots` 返回 `int[]` 槽位映射或 null），
  不要按槽位顺序硬匹配。
- 机器侧的流体输入槽与灵感充能罐是两个独立槽：坦克校验用
  `stack -> !InspirationCharge.isChargeFluid(stack)`，消耗一律 `drainForced`。

### 5. 验证方式（本项目没有客户端自动化测试）

- `runData` 会打印新写入的文件数；产物落在 `src/generated/resources/`，用 `cat` 抽查 JSON 是否符合预期。
- `runServer` 日志里的 `Loaded N recipes` 是**配方总数的硬指标**：先算出本次应增加的条数，
  跑完对总数；数量对不上说明有配方没被解析。日志同时会打印 `Couldn't parse data file ...`。
- 测试服务器前把 `run/server.properties` 的 `server-port` 改成 25580（25565 是用户自己在跑的服务器），
  验证完必须改回 25565。
- 抄原版公式（时运、掉落数量等）时，解出
  `build/moddev/artifacts/neoforge-<version>-sources.jar` 里的原版类逐行核对，别凭记忆写。

### 6. 机器贴图

`tools/textures/gen_mineral_twister_block.py` 是机器方块贴图的参考实现：
16×16、1px 描边 + 顶部高光 + 内凹面板，按 `<机器名>_front/_side/_top.png` 命名写进
`src/main/resources/assets/inspiration/textures/block/`。
配色要能一眼区分同色系机器（现有三种绿：物质重塑机黄绿、矿物扭曲机青绿、灵感装配台橄榄绿 + 舷窗）。
JEI 面板背景用 `tools/textures/gen_jei_background.py`；`BACKGROUNDS` 表里每项是
`(底色, 箭头杆 x, 高度, 箭头 y)`，高度默认 54，槽位多的分类（如 2×2 的 `assembling`）可加高到 74。
锭类物品想与已有锭形状完全一致，用 `tools/textures/gen_ingot_textures.py` 按亮度重映射到新调色板。
所有脚本都用相对仓库根的路径，不要写入绝对路径。

### 7. 普通方块与自定义方块

机器之外的方块用 `ModBlocks#registerBlocks(name, supplier)` 注册，它会顺带注册方块物品。
需要手动补的位置（少一项都会在运行期或数据生成时暴露）：

| 位置 | 做什么 |
| --- | --- |
| `ModBlockStatesProvider` | `simpleBlock(block, cubeAll(block))`；半透明见下 |
| `ModBlockLootTablesProvider` | `dropSelf` / `dropOther` / `createSilkTouchDispatchTable` |
| `ModBlockTagsProvider` | `MINEABLE_WITH_PICKAXE`、`NEEDS_IRON_TOOL` 等 |
| `ModItemModelsProvider` | `blockItem(block)`（名字从 `getDescriptionId()` 推出） |
| `ModCreativeModeTabs` | `output.accept(ModBlocks.XXX)` |
| 中英文语言文件 | 方块名；需要提示的话再加 `tooltip.inspiration.*` |

**半透明方块**：模型 JSON 里写 `"render_type": "minecraft:translucent"`，属性加 `.noOcclusion()`。
数据生成写法是 `models().cubeAll(name, tex).renderType("translucent")`。
`ItemBlockRenderTypes#setRenderLayer` 自 1.19 起废弃，不要再走客户端注册那条路。

**仿原版行为的方块**：直接继承原版类最省事（如继承 `SlimeBlock` 得到弹跳 / 免摔落 / 减速）。
注意 codec 的泛型不变型——父类返回 `MapCodec<SlimeBlock>` 时子类只能
`return simpleCodec(子类::new);`，无法收窄返回类型。

**带时运的掉落**（1.21 写法）：

```java
HolderLookup.RegistryLookup<Enchantment> enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT);
add(block, createSilkTouchDispatchTable(block,
        applyExplosionDecay(block, LootItem.lootTableItem(drop)
                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(count)))
                .apply(ApplyBonusCount.addUniformBonusCount(enchantments.getOrThrow(Enchantments.FORTUNE))))));
```

常量叫 `Enchantments.FORTUNE`（不是 `BLOCK_FORTUNE`）。

**流体反应方块**：不要用 NeoForge 的 `FluidInteractionRegistry`。它沿用原版
`LiquidBlock#shouldSpreadLiquid` 的方向表 `POSSIBLE_FLOW_DIRECTIONS`
（内部取 `direction.getOpposite()`，实际只覆盖**正上方 + 四个水平方向**），
漏掉正下方，「流体正好落在水面上方」时不会判定。
做法是写一个 `LiquidBlock` 子类接管 `onPlace` 与 `neighborChanged`，
自己遍历 `Direction.values()`（参考 `block/custom/MindFluidBlock` +
`fluid/MindFluidReactions`）。在 `onPlace` 里 `setBlock` 替换自己是允许的，
原版 `WetSpongeBlock` 就是先例。
规则表里**不要**在静态字段缓存 `DeferredBlock` 或调 `.get()`：该类的加载可能早于注册完成，
会拿到 null；以 `Supplier<? extends Block>` 传参、调用时再解析。

## 关键约束与踩坑

- Titanium 的朝向属性是 `subfacing`（`RotatableBlock.FACING_HORIZONTAL`），
  与 `BlockStateProperties.HORIZONTAL_FACING` 不通用，`BlockStateProvider#horizontalBlock` 不可用，
  必须手动构建 variant，旋转角 `(direction.toYRot() + 180) % 360`。
- `SidedInventoryComponent(name, x, y, slots, id)` 的 `setRange(w, h)` 是**界面里的网格列数 × 行数**，
  不是槽位数量、也不限制堆叠上限；槽位数量由构造函数第四个参数决定。
- 机器方块不在 `ModBlocks.BLOCKS` 里，战利品表必须手动 `dropSelf` 并补进 `getKnownBlocks`。
- 机器界面是 176×184：能量条 (8,14)、输入槽 (44,30)、输出槽 (116,39)、侧向按钮 y=84；
  充能罐占 (150,14) 18×56、充能槽 (150,74)，新增部件按这个网格避让。
- 流体罐构造是 `SidedFluidTankComponent(name, 容量, x, y, 侧栏序号)`（容量在第 2 位），
  与库存组件参数顺序不同；罐体设 `Action.FILL` 后机器自身扣减必须走 `drainForced`；
  罐体字段可直接加 `@Save`（Titanium 内置 `TankNBTHandler`）。
- 配方若一次消耗多个材料，必须在 `canProcess` 里校验槽内数量足够，
  否则会出现「只扣 1 个却产出整份产物」。
- 随机产量的配方，输出槽容量要按**上限**预留，否则结算时物品塞不进去。
- JEI 19.27：`IRecipeCategory#getBackground()` 已废弃，改用 `getWidth()/getHeight()` +
  `createRecipeExtras` 里 `builder.addDrawable(背景, 0, 0)`。
  展示数量大于 1 的输入用 `slot.addItemStacks(...)`（`Ingredient` 本身不带数量）。
- `RecipeManager#getAllRecipesFor` 要的是**原版** `net.minecraft...RecipeType`，
  不是同名的 `mezz.jei.api.recipe.RecipeType`。
- 给基类加抽象方法前先查子类是否已有同名方法：给 `InspiredMachineTile` 加
  `protected abstract ProgressBarComponent<T> getProgressBar()` 时，三台机器已有的
  `public getProgressBar()` 会撞名报“已在类 X 中定义了方法”。正确做法是给已有的那个加 `@Override`。
- Titanium 的能力按**朝向**注册，`tile.getItemHandler(null)` /
  `level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null)` 都可能返回 null；
  自检代码里用 `tile.getHandlerFromName("input" / "output")`，流体能力遍历 `Direction.values()`。
- 自检代码会把测试方块留在存档里，污染下一次验证（旧方块上的插件让基数看起来已被加成）；
  自检最后必须把方块清成空气，并删掉 `run/crash-reports`。
- 删配方 / 删方块后要手工清理 `src/generated/resources/` 里的残留文件。
- 数据生成 `.save(recipeOutput, "名字")` 会把配方写进 **minecraft 命名空间**
  （`ResourceLocation.parse` 的默认命名空间）。要落到本模组就别传 id（用物品注册名），
  或传 `ResourceLocation.fromNamespaceAndPath(MODID, ...)`。项目里约 20 个旧配方有这个遗留问题。
- 静态字段里不要缓存 `DeferredBlock` / 调 `.get()`：类加载可能早于注册完成，会拿到 null。
- 子类化原版方块时注意 codec 泛型不变型（见第 7 节）。

API 细节与代码骨架见 `references/api_reference.md`。
