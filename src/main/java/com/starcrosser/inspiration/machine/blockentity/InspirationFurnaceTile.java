package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.machine.plugin.PluginEnergyStorage;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

public class InspirationFurnaceTile extends BatchProcessingTile<InspirationFurnaceTile> {

    public static final int BASE_ENERGY_CAPACITY = 40_000;
    public static final int BASE_MAX_ENERGY_INPUT = 1_000;
    public static final int BASE_CHARGE_PER_TICK = 1;
    public static final int BASE_ENERGY_PER_TICK = 10;
    public static final int DURATION = 100;

    private static final List<RecipeType<? extends AbstractCookingRecipe>> TYPES = List.of(
            RecipeType.SMELTING, RecipeType.BLASTING, RecipeType.SMOKING);

    @Save
    private SidedInventoryComponent<InspirationFurnaceTile> inputInventory;
    @Save
    private SidedInventoryComponent<InspirationFurnaceTile> outputInventory;
    @Save
    private ProgressBarComponent<InspirationFurnaceTile> progressBar;

    private AbstractCookingRecipe cachedRecipe;
    private ItemStack cachedInput = ItemStack.EMPTY;

    public InspirationFurnaceTile(MachineSpec spec, BasicTileBlock<InspirationFurnaceTile> block,
                                  BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(spec, block, type, pos, state);

        SidedInventoryComponent<InspirationFurnaceTile> input = new SidedInventoryComponent<>("input", 44, 30, 1, 0);
        input.setRange(1, 1);
        input.setColor(DyeColor.LIME);
        input.setFacingHandlerPos(8, 84);
        this.inputInventory = input;
        addInventory(this.inputInventory);

        SidedInventoryComponent<InspirationFurnaceTile> output = new SidedInventoryComponent<>("output", 116, 39, 1, 1);
        output.setRange(1, 1);
        output.setColor(DyeColor.LIME);
        output.setFacingHandlerPos(26, 84);
        this.outputInventory = output;
        addInventory(this.outputInventory);

        ProgressBarComponent<InspirationFurnaceTile> bar = new ProgressBarComponent<>(78, 38, DURATION);
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        bar.setColor(spec.barColor());
        bar.setCanIncrease(this::canProcess);
        bar.setCanReset(tile -> tile.isFinished() || tile.findRecipe() == null);
        bar.setOnTickWork(this::consumeBatchEnergy);
        bar.setOnFinishWork(this::finishCraft);
        this.progressBar = bar;
        addProgressBar(this.progressBar);
    }

    @Nonnull
    @Override
    protected PluginEnergyStorage<InspirationFurnaceTile> createEnergyStorage() {
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
    public InspirationFurnaceTile getSelf() {
        return this;
    }

    @Override
    public ProgressBarComponent<InspirationFurnaceTile> getProgressBar() {
        return progressBar;
    }

    private boolean canProcess(InspirationFurnaceTile tile) {
        if (level == null || level.isClientSide) {
            return false;
        }
        AbstractCookingRecipe recipe = findRecipe();
        if (recipe == null) {
            return false;
        }
        return prepareBatch(DURATION, BASE_ENERGY_PER_TICK,
                n -> inputInventory.getStackInSlot(0).getCount() >= n
                        && fitsOutput(outputInventory, 0, resultOf(recipe), n));
    }

    private ItemStack resultOf(AbstractCookingRecipe recipe) {
        return recipe.getResultItem(level.registryAccess());
    }

    private void finishCraft() {
        AbstractCookingRecipe recipe = findRecipe();
        if (recipe == null || !fitsOutput(outputInventory, 0, resultOf(recipe), batchSize())) {
            return;
        }
        outputInventory.insertItem(0, batchOutput(resultOf(recipe)), false);
        inputInventory.extractItem(0, batchSize(), false);
        invalidateCache();
        markForUpdate();
    }

    private boolean isFinished() {
        return progressBar.getProgress() >= progressBar.getMaxProgress();
    }

    private AbstractCookingRecipe findRecipe() {
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
        cachedRecipe = null;
        SingleRecipeInput recipeInput = new SingleRecipeInput(input);
        for (RecipeType<? extends AbstractCookingRecipe> type : TYPES) {
            var holder = level.getRecipeManager().getRecipeFor(type, recipeInput, level);
            if (holder.isPresent()) {
                cachedRecipe = trackRecipe(holder.get());
                break;
            }
        }
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
