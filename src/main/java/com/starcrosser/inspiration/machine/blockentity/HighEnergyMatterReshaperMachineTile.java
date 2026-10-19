package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.machine.charge.InspirationCharge;
import com.starcrosser.inspiration.machine.plugin.PluginEnergyStorage;
import com.starcrosser.inspiration.machine.recipe.ReshapingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


/**
 * 高能物质重塑机的方块实体。
 * <p>
 * 配方与 {@link MatterReshaperMachineTile} 完全共用（两个输入槽、同一套解析与结算），
 * 差别只在「高能机型」的倍率上：
 * <ul>
 *     <li>能量缓冲、灵感罐容量、每刻耗电、每刻灵感充能消耗、运行速度全部是上一阶段的 2 倍；</li>
 *     <li>除此之外没有本机专属效果。</li>
 * </ul>
 * 倍率只写在本机内：{@link InspiredMachineTile} 的插件结算流程会在 {@code applyPlugins} 里读
 * {@link #baseSpeed()} 与 {@link #baseChargeCapacity()}，因此基类一行都不用改。
 */
public class HighEnergyMatterReshaperMachineTile
        extends InspiredMachineTile<HighEnergyMatterReshaperMachineTile> {

    /** 能量缓冲：物质重塑机的 2 倍。 */
    public static final int ENERGY_CAPACITY = 400_000;
    public static final int MAX_ENERGY_INPUT = 8_000;

    /** 本机每运行一刻消耗的灵感充能（mB），是物质重塑机（2 mB）的 2 倍。 */
    public static final int CHARGE_PER_TICK = 4;

    /** 灵感罐容量相对基准值的倍率。 */
    public static final int CHARGE_CAPACITY_MULTIPLIER = 2;

    /** 运行速度与每刻耗电相对基准值的倍率。 */
    public static final int SPEED_MULTIPLIER = 2;
    public static final int ENERGY_MULTIPLIER = 2;

    @Save
    private SidedInventoryComponent<HighEnergyMatterReshaperMachineTile> inputInventory;
    @Save
    private SidedInventoryComponent<HighEnergyMatterReshaperMachineTile> outputInventory;
    @Save
    private ProgressBarComponent<HighEnergyMatterReshaperMachineTile> progressBar;

    /** 缓存最近一次解析结果，避免每刻重复查表。 */
    private ReshaperTask cachedTask;
    private ItemStack cachedFirst = ItemStack.EMPTY;
    private ItemStack cachedSecond = ItemStack.EMPTY;

    public HighEnergyMatterReshaperMachineTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<HighEnergyMatterReshaperMachineTile>)
                        ModMachines.HIGH_ENERGY_MATTER_RESHAPER.getBlock(),
                ModMachines.HIGH_ENERGY_MATTER_RESHAPER.type().get(), pos, state);

        SidedInventoryComponent<HighEnergyMatterReshaperMachineTile> inputs =
                new SidedInventoryComponent<>("input", 44, 30, 2, 0);
        inputs.setRange(1, 2);
        inputs.setColor(DyeColor.GREEN);
        inputs.setFacingHandlerPos(8, 84);
        this.inputInventory = inputs;
        addInventory(this.inputInventory);

        SidedInventoryComponent<HighEnergyMatterReshaperMachineTile> outputs =
                new SidedInventoryComponent<>("output", 116, 39, 1, 1);
        outputs.setRange(1, 1);
        outputs.setColor(DyeColor.LIME);
        outputs.setInputFilter((stack, slot) -> false);
        outputs.setFacingHandlerPos(26, 84);
        this.outputInventory = outputs;
        addInventory(this.outputInventory);

        ProgressBarComponent<HighEnergyMatterReshaperMachineTile> bar =
                new ProgressBarComponent<>(78, 38, ReshapingRecipe.DEFAULT_DURATION);
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        bar.setColor(DyeColor.PURPLE);
        bar.setCanIncrease(this::canProcess);
        bar.setCanReset(tile -> tile.isFinished() || tile.findTask() == null);
        bar.setOnTickWork(this::consumeEnergy);
        bar.setOnFinishWork(this::finishCraft);
        this.progressBar = bar;
        addProgressBar(this.progressBar);
    }

    @Nonnull
    @Override
    protected PluginEnergyStorage<HighEnergyMatterReshaperMachineTile> createEnergyStorage() {
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
    public HighEnergyMatterReshaperMachineTile getSelf() {
        return this;
    }

    @Override
    public ProgressBarComponent<HighEnergyMatterReshaperMachineTile> getProgressBar() {
        return progressBar;
    }

    /* ============================ 配方逻辑 ============================ */

    private boolean canProcess(HighEnergyMatterReshaperMachineTile tile) {
        if (level == null || level.isClientSide) {
            return false;
        }
        ReshaperTask task = findTask();
        if (task == null) {
            return false;
        }
        if (progressBar.getMaxProgress() != task.duration()) {
            progressBar.setMaxProgress(task.duration());
        }
        if (!hasEnoughInput(task)) {
            return false;
        }
        return getEnergyStorage().getEnergyStored() >= energyCost(task)
                && hasCharge(effectiveChargePerTick())
                && canAcceptResult(task.result());
    }

    /** 本机每刻的耗电：任务基准值 × 本机倍率，再乘插件加成。 */
    private int energyCost(ReshaperTask task) {
        return effectiveEnergyPerTick(task.energyPerTick() * ENERGY_MULTIPLIER);
    }

    /** 配方可能一次消耗多个材料，必须凑齐整份才开始工作，否则会白扣材料。 */
    private boolean hasEnoughInput(ReshaperTask task) {
        if (inputInventory.getStackInSlot(0).getCount() < task.firstInputCount()) {
            return false;
        }
        return !task.consumesSecondInput()
                || inputInventory.getStackInSlot(1).getCount() >= task.secondInputCount();
    }

    private boolean canAcceptResult(ItemStack result) {
        ItemStack current = outputInventory.getStackInSlot(0);
        if (current.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(current, result)) {
            return false;
        }
        int limit = Math.min(outputInventory.getSlotLimit(0), result.getMaxStackSize());
        return current.getCount() + result.getCount() <= limit;
    }

    private void consumeEnergy() {
        ReshaperTask task = findTask();
        if (task == null) {
            return;
        }
        PluginEnergyStorage<HighEnergyMatterReshaperMachineTile> storage =
                (PluginEnergyStorage<HighEnergyMatterReshaperMachineTile>) getEnergyStorage();
        storage.setEnergyStored(storage.getEnergyStored() - energyCost(task));
        consumeCharge(effectiveChargePerTick());
    }

    private void finishCraft() {
        ReshaperTask task = findTask();
        if (task == null) {
            return;
        }
        ItemStack result = task.result().copy();
        if (!outputInventory.insertItem(0, result.copy(), true).isEmpty()) {
            return;
        }
        outputInventory.insertItem(0, result.copy(), false);
        inputInventory.extractItem(0, task.firstInputCount(), false);
        if (task.consumesSecondInput()) {
            inputInventory.extractItem(1, task.secondInputCount(), false);
        }
        invalidateCache();
        markForUpdate();
    }

    private boolean isFinished() {
        return progressBar.getProgress() >= progressBar.getMaxProgress();
    }

    /** 取当前输入对应的任务，输入未变化时直接复用缓存。 */
    private ReshaperTask findTask() {
        if (level == null || level.isClientSide) {
            return null;
        }
        ItemStack first = inputInventory.getStackInSlot(0);
        ItemStack second = inputInventory.getStackInSlot(1);
        if (first.isEmpty()) {
            invalidateCache();
            return null;
        }
        if (cachedTask != null
                && ItemStack.matches(first, cachedFirst)
                && ItemStack.matches(second, cachedSecond)) {
            return cachedTask;
        }
        cachedFirst = first.copy();
        cachedSecond = second.copy();
        cachedTask = ReshaperTaskResolver.resolve(level, first, second);
        return cachedTask;
    }

    private void invalidateCache() {
        cachedTask = null;
        cachedFirst = ItemStack.EMPTY;
        cachedSecond = ItemStack.EMPTY;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        addFacingLabels(inputInventory, outputInventory);
    }
}
