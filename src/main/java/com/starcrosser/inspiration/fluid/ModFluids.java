package com.starcrosser.inspiration.fluid;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static com.starcrosser.inspiration.Inspiration.MODID;

public class ModFluids {
    public static class Fluids  {
        public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, MODID);
        public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_MIND_FLUID = FLUIDS.register(
                "flowing_mind_fluid",
                () -> new MindFluid.Flowing(MindFluid.PROPERTIES)
        );
        public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> MIND_FLUID = FLUIDS.register(
                "mind_fluid",
                () -> new MindFluid.Source(MindFluid.PROPERTIES)
        );
    }

    public static class FluidsTypes {
        public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, MODID);
        public static final DeferredHolder<FluidType, MindFluidType> MIND_FLUID_TYPE = FLUID_TYPES.register(
                "mind_fluid",
                MindFluidType::new
        );
    }
    
    public static void register(IEventBus bus) {
        Fluids.FLUIDS.register(bus);
        FluidsTypes.FLUID_TYPES.register(bus);
    }
}
