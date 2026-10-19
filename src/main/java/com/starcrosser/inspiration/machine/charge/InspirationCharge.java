package com.starcrosser.inspiration.machine.charge;

import com.starcrosser.inspiration.fluid.ModFluids;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * 灵感充能：机器除电力之外的第二种消耗品。
 * <p>
 * 灵感充能是一种只存在于机器罐体中的流体，由“灵感”物品按
 * {@link #MB_PER_ITEM} 的比例转化而来，机器每运行一刻按自身速率扣除。
 */
public final class InspirationCharge {

    /** 单个「灵感」可转化的充能量（mB）。 */
    public static final int MB_PER_ITEM = 1_000;

    /** 单个「灵感碎片」可转化的充能量（mB）——灵感的零碎形态，只有十分之一。 */
    public static final int MB_PER_SHARD = 100;

    /** 机器内部罐体的容量（mB）。 */
    public static final int TANK_CAPACITY = 16_000;

    private InspirationCharge() {
    }

    public static Fluid fluid() {
        return ModFluids.Fluids.INSPIRATION_CHARGE.get();
    }

    public static FluidStack stack(int amount) {
        return new FluidStack(fluid(), amount);
    }

    /** 罐体只允许装灵感充能，避免管道把别的液体灌进来。 */
    public static boolean isChargeFluid(FluidStack stack) {
        return stack.getFluid() == fluid();
    }

    /**
     * 单个该物品可转化的充能量（mB）；不能作为充能来源时返回 0。
     * <p>
     * 新增可充能物品时只需在这里加一条分支，机器端的折算逻辑不必改动。
     */
    public static int chargeValue(ItemStack stack) {
        if (stack.is(ModItems.INSPIRATION.get())) {
            return MB_PER_ITEM;
        }
        if (stack.is(ModItems.INSPIRATION_SHARD.get())) {
            return MB_PER_SHARD;
        }
        return 0;
    }

    /** 可投入充能槽转化为灵感充能的物品。 */
    public static boolean isChargeSource(ItemStack stack) {
        return chargeValue(stack) > 0;
    }
}
