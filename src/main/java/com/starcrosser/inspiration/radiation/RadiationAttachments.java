package com.starcrosser.inspiration.radiation;

import com.mojang.serialization.Codec;
import com.starcrosser.inspiration.Inspiration;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * 玩家身上的累积辐射值。
 * <p>
 * 存在 NeoForge 的实体 attachment 里：跟着玩家走、随玩家存档落盘，
 * 跨维度也不会丢（返回主世界的那次「克隆」由 {@code RadiationEvents} 显式搬运）。
 * <p>
 * 注意 {@code DeferredRegister} 必须调用 {@link #register(IEventBus)}，
 * 漏了注册不会报错，只会在第一次取值时抛「unbound value」。
 */
public final class RadiationAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Inspiration.MODID);

    public static final Supplier<AttachmentType<Integer>> RADIATION =
            ATTACHMENT_TYPES.register("radiation",
                    () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());

    private RadiationAttachments() {
    }

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }

    public static int get(Player player) {
        return player.getData(RADIATION.get());
    }

    /** 写入玩家辐射值，负数一律夹到 0。 */
    public static void set(Player player, int value) {
        player.setData(RADIATION.get(), Math.max(0, value));
    }

    public static void add(Player player, int delta) {
        set(player, get(player) + delta);
    }
}
