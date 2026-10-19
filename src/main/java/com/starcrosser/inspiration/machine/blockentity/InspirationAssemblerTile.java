package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.machine.charge.InspirationCharge;
import com.starcrosser.inspiration.machine.plugin.PluginEnergyStorage;
import com.starcrosser.inspiration.machine.recipe.AssemblingRecipe;
import com.starcrosser.inspiration.machine.recipe.AssemblingRecipeInput;
import com.starcrosser.inspiration.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


/**
 * 灵感装配台的方块实体。
 * <p>
 * 四个物品输入槽、一个独立的流体输入槽、一个输出槽。材料不要求按槽位摆放，
 * 只要每种材料都能在输入区里各找到一份即可；流体原料按配方声明的种类与用量扣除。
 * 灵感充能照常作为机器燃料消耗，与流体输入槽互不相干。
 */
public class InspirationAssemblerTile extends InspiredMachineTile<InspirationAssemblerTile> {

    /** 能量缓冲：介于物质重塑机与矿物扭曲机之间。 */
    public static final int ENERGY_CAPACITY = 300_000;
    public static final int MAX_ENERGY_INPUT = 6_000;

    /** 本机每运行一刻消耗的灵感充能（mB）。 */
    public static final int CHARGE_PER_TICK = 2;

    /** 流体输入槽容量（mB）。 */
    public static final int FLUID_CAPACITY = 8_000;

    /** 输入区在 GUI 中的位置：2×2 排布。 */
    private static final int INPUT_X = 52;
    private static final int INPUT_Y = 25;

    /** 流体输入槽：与能量条同高，位于能量条右侧。 */
    private static final int FLUID_TANK_X = 26;
    private static final int FLUID_TANK_Y = 14;

    private static final int PROGRESS_X = 104;
    private static final int PROGRESS_Y = 34;
    private static final int OUTPUT_X = 128;
    private static final int OUTPUT_Y = 34;

    /** 侧向配置按钮排在同一行，充能槽占 44、充能罐占 62，流体槽顺延。 */
    private static final int FACING_HANDLER_Y = 84;
    private static final int INPUT_FACING_X = 8;
    private static final int OUTPUT_FACING_X = 26;
    private static final int FLUID_FACING_X = 80;

    /** 侧栏序号：输入 0、输出 1，充能槽 / 充能罐占 2 与 3，流体槽顺延。 */
    private static final int FLUID_TANK_POS = 4;

    @Save
    private SidedInventoryComponent<InspirationAssemblerTile> inputInventory;
    @Save
    private SidedInventoryComponent<InspirationAssemblerTile> outputInventory;
    @Save
    private SidedFluidTankComponent<InspirationAssemblerTile> fluidInput;
    @Save
    private ProgressBarComponent<InspirationAssemblerTile> progressBar;

    /** 缓存最近一次命中的配方，输入未变化时直接复用。 */
    private AssemblingRecipe cachedRecipe;
    private List<ItemStack> cachedInputs = List.of();
    private List<FluidStack> cachedFluids = List.of();

