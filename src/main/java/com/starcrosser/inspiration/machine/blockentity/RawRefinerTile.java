package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.machine.plugin.PluginEnergyStorage;
import com.starcrosser.inspiration.machine.recipe.RefiningRecipe;
import com.starcrosser.inspiration.machine.recipe.RefiningRecipeInput;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import com.starcrosser.inspiration.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 原料精炼机：一份原料 → 一份主产物 + 最多两份副产物。
 * <p>
 * 三个输出槽是**同一个物品栏组件的三个格子**，摆成一列（竖排 3 格放在 x=116）：
 * 第 0 格是主产物，第 1、2 格是副产物。横向摆会撞到右侧 x=150 的灵感充能罐。
 * <p>
 * 副产物产量受回收升级影响 —— 只有这一台机器读 {@code PluginEffects#byproductCount}，
 * 别的机器装上回收升级不会有任何效果（插件槽是通用的，效果得各机器自己取用）。
 * <p>
 * 结算的顺序很关键：**先确认三个输出格都塞得下，才开始扣材料**。
 * 否则副产物槽满了就会吞掉原料而不产出，玩家会莫名其妙少东西。
 */
public class RawRefinerTile extends BatchProcessingTile<RawRefinerTile> {

    /** tier 1 的能量缓冲容量（FE）。 */
    public static final int BASE_ENERGY_CAPACITY = 300_000;

    /** tier 1 的每刻最大输入功率（FE）。 */
    public static final int BASE_MAX_ENERGY_INPUT = 6_000;

    /** tier 1 每刻消耗的灵感充能（mB）。 */
    public static final int BASE_CHARGE_PER_TICK = 2;

    /** 输入槽在 GUI 中的位置。 */
    private static final int INPUT_X = 44;
    private static final int INPUT_Y = 30;

    /** 三个输出槽：一列三行（受 x=150 的充能罐挤压，只能竖着摆）。 */
    private static final int OUTPUT_X = 116;
    private static final int OUTPUT_Y = 20;
    private static final int OUTPUT_SLOTS = 3;

    /** 进度条：夹在输入与输出中间。 */
    private static final int PROGRESS_X = 78;
    private static final int PROGRESS_Y = 38;

    /** 侧向配置按钮排在同一行，输入槽占 8、输出槽占 26。 */
    private static final int FACING_HANDLER_Y = 84;
    private static final int INPUT_FACING_X = 8;
    private static final int OUTPUT_FACING_X = 26;

    @Save
    private SidedInventoryComponent<RawRefinerTile> inputInventory;
    @Save
    private SidedInventoryComponent<RawRefinerTile> outputInventory;
    @Save
    private ProgressBarComponent<RawRefinerTile> progressBar;

    /** 缓存最近一次命中的配方，避免每刻重复查表。 */
    private RefiningRecipe cachedRecipe;
    private ItemStack cachedInput = ItemStack.EMPTY;

    public RawRefinerTile(MachineSpec spec, BasicTileBlock<RawRefinerTile> block,
                          BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(spec, block, type, pos, state);

        SidedInventoryComponent<RawRefinerTile> inputs =
                new SidedInventoryComponent<>("input", INPUT_X, INPUT_Y, 1, 0);
        inputs.setRange(1, 1);
        inputs.setColor(DyeColor.ORANGE);
        inputs.setFacingHandlerPos(INPUT_FACING_X, FACING_HANDLER_Y);
        this.inputInventory = inputs;
        addInventory(this.inputInventory);

        SidedInventoryComponent<RawRefinerTile> outputs =
                new SidedInventoryComponent<>("output", OUTPUT_X, OUTPUT_Y, OUTPUT_SLOTS, 1);
        outputs.setRange(1, OUTPUT_SLOTS);
        outputs.setColor(DyeColor.LIME);
        // 输出槽不接受玩家塞东西进去，只能由机器写入或由管道 / 玩家取出
        outputs.setInputFilter((stack, slot) -> false);
        outputs.setFacingHandlerPos(OUTPUT_FACING_X, FACING_HANDLER_Y);
        this.outputInventory = outputs;
        addInventory(this.outputInventory);

        ProgressBarComponent<RawRefinerTile> bar =
                new ProgressBarComponent<>(PROGRESS_X, PROGRESS_Y, RefiningRecipe.DEFAULT_DURATION);
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        bar.setColor(spec.barColor());
        bar.setCanIncrease(this::canProcess);
        bar.setCanReset(tile -> tile.isFinished() || tile.findRecipe() == null);
        bar.setOnTickWork(this::consumeBatchEnergy);
        bar.setOnFinishWork(this::finishCraft);
        this.progressBar = bar;
        addProgressBar(this.progressBar);
    }

    @Nonnull
    @Override
    protected PluginEnergyStorage<RawRefinerTile> createEnergyStorage() {
        return new PluginEnergyStorage<>(BASE_ENERGY_CAPACITY, BASE_MAX_ENERGY_INPUT, 0, 8, 14);
    }

    @Override
    public int chargePerTick() {
        return BASE_CHARGE_PER_TICK;
    }

    @Override
    protected int baseEnergyCapacity() {
        return BASE_ENERGY_CAPACITY;
    }

    @Override
    protected int maxEnergyInput() {
        return BASE_MAX_ENERGY_INPUT;
    }

    @Override
    public boolean isActive() {
        return progressBar != null && progressBar.getProgress() > 0;
    }

    @Nonnull
    @Override
    public RawRefinerTile getSelf() {
        return this;
    }

    @Override
    public ProgressBarComponent<RawRefinerTile> getProgressBar() {
        return progressBar;
    }

    /* ============================ 配方逻辑 ============================ */

    private boolean canProcess(RawRefinerTile tile) {
        if (level == null || level.isClientSide) {
            return false;
        }
        RefiningRecipe recipe = findRecipe();
        if (recipe == null) {
            return false;
        }
        return prepareBatch(recipe.duration(), recipe.energyPerTick(),
                n -> inputInventory.getStackInSlot(0).getCount() >= (long) recipe.inputCount() * n
                        && canAcceptOutputs(recipe, n));
    }

    /** 三个输出格必须**同时**塞得下：副产物槽满了的话这次作业就不该开始。 */
    private boolean canAcceptOutputs(RefiningRecipe recipe, int n) {
        return fitsOutput(outputInventory, 0, recipe.result(), n)
                && fitsOutput(outputInventory, 1, outputStack(recipe.byproduct(0)), n)
                && fitsOutput(outputInventory, 2, outputStack(recipe.byproduct(1)), n);
    }

    /**
     * 把第 {@code slot} 格真正写进去。
     *
     * @return 是否完整放下；放不下时已经写入的部分要由调用方回滚
     */
    private boolean insert(int slot, ItemStack stack) {
        return stack.isEmpty() || outputInventory.insertItem(slot, stack.copy(), false).isEmpty();
    }

    /**
     * 副产物的实际产量：基准数量 × 回收升级的加算倍率。
     * 上限压到物品自身的最大堆叠，免得装一堆升级后算出来的数塞不进单格。
     */
    private ItemStack outputStack(ItemStack byproduct) {
        if (byproduct.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = byproduct.copy();
        int count = Math.min(pluginEffects().byproductCount(byproduct.getCount()), stack.getMaxStackSize());
        stack.setCount(count);
        return stack;
    }

    private void finishCraft() {
        if (level == null) {
            return;
        }
        RefiningRecipe recipe = findRecipe();
        if (recipe == null || !canAcceptOutputs(recipe, batchSize())) {
            return;
        }
        // 三格要么全成、要么全不动：主产物放进去了副产物放不下的话，原料照样不扣，
        // 也不该白送一份主产物，所以失败时按快照回滚
        List<ItemStack> before = List.of(
                outputInventory.getStackInSlot(0).copy(),
                outputInventory.getStackInSlot(1).copy(),
                outputInventory.getStackInSlot(2).copy());
        if (!insert(0, batchOutput(recipe.result()))
                || !insert(1, batchOutput(outputStack(recipe.byproduct(0))))
                || !insert(2, batchOutput(outputStack(recipe.byproduct(1))))) {
            for (int slot = 0; slot < before.size(); slot++) {
                outputInventory.setStackInSlot(slot, before.get(slot));
            }
            return;
        }
        inputInventory.extractItem(0, recipe.inputCount() * batchSize(), false);
        invalidateCache();
        markForUpdate();
    }

    private boolean isFinished() {
        return progressBar.getProgress() >= progressBar.getMaxProgress();
    }

    /** 取当前输入对应的配方，输入未变化时直接复用缓存。 */
    private RefiningRecipe findRecipe() {
        if (level == null || level.isClientSide) {
            return null;
        }
        ItemStack input = inputInventory.getStackInSlot(0);
        if (input.isEmpty()) {
            invalidateCache();
            return null;
        }
        if (cachedRecipe != null && ItemStack.matches(input, cachedInput)) {
            return cachedRecipe;
        }
        cachedInput = input.copy();
        cachedRecipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.REFINING_TYPE.get(), new RefiningRecipeInput(input), level)
                .map(this::trackRecipe)
                .orElse(null);
        return cachedRecipe;
    }

    private void invalidateCache() {
        cachedRecipe = null;
        cachedInput = ItemStack.EMPTY;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        addFacingLabels(inputInventory, outputInventory);
    }
}
