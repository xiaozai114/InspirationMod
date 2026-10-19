package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.block.BasicTileBlock;
import com.starcrosser.inspiration.alkali.Alkalinity;
import com.starcrosser.inspiration.machine.ModMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

/**
 * 碱性发电机：烧碱性物质发电。
 * <p>
 * 一份碱性物质烧 12,000 刻（10 分钟），每刻发 {@code 100 × 碱性值} FE，
 * 全程只消耗燃料、不消耗灵感充能，也没有副产物与辐射。
 * 碱性值走数据地图 {@code data_maps/item/alkalinity.json}，所以任何物品登记了就能烧。
 */
public class AlkalineReactorTile extends PowerGeneratorTile<AlkalineReactorTile> {

    /** 能量缓冲与每刻输出上限。 */
    public static final int ENERGY_CAPACITY = 1_000_000;
    public static final int MAX_EXTRACT = 8_000;

    /** 一份燃料烧多少刻。 */
    public static final int BURN_TICKS = 12_000;

    /** 每刻产电 = 100 × 碱性值。 */
    public static final int FE_PER_ALKALINITY = 100;

    public AlkalineReactorTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<AlkalineReactorTile>) ModMachines.ALKALINE_REACTOR.getBlock(),
                ModMachines.ALKALINE_REACTOR.type().get(), pos, state);
    }

    @Nonnull
    @Override
    public AlkalineReactorTile getSelf() {
        return this;
    }

    @Override
    protected boolean isFuel(ItemStack stack) {
        return Alkalinity.of(stack) > 0;
    }

    @Override
    protected int fuelValue(ItemStack stack) {
        return Alkalinity.of(stack);
    }

    @Override
    protected int burnTicks() {
        return BURN_TICKS;
    }

    @Override
    protected int energyPerTick(int value) {
        return FE_PER_ALKALINITY * value;
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
