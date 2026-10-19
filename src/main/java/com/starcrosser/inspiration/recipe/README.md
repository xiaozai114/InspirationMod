# 配方体系（`recipe` 包）

这里放独立于机器的配方：全局配方注册 `ModRecipes`，以及右键转化（在原版方块上右键的转换类配方）。
机器专属的配方类型分别在 [`../machine/recipe`](../machine/recipe) 与 [`../injection/recipe`](../injection/recipe)，
但都遵循下面这套结构。

相关：[机器体系](../machine/README.md) ・ [注入合成](../injection/README.md) ・ [数据生成](../datagen/README.md)

## 一个配方类型 = 三件套

| 类 | 作用 |
| --- | --- |
| `XxxRecipe` | 配方本体，含 `MapCodec` |
| `XxxRecipeInput` | 实现 `RecipeInput` 的 record，作为合成容器的输入快照 |
| `XxxRecipeSerializer` | 流式与 JSON 双向序列化 |

三者都要在 `recipe/ModRecipes.java` 里注册 **SERIALIZER 与 TYPE** 两项。

### 序列化器的字段数上限

**超过 6 个字段的序列化器必须手写 `StreamCodec.of(写, 读)`** —— `composite(...)` 的重载只到 6 个字段。
手写时编码顺序必须与构造参数顺序一一对应，而且**加字段时两个方法都要记得改**（漏一个会在联机同步时才炸）。
目前有两处：注入配方 8 个（`base` / `inject` / `tier` / `min_energy` / `max_energy` / `result` /
`duration` / `seeded_energy`，见 `InjectionRecipeSerializer`），
物质融合配方 9 个（多了三处材料消耗数量，见 `FusionRecipeSerializer`）。

### 写出的 JSON 落在哪

- 用 `RecipeOutput#accept(id, recipe, null)`；id 里带 `/` 会自动落到子目录。
- `.save(output, "名字")` 会落到 **minecraft 命名空间**。要落在本模组命名空间，
  就用物品注册名或 `ResourceLocation.fromNamespaceAndPath(MODID, ...)`。

## 配方加载与出错的判断

配方是否真的进了游戏，看服务端日志的 `Loaded N recipes`；把某个 JSON 改坏会让数量下降并报错，
据此可以反证加载路径有效。

## 新增内容时

- 新增一台机器 / 一种机器配方：完整清单见项目技能 `.workbuddy/skills/inspiration-add-machine/`。
- 新增方块要补的数据侧登记点：见 [方块文档](../block/README.md)。
