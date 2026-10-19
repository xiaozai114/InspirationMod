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
 * 高能灵感装配台的方块实体：本阶段最重要的合成机器。
 * <p>
 * 结构照搬 {@link InspirationAssemblerTile}，差别在四处：
 * <ul>
 *     <li>物品输入槽由 4 个扩到 6 个（界面里 2 列 × 3 行），因此能吃下六种材料的配方；</li>
 *     <li>流体输入槽由 1 个扩到 2 个，因此能吃下需要两种流体原料的配方；</li>
 *     <li>能量缓冲、灵感罐容量、每刻耗电、每刻灵感充能消耗、运行速度全是上一阶段的 2 倍；</li>
 *     <li>材料与流体都顺序无关：结算时按回溯指派到的槽位 / 罐位扣除。</li>
 * </ul>
 * 倍率只写在本机内：{@link InspiredMachineTile} 的插件结算流程读
 * {@link #baseSpeed()} 与 {@link #baseChargeCapacity()} 即可，不用改。
 */
public class HighEnergyInspirationAssemblerTile extends InspiredMachineTile<HighEnergyInspirationAssemblerTile> {

    /** 能量缓冲：灵感装配台的 2 倍。 */
    public static final int ENERGY_CAPACITY = 600_000;
    public static final int MAX_ENERGY_INPUT = 12_000;

    /** 本机每运行一刻消耗的灵感充能（mB），是灵感装配台（2 mB）的 2 倍。 */
    public static final int CHARGE_PER_TICK = 4;

    /** 灵感罐容量相对基准值的倍率。 */
    public static final int CHARGE_CAPACITY_MULTIPLIER = 2;

    /** 运行速度与每刻耗电相对基准值的倍率。 */
    public static final int SPEED_MULTIPLIER = 2;
    public static final int ENERGY_MULTIPLIER = 2;

    /** 单个流体输入槽容量（mB），与灵感装配台一致。 */
    public static final int FLUID_CAPACITY = 8_000;

    /** 物品输入槽数量。 */
    public static final int INPUT_SLOTS = AssemblingRecipeInput.HIGH_ENERGY_SLOT_COUNT;

    /** 流体输入槽数量。 */
    public static final int FLUID_TANKS = 2;

    /** 输入区在 GUI 中的位置：2 列 × 3 行，紧贴两个流体罐右侧。 */
    private static final int INPUT_X = 66;
    private static final int INPUT_Y = 16;

    /** 两个流体输入槽：与能量条同高，横向并排。 */
    private static final int FLUID_TANK_X = 26;
    private static final int FLUID_TANK_X2 = 44;
    private static final int FLUID_TANK_Y = 14;

    private static final int PROGRESS_X = 104;
    private static final int PROGRESS_Y = 34;
    private static final int OUTPUT_X = 128;
    private static final int OUTPUT_Y = 34;

    /** 侧向配置按钮排在同一行：输入 8、输出 26，充能槽占 44、充能罐占 62，两个流体槽顺延。 */
    private static final int FACING_HANDLER_Y = 84;
    private static final int INPUT_FACING_X = 8;
    private static final int OUTPUT_FACING_X = 26;
    private static final int FLUID_FACING_X = 80;
    private static final int FLUID_FACING_X2 = 98;

    /** 侧栏序号：输入 0、输出 1，充能槽 / 充能罐占 2 与 3，两个流体槽顺延。 */
    private static final int FLUID_TANK_POS = 4;
    private static final int FLUID_TANK_POS2 = 5;

    @Save
    private SidedInventoryComponent<HighEnergyInspirationAssemblerTile> inputInventory;
    @Save
    private SidedInventoryComponent<HighEnergyInspirationAssemblerTile> outputInventory;
    @Save
    private SidedFluidTankComponent<HighEnergyInspirationAssemblerTile> fluidInput;
    @Save
    private SidedFluidTankComponent<HighEnergyInspirationAssemblerTile> fluidInput2;
    @Save
    private ProgressBarComponent<HighEnergyInspirationAssemblerTile> progressBar;

    /** 缓存最近一次命中的配方，输入未变化时直接复用。 */
    private AssemblingRecipe cachedRecipe;
    private List<ItemStack> cachedInputs = List.of();
    private List<FluidStack> cachedFluids = List.of();

    public HighEnergyInspirationAssemblerTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<HighEnergyInspirationAssemblerTile>) ModMachines.HIGH_ENERGY_INSPIRATION_ASSEMBLER.getBlock(),
                ModMachines.HIGH_ENERGY_INSPIRATION_ASSEMBLER.type().get(), pos, state);

        SidedInventoryComponent<HighEnergyInspirationAssemblerTile> inputs =
                new SidedInventoryComponent<>("input", INPUT_X, INPUT_Y, INPUT_SLOTS, 0);
        inputs.setRange(2, 3);
        inputs.setColor(DyeColor.YELLOW);
        inputs.setFacingHandlerPos(INPUT_FACING_X, FACING_HANDLER_Y);
        this.inputInventory = inputs;
        addInventory(this.inputInventory);

        SidedInventoryComponent<HighEnergyInspirationAssemblerTile> outputs =
                new SidedInventoryComponent<>("output", OUTPUT_X, OUTPUT_Y, 1, 1);
        outputs.setRange(1, 1);
        outputs.setColor(DyeColor.ORANGE);
        outputs.setInputFilter((stack, slot) -> false);
        outputs.setFacingHandlerPos(OUTPUT_FACING_X, FACING_HANDLER_Y);
        this.outputInventory = outputs;
        addInventory(this.outputInventory);

        this.fluidInput = fluidTank("fluid_input", FLUID_TANK_X, FLUID_TANK_POS, FLUID_FACING_X);
        this.fluidInput2 = fluidTank("fluid_input_2", FLUID_TANK_X2, FLUID_TANK_POS2, FLUID_FACING_X2);

        ProgressBarComponent<HighEnergyInspirationAssemblerTile> bar =
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

    /** 建一个只进不出、拒绝灵感充能的原料罐。 */
    private SidedFluidTankComponent<HighEnergyInspirationAssemblerTile> fluidTank(
            String name, int x, int pos, int facingX) {
        SidedFluidTankComponent<HighEnergyInspirationAssemblerTile> tank =
                new SidedFluidTankComponent<>(name, FLUID_CAPACITY, x, FLUID_TANK_Y, pos);
        tank.setTankType(FluidTankComponent.Type.NORMAL);
        // 只进不出：原料流体只能灌入，不能被抽走
        tank.setTankAction(FluidTankComponent.Action.FILL);
        // 灵感充能是燃料，走基类的充能罐；这里只收别的流体，避免管道灌错罐
        tank.setValidator(stack -> !InspirationCharge.isChargeFluid(stack));
        tank.setColor(DyeColor.CYAN);
        tank.setFacingHandlerPos(facingX, FACING_HANDLER_Y);
        addTank(tank);
        return tank;
    }

    @Nonnull
    @Override
    protected PluginEnergyStorage<HighEnergyInspirationAssemblerTile> createEnergyStorage() {
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

    @Nonnull
    @Override
    public ProgressBarComponent<HighEnergyInspirationAssemblerTile> getProgressBar() {
        return progressBar;
    }

    @Override
    public boolean isActive() {
        return progressBar != null && progressBar.getProgress() > 0;
    }

    @Nonnull
    @Override
    public HighEnergyInspirationAssemblerTile getSelf() {
        return this;
    }

    /* ============================ 配方逻辑 ============================ */

    private boolean canProcess(HighEnergyInspirationAssemblerTile tile) {
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
        return getEnergyStorage().getEnergyStored() >= energyCost(recipe)
                && hasCharge(effectiveChargePerTick())
                && canAcceptResult(recipe.result());
    }

    /** 本机每刻的耗电：配方基准值 × 本机倍率，再乘插件加成。 */
    private int energyCost(AssemblingRecipe recipe) {
        return effectiveEnergyPerTick(recipe.energyPerTick() * ENERGY_MULTIPLIER);
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
        PluginEnergyStorage<HighEnergyInspirationAssemblerTile> storage =
                (PluginEnergyStorage<HighEnergyInspirationAssemblerTile>) getEnergyStorage();
        storage.setEnergyStored(storage.getEnergyStored() - energyCost(recipe));
        consumeCharge(effectiveChargePerTick());
    }

    private void finishCraft() {
        AssemblingRecipe recipe = findRecipe();
        if (recipe == null || level == null) {
            return;
        }
        int[] slots = recipe.resolveSlots(collectInputs());
        int[] tanks = recipe.resolveFluids(collectFluids());
        if (slots == null || tanks == null) {
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
        for (int index = 0; index < tanks.length; index++) {
            // 罐体被配置为只进不出，机器自身扣料走 drainForced
            tankAt(tanks[index]).drainForced(recipe.fluids().get(index).amount(),
                    IFluidHandler.FluidAction.EXECUTE);
        }
        invalidateCache();
        markForUpdate();
    }

    private SidedFluidTankComponent<HighEnergyInspirationAssemblerTile> tankAt(int index) {
        return index == 0 ? fluidInput : fluidInput2;
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
        List<FluidStack> tanks = collectFluids();
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
        List<ItemStack> items = new ArrayList<>(INPUT_SLOTS);
        for (int slot = 0; slot < inputInventory.getSlots(); slot++) {
            items.add(inputInventory.getStackInSlot(slot));
        }
        return items;
    }

    private List<FluidStack> collectFluids() {
        return List.of(fluidInput.getFluid(), fluidInput2.getFluid());
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
        addFacingLabels(inputInventory, outputInventory, fluidInput, fluidInput2);
    }
}