    public InspirationAssemblerTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<InspirationAssemblerTile>) ModMachines.INSPIRATION_ASSEMBLER.getBlock(),
                ModMachines.INSPIRATION_ASSEMBLER.type().get(), pos, state);

        SidedInventoryComponent<InspirationAssemblerTile> inputs =
                new SidedInventoryComponent<>("input", INPUT_X, INPUT_Y,
                        AssemblingRecipeInput.SLOT_COUNT, 0);
        inputs.setRange(2, 2);
        inputs.setColor(DyeColor.YELLOW);
        inputs.setFacingHandlerPos(INPUT_FACING_X, FACING_HANDLER_Y);
        this.inputInventory = inputs;
        addInventory(this.inputInventory);

        SidedInventoryComponent<InspirationAssemblerTile> outputs =
                new SidedInventoryComponent<>("output", OUTPUT_X, OUTPUT_Y, 1, 1);
        outputs.setRange(1, 1);
        outputs.setColor(DyeColor.ORANGE);
        outputs.setInputFilter((stack, slot) -> false);
        outputs.setFacingHandlerPos(OUTPUT_FACING_X, FACING_HANDLER_Y);
        this.outputInventory = outputs;
        addInventory(this.outputInventory);

        SidedFluidTankComponent<InspirationAssemblerTile> tank = new SidedFluidTankComponent<>(
                "fluid_input", FLUID_CAPACITY, FLUID_TANK_X, FLUID_TANK_Y, FLUID_TANK_POS);
        tank.setTankType(FluidTankComponent.Type.NORMAL);
        // 只进不出：原料流体只能灌入，不能被抽走
        tank.setTankAction(FluidTankComponent.Action.FILL);
        // 灵感充能是燃料，走基类的充能罐；这里只收别的流体，避免管道灌错罐
        tank.setValidator(stack -> !InspirationCharge.isChargeFluid(stack));
        tank.setColor(DyeColor.CYAN);
        tank.setFacingHandlerPos(FLUID_FACING_X, FACING_HANDLER_Y);
        this.fluidInput = tank;
        addTank(this.fluidInput);

        ProgressBarComponent<InspirationAssemblerTile> bar =
                new ProgressBarComponent<>(PROGRESS_X, PROGRESS_Y, AssemblingRecipe.DEFAULT_DURATION);
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        bar.setColor(DyeColor.YELLOW);
        bar.setCanIncrease(this::canProcess);
        bar.setCanReset(tile -> tile.isFinished() || tile.findRecipe() == null);
        bar.setOnTickWork(this::consumeEnergy);
        bar.setOnFinishWork(this::finishCraft);
        this.progressBar = bar;
        addProgressBar(this.progressBar);
    }

    @Nonnull
    @Override
    protected PluginEnergyStorage<InspirationAssemblerTile> createEnergyStorage() {
        // 只进不出：maxExtract 为 0，能量只能被机器自身消耗
        return new PluginEnergyStorage<>(ENERGY_CAPACITY, MAX_ENERGY_INPUT, 0, 8, 14);
    }

    @Override
    public int chargePerTick() {
        return CHARGE_PER_TICK;
    }

    @Nonnull
    @Override
    public ProgressBarComponent<InspirationAssemblerTile> getProgressBar() {
        return progressBar;
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
    public InspirationAssemblerTile getSelf() {
        return this;
    }

    /* ============================ 配方逻辑 ============================ */

    private boolean canProcess(InspirationAssemblerTile tile) {
        if (level == null || level.isClientSide) {
            return false;
        }
        AssemblingRecipe recipe = findRecipe();
        if (recipe == null) {
            return false;
        }
        if (progressBar.getMaxProgress() != recipe.duration()) {
            progressBar.setMaxProgress(recipe.duration());
        }
        return getEnergyStorage().getEnergyStored() >= effectiveEnergyPerTick(recipe.energyPerTick())
                && hasCharge(effectiveChargePerTick())
                && canAcceptResult(recipe.result());
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
        AssemblingRecipe recipe = findRecipe();
        if (recipe == null) {
            return;
        }
        PluginEnergyStorage<InspirationAssemblerTile> storage =
                (PluginEnergyStorage<InspirationAssemblerTile>) getEnergyStorage();
        storage.setEnergyStored(storage.getEnergyStored() - effectiveEnergyPerTick(recipe.energyPerTick()));
        consumeCharge(effectiveChargePerTick());
    }

    private void finishCraft() {
        AssemblingRecipe recipe = findRecipe();
        if (recipe == null || level == null) {
            return;
        }
        int[] slots = recipe.resolveSlots(collectInputs());
        if (slots == null) {
            return;
        }
        ItemStack result = recipe.getResultItem(level.registryAccess());
        if (!outputInventory.insertItem(0, result.copy(), true).isEmpty()) {
            return;
        }
        outputInventory.insertItem(0, result.copy(), false);
        for (int index = 0; index < slots.length; index++) {
            // 配方可声明「这种材料一次吃几个」（沉船数据要 64 块木板）
            inputInventory.extractItem(slots[index], recipe.countOf(index), false);
        }
        drainFluids(recipe);
        invalidateCache();
        markForUpdate();
    }

    /** 按配方扣掉流体原料；罐体被配置为只进不出，机器自身扣料走 drainForced。 */
    private void drainFluids(AssemblingRecipe recipe) {
        int[] tanks = recipe.resolveFluids(List.of(fluidInput.getFluid()));
        if (tanks == null) {
            return;
        }
        for (int index = 0; index < tanks.length; index++) {
            fluidInput.drainForced(recipe.fluids().get(index).amount(), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    private boolean isFinished() {
        return progressBar.getProgress() >= progressBar.getMaxProgress();
    }

    /** 取当前输入对应的配方，输入与流体都未变化时直接复用缓存。 */
    private AssemblingRecipe findRecipe() {
        if (level == null || level.isClientSide) {
            return null;
        }
        List<ItemStack> items = collectInputs();
        List<FluidStack> tanks = List.of(fluidInput.getFluid());
        AssemblingRecipeInput input = new AssemblingRecipeInput(items, tanks);
        if (input.isEmpty()) {
            invalidateCache();
            return null;
        }
        if (cachedRecipe != null && sameInput(items, tanks)) {
            return cachedRecipe;
        }
        cachedInputs = items.stream().map(ItemStack::copy).toList();
        cachedFluids = tanks.stream().map(FluidStack::copy).toList();
        cachedRecipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.ASSEMBLING_TYPE.get(), input, level)
                .map(RecipeHolder::value)
                .orElse(null);
        return cachedRecipe;
    }

    private List<ItemStack> collectInputs() {
        List<ItemStack> items = new ArrayList<>(AssemblingRecipeInput.SLOT_COUNT);
        for (int slot = 0; slot < inputInventory.getSlots(); slot++) {
            items.add(inputInventory.getStackInSlot(slot));
        }
        return items;
    }

    private boolean sameInput(List<ItemStack> items, List<FluidStack> tanks) {
        if (cachedInputs.size() != items.size() || cachedFluids.size() != tanks.size()) {
            return false;
        }
        for (int i = 0; i < items.size(); i++) {
            if (!ItemStack.matches(items.get(i), cachedInputs.get(i))) {
                return false;
            }
        }
        for (int i = 0; i < tanks.size(); i++) {
            FluidStack cached = cachedFluids.get(i);
            if (tanks.get(i).getFluid() != cached.getFluid() || tanks.get(i).getAmount() != cached.getAmount()) {
                return false;
            }
        }
        return true;
    }

    private void invalidateCache() {
        cachedRecipe = null;
        cachedInputs = List.of();
        cachedFluids = List.of();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        addFacingLabels(inputInventory, outputInventory, fluidInput);
    }
}
