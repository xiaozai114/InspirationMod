package com.starcrosser.inspiration.fluid;

import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * 通用熔融金属的流体类型：参数与液态等离子同档，颜色由 {@link #tint()} 决定。
 * <p>
 * 贴图用的是白色纹理 + 这里给的染色值，所以一条新熔融金属不需要新画图；
 * 如果材料目录下已经有同名贴图（{@code block/molten_<base>_still.png}），染色保持白色，
 * 贴图原样显示 —— 已经在用的美术资源不会被覆盖。
 */
public class MoltenMetalFluidType extends FluidType {

    private final int tint;

    public MoltenMetalFluidType(int tint) {
        super(FluidType.Properties.create()
                .density(3600)
                .viscosity(7200)
                .temperature(4000)
                .lightLevel(12)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA));
        this.tint = tint;
    }

    public int tint() {
        return tint;
    }
}
