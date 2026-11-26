# 配方生成（`datagen/recipe` 包）

> `ModRecipesProvider` 一度有 1300 行，加一条配方要在长文件里找位置。
> P2.2 之后它只做派活，配方按**机制**落到下面这些文件里。

## 文件与分工

| 文件 | 管什么 |
| --- | --- |
| `CookingRecipes` | 熔炉 / 高炉 / 烟熏 / 营火 |
| `CraftingRecipes` | 工作台的有序与无序合成 |
| `SmithingRecipes` | 锻造转换 + 翡翠钢 / 注能翡翠钢升级 |
| `RightClickRecipes` | 手持物品右键方块转换（含铯盐块→铯块、钫盐块→钫块） |
| `MachineBlockRecipes` | 机器、多方块结构方块与注入结构方块本身（含 `machine()` / `hatch()` 模板） |
| `FusionRecipes` | 物质融合机 |
| `ReshapingRecipes` | 物质重塑机的材料转换、模板和核心配方 |
| `TwistingRecipes` | 矿物扭曲机 |
| `AssemblingRecipes` | 灵感装配台 |
| `InjectionRecipes` | 注入（含能量窗口写死的与种子随机的两种） |
| `FranciumRecipes` | 钫盐系列（烧炼 / 合成 / 分解） |
| `RecipeUnlocks` | `getHasName` / `has` 两个解锁条件小工具 |

加一条新配方：找到对应文件往里写。要加一整类机制：新建文件，
然后在 `ModRecipesProvider#buildRecipes` 里补一行。

## ⚠️ `RecipeUnlocks` 为什么存在

`RecipeProvider#has` 与 `#getHasName` 是 **protected static** —— 只有 `RecipeProvider`
的子类能直接调。配方类拆出来以后不再是它的子类，所以在 `RecipeUnlocks` 里按原版实现
**原样复刻**了这两个方法（触发器用 `CriteriaTriggers.INVENTORY_CHANGED.createCriterion`
构造，与 `RecipeProvider#inventoryTrigger` 逐字一致）。

各配方类用 `import static ...RecipeUnlocks.getHasName / has` 引入，
所以调用处写法与拆分前完全一样，改这个方法要同步核对齐生成资源零变化。

## ⚠️ `bus = Bus.MOD` 与配方无关但同源的一个坑

（见 [event 包](../../event/README.md)）—— 那边的 `RegisterDataMapTypesEvent` 是
`IModBusEvent`，新版 FML 按事件类型自己分派，不要写 `bus = Bus.MOD`。
