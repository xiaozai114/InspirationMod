# 机器与配方代码骨架（Inspiration / NeoForge 1.21.1）

以下骨架从现有三台机器抽取，改动时保持同一形状即可。

## 配方类

```java
public class XxxRecipe implements Recipe<XxxRecipeInput> {

    public static final int DEFAULT_DURATION = 400;
    public static final int DEFAULT_ENERGY_PER_TICK = 300;

    public static final MapCodec<XxxRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(XxxRecipe::input),
            ItemStack.CODEC.fieldOf("result").forGetter(XxxRecipe::result),
            Codec.INT.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(XxxRecipe::duration),
            Codec.INT.optionalFieldOf("energy_per_tick", DEFAULT_ENERGY_PER_TICK).forGetter(XxxRecipe::energyPerTick)
    ).apply(instance, XxxRecipe::new));
    // ... 构造、matches（判空 + Ingredient.test）、assemble、getSerializer、getType
}
```

要点：

- `Ingredient.CODEC_NONEMPTY` 用于必填材料，`optionalFieldOf` 用于带默认值的数值字段。
- 可选材料用 `Ingredient.CODEC_NONEMPTY.optionalFieldOf("second_input")` 存成 `Optional<Ingredient>`，
  在流编码里用 `ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC)` 表示，
  避免 `Ingredient` 没有空值编码的问题。
- 数据生成写出的 JSON 会省略等于默认值的字段，读回时由 codec 自动补默认值，这是正常现象。
- 数量为 1 的 `result` 在 JSON 里只写 `id`，数量不为 1 时才会出现 `count`。

## 序列化器

```java
public class XxxRecipeSerializer implements RecipeSerializer<XxxRecipe> {
    @Override public MapCodec<XxxRecipe> codec() { return XxxRecipe.CODEC; }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, XxxRecipe> streamCodec() {
        return StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, XxxRecipe::input,
                ItemStack.STREAM_CODEC, XxxRecipe::result,
                ByteBufCodecs.VAR_INT, XxxRecipe::duration,
                ByteBufCodecs.VAR_INT, XxxRecipe::energyPerTick,
                XxxRecipe::new);
    }
}
```

`StreamCodec.composite` 的字段顺序必须与构造函数形参顺序**完全一致**，否则数据包解析错位。

## 方块与方块实体

```java
public class XxxBlock extends RotatableBlock<XxxTile> {
    public XxxBlock() {
        super("xxx", BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .strength(5.0F, 10.0F).requiresCorrectToolForDrops(), XxxTile.class);
    }
    @Override public RotationType getRotationType() { return RotationType.FOUR_WAY; }
    @Override public BlockEntityType.BlockEntitySupplier<XxxTile> getTileEntityFactory() { return XxxTile::new; }
}
```

方块实体组装顺序：`inputInventory` → `outputInventory` → `progressBar` → 能量（`createEnergyStorage`）。

```java
public XxxTile(BlockPos pos, BlockState state) {
    super((BasicTileBlock<XxxTile>) ModMachines.XXX.getBlock(), ModMachines.XXX.type().get(), pos, state);

    SidedInventoryComponent<XxxTile> inputs = new SidedInventoryComponent<>("input", 44, 30, 1, 0);
    inputs.setRange(1, 1);                  // 1 列 1 行
    inputs.setColor(DyeColor.GREEN);
    inputs.setFacingHandlerPos(8, 84);
    addInventory(this.inputInventory = inputs);

    SidedInventoryComponent<XxxTile> outputs = new SidedInventoryComponent<>("output", 116, 39, 1, 1);
    outputs.setRange(1, 1);
    outputs.setColor(DyeColor.LIME);
    outputs.setInputFilter((stack, slot) -> false);   // 输出槽禁止插入
    outputs.setFacingHandlerPos(26, 84);
    addInventory(this.outputInventory = outputs);

    ProgressBarComponent<XxxTile> bar = new ProgressBarComponent<>(78, 38, XxxRecipe.DEFAULT_DURATION);
    bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
    bar.setColor(DyeColor.GREEN);
    bar.setCanIncrease(this::canProcess);
    bar.setCanReset(tile -> tile.isFinished() || tile.findRecipe() == null);
    bar.setOnTickWork(this::consumeEnergy);
    bar.setOnFinishWork(this::finishCraft);
    addProgressBar(this.progressBar = bar);
}
```

进度条回调职责：

- `canProcess`：解析配方、把 `progressBar.setMaxProgress(recipe.duration())` 同步成当前配方的耗时、
  校验能量与材料数量、校验输出槽能装下产物。
- `consumeEnergy`：`storage.setEnergyStored(stored - recipe.energyPerTick())`。
- `finishCraft`：先 `insertItem(0, result, true)` 空跑校验，再真正插入并扣除输入；末尾
  `invalidateCache(); markForUpdate();`。

能量存储只进不出：`new EnergyStorageComponent<>(CAPACITY, MAX_INSERT, 0, 8, 14)`。

### 灵感充能 + 插件（基类已封装，子类只做四件事）

```java
public class XxxTile extends InspiredMachineTile<XxxTile> {
    public static final int CHARGE_PER_TICK = 2;
    public static final int ENERGY_CAPACITY = 300_000;

    @Override public int chargePerTick() { return CHARGE_PER_TICK; }
    @Override protected int baseEnergyCapacity() { return ENERGY_CAPACITY; }
    @Override public ProgressBarComponent<XxxTile> getProgressBar() { return progressBar; } // 已有方法加 @Override

    // createEnergyStorage 返回 new PluginEnergyStorage<>(...)（不要用 EnergyStorageComponent，
    // 否则电容插件无法改容量）
    // canProcess 末尾追加：&& hasCharge(effectiveChargePerTick())
    // 能耗判断用 effectiveEnergyPerTick(基础值)，每刻结算里 consumeCharge(effectiveChargePerTick())
}
```

