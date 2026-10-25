package com.starcrosser.inspiration.network;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.player.InspirationValue;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncInspirationValuePayload(int value) implements CustomPacketPayload {

    public static final Type<SyncInspirationValuePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "sync_inspiration_value"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncInspirationValuePayload> STREAM_CODEC =
            StreamCodec.of((buffer, payload) -> buffer.writeVarInt(payload.value()),
                    buffer -> new SyncInspirationValuePayload(buffer.readVarInt()));

    @Override
    public Type<SyncInspirationValuePayload> type() {
        return TYPE;
    }

    public static void handle(SyncInspirationValuePayload payload, IPayloadContext context) {
        InspirationValue.setSynced(payload.value());
    }
}
