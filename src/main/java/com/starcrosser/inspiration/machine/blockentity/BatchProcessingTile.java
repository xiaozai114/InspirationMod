package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.block.BasicTileBlock;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import java.util.function.IntPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

/** Chooses and saves the number of recipes that share one progress bar. */
public abstract class BatchProcessingTile<T extends BatchProcessingTile<T>> extends InspiredMachineTile<T> {
    private int batches = 1;
    private ResourceLocation activeRecipe;
    private ResourceLocation selectedRecipe;
    private int tickEnergy;
    private int tickCharge;

    protected BatchProcessingTile(MachineSpec spec, BasicTileBlock<T> block, BlockEntityType<?> type,
                                  BlockPos pos, BlockState state) {
        super(spec, block, type, pos, state);
    }

    protected final <R extends Recipe<?>> R trackRecipe(RecipeHolder<R> holder) {
        selectedRecipe = holder.id();
        return holder.value();
    }

    protected final void selectRecipe(ResourceLocation id) {
        selectedRecipe = id;
    }

    protected final int batchSize() {
        return batches;
    }

    protected final boolean prepareBatch(int duration, int energy, IntPredicate fits) {
        var bar = getProgressBar();
        int limit = pluginEffects().parallel();
        boolean changedRecipe = activeRecipe != null && !activeRecipe.equals(selectedRecipe);
        if (bar.getProgress() == 0 || changedRecipe || batches > limit || !fits.test(batches)) {
            int next = limit;
            while (next > 0 && !fits.test(next)) {
                next--;
            }
            if (next == 0) {
                return false;
            }
            // A larger batch cannot inherit work paid for by a smaller one.
            if (changedRecipe || next != batches) {
                bar.setProgress(0);
            }
            batches = next;
        }
        activeRecipe = selectedRecipe;
        if (bar.getMaxProgress() != duration) {
            bar.setMaxProgress(duration);
        }
        long power = (long) effectiveEnergyPerTick(energy) * batches;
        long charge = (long) effectiveChargePerTick() * batches;
        if (power > getEnergyStorage().getEnergyStored() || charge > getChargeAmount()) {
            return false;
        }
        tickEnergy = (int) power;
        tickCharge = (int) charge;
        return true;
    }

    protected final void consumeBatchEnergy() {
        getEnergyStorage().setEnergyStored(getEnergyStorage().getEnergyStored() - tickEnergy);
        consumeCharge(tickCharge);
        setChanged();
    }

    protected static boolean fitsOutput(IItemHandler output, int slot, ItemStack stack, int count) {
        if (stack.isEmpty()) {
            return true;
        }
        ItemStack current = output.getStackInSlot(slot);
        int capacity = Math.min(output.getSlotLimit(slot), stack.getMaxStackSize());
        return (current.isEmpty() || ItemStack.isSameItemSameComponents(current, stack))
                && (long) stack.getCount() * count <= capacity - current.getCount();
    }

    protected final ItemStack batchOutput(ItemStack unit) {
        return unit.copyWithCount(unit.getCount() * batches);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("ParallelBatchSize", batches);
        if (activeRecipe != null) {
            tag.putString("ParallelRecipe", activeRecipe.toString());
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        batches = Math.max(1, tag.getInt("ParallelBatchSize"));
        activeRecipe = tag.contains("ParallelRecipe") ? ResourceLocation.tryParse(tag.getString("ParallelRecipe")) : null;
    }
}
