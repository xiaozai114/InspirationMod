package com.starcrosser.inspiration.fluid;

import com.starcrosser.inspiration.item.ModItems;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class NutrientFluid extends BaseFlowingFluid {

    public static final Properties PROPERTIES = new Properties(
            ModFluids.FluidsTypes.NUTRIENT_FLUID_TYPE,
            ModFluids.Fluids.NUTRIENT_FLUID,
            ModFluids.Fluids.FLOWING_NUTRIENT_FLUID
    ).bucket(ModItems.NUTRIENT_FLUID_BUCKET);

    public NutrientFluid() {
        super(PROPERTIES);
    }
}
