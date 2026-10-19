package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.machine.plugin.PluginEnergyStorage;
import com.starcrosser.inspiration.machine.recipe.TwistingRecipe;
import com.starcrosser.inspiration.machine.recipe.TwistingRecipeInput;
import com.starcrosser.inspiration.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


/**
 * 矿物扭曲机的方块实体。
 * <p>
 * 只有一个输入槽：放入矿石，机器按配方声明的等效时运（默认 6）掷出本次产量，
 * 产出对应的粗矿。随机数取世界随机源，因此同一块矿石的产出数量每次都可能不同。
 */
public class MineralTwisterTile extends InspiredMachineTile<MineralTwisterTile> {

    /** 能量缓冲：与物质重塑机同级，但单次作业耗能更高。 */
    public static final int ENERGY_CAPACITY = 400_000;
    public static final int MAX_ENERGY_INPUT = 8_000;

    /** 本机每运行一刻消耗的灵感充能（mB），与物质重塑机同为翡翠钢机器的档位。 */
    public static final int CHARGE_PER_TICK = 2;

    @Save
    private SidedInventoryComponent<MineralTwisterTile> inputInventory;
    @Save
    private SidedInventoryComponent<MineralTwisterTile> outputInventory;
    @Save
    private ProgressBarComponent<MineralTwisterTile> progressBar;

    /** 缓存最近一次命中的配方，避免每刻重复查表。 */
    private TwistingRecipe cachedRecipe;
    private ItemStack cachedInput = ItemStack.EMPTY;

    public MineralTwisterTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<MineralTwisterTile>) ModMachines.MINERAL_TWISTER.getBlock(),
                ModMachines.MINERAL_TWISTER.type().get(), pos, state);

        SidedInventoryComponent<MineralTwisterTile> inputs =
                new SidedInventoryComponent<>("input", 44, 30, 1, 0);
        inputs.setRange(1, 1);
        inputs.setColor(DyeColor.GREEN);
        inputs.setFacingHandlerPos(8, 84);
        this.inputInventory = inputs;
        addInventory(this.inputInventory);

        SidedInventoryComponent<MineralTwisterTile> outputs =
                new SidedInventoryComponent<>("output", 116, 39, 1, 1);
        outputs.setRange(1, 1);
        outputs.setColor(DyeColor.LIME);
        outputs.setInputFilter((stack, slot) -> false);
        outputs.setFacingHandlerPos(26, 84);
        this.outputInventory = outputs;
        addInventory(this.outputInventory);

        ProgressBarComponent<MineralTwisterTile> bar =
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
    protected PluginEnergyStorage<MineralTwisterTile> createEnergyStorage() {
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
    public MineralTwisterTile getSelf() {
        return this;
    }

    @Override
    public ProgressBarComponent<MineralTwisterTile> getProgressBar() {
        return progressBar;
    }

    /* ============================ 配方逻辑 ============================ */

    private boolean canProcess(MineralTwisterTile tile) {
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
        return getEnergyStorage().getEnergyStored() >= effectiveEnergyPerTick(recipe.energyPerTick())
                && hasCharge(effectiveChargePerTick())
                && canAcceptResult(recipe);
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
        int limit = Math.min(outputInventory.getSlotLimit(0), recipe.result().getMaxStackSize());
        return current.getCount() + recipe.maxResultCount() <= limit;
    }

    private void consumeEnergy() {
        TwistingRecipe recipe = findRecipe();
        if (recipe == null) {
            return;
        }
        PluginEnergyStorage<MineralTwisterTile> storage =
                (PluginEnergyStorage<MineralTwisterTile>) getEnergyStorage();
        storage.setEnergyStored(storage.getEnergyStored() - effectiveEnergyPerTick(recipe.energyPerTick()));
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
        result.setCount(recipe.rollResultCount(level.random));
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
