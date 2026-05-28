// 文件名: client/MindFluidClientExtensions.java
package com.starcrosser.inspiration.client; // 请替换为你的实际包名

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;

public class MindFluidClientExtensions implements IClientFluidTypeExtensions {

    // 纹理路径定义
    private static final ResourceLocation STILL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "block/mind_fluid_still");
    private static final ResourceLocation FLOWING_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "block/mind_fluid_flowing");

    @Override
    public @NotNull ResourceLocation getStillTexture() {
        return STILL_TEXTURE;
    }

    @Override
    public @NotNull ResourceLocation getFlowingTexture() {
        return FLOWING_TEXTURE;
    }

    // 可选：自定义颜色，默认是白色 (0xFFFFFFFF)
    // @Override
    // public int getTintColor() {
    //     return 0xFF00FFFF; // 示例：青色
    // }
}