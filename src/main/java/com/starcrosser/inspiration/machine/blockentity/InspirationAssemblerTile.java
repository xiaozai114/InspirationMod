package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.starcrosser.inspiration.machine.charge.InspirationCharge;
import com.starcrosser.inspiration.machine.plugin.PluginEnergyStorage;
import com.starcrosser.inspiration.machine.recipe.AssemblingRecipe;
import com.starcrosser.inspiration.machine.recipe.AssemblingRecipeInput;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import com.starcrosser.inspiration.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


/**
 * 灵感装配台一族的方块实体：灵感装配台（等级 1）与高能灵感装配台（等级 2）共用这一份实现。
 * <p>
 * 若干个物品输入槽、若干个独立的流体输入槽、一个输出槽。材料不要求按槽位摆放，
 * 只要每种材料都能在输入区里各找到一份即可；流体原料按配方声明的种类与用量扣除。
 * 灵感充能照常作为机器燃料消耗，与流体输入槽互不相干。
 * <p>
 * 两档的差别全部由注册时的 {@link MachineSpec} 表达，而且**等级与槽数是独立的**：
 * 等级 1 是 4 物品槽 + 1 流体槽，等级 2 是 6 物品槽 + 2 流体槽；
 * 容量、速率那些数值另按 {@code 2^(tier-1)} 放大。
 */
public class InspirationAssemblerTile extends BatchProcessingTile<InspirationAssemblerTile> {

    /** tier 1 的能量缓冲容量（FE）。 */
    public static final int BASE_ENERGY_CAPACITY = 300_000;

    /** tier 1 的每刻最大输入功率（FE）。 */
    public static final int BASE_MAX_ENERGY_INPUT = 6_000;

    /** tier 1 每刻消耗的灵感充能（mB）。 */
    public static final int BASE_CHARGE_PER_TICK = 2;

    /** 普通装配台的单罐容量（mB），高能机按等级翻倍。 */
    public static final int FLUID_CAPACITY = 8_000;

    /** 物品输入区固定 2 列，行数由槽数决定。 */
    private static final int INPUT_COLUMNS = 2;

    /** 第一个流体罐的 GUI 位置，后面的罐每个往右挪一格。 */
    private static final int FIRST_FLUID_TANK_X = 26;
    private static final int FLUID_TANK_GAP = 18;
    private static final int FLUID_TANK_Y = 14;
    private static final int FIRST_FLUID_TANK_POS = 4;
    private static final int FIRST_FLUID_FACING_X = 80;

    private static final int PROGRESS_X = 104;
    private static final int PROGRESS_Y = 34;
    private static final int OUTPUT_X = 128;
    private static final int OUTPUT_Y = 34;

    /** 侧向配置按钮排在同一行，充能槽占 44、充能罐占 62，流体槽顺延。 */
    private static final int FACING_HANDLER_Y = 84;
    private static final int INPUT_FACING_X = 8;
    private static final int OUTPUT_FACING_X = 26;

    @Save
    private SidedInventoryComponent<InspirationAssemblerTile> inputInventory;
    @Save
    private SidedInventoryComponent<InspirationAssemblerTile> outputInventory;
    @Save
    private ProgressBarComponent<InspirationAssemblerTile> progressBar;

    /**
     * 流体输入罐，数量由 {@link MachineSpec#fluidInputSlots} 决定。
     * <p>
     * ⚠️ 没有 {@code @Save}：Titanium 的 {@code @Save} 只认实现了 {@code INBTSerializable}
     * 的单个对象（它的处理器里没有 List 这一项），所以这里改用 {@link #saveAdditional} 手写。
     */
    private final List<SidedFluidTankComponent<InspirationAssemblerTile>> fluidInputs = new ArrayList<>();

    /** 缓存最近一次命中的配方，输入未变化时直接复用。 */
    private AssemblingRecipe cachedRecipe;
    private List<ItemStack> cachedInputs = List.of();
    private List<FluidStack> cachedFluids = List.of();

    public InspirationAssemblerTile(MachineSpec spec, BasicTileBlock<InspirationAssemblerTile> block,
                                    BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(spec, block, type, pos, state);

        int itemSlots = spec.itemInputSlots();
        int rows = (itemSlots + INPUT_COLUMNS - 1) / INPUT_COLUMNS;
        Layout layout = inputLayout(rows);

        SidedInventoryComponent<InspirationAssemblerTile> inputs =
                new SidedInventoryComponent<>("input", layout.x(), layout.y(), itemSlots, 0);
        inputs.setRange(INPUT_COLUMNS, rows);
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

        for (int i = 0; i < spec.fluidInputSlots(); i++) {
            fluidInputs.add(fluidTank(i == 0 ? "fluid_input" : "fluid_input_" + (i + 1),
                    FIRST_FLUID_TANK_X + FLUID_TANK_GAP * i,
                    FIRST_FLUID_TANK_POS + i,
                    FIRST_FLUID_FACING_X + FLUID_TANK_GAP * i));
        }

        ProgressBarComponent<InspirationAssemblerTile> bar =
                new ProgressBarComponent<>(PROGRESS_X, PROGRESS_Y, AssemblingRecipe.DEFAULT_DURATION);
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        bar.setColor(spec.barColor());
        bar.setCanIncrease(this::canProcess);
        bar.setCanReset(tile -> tile.isFinished() || tile.findRecipe() == null);
        bar.setOnTickWork(this::consumeBatchEnergy);
        bar.setOnFinishWork(this::finishCraft);
        this.progressBar = bar;
        addProgressBar(this.progressBar);
    }

