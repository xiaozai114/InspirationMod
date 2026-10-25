package com.starcrosser.inspiration.network;

import com.starcrosser.inspiration.Inspiration;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Inspiration.MODID)
public final class ModPayloads {

    private ModPayloads() {
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(ExtractInspirationPayload.TYPE, ExtractInspirationPayload.STREAM_CODEC,
                ExtractInspirationPayload::handle);
        registrar.playToClient(SyncInspirationValuePayload.TYPE, SyncInspirationValuePayload.STREAM_CODEC,
                SyncInspirationValuePayload::handle);
    }
}
