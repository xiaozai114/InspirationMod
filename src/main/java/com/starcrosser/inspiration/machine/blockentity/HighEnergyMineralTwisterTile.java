package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.machine.charge.InspirationCharge;
import com.starcrosser.inspiration.machine.plugin.PluginEnergyStorage;
import com.starcrosser.inspiration.machine.recipe.TwistingRecipe;
import com.starcrosser.inspiration.machine.recipe.TwistingRecipeInput;
import com.starcrosser.inspiration.recipe.ModRecipes;
import com.starcrosser.inspiration.util.OreFortuneBonus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


/**
 * 高能矿物扭曲机的方块实体。
 * <p>
 * 结构与 {@link MineralTwisterTile} 完全一致（一个输入槽、一个输出槽、随机产量），
 * 差别全在「高能机型」的倍率上，外加本机专属的等效时运：
 * <ul>
 *     <li>能量缓冲、灵感罐容量、每刻耗电、每刻灵感充能消耗、运行速度全部是上一阶段的 2 倍；</li>
 *     <li>等效时运固定抬到 {@value #FORTUNE_LEVEL}（取配方声明值与它的较大者，
 *         这样配方里写更高的时运时仍然生效）。</li>
 * </ul>
 * 倍率只写在本机内：{@link InspiredMachineTile} 的插件结算流程不用改，
 * 它在 {@code applyPlugins} 里读 {@link #baseSpeed()} 与 {@link #baseChargeCapacity()} 即可。
 */
public class HighEnergyMineralTwisterTile extends InspiredMachineTile<HighEnergyMineralTwisterTile> {

    /** 能量缓冲：矿物扭曲机的 2 倍。 */
    public static final int ENERGY_CAPACITY = 800_000;
    public static final int MAX_ENERGY_INPUT = 16_000;

    /** 本机每运行一刻消耗的灵感充能（mB），是矿物扭曲机（2 mB）的 2 倍。 */
    public static final int CHARGE_PER_TICK = 4;

    /** 灵感罐容量相对基准值的倍率。 */
    public static final int CHARGE_CAPACITY_MULTIPLIER = 2;

    /** 运行速度与每刻耗电相对基准值的倍率。 */
    public static final int SPEED_MULTIPLIER = 2;
    public static final int ENERGY_MULTIPLIER = 2;

    /** 本机的等效时运：矿物扭曲机的配方默认是 6，这里抬到 12。 */
    public static final int FORTUNE_LEVEL = 12;

    @Save
    private SidedInventoryComponent<HighEnergyMineralTwisterTile> inputInventory;
    @Save
    private SidedInventoryComponent<HighEnergyMineralTwisterTile> outputInventory;
    @Save
    private ProgressBarComponent<HighEnergyMineralTwisterTile> progressBar;

    /** 缓存最近一次命中的配方，避免每刻重复查表。 */
    private TwistingRecipe cachedRecipe;
    private ItemStack cachedInput = ItemStack.EMPTY;

    public HighEnergyMineralTwisterTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<HighEnergyMineralTwisterTile>) ModMachines.HIGH_ENERGY_MINERAL_TWISTER.getBlock(),
                ModMachines.HIGH_ENERGY_MINERAL_TWISTER.type().get(), pos, state);

        SidedInventoryComponent<HighEnergyMineralTwisterTile> inputs =
                new SidedInventoryComponent<>("input", 44, 30, 1, 0);
        inputs.setRange(1, 1);
        inputs.setColor(DyeColor.GREEN);
        inputs.setFacingHandlerPos(8, 84);
        this.inputInventory = inputs;
        addInventory(this.inputInventory);

        SidedInventoryComponent<HighEnergyMineralTwisterTile> outputs =
                new SidedInventoryComponent<>("output", 116, 39, 1, 1);
        outputs.setRange(1, 1);
        outputs.setColor(DyeColor.LIME);
        outputs.setInputFilter((stack, slot) -> false);
        outputs.setFacingHandlerPos(26, 84);
        this.outputInventory = outputs;
        addInventory(this.outputInventory);

        ProgressBarComponent<HighEnergyMineralTwisterTile> bar =
                new ProgressBarComponent<>(78, 38, TwistingRecipe.DEFAULT_DURATION);
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        bar.setColor(DyeColor.LIME);
        bar.setCanIncrease(this::canProcess);
        bar.setCanReset(tile -> tile.isFinished() || tile.findRecipe() == null);
        bar.setOnTickWork(this::consumeEnergy);
        bar.setOnFinishWork(this::finishCraft);
        this.progressBar = bar;
        addProgressBar(this.progressBar);
    }

    @Nonnull
    @Override
    protected PluginEnergyStorage<HighEnergyMineralTwisterTile> createEnergyStorage() {
        // 只进不出：maxExtract 为 0，能量只能被机器自身消耗
        return new PluginEnergyStorage<>(ENERGY_CAPACITY, MAX_ENERGY_INPUT, 0, 8, 14);
    }

    @Override
    public int chargePerTick() {
        return CHARGE_PER_TICK;
    }

    @Override
    protected int baseEnergyCapacity() {
        return ENERGY_CAPACITY;
    }

    @Override
    protected int baseChargeCapacity() {
        return InspirationCharge.TANK_CAPACITY * CHARGE_CAPACITY_MULTIPLIER;
    }

    @Override
    protected int baseSpeed() {
        return SPEED_MULTIPLIER;
    }

    @Override
    public boolean isActive() {
        return progressBar != null && progressBar.getProgress() > 0;
    }

    @Nonnull
    @Override
    public HighEnergyMineralTwisterTile getSelf() {
        return this;
    }

    @Override
    public ProgressBarComponent<HighEnergyMineralTwisterTile> getProgressBar() {
        return progressBar;
    }

    /* ============================ 配方逻辑 ============================ */

    private boolean canProcess(HighEnergyMineralTwisterTile tile) {
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
        return getEnergyStorage().getEnergyStored() >= energyCost(recipe)
                && hasCharge(effectiveChargePerTick())
                && canAcceptResult(recipe);
    }

    /** 本机每刻的耗电：配方基准值 × 本机倍率，再乘插件加成。 */
    private int energyCost(TwistingRecipe recipe) {
        return effectiveEnergyPerTick(recipe.energyPerTick() * ENERGY_MULTIPLIER);
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

    /** 一次扭曲的产出上限，按本机的等效时运计算后还要受槽位与物品堆叠上限约束。 */
    private int maxResultCount(TwistingRecipe recipe) {
        return Math.min(recipe.result().getCount() * OreFortuneBonus.maxMultiplier(fortuneLevel(recipe)),
                resultLimit(recipe));
    }

    private int resultLimit(TwistingRecipe recipe) {
        return Math.min(outputInventory.getSlotLimit(0), recipe.result().getMaxStackSize());
    }

    /** 本机的等效时运：取配方声明值与 {@link #FORTUNE_LEVEL} 的较大者。 */
    private int fortuneLevel(TwistingRecipe recipe) {
        return Math.max(recipe.fortuneLevel(), FORTUNE_LEVEL);
    }

    private void consumeEnergy() {
        TwistingRecipe recipe = findRecipe();
        if (recipe == null) {
            return;
        }
        PluginEnergyStorage<HighEnergyMineralTwisterTile> storage =
                (PluginEnergyStorage<HighEnergyMineralTwisterTile>) getEnergyStorage();
        storage.setEnergyStored(storage.getEnergyStored() - energyCost(recipe));
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
                recipe.result().getCount() * OreFortuneBonus.rollMultiplier(level.random, fortuneLevel(recipe)),
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
