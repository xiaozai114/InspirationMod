package com.starcrosser.inspiration.network;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.player.InspirationValue;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ExtractInspirationPayload() implements CustomPacketPayload {

    public static final Type<ExtractInspirationPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "extract_inspiration"));

    public static final StreamCodec<ByteBuf, ExtractInspirationPayload> STREAM_CODEC =
            StreamCodec.unit(new ExtractInspirationPayload());

    @Override
    public Type<ExtractInspirationPayload> type() {
        return TYPE;
    }

    public static void handle(ExtractInspirationPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            InspirationValue.extract(player);
        }
    }
}
