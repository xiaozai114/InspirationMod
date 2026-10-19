package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.machine.plugin.PluginEnergyStorage;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.machine.recipe.FusionRecipe;
import com.starcrosser.inspiration.machine.recipe.FusionRecipeInput;
import com.starcrosser.inspiration.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.List;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


/**
 * 物质融合器的方块实体。
 * <p>
 * 结构由 Titanium 的组件系统拼装：两个输入槽、一个输出槽、一个能量缓冲与一个进度条，
 * 灵感充能槽与充能罐由 {@link InspiredMachineTile} 提供。
 * GUI 由 Titanium 的 {@code BasicAddonScreen} 依据这些组件自动生成。
 */
public class MatterFusionMachineTile extends InspiredMachineTile<MatterFusionMachineTile> {

    public static final int ENERGY_CAPACITY = 60_000;
    public static final int MAX_ENERGY_INPUT = 1_000;

    /** 本机每运行一刻消耗的灵感充能（mB）。 */
    public static final int CHARGE_PER_TICK = 1;

    @Save
    private SidedInventoryComponent<MatterFusionMachineTile> inputInventory;
    @Save
    private SidedInventoryComponent<MatterFusionMachineTile> outputInventory;
    @Save
    private ProgressBarComponent<MatterFusionMachineTile> progressBar;

    private FusionRecipe cachedRecipe;

    public MatterFusionMachineTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<MatterFusionMachineTile>) ModMachines.MATTER_FUSION_MACHINE.getBlock(),
                ModMachines.MATTER_FUSION_MACHINE.type().get(), pos, state);

        SidedInventoryComponent<MatterFusionMachineTile> inputs =
                new SidedInventoryComponent<>("input", 44, 30, 2, 0);
        inputs.setRange(1, 2);
        inputs.setColor(DyeColor.LIGHT_BLUE);
        inputs.setFacingHandlerPos(8, 84);
        this.inputInventory = inputs;
        addInventory(this.inputInventory);

        SidedInventoryComponent<MatterFusionMachineTile> outputs =
                new SidedInventoryComponent<>("output", 116, 39, 1, 1);
        outputs.setRange(1, 1);
        outputs.setColor(DyeColor.ORANGE);
        outputs.setInputFilter((stack, slot) -> false);
        outputs.setFacingHandlerPos(26, 84);
        this.outputInventory = outputs;
        addInventory(this.outputInventory);

        ProgressBarComponent<MatterFusionMachineTile> bar =
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
    protected PluginEnergyStorage<MatterFusionMachineTile> createEnergyStorage() {
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
    public MatterFusionMachineTile getSelf() {
        return this;
    }

    @Override
    public ProgressBarComponent<MatterFusionMachineTile> getProgressBar() {
        return progressBar;
    }

    /* ============================ 配方逻辑 ============================ */

    private boolean canProcess(MatterFusionMachineTile tile) {
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
        if (getEnergyStorage().getEnergyStored() < effectiveEnergyPerTick(recipe.energyPerTick())) {
            return false;
        }
        return hasCharge(effectiveChargePerTick()) && canAcceptResult(recipe);
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

    /** 每刻同时扣电与扣充能，两者缺一机器都不会推进进度；两个数值都已计入插件加成。 */
    private void consumeEnergy() {
        FusionRecipe recipe = findRecipe();
        if (recipe == null) {
            return;
        }
        PluginEnergyStorage<MatterFusionMachineTile> storage =
                (PluginEnergyStorage<MatterFusionMachineTile>) getEnergyStorage();
        storage.setEnergyStored(storage.getEnergyStored() - effectiveEnergyPerTick(recipe.energyPerTick()));
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
        int[] assignment = recipe.resolveSlots(List.of(
                inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1)));
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

    private FusionRecipe findRecipe() {
        if (level == null || level.isClientSide) {
            return null;
        }
        FusionRecipeInput input = new FusionRecipeInput(List.of(
                inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1)));
        if (input.items().stream().allMatch(ItemStack::isEmpty)) {
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
