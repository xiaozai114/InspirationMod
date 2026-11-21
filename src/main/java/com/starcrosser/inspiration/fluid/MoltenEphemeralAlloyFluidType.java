package com.starcrosser.inspiration.fluid;

import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * 液态无恒合金的流体类型：比等离子略「冷」、更稠，亮度低一档（无恒合金本身不发光）。
 */
public class MoltenEphemeralAlloyFluidType extends FluidType {

    public MoltenEphemeralAlloyFluidType() {
        super(FluidType.Properties.create()
                .density(3600)
                .viscosity(7200)
                .temperature(3400)
                .lightLevel(9)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
        );
    }
}
