---
name: inspiration-add-tool
description: This skill should be used when adding or modifying tools, weapons, their tiers (Tier), experience-on-action behaviours, or smithing-table upgrade chains in the Inspiration mod (NeoForge 1.21.1, modid inspiration) at the workspace root. Trigger it for requests like 新增工具、新增武器、加工具品质、把某系列工具升级、锻造升级模板、调整攻击力/攻速/挖掘等级/经验产出, or any change touching the item/tool/ package.
agent_created: true
---

# 在 Inspiration 模组中新增工具 / 品质 / 升级链

本模组沿两条轴组织工具：**品质**（`Tier`，决定耐久、挖掘速度、攻击加成、附魔价值、修复材料）
与**工具类**（决定特殊行为：掉经验、剥皮、锄地）。新增工具优先复用现有工具类 + 新品质参数，
不要复制粘贴出一个新类。

相关目录：

| 内容 | 位置 |
| --- | --- |
| 品质定义 | `item/tool/ModToolTiers.java` |
| 经验产出接口 | `item/tool/{ExperienceTool,MiningExperienceTool}.java` |
| 经验球生成 | `item/tool/ExperienceDrops.java` |
| 挖掘事件 | `item/tool/ExpMiningHandler.java` |
| 工具类 | `item/custom/{ExperienceSword,StickyAxe,ExperiencePickaxe,ExperienceHoe}.java` |
| 注册 | `item/ModItems.java`、`item/ModCreativeModeTabs.java` |
| 升级模板物品 | `item/custom/JadeSteelUpgradeTemplate.java` |
| 升级配方 | `datagen/ModRecipesProvider.java#buildJadeSteelUpgrades` |
| 贴图脚本 | `tools/textures/gen_jade_steel_tools.py`、`gen_jade_steel_upgrade_template.py` |

## 1. 新增一个品质

在 `ModToolTiers` 里加一个常量即可，品质实现是数据驱动的 `SimpleTier` 记录：

```java
public static final Tier MY_TIER = new SimpleTier(
        BlockTags.INCORRECT_FOR_NETHERITE_TOOL,  // 挖掘等级：反着描述，见下方注意
        2500,                                    // 耐久
        10.0F,                                   // 挖掘速度
        3.5F,                                    // 近战攻击加成
        16,                                      // 附魔价值
        () -> Ingredient.of(ModItems.MY_INGOT.get()));  // 修复材料，必须是 Supplier
```

注意：

- 修复材料用 `Supplier` 延迟取物品，避免注册期提前解析 `DeferredItem`。
- **「挖掘等级」是反着描述的**：`incorrect_for_<tier>_tool` 列出的是该档**挖不动**的方块。
  1.21.1 里 `incorrect_for_diamond_tool` 与 `incorrect_for_netherite_tool` 都是空标签
  （从 client-extra 资源包 `data/minecraft/tags/block/` 可读到 `{"values": []}`），
  所以钻石档与下界合金档在掉落判定上等价；铁档的标签则包含钻石级方块。
- **`Item.Properties#durability` 对 `TieredItem` 无效**：`TieredItem` 构造里会
  `properties.durability(tier.getUses())` 覆盖调用方写的值。改耐久只能改品质的 `uses`。
  项目里 `exp_sword` 写 899 实际是 250、`world_breaker` 写 999999 实际是 2031，都是这个原因。

## 2. 新增一件工具

1. `ModItems` 注册，构造参数依次是：品质、攻击力加成、攻击/使用速度、（可选）每次动作的经验量：

   ```java
   public static final DeferredItem<Item> MY_SWORD =
           ITEMS.register("my_sword", () -> new ExperienceSword(ModToolTiers.MY_TIER,
                   3, -2.0F, 12, new Item.Properties().rarity(Rarity.RARE)));
   ```

   面板值 = 参数 + 品质加成 + 玩家基础（伤害 +1、攻速 +4 才是每秒次数）：

   | 工具 | 属性来源 |
   | --- | --- |
   | 剑 | `SwordItem.createAttributes(tier, dmg, spd)` |
   | 斧 / 镐 / 锄 | `DiggerItem.createAttributes(tier, dmg, spd)` |

2. 数据生成挂钩：`ModItemModelsProvider`（剑 / 镐 / 锄用 `basicItem`，斧用 `handheldItem`）、
   `ModCreativeModeTabs`、中英文语言文件。
3. 贴图：放进 `src/main/resources/assets/inspiration/textures/item/<name>.png`。

