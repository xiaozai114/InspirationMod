package com.starcrosser.inspiration.client;

import com.starcrosser.inspiration.client.renderer.EndAlloyShieldRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/**
 * 末地合金盾牌的客户端扩展：把物品渲染交给 {@link EndAlloyShieldRenderer}。
 * <p>
 * 什么时候会用到它由模型决定 —— 盾牌的模型是 {@code builtin/entity}（{@code isCustomRenderer()} 为 true），
 * 所以无论拿在手上、丢在地上还是摆在物品栏里，都走这个渲染器。
 */
public class EndAlloyShieldClientExtensions implements IClientItemExtensions {

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return EndAlloyShieldRenderer.get();
    }
}
