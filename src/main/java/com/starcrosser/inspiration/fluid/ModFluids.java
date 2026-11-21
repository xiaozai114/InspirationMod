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
        public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_INSPIRATION_CHARGE = FLUIDS.register(
                "flowing_inspiration_charge",
                () -> new InspirationChargeFluid.Flowing(InspirationChargeFluid.PROPERTIES)
        );
        public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> INSPIRATION_CHARGE = FLUIDS.register(
                "inspiration_charge",
                () -> new InspirationChargeFluid.Source(InspirationChargeFluid.PROPERTIES)
        );
        public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_MOLTEN_PLASMA = FLUIDS.register(
                "flowing_molten_plasma",
                () -> new MoltenPlasma.Flowing(MoltenPlasma.PROPERTIES)
        );
        public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> MOLTEN_PLASMA = FLUIDS.register(
                "molten_plasma",
                () -> new MoltenPlasma.Source(MoltenPlasma.PROPERTIES)
        );
        public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_NUTRIENT_FLUID = FLUIDS.register(
                "flowing_nutrient_fluid",
                () -> new NutrientFluid.Flowing(NutrientFluid.PROPERTIES)
        );
        public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> NUTRIENT_FLUID = FLUIDS.register(
                "nutrient_fluid",
                () -> new NutrientFluid.Source(NutrientFluid.PROPERTIES)
        );
    }

    public static class FluidsTypes {
        public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, MODID);
        public static final DeferredHolder<FluidType, MindFluidType> MIND_FLUID_TYPE = FLUID_TYPES.register(
                "mind_fluid",
                MindFluidType::new
        );
        public static final DeferredHolder<FluidType, InspirationChargeFluidType> INSPIRATION_CHARGE_TYPE =
                FLUID_TYPES.register(
                        "inspiration_charge",
                        InspirationChargeFluidType::new
                );
        public static final DeferredHolder<FluidType, MoltenPlasmaFluidType> MOLTEN_PLASMA_TYPE =
                FLUID_TYPES.register(
                        "molten_plasma",
                        MoltenPlasmaFluidType::new
                );
        public static final DeferredHolder<FluidType, NutrientFluidType> NUTRIENT_FLUID_TYPE =
                FLUID_TYPES.register(
                        "nutrient_fluid",
                        NutrientFluidType::new
                );
    }
    
    public static void register(IEventBus bus) {
        Fluids.FLUIDS.register(bus);
        FluidsTypes.FLUID_TYPES.register(bus);
    }
}
