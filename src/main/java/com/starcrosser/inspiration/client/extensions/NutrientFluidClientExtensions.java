package com.starcrosser.inspiration.client.extensions;

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;

public class NutrientFluidClientExtensions implements IClientFluidTypeExtensions {

    private static final ResourceLocation STILL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "block/nutrient_fluid_still");
    private static final ResourceLocation FLOWING_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "block/nutrient_fluid_flowing");

    @Override
    public @NotNull ResourceLocation getStillTexture() {
        return STILL_TEXTURE;
    }

    @Override
    public @NotNull ResourceLocation getFlowingTexture() {
        return FLOWING_TEXTURE;
    }
}
