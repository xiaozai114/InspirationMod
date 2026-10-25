package com.starcrosser.inspiration.player;

import com.mojang.serialization.Codec;
import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.network.SyncInspirationValuePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class InspirationValue {

    public static final int MAX = 1000;
    public static final int EXTRACT_COST = 100;

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Inspiration.MODID);

    public static final Supplier<AttachmentType<Integer>> VALUE =
            ATTACHMENT_TYPES.register("inspiration_value",
                    () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());

    /** 客户端缓存：由 {@code SyncInspirationValuePayload} 写，只给界面读。 */
    private static int synced;

    private InspirationValue() {
    }

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

    public static int get(Player player) {
        return player.getData(VALUE.get());
    }

    public static void set(Player player, int value) {
        int clamped = Mth.clamp(value, 0, MAX);
        player.setData(VALUE.get(), clamped);
        sync(player);
    }

    public static void add(Player player, int delta) {
        set(player, get(player) + delta);
    }

    public static void sync(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new SyncInspirationValuePayload(get(player)));
        }
    }

    public static int synced() {
        return synced;
    }

    public static void setSynced(int value) {
        synced = value;
    }

    public static boolean extract(ServerPlayer player) {
        if (get(player) < EXTRACT_COST) {
            return false;
        }
        set(player, get(player) - EXTRACT_COST);
        ItemStack stack = new ItemStack(ModItems.INSPIRATION.get());
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        return true;
    }
}