换算规则定义在 `machine/charge/InspirationCharge.java`：1 个「灵感」= 1000 mB，罐体 16000 mB。
基类 `InspiredMachineTile` 在构造时创建充能槽 (150,74) 与充能罐 (150,14)（容量第 2 个参数），
并把 augment 槽的过滤器换成插件：

```java
SidedFluidTankComponent<T> tank = new SidedFluidTankComponent<>(
        "inspiration_charge", InspirationCharge.TANK_CAPACITY, 150, 14, 3);
tank.setTankAction(FluidTankComponent.Action.FILL);   // 只进不出
tank.setValidator(InspirationCharge::isChargeFluid);

getAugmentInventory().setInputFilter((stack, i) -> stack.getItem() instanceof MachinePluginItem);
```

插件倍率集中在 `machine/plugin/PluginEffects`（record）：`speed` 一个变量推出
`energyUse = speed²`、`chargeUse = speed`；容量插件只通过
`PluginEnergyStorage#setCapacity` 与 `setChargeCapacity` 翻容量，缩容时截断超出部分。
`InspiredMachineTile` 每刻比对插件槽的内容指纹，变了才重算并套用。

## 带流体的装配配方

```java
public record AssemblingRecipeInput(List<ItemStack> items, FluidStack fluid) implements RecipeInput {
    public static final int SLOT_COUNT = 4;
}
```

- 配方字段：`List<Ingredient>`（1~4）+ `Optional<FluidIngredient>` + `fluid_amount`
  + `result` + `duration`(300) + `energy_per_tick`(200)。
- 原料顺序无关靠回溯指派：

```java
int[] slots = recipe.resolveSlots(input.items());   // 返回「第 n 条原料 → 第几个槽」，或 null
```

- 流体判定：`fluid.get().test(stack) && stack.getAmount() >= fluidAmount`；
  扣减用 `fluidInput.drainForced(recipe.fluidAmount(), IFluidHandler.FluidAction.EXECUTE)`。
- 机器侧的流体输入槽必须与灵感充能罐分开，校验用
  `stack -> !InspirationCharge.isChargeFluid(stack)`（只收非充能流体）。

## 随机产量（等效时运）

`util/OreFortuneBonus` 复刻原版 `minecraft:ore_drops` 公式：

```java
int bonus = random.nextInt(fortuneLevel + 2) - 1;   // -1 .. fortuneLevel
return Math.max(bonus, 0) + 1;                       // 倍率 1 .. fortuneLevel + 1
```

期望值 `1 + L(L+1) / (2(L+2))`：L=3 → 2.2，L=6 → 3.625。
产量在 `finishCraft` 里用 `level.random` 掷出，因此 JEI 只能展示区间与期望，不能展示确定数量。

## JEI 分类（19.27）

```java
public class XxxRecipeCategory implements IRecipeCategory<XxxRecipe> {
    public XxxRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(JeiLayout.background("xxx"), 0, 0,
                JeiLayout.WIDTH, JeiLayout.HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModMachines.XXX.getBlock()));
    }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, XxxRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(JeiLayout.FIRST_INPUT_X, JeiLayout.SLOT_Y).setStandardSlotBackground()
                .addIngredients(recipe.input());
        builder.addOutputSlot(JeiLayout.OUTPUT_X, JeiLayout.SLOT_Y).setOutputSlotBackground()
                .addItemStack(recipe.result());
    }

    @Override public void createRecipeExtras(IRecipeExtrasBuilder builder, XxxRecipe recipe, IFocusGroup focuses) {
        builder.addDrawable(background, 0, 0);
        builder.addText(List.of(Component.translatable("jei.inspiration.xxx.duration", recipe.duration())),
                        JeiLayout.WIDTH - 12, JeiLayout.HEIGHT - JeiLayout.TEXT_Y - 4)
                .setPosition(6, JeiLayout.TEXT_Y)
                .setTextAlignment(HorizontalAlignment.CENTER);
    }
}
```

- 底部文字区从 `JeiLayout.TEXT_Y`（=30）开始，最多两行（104×54 的面板）。
- `Component.translatable` 的 `%s` 参数若是 `double`，先用 `String.format(Locale.ROOT, "%.1f", v)`
  格式化成字符串再传入，避免小数点随语言环境变化。
- 展示数量大于 1 的输入槽：把 `ingredient.getItems()` 逐个 `copyWithCount(n)` 后用
  `slot.addItemStacks(...)` 塞入。

## 数据生成挂钩清单

| 文件 | 需要加的内容 |
| --- | --- |
| `ModRecipesProvider` | 机器本体 `ShapedRecipeBuilder`；新配方按表驱动批量 `recipeOutput.accept(id, new XxxRecipe(...), null)` |
| `ModBlockStatesProvider` | `machineBlock(ModMachines.XXX);` 一行 |
| `ModBlockLootTablesProvider` | `dropSelf(...)` + `getKnownBlocks()` 各一行 |
| `ModBlockTagsProvider` | `MINEABLE_WITH_PICKAXE`、`NEEDS_IRON_TOOL` 等 |
| `ModEnUsLangProvider` / `ModZhCnLangProvider` | 机器名与 `jei.inspiration.*` 文案 |
| `ModCreativeModeTabs` | `output.accept(ModMachines.XXX);` |
