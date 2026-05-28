package com.starcrosser.inspiration.fluid;

import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class MindFluid extends BaseFlowingFluid {
    public static final Properties PROPERTIES = new Properties(
            ModFluids.FluidsTypes.MIND_FLUID_TYPE,
            ModFluids.Fluids.MIND_FLUID,
            ModFluids.Fluids.FLOWING_MIND_FLUID
    )
            .block(ModBlocks.MIND_FLUID_BLOCK)
            .explosionResistance(120.0f)
            .bucket(ModItems.MIND_FLUID_BUCKET);
    public MindFluid() {super(PROPERTIES);}
}
