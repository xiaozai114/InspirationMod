package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.machine.plugin.PluginEnergyStorage;
import com.starcrosser.inspiration.machine.recipe.TwistingRecipe;
import com.starcrosser.inspiration.machine.recipe.TwistingRecipeInput;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import com.starcrosser.inspiration.recipe.ModRecipes;
import com.starcrosser.inspiration.util.OreFortuneBonus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


/**
 * 矿物扭曲机一族的方块实体：矿物扭曲机（等级 1）与高能矿物扭曲机（等级 2）共用这一份实现。
 * <p>
 * 只有一个输入槽：放入矿石，机器按等效时运掷出本次产量，产出对应的粗矿。
 * 随机数取世界随机源，因此同一块矿石的产出数量每次都可能不同。
 * <p>
 * 两档的差别只有等级倍率，外加一项本机专属属性 —— **等效时运下限**
 * （见 {@link #effectiveFortune}）：tier 1 是 6、tier 2 是 12，取它与配方声明值的较大者，
 * 所以配方里写更高的时运时仍然生效。
 */
public class MineralTwisterTile extends InspiredMachineTile<MineralTwisterTile> {

    /** tier 1 的能量缓冲容量（FE）。 */
    public static final int BASE_ENERGY_CAPACITY = 400_000;

    /** tier 1 的每刻最大输入功率（FE）。 */
    public static final int BASE_MAX_ENERGY_INPUT = 8_000;

    /** tier 1 每刻消耗的灵感充能（mB）。 */
    public static final int BASE_CHARGE_PER_TICK = 2;

    /** tier 1 的等效时运下限。等级更高时按 {@code 2^(tier-1)} 放大（6 → 12 → 24）。 */
    public static final int BASE_FORTUNE_LEVEL = 6;

    @Save
    private SidedInventoryComponent<MineralTwisterTile> inputInventory;
    @Save
    private SidedInventoryComponent<MineralTwisterTile> outputInventory;
    @Save
    private ProgressBarComponent<MineralTwisterTile> progressBar;

    /** 缓存最近一次命中的配方，避免每刻重复查表。 */
    private TwistingRecipe cachedRecipe;
    private ItemStack cachedInput = ItemStack.EMPTY;

    public MineralTwisterTile(MachineSpec spec, BasicTileBlock<MineralTwisterTile> block,
                              BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(spec, block, type, pos, state);

        SidedInventoryComponent<MineralTwisterTile> inputs =
                new SidedInventoryComponent<>("input", 44, 30, 1, 0);
        inputs.setRange(1, 1);
        inputs.setColor(DyeColor.GREEN);
        inputs.setFacingHandlerPos(8, 84);
        this.inputInventory = inputs;
        addInventory(this.inputInventory);

        SidedInventoryComponent<MineralTwisterTile> outputs =
                new SidedInventoryComponent<>("output", 116, 39, 1, 1);
        outputs.setRange(1, 1);
        outputs.setColor(DyeColor.LIME);
        outputs.setInputFilter((stack, slot) -> false);
        outputs.setFacingHandlerPos(26, 84);
        this.outputInventory = outputs;
        addInventory(this.outputInventory);

        ProgressBarComponent<MineralTwisterTile> bar =
                new ProgressBarComponent<>(78, 38, TwistingRecipe.DEFAULT_DURATION);
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        bar.setColor(spec.barColor());
        bar.setCanIncrease(this::canProcess);
        bar.setCanReset(tile -> tile.isFinished() || tile.findRecipe() == null);
        bar.setOnTickWork(this::consumeEnergy);
        bar.setOnFinishWork(this::finishCraft);
        this.progressBar = bar;
        addProgressBar(this.progressBar);
    }

    @Nonnull
    @Override
    protected PluginEnergyStorage<MineralTwisterTile> createEnergyStorage() {
        // 只进不出：maxExtract 为 0，能量只能被机器自身消耗。
        // 容量是 tier 1 基准值，等级倍率由基类在构造末尾补正（见 InspiredMachineTile#applyTier）。
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
    public MineralTwisterTile getSelf() {
        return this;
    }

    @Override
    public ProgressBarComponent<MineralTwisterTile> getProgressBar() {
        return progressBar;
    }

    /* ============================ 配方逻辑 ============================ */

    private boolean canProcess(MineralTwisterTile tile) {
        if (level == null || level.isClientSide) {
            return false;
        }
        TwistingRecipe recipe = findRecipe();
        if (recipe == null) {
            return false;
        }
        if (progressBar.getMaxProgress() != recipe.duration()) {
            progressBar.setMaxProgress(recipe.duration());
        }
        return getEnergyStorage().getEnergyStored() >= effectiveEnergyPerTick(recipe.energyPerTick())
                && hasCharge(effectiveChargePerTick())
                && canAcceptResult(recipe);
    }

    /** 产量是随机的，因此按上限预留槽位，避免结算时塞不进去。 */
    private boolean canAcceptResult(TwistingRecipe recipe) {
        ItemStack current = outputInventory.getStackInSlot(0);
        if (current.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(current, recipe.result())) {
            return false;
        }
        return current.getCount() + maxResultCount(recipe) <= resultLimit(recipe);
    }

    /** 一次扭曲的产出上限，按本机等效时运计算后还要受槽位与物品堆叠上限约束。 */
    private int maxResultCount(TwistingRecipe recipe) {
        return Math.min(recipe.result().getCount() * OreFortuneBonus.maxMultiplier(effectiveFortune(recipe)),
                resultLimit(recipe));
    }

    private int resultLimit(TwistingRecipe recipe) {
        return Math.min(outputInventory.getSlotLimit(0), recipe.result().getMaxStackSize());
    }

    /**
     * 本机本次作业实际使用的等效时运：取配方声明值与「等级下限」的较大者，
     * 所以配方里写更高的时运时仍然生效，而高等级机型会把下限整体抬上去。
     */
    private int effectiveFortune(TwistingRecipe recipe) {
        return Math.max(recipe.fortuneLevel(), spec().scale(BASE_FORTUNE_LEVEL));
    }

    private void consumeEnergy() {
        TwistingRecipe recipe = findRecipe();
        if (recipe == null) {
            return;
        }
        PluginEnergyStorage<MineralTwisterTile> storage =
                (PluginEnergyStorage<MineralTwisterTile>) getEnergyStorage();
        storage.setEnergyStored(storage.getEnergyStored() - effectiveEnergyPerTick(recipe.energyPerTick()));
        consumeCharge(effectiveChargePerTick());
    }

    private void finishCraft() {
        if (level == null) {
            return;
        }
        TwistingRecipe recipe = findRecipe();
        if (recipe == null) {
            return;
        }
        ItemStack result = recipe.result().copy();
        result.setCount(Math.min(
                recipe.result().getCount() * OreFortuneBonus.rollMultiplier(level.random, effectiveFortune(recipe)),
                resultLimit(recipe)));
        if (!outputInventory.insertItem(0, result.copy(), true).isEmpty()) {
            return;
        }
        outputInventory.insertItem(0, result.copy(), false);
        inputInventory.extractItem(0, 1, false);
        invalidateCache();
        markForUpdate();
    }

    private boolean isFinished() {
        return progressBar.getProgress() >= progressBar.getMaxProgress();
    }

    /** 取当前输入对应的配方，输入未变化时直接复用缓存。 */
    private TwistingRecipe findRecipe() {
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
                .getRecipeFor(ModRecipes.TWISTING_TYPE.get(), new TwistingRecipeInput(input), level)
                .map(RecipeHolder::value)
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
