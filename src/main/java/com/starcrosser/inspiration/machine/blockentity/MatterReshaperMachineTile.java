package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.machine.ModMachines;
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
 * 物质重塑机的方块实体。
 * <p>
 * 相比物质融合器：能量缓冲更大、输入功率更高，但配方耗时与每刻能耗都明显更高。
 * 可执行的配方有两类 —— 通用的右键转化配方，以及本机专属的「重塑配方」。
 */
public class MatterReshaperMachineTile extends InspiredMachineTile<MatterReshaperMachineTile> {

    /** 能量缓冲：约为物质融合器的三倍。 */
    public static final int ENERGY_CAPACITY = 200_000;
    public static final int MAX_ENERGY_INPUT = 4_000;

    /** 本机每运行一刻消耗的灵感充能（mB），是物质融合器的两倍。 */
    public static final int CHARGE_PER_TICK = 2;

    @Save
    private SidedInventoryComponent<MatterReshaperMachineTile> inputInventory;
    @Save
    private SidedInventoryComponent<MatterReshaperMachineTile> outputInventory;
    @Save
    private ProgressBarComponent<MatterReshaperMachineTile> progressBar;

    /** 缓存最近一次解析结果，避免每刻重复查表。 */
    private ReshaperTask cachedTask;
    private ItemStack cachedFirst = ItemStack.EMPTY;
    private ItemStack cachedSecond = ItemStack.EMPTY;

    public MatterReshaperMachineTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<MatterReshaperMachineTile>) ModMachines.MATTER_RESHAPER.getBlock(),
                ModMachines.MATTER_RESHAPER.type().get(), pos, state);

        SidedInventoryComponent<MatterReshaperMachineTile> inputs =
                new SidedInventoryComponent<>("input", 44, 30, 2, 0);
        inputs.setRange(1, 2);
        inputs.setColor(DyeColor.GREEN);
        inputs.setFacingHandlerPos(8, 84);
        this.inputInventory = inputs;
        addInventory(this.inputInventory);

        SidedInventoryComponent<MatterReshaperMachineTile> outputs =
                new SidedInventoryComponent<>("output", 116, 39, 1, 1);
        outputs.setRange(1, 1);
        outputs.setColor(DyeColor.LIME);
        outputs.setInputFilter((stack, slot) -> false);
        outputs.setFacingHandlerPos(26, 84);
        this.outputInventory = outputs;
        addInventory(this.outputInventory);

        ProgressBarComponent<MatterReshaperMachineTile> bar =
                new ProgressBarComponent<>(78, 38, ReshapingRecipe.DEFAULT_DURATION);
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        bar.setColor(DyeColor.GREEN);
        bar.setCanIncrease(this::canProcess);
        bar.setCanReset(tile -> tile.isFinished() || tile.findTask() == null);
        bar.setOnTickWork(this::consumeEnergy);
        bar.setOnFinishWork(this::finishCraft);
        this.progressBar = bar;
        addProgressBar(this.progressBar);
    }

    @Nonnull
    @Override
    protected PluginEnergyStorage<MatterReshaperMachineTile> createEnergyStorage() {
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
    public boolean isActive() {
        return progressBar != null && progressBar.getProgress() > 0;
    }

    @Nonnull
    @Override
    public MatterReshaperMachineTile getSelf() {
        return this;
    }

    @Override
    public ProgressBarComponent<MatterReshaperMachineTile> getProgressBar() {
        return progressBar;
    }

    /* ============================ 配方逻辑 ============================ */

    private boolean canProcess(MatterReshaperMachineTile tile) {
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
        return getEnergyStorage().getEnergyStored() >= effectiveEnergyPerTick(task.energyPerTick())
                && hasCharge(effectiveChargePerTick())
                && canAcceptResult(task.result());
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
        PluginEnergyStorage<MatterReshaperMachineTile> storage =
                (PluginEnergyStorage<MatterReshaperMachineTile>) getEnergyStorage();
        storage.setEnergyStored(storage.getEnergyStored() - effectiveEnergyPerTick(task.energyPerTick()));
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
