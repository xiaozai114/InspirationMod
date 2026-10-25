package com.starcrosser.inspiration.fluid;

import net.neoforged.neoforge.fluids.FluidType;

public class NutrientFluidType extends FluidType {

    public NutrientFluidType() {
        super(FluidType.Properties.create()
                .density(1200)
                .viscosity(1400)
                .temperature(300)
        );
    }
}
