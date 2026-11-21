package com.starcrosser.inspiration.client.extensions;

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;

/** 液态无恒合金的贴图绑定：静止与流动两张，颜色画在贴图里。 */
public class MoltenEphemeralAlloyClientExtensions implements IClientFluidTypeExtensions {

    private static final ResourceLocation STILL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "block/molten_ephemeral_alloy_still");
    private static final ResourceLocation FLOWING_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "block/molten_ephemeral_alloy_flowing");

    @Override
    public @NotNull ResourceLocation getStillTexture() {
        return STILL_TEXTURE;
    }

    @Override
    public @NotNull ResourceLocation getFlowingTexture() {
        return FLOWING_TEXTURE;
    }
}
