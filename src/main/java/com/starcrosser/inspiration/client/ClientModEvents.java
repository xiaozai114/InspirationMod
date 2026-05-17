package com.starcrosser.inspiration.client;

import com.starcrosser.inspiration.entity.ModEntities;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ClientModEvents {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 将原版 TNT 的渲染器绑定到我们的实体上
        event.registerEntityRenderer(
                ModEntities.INTENSIFIED_TNT.get(), // 获取我们注册的实体类型
                TntRenderer::new   // 使用原版 TNT 的渲染器
        );
    }
}
