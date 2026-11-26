package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.block.BasicTileBlock;
import com.starcrosser.inspiration.machine.ModMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

/** Burns furnace fuels using their own burn time and sends FE to adjacent blocks. */
public class CombustionGeneratorTile extends PowerGeneratorTile<CombustionGeneratorTile> {
    private static final int ENERGY_CAPACITY = 32_000;
    private static final int MAX_EXTRACT = 160;
    public static final int FE_PER_TICK = 40;

    public CombustionGeneratorTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<CombustionGeneratorTile>) ModMachines.COMBUSTION_GENERATOR.getBlock(),
                ModMachines.COMBUSTION_GENERATOR.type().get(), pos, state);
    }

    @Nonnull
    @Override
    public CombustionGeneratorTile getSelf() {
        return this;
    }

    @Override
    protected boolean isFuel(ItemStack stack) {
        return fuelValue(stack) > 0;
    }

    @Override
    protected int fuelValue(ItemStack stack) {
        return stack.getBurnTime(RecipeType.SMELTING);
    }

    @Override
    protected ItemStack fuelRemainder(ItemStack used) {
        return used.getCraftingRemainingItem();
    }

    @Override
    protected int burnTicks(int fuelValue) {
        return fuelValue;
    }

    @Override
    protected int energyPerTick(int value) {
        return FE_PER_TICK;
    }

    @Override
    protected int baseEnergyCapacity() {
        return ENERGY_CAPACITY;
    }

    @Override
    protected int baseMaxExtract() {
        return MAX_EXTRACT;
    }
}
