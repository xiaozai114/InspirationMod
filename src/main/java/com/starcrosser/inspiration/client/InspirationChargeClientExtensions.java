package com.starcrosser.inspiration.client;

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;

/**
 * 灵感充能的客户端表现：指定静止与流动两张贴图。
 */
public class InspirationChargeClientExtensions implements IClientFluidTypeExtensions {

    private static final ResourceLocation STILL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "block/inspiration_charge_still");
    private static final ResourceLocation FLOWING_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "block/inspiration_charge_flowing");

    @Override
    public @NotNull ResourceLocation getStillTexture() {
        return STILL_TEXTURE;
    }

    @Override
    public @NotNull ResourceLocation getFlowingTexture() {
        return FLOWING_TEXTURE;
    }
}
