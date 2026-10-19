package com.starcrosser.inspiration.fluid;

import net.neoforged.neoforge.fluids.BaseFlowingFluid;

/**
 * 灵感充能。
 * <p>
 * 与 {@link MindFluid} 不同，它只存在于机器的罐体之中：既没有对应的流体方块，也没有桶，
 * 因此 {@code Properties} 里不设置 {@code block} 与 {@code bucket}。
 * 唯一的获取方式是把“灵感”放进机器的充能槽，或用管道从别处泵入。
 */
public abstract class InspirationChargeFluid extends BaseFlowingFluid {

    public static final Properties PROPERTIES = new Properties(
            ModFluids.FluidsTypes.INSPIRATION_CHARGE_TYPE,
            ModFluids.Fluids.INSPIRATION_CHARGE,
            ModFluids.Fluids.FLOWING_INSPIRATION_CHARGE
    );

    public InspirationChargeFluid() {
        super(PROPERTIES);
    }
}
