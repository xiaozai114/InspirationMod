package com.starcrosser.inspiration.client;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.client.guide.GuideBookScreen;
import com.starcrosser.inspiration.client.renderer.MatterBinderRenderer;
import com.starcrosser.inspiration.entity.ModEntities;
import com.starcrosser.inspiration.guide.ModGuideMenus;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.injection.blockentity.MatterBinderTile;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * 客户端渲染相关的注册。
 * <p>
 * 用 {@code Dist.CLIENT} 限定，服务端加载时才不会碰到这些客户端类。
 */
@EventBusSubscriber(modid = Inspiration.MODID, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 两种强化 TNT 都沿用原版 TNT 的外观，只是爆炸威力不同
        event.registerEntityRenderer(ModEntities.INTENSIFIED_TNT.get(), TntRenderer::new);
        event.registerEntityRenderer(ModEntities.EMPOWERED_TNT.get(), TntRenderer::new);

        // 物质束缚器上方悬浮的那份物质。
        // Titanium 的 type() 是无 wildcard 的 BlockEntityType<?>，这里要收窄到具体类型
        // 才能匹配 registerBlockEntityRenderer 的 BlockEntityType<? extends T>。
        @SuppressWarnings("unchecked")
        BlockEntityType<MatterBinderTile> binderType =
                (BlockEntityType<MatterBinderTile>) ModInjection.MATTER_BINDER.type().get();
        event.registerBlockEntityRenderer(binderType, MatterBinderRenderer::new);
    }

    /** 指导书的界面：菜单只负责开门，内容全在这个 Screen 里。 */
    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModGuideMenus.GUIDE_BOOK.get(), GuideBookScreen::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 举盾时换成另一套 display 变换（就是原版盾牌那套「举到身前」的姿态）。
        // 原版把 blocking 属性注册在 Items.SHIELD 上，自定义盾牌要自己来一份。
        event.enqueueWork(() -> ItemProperties.register(
                ModItems.END_ALLOY_SHIELD.get(),
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "blocking"),
                (stack, level, entity, seed) ->
                        entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F));
    }
}