    /** 物品输入区在 GUI 中的位置。界面只有 176×184，行数变多就得把输入区往左上挪。 */
    private static Layout inputLayout(int rows) {
        return switch (rows) {
            case 2 -> new Layout(52, 25);
            case 3 -> new Layout(66, 16);
            default -> throw new IllegalArgumentException("装配台暂不支持 " + rows + " 行物品输入槽");
        };
    }

    private record Layout(int x, int y) {
    }

    /** 建一个只进不出、拒绝灵感充能的原料罐。 */
    private SidedFluidTankComponent<InspirationAssemblerTile> fluidTank(String name, int x, int pos, int facingX) {
        SidedFluidTankComponent<InspirationAssemblerTile> tank =
                new SidedFluidTankComponent<>(name, spec().scale(FLUID_CAPACITY), x, FLUID_TANK_Y, pos);
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
    protected PluginEnergyStorage<InspirationAssemblerTile> createEnergyStorage() {
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

    @Nonnull
    @Override
    public ProgressBarComponent<InspirationAssemblerTile> getProgressBar() {
        return progressBar;
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
        return prepareBatch(recipe.duration(), recipe.energyPerTick(),
                n -> recipe.resolveSlots(collectInputs(), n) != null
                        && recipe.resolveFluids(collectFluids(), n) != null
                        && fitsOutput(outputInventory, 0, recipe.result(), n));
    }

    private void finishCraft() {
        AssemblingRecipe recipe = findRecipe();
        if (recipe == null || level == null) {
            return;
        }
        int[] slots = recipe.resolveSlots(collectInputs(), batchSize());
        int[] tanks = recipe.resolveFluids(collectFluids(), batchSize());
        if (slots == null || tanks == null) {
            return;
        }
        ItemStack result = batchOutput(recipe.getResultItem(level.registryAccess()));
        if (!outputInventory.insertItem(0, result.copy(), true).isEmpty()) {
            return;
        }
        outputInventory.insertItem(0, result.copy(), false);
        for (int index = 0; index < slots.length; index++) {
            // 配方可声明「这种材料一次吃几个」（沉船数据要 64 块木板）
            inputInventory.extractItem(slots[index], recipe.countOf(index) * batchSize(), false);
        }
        for (int index = 0; index < tanks.length; index++) {
            // 罐体被配置为只进不出，机器自身扣料走 drainForced
            tankAt(tanks[index]).drainForced(recipe.fluids().get(index).amount() * batchSize(),
                    IFluidHandler.FluidAction.EXECUTE);
        }
        invalidateCache();
        markForUpdate();
    }

    private SidedFluidTankComponent<InspirationAssemblerTile> tankAt(int index) {
        return fluidInputs.get(index);
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
                .map(this::trackRecipe)
                .orElse(null);
        return cachedRecipe;
    }

    private List<ItemStack> collectInputs() {
        List<ItemStack> items = new ArrayList<>(inputInventory.getSlots());
        for (int slot = 0; slot < inputInventory.getSlots(); slot++) {
            items.add(inputInventory.getStackInSlot(slot));
        }
        return items;
    }

    private List<FluidStack> collectFluids() {
        return fluidInputs.stream().map(SidedFluidTankComponent::getFluid).toList();
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
        List<IFacingComponent> labelled = new ArrayList<>();
        labelled.add(inputInventory);
        labelled.add(outputInventory);
        labelled.addAll(fluidInputs);
        addFacingLabels(labelled.toArray(new IFacingComponent[0]));
    }

    /* ============================ 持久化 ============================ */

    /**
     * 流体罐列表要手动存：数量由注册配置决定、读档时按同样的配置重建，
     * 所以这里只要按顺序把每个罐的内容写进去即可。
     */
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!fluidInputs.isEmpty()) {
            ListTag list = new ListTag();
            for (SidedFluidTankComponent<InspirationAssemblerTile> tank : fluidInputs) {
                list.add(tank.serializeNBT(registries));
            }
            tag.put("FluidInputs", list);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ListTag list = tag.getList("FluidInputs", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size() && i < fluidInputs.size(); i++) {
            fluidInputs.get(i).deserializeNBT(registries, list.getCompound(i));
        }
    }
}
