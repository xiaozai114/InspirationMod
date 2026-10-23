package com.starcrosser.inspiration.client.extensions;

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;

/** 液态等离子的贴图绑定：静止与流动两张，颜色走贴图本身（不额外染色）。 */
public class MoltenPlasmaClientExtensions implements IClientFluidTypeExtensions {

    private static final ResourceLocation STILL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "block/molten_plasma_still");
    private static final ResourceLocation FLOWING_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "block/molten_plasma_flowing");

    @Override
    public @NotNull ResourceLocation getStillTexture() {
        return STILL_TEXTURE;
    }

    @Override
    public @NotNull ResourceLocation getFlowingTexture() {
        return FLOWING_TEXTURE;
    }
}
