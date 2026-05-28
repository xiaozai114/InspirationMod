// client/ClientExtensionsHandler.java
package com.starcrosser.inspiration.client;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.fluid.ModFluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;


@EventBusSubscriber(modid = Inspiration.MODID, value = Dist.CLIENT)
public class ClientExtensionsHandler {

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        // 创建扩展实例
        MindFluidClientExtensions extensions = new MindFluidClientExtensions();
        // 将扩展绑定到你的流体类型
        event.registerFluidType(extensions, ModFluids.FluidsTypes.MIND_FLUID_TYPE.get());
    }
}