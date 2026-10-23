package com.starcrosser.inspiration.fluid;

import com.starcrosser.inspiration.item.ModItems;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

/**
 * 液态等离子：等离子锭熔出来的熔融金属，流体提取机的产物。
 * <p>
 * 与思维流体不同，它**没有流体方块** —— 熔融金属不浇筑成块，只在机器罐体里流转；
 * 但保留桶，因为灵感装配台要用它当流体原料，没有容器就装不进去。
 */
public abstract class MoltenPlasma extends BaseFlowingFluid {

    public static final Properties PROPERTIES = new Properties(
            ModFluids.FluidsTypes.MOLTEN_PLASMA_TYPE,
            ModFluids.Fluids.MOLTEN_PLASMA,
            ModFluids.Fluids.FLOWING_MOLTEN_PLASMA
    ).bucket(ModItems.MOLTEN_PLASMA_BUCKET);

    public MoltenPlasma() {
        super(PROPERTIES);
    }
}
