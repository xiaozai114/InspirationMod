package com.starcrosser.inspiration.client.extensions;

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;

/**
 * 所有熔融金属共用的贴图绑定：按流体 id 找 {@code block/<fluidId>_still} 与 {@code _flowing}。
 * <p>
 * 贴图缺失时由 {@code tools/textures/gen_molten_metals.py} 用通用纹理染色补出来，
 * 已经有专属贴图的材料不会被覆盖，也不会额外染色。
 */
public class MoltenMetalClientExtensions implements IClientFluidTypeExtensions {

    private final ResourceLocation still;
    private final ResourceLocation flowing;

    public MoltenMetalClientExtensions(String fluidId) {
        this.still = ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "block/" + fluidId + "_still");
        this.flowing = ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "block/" + fluidId + "_flowing");
    }

    @Override
    public @NotNull ResourceLocation getStillTexture() {
        return still;
    }

    @Override
    public @NotNull ResourceLocation getFlowingTexture() {
        return flowing;
    }
}
