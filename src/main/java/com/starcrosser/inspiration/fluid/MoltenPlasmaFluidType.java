package com.starcrosser.inspiration.fluid;

import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * 液态等离子的流体类型：高温、高密度、发光。
 * <p>
 * 温度给到 4000（岩浆是 1300），用来表达「熔融金属」；亮度 12 让它在罐体里能照亮机器内部。
 */
public class MoltenPlasmaFluidType extends FluidType {

    public MoltenPlasmaFluidType() {
        super(FluidType.Properties.create()
                .density(3200)
                .viscosity(6000)
                .temperature(4000)
                .lightLevel(12)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
        );
    }
}
