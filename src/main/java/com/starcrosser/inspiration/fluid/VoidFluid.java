package com.starcrosser.inspiration.fluid;

import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

final class VoidFluid {
    private VoidFluid() {}

    static BaseFlowingFluid.Properties properties() {
        return new BaseFlowingFluid.Properties(ModFluids.FluidsTypes.VOID_FLUID_TYPE,
                ModFluids.Fluids.VOID_FLUID, ModFluids.Fluids.FLOWING_VOID_FLUID)
                .bucket(ModItems.VOID_FLUID_BUCKET).block(ModBlocks.VOID_FLUID);
    }
}
