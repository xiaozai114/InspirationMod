package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.machine.charge.InspirationCharge;
import com.starcrosser.inspiration.machine.plugin.PluginEnergyStorage;
import com.starcrosser.inspiration.machine.recipe.FusionRecipe;
import com.starcrosser.inspiration.machine.recipe.FusionRecipeInput;
import com.starcrosser.inspiration.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


/**
 * 强能物质融合机的方块实体。
 * <p>
 * 结构照搬 {@link MatterFusionMachineTile}，差别只在四处：
 * <ul>
 *     <li>输入槽由 2 个扩到 3 个，因此能吃下需要三种材料的融合配方；</li>
 *     <li>耗电相对配方基准值翻倍（{@link #ENERGY_MULTIPLIER}）；</li>
 *     <li>每刻消耗的灵感充能、灵感罐容量、能量缓冲容量全部翻倍；</li>
 *     <li>运行速度翻倍 —— 由 {@link #baseSpeed()} 改写进度条的每刻推进量实现。</li>
 * </ul>
 * 倍率只写在本机内： {@link InspiredMachineTile} 的插件结算流程不用改，
 * 它在 {@code applyPlugins} 里读取 {@link #baseSpeed()} 与 {@link #baseChargeCapacity()}即可。
 */
public class EnergizedMatterFusionMachineTile extends InspiredMachineTile<EnergizedMatterFusionMachineTile> {

    /** 输入槽数量：比基础机型多一个。 */
    public static final int INPUT_SLOTS = 3;

    public static final int ENERGY_CAPACITY = 120_000;
    public static final int MAX_ENERGY_INPUT = 2_000;

    /** 本机每运行一刻消耗的灵感充能（mB），是基础机型的 2 倍。 */
    public static final int CHARGE_PER_TICK = 2;

    /** 相对配方基准值的耗电倍率。 */
    public static final int ENERGY_MULTIPLIER = 2;

    /** 灵感罐容量相对基准值的倍率。 */
    public static final int CHARGE_CAPACITY_MULTIPLIER = 2;

    /** 运行速度相对基准值的倍率。 */
    public static final int SPEED_MULTIPLIER = 2;

    @Save
    private SidedInventoryComponent<EnergizedMatterFusionMachineTile> inputInventory;
    @Save
    private SidedInventoryComponent<EnergizedMatterFusionMachineTile> outputInventory;
    @Save
    private ProgressBarComponent<EnergizedMatterFusionMachineTile> progressBar;

    private FusionRecipe cachedRecipe;

    public EnergizedMatterFusionMachineTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<EnergizedMatterFusionMachineTile>) ModMachines.ENERGIZED_MATTER_FUSION_MACHINE.getBlock(),
                ModMachines.ENERGIZED_MATTER_FUSION_MACHINE.type().get(), pos, state);

        SidedInventoryComponent<EnergizedMatterFusionMachineTile> inputs =
                new SidedInventoryComponent<>("input", 44, 30, INPUT_SLOTS, 0);
        inputs.setRange(1, INPUT_SLOTS);
        inputs.setColor(DyeColor.LIGHT_BLUE);
        inputs.setFacingHandlerPos(8, 84);
        this.inputInventory = inputs;
        addInventory(this.inputInventory);

        SidedInventoryComponent<EnergizedMatterFusionMachineTile> outputs =
                new SidedInventoryComponent<>("output", 116, 39, 1, 1);
        outputs.setRange(1, 1);
        outputs.setColor(DyeColor.ORANGE);
        outputs.setInputFilter((stack, slot) -> false);
        outputs.setFacingHandlerPos(26, 84);
        this.outputInventory = outputs;
        addInventory(this.outputInventory);

        ProgressBarComponent<EnergizedMatterFusionMachineTile> bar =
                new ProgressBarComponent<>(78, 38, FusionRecipe.DEFAULT_DURATION);
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        bar.setColor(DyeColor.LIGHT_BLUE);
        bar.setCanIncrease(this::canProcess);
        bar.setCanReset(tile -> tile.isFinished() || !tile.hasRecipe());
        bar.setOnTickWork(this::consumeEnergy);
        bar.setOnFinishWork(this::finishCraft);
        this.progressBar = bar;
        addProgressBar(this.progressBar);
    }

    @Nonnull
    @Override
    protected PluginEnergyStorage<EnergizedMatterFusionMachineTile> createEnergyStorage() {
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
    public EnergizedMatterFusionMachineTile getSelf() {
        return this;
    }

    @Override
    public ProgressBarComponent<EnergizedMatterFusionMachineTile> getProgressBar() {
        return progressBar;
    }

    /* ============================ 配方逻辑 ============================ */

    private boolean canProcess(EnergizedMatterFusionMachineTile tile) {
        if (level == null || level.isClientSide) {
            return false;
        }
        FusionRecipe recipe = findRecipe();
        if (recipe == null) {
            return false;
        }
        if (progressBar.getMaxProgress() != recipe.duration()) {
            progressBar.setMaxProgress(recipe.duration());
        }
        if (getEnergyStorage().getEnergyStored() < energyCost(recipe)) {
            return false;
        }
        return hasCharge(effectiveChargePerTick()) && canAcceptResult(recipe);
    }

    /** 本机每刻的耗电：配方基准值 × 本机倍率，再乘插件加成。 */
    private int energyCost(FusionRecipe recipe) {
        return effectiveEnergyPerTick(recipe.energyPerTick() * ENERGY_MULTIPLIER);
    }

    private boolean canAcceptResult(FusionRecipe recipe) {
        ItemStack result = recipe.result();
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

    /** 每刻同时扣电与扣充能，两者缺一机器都不会推进进度；两个数值都已计入本机与插件的加成。 */
    private void consumeEnergy() {
        FusionRecipe recipe = findRecipe();
        if (recipe == null) {
            return;
        }
        PluginEnergyStorage<EnergizedMatterFusionMachineTile> storage =
                (PluginEnergyStorage<EnergizedMatterFusionMachineTile>) getEnergyStorage();
        storage.setEnergyStored(storage.getEnergyStored() - energyCost(recipe));
        consumeCharge(effectiveChargePerTick());
    }

    private void finishCraft() {
        if (level == null) {
            return;
        }
        FusionRecipe recipe = findRecipe();
        if (recipe == null) {
            return;
        }
        ItemStack result = recipe.getResultItem(level.registryAccess());
        if (!outputInventory.insertItem(0, result.copy(), true).isEmpty()) {
            return;
        }
        outputInventory.insertItem(0, result.copy(), false);
        int[] assignment = recipe.resolveSlots(inputStacks());
        if (assignment != null) {
            // 每种材料按配方声明的数量扣除，指派顺序即材料下标
            for (int material = 0; material < assignment.length; material++) {
                inputInventory.extractItem(assignment[material], recipe.countFor(material), false);
            }
        }
        markForUpdate();
    }

    private boolean hasRecipe() {
        return findRecipe() != null;
    }

    private boolean isFinished() {
        return progressBar.getProgress() >= progressBar.getMaxProgress();
    }

    /** 三个输入槽的当前内容，顺序与槽位序号一致。 */
    private List<ItemStack> inputStacks() {
        List<ItemStack> stacks = new ArrayList<>(INPUT_SLOTS);
        for (int slot = 0; slot < INPUT_SLOTS; slot++) {
            stacks.add(inputInventory.getStackInSlot(slot));
        }
        return stacks;
    }

    private FusionRecipe findRecipe() {
        if (level == null || level.isClientSide) {
            return null;
        }
        List<ItemStack> stacks = inputStacks();
        FusionRecipeInput input = new FusionRecipeInput(stacks);
        if (stacks.stream().allMatch(ItemStack::isEmpty)) {
            cachedRecipe = null;
            return null;
        }
        if (cachedRecipe != null && cachedRecipe.matches(input, level)) {
            return cachedRecipe;
        }
        cachedRecipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.FUSION_TYPE.get(), input, level)
                .map(RecipeHolder::value)
                .orElse(null);
        return cachedRecipe;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        addFacingLabels(inputInventory, outputInventory);
    }
}
