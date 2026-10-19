package com.starcrosser.inspiration.fluid;

import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * 灵感充能的流体类型：密度与黏度偏高，仅用于机器内部流转。
 */
public class InspirationChargeFluidType extends FluidType {

    public InspirationChargeFluidType() {
        super(FluidType.Properties.create()
                .density(1600)
                .viscosity(1600)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
        );
    }
}
