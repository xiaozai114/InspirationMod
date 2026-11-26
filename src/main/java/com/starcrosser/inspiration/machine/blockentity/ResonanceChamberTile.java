package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.PoweredTile;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.client.util.FacingLabels;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.machine.recipe.ResonanceRecipe;
import com.starcrosser.inspiration.recipe.ModRecipes;
import com.starcrosser.inspiration.voidenergy.VoidEnergyNetwork;
import com.starcrosser.inspiration.voidenergy.entity.VoidClusterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class ResonanceChamberTile extends PoweredTile<ResonanceChamberTile> {
    @Save private SidedInventoryComponent<ResonanceChamberTile> input;
    @Save private SidedInventoryComponent<ResonanceChamberTile> output;
    @Save private ProgressBarComponent<ResonanceChamberTile> progress;

    // One item is reserved on the first paid tick. The rest of the input stays accessible.
    private ItemStack workpiece = ItemStack.EMPTY;
    private ItemStack pendingResult = ItemStack.EMPTY;
    private int powerPerTick;

    public ResonanceChamberTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<ResonanceChamberTile>) ModMachines.RESONANCE_CHAMBER.getBlock(),
                ModMachines.RESONANCE_CHAMBER.type().get(), pos, state);
        input = new SidedInventoryComponent<>("input", 44, 39, 1, 0);
        input.setRange(1, 1);
        input.setColor(DyeColor.YELLOW);
        input.setFacingHandlerPos(8, 84);
        addInventory(input);
        output = new SidedInventoryComponent<>("output", 116, 39, 1, 1);
        output.setRange(1, 1);
        output.setInputFilter((stack, slot) -> false);
        output.setColor(DyeColor.PURPLE);
        output.setFacingHandlerPos(26, 84);
        addInventory(output);
        progress = new ProgressBarComponent<>(78, 38, ResonanceRecipe.DURATION);
        progress.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        progress.setColor(DyeColor.PURPLE);
        // The server advances this bar only after both energy costs have been paid.
        progress.setCanIncrease(tile -> false);
        progress.setCanReset(tile -> false);
        addProgressBar(progress);
    }

    @Nonnull
    @Override
    protected EnergyStorageComponent<ResonanceChamberTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(200_000, 10_000, 0, 8, 14);
    }

    @Nonnull
    @Override
    public ResonanceChamberTile getSelf() {
        return this;
    }

    @Override
    public ItemInteractionResult onActivated(Player player, InteractionHand hand, Direction facing,
                                            double hitX, double hitY, double hitZ) {
        if (level != null && !level.isClientSide) {
            openGui(player);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, ResonanceChamberTile self) {
        super.serverTick(level, pos, state, self);
        ResonanceRecipe start = null;
        if (workpiece.isEmpty()) {
            ItemStack stack = input.getStackInSlot(0);
            if (stack.isEmpty()) {
                return;
            }
            start = level.getRecipeManager().getRecipeFor(ModRecipes.RESONANCE_TYPE.get(),
                    new SingleRecipeInput(stack), level).map(RecipeHolder::value).orElse(null);
            if (start == null || getEnergyStorage().getEnergyStored() < start.energyPerTick()
                    || !output.insertItem(0, start.result().copy(), true).isEmpty()) {
                return;
            }
        }
        VoidClusterEntity source = VoidEnergyNetwork.findCluster(level, pos, ResonanceRecipe.VE_PER_TICK);
        if (source == null) {
            if (!workpiece.isEmpty()) {
                releaseWorkpiece();
            }
            return;
        }
        if (start != null) {
            workpiece = input.extractItem(0, 1, false);
            pendingResult = start.result().copy();
            powerPerTick = start.energyPerTick();
            progress.setMaxProgress(start.duration());
        }
        if (getEnergyStorage().getEnergyStored() < powerPerTick
                || !output.insertItem(0, pendingResult.copy(), true).isEmpty()) {
            return;
        }
        source.addEnergy(-ResonanceRecipe.VE_PER_TICK);
        getEnergyStorage().setEnergyStored(getEnergyStorage().getEnergyStored() - powerPerTick);
        progress.setProgress(progress.getProgress() + 1);
        if (progress.getProgress() >= progress.getMaxProgress()) {
            output.insertItem(0, pendingResult, false);
            releaseWorkpiece();
        }
        setChanged();
    }

    public ItemStack releaseWorkpiece() {
        ItemStack item = workpiece;
        workpiece = ItemStack.EMPTY;
        pendingResult = ItemStack.EMPTY;
        powerPerTick = 0;
        progress.setProgress(0);
        setChanged();
        return item;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!workpiece.isEmpty()) {
            tag.put("Workpiece", workpiece.save(registries));
            tag.put("ResonanceResult", pendingResult.save(registries));
            tag.putInt("ResonancePower", powerPerTick);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        workpiece = ItemStack.parseOptional(registries, tag.getCompound("Workpiece"));
        pendingResult = ItemStack.parseOptional(registries, tag.getCompound("ResonanceResult"));
        powerPerTick = tag.getInt("ResonancePower");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        FacingLabels.add(this, input, output);
    }
}