## 3. 让工具产出经验

- 动作型（命中、剥皮、锄地）实现 `ExperienceTool#experiencePerAction()`，
  在动作发生处调用 `ExperienceDrops.spawnExperience(level, x, y, z, 数量)`。
- 挖掘型实现 `MiningExperienceTool`（它继承 `ExperienceTool`），
  `ExpMiningHandler` 会在 `BlockEvent.BreakEvent` 里按接口取值，
  新工具只要实现接口就自动接入，**不要**在处理器里写具体类型的 `instanceof`。
- 经验量作为构造参数存字段，物品描述里用 `%s` 占位符展示
  （`tooltip.inspiration.<工具>.description`，需要的参数直接传给 `Component.translatable`）。

## 4. 新增一条锻造升级链

以「翡翠钢升级」为模板，四步：

1. **模板物品**：写一个工厂类产出 `SmithingTemplateItem`（见 `JadeSteelUpgradeTemplate`），
   提示框的「应用于 / 材料」标题沿用原版键
   `item.minecraft.smithing_template.applies_to` / `.ingredients`，
   槽位空图标用原版 `item/empty_slot_*`。形制与下界合金模板一致，只换配色。
2. **配方**：在 `ModRecipesProvider#buildJadeSteelUpgrades` 里按模板加一行：

   ```java
   smithingUpgrade(recipeOutput, "my_sword_upgrade", ModItems.BASE_SWORD.get(),
           ModItems.MY_SWORD.get(), RecipeCategory.COMBAT);
   ```

   统一走 `smithingUpgrade(...)`，它内部用
   `ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, path)` 写 id——
   **不要**用 `.save(recipeOutput, "名字")`，那会落到 minecraft 命名空间。
3. **贴图**：把基础版贴图换色成升级版（见第 5 节）。
4. **语言文件**：物品名 + 模板的四个键
   （`upgrade.inspiration.<链名>`、`item.inspiration.<链名>.{applies_to,ingredients,
   base_slot_description,additions_slot_description}`）。

## 5. 贴图：成套工具的换色

`tools/textures/gen_jade_steel_tools.py` 是参考实现。要点：

- **不要按亮度重映射**（会把金属压成灰绿）。按像素分类后只换色相：
  保留明度 V 与透明度 A，设 `H = 目标色相`、`S = 0.30 + 0.55 * 原饱和度`、`V *= 0.95`。
- 木柄按色相窗口排除（20°~45°），描边与高光按饱和度阈值排除（< 0.18），
  这样轮廓、阴影位置与原贴图完全一致。
- 要「与原版某物品同形、只换配色」时（如升级模板），写显式的
  原版色 → 目标色映射表，并对未覆盖的颜色直接报错；原版材质不进仓库，
  运行时把 client-extra 资源包里的贴图路径当命令行参数传进去。
- 本机只有 conda 的 python 带 Pillow，脚本用 `D:/programfiles/conda/python.exe` 跑。

## 6. 验证

1. `./gradlew compileJava` → `./gradlew runData`：确认编译与数据生成
   （配方落在 `src/generated/resources/data/inspiration/recipe/`，模型与语言文件同步更新）。
2. 数值与配方必须跑一次运行时自检，**不能只看构造参数**。临时类写好后挂在
   `ServerStartedEvent` 上，打印完直接 `server.halt(false)` 让 `runServer` 自己结束，
   确认后立刻删除该文件：

   ```java
   ItemStack stack = new ItemStack(ModItems.MY_SWORD.get());
   ItemAttributeModifiers mods = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ...);
   // ATTACK_DAMAGE 要 +1、ATTACK_SPEED 要 +4 才是面板值；耐久看 stack.getMaxDamage()
   // 挖掘等级看 stack.isCorrectToolForDrops(Blocks.OBSIDIAN.defaultBlockState())
   // （剑没有镐的规则，结果恒为 false，别据此判断）
   Tier tier = ((TieredItem) ModItems.MY_SWORD.get()).getTier();
   ```

   配方侧用 `new SmithingRecipeInput(模板, 底材, 材料)` 配
   `server.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING)` 做
   `matches` / `assemble` 校验，并补一条「底材或材料不对时不应匹配」的反例。
3. `runServer` 前后记得把 `run/server.properties` 的端口改回 25565（25565 上常驻用户自己的服务器），
   并清理 `run/crash-reports`。
4. 最后把新内容写进 `README.md`（工具数值给一张对照表，注明与下界合金的取舍）。
