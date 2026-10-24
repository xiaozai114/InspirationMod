package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.machine.plugin.PluginEnergyStorage;
import com.starcrosser.inspiration.machine.recipe.FluidExtractingRecipe;
import com.starcrosser.inspiration.machine.recipe.FluidExtractingRecipeInput;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import com.starcrosser.inspiration.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

/**
 * 流体提取机：把材料熔成对应的液态金属。
 * <p>
 * 一个输入槽放材料（锭 / 粒 / 块都行，配方决定换算比例），一个输出罐存液态金属，
 * 罐里的流体可以用桶舀走，也可以接管道抽到别处。
 * <p>
 * 输出罐容量固定 16000 mB（约 111 锭），目前只有 tier 1 机型，所以不做等级倍率；
 * 将来要是加高能版，记得把 {@code applyTier} 那条路子也用到罐子上。
 */
public class FluidExtractorTile extends InspiredMachineTile<FluidExtractorTile> {

    /** tier 1 的能量缓冲容量（FE）。 */
    public static final int BASE_ENERGY_CAPACITY = 400_000;

    /** tier 1 的每刻最大输入功率（FE）。 */
    public static final int BASE_MAX_ENERGY_INPUT = 8_000;

    /** tier 1 每刻消耗的灵感充能（mB）。 */
    public static final int BASE_CHARGE_PER_TICK = 2;

    /** tier 1 的输出罐容量（mB）：14400 是 100 锭，留一点余量给不整除的配方。 */
    public static final int BASE_TANK_CAPACITY = 16_000;

    @Save
    private SidedInventoryComponent<FluidExtractorTile> inputInventory;
    @Save
    private SidedFluidTankComponent<FluidExtractorTile> outputTank;
    @Save
    private ProgressBarComponent<FluidExtractorTile> progressBar;

    /** 缓存最近一次命中的配方，避免每刻重复查表。 */
    private FluidExtractingRecipe cachedRecipe;
    private ItemStack cachedInput = ItemStack.EMPTY;

    public FluidExtractorTile(MachineSpec spec, BasicTileBlock<FluidExtractorTile> block,
                              BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(spec, block, type, pos, state);

        SidedInventoryComponent<FluidExtractorTile> inputs =
                new SidedInventoryComponent<>("input", 44, 30, 1, 0);
        inputs.setRange(1, 1);
        inputs.setColor(DyeColor.CYAN);
        inputs.setFacingHandlerPos(8, 84);
        this.inputInventory = inputs;
        addInventory(this.inputInventory);

        // 输出罐：只出不进（DRAIN），玩家可以用桶舀、管道也只能往外抽。
        // 组件名叫 fluid_output —— 侧向按钮的标签与悬停提示都按组件名查语言键，
        // 叫 output 会复用物品输出那一套文案（"物品输出"），这里是流体。
        SidedFluidTankComponent<FluidExtractorTile> tank = new SidedFluidTankComponent<>(
                "fluid_output", BASE_TANK_CAPACITY, 116, 20, 1);
        tank.setTankType(FluidTankComponent.Type.NORMAL);
        tank.setTankAction(FluidTankComponent.Action.DRAIN);
        tank.setColor(DyeColor.CYAN);
        tank.setFacingHandlerPos(26, 84);
        this.outputTank = tank;
        addTank(this.outputTank);

        ProgressBarComponent<FluidExtractorTile> bar =
                new ProgressBarComponent<>(78, 38, FluidExtractingRecipe.DEFAULT_DURATION);
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
    protected PluginEnergyStorage<FluidExtractorTile> createEnergyStorage() {
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
    public FluidExtractorTile getSelf() {
        return this;
    }

    @Override
    public ProgressBarComponent<FluidExtractorTile> getProgressBar() {
        return progressBar;
    }

    /* ============================ 配方逻辑 ============================ */

    private boolean canProcess(FluidExtractorTile tile) {
        if (level == null || level.isClientSide) {
            return false;
        }
        FluidExtractingRecipe recipe = findRecipe();
        if (recipe == null) {
            return false;
        }
        if (progressBar.getMaxProgress() != recipe.duration()) {
            progressBar.setMaxProgress(recipe.duration());
        }
        return getEnergyStorage().getEnergyStored() >= effectiveEnergyPerTick(recipe.energyPerTick())
                && hasCharge(effectiveChargePerTick())
                && canAccept(recipe);
    }

    /** 罐里能装下这次产物：要么是空的，要么装的同一种流体且剩余空间够。 */
    private boolean canAccept(FluidExtractingRecipe recipe) {
        FluidStack current = outputTank.getFluid();
        FluidStack result = recipe.result();
        if (current.isEmpty()) {
            return outputTank.getCapacity() >= result.getAmount();
        }
        if (!FluidStack.isSameFluidSameComponents(current, result)) {
            return false;
        }
        return outputTank.getCapacity() - current.getAmount() >= result.getAmount();
    }

    private void consumeEnergy() {
        FluidExtractingRecipe recipe = findRecipe();
        if (recipe == null) {
            return;
        }
        PluginEnergyStorage<FluidExtractorTile> storage =
                (PluginEnergyStorage<FluidExtractorTile>) getEnergyStorage();
        storage.setEnergyStored(storage.getEnergyStored() - effectiveEnergyPerTick(recipe.energyPerTick()));
        consumeCharge(effectiveChargePerTick());
    }

    private void finishCraft() {
        if (level == null) {
            return;
        }
        FluidExtractingRecipe recipe = findRecipe();
        if (recipe == null || !canAccept(recipe)) {
            return;
        }
        // 必须 fillForced：罐体被设成只出不进（Action.DRAIN），普通 fill 会被动作校验挡掉返回 0，
        // 表现为「原料被吃掉、罐里一滴都没有」。与充能罐扣减要 drainForced 是同一个坑。
        outputTank.fillForced(recipe.result().copy(), IFluidHandler.FluidAction.EXECUTE);
        inputInventory.extractItem(0, recipe.inputCount(), false);
        invalidateCache();
        markForUpdate();
    }

    private boolean isFinished() {
        return progressBar.getProgress() >= progressBar.getMaxProgress();
    }

    /** 取当前输入对应的配方，输入未变化时直接复用缓存。 */
    private FluidExtractingRecipe findRecipe() {
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
                .getRecipeFor(ModRecipes.FLUID_EXTRACTING_TYPE.get(), new FluidExtractingRecipeInput(input), level)
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
        addFacingLabels(inputInventory, outputTank);
    }
}
