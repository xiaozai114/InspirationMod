package com.starcrosser.inspiration.fluid;

import com.starcrosser.inspiration.item.ModItems;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

/**
 * 液态无恒合金：无恒合金锭熔出来的熔融金属，零点能量增幅器的装配原料。
 * <p>
 * 与液态等离子同一路数：没有流体方块（熔融金属不浇筑成块），但保留桶，
 * 否则装配台的流体原料没法装进机器。
 */
public abstract class MoltenEphemeralAlloy extends BaseFlowingFluid {

    public static final Properties PROPERTIES = new Properties(
            ModFluids.FluidsTypes.MOLTEN_EPHEMERAL_ALLOY_TYPE,
            ModFluids.Fluids.MOLTEN_EPHEMERAL_ALLOY,
            ModFluids.Fluids.FLOWING_MOLTEN_EPHEMERAL_ALLOY
    ).bucket(ModItems.MOLTEN_EPHEMERAL_ALLOY_BUCKET);

    public MoltenEphemeralAlloy() {
        super(PROPERTIES);
    }
}
