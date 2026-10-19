package com.starcrosser.inspiration.radiation;

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

/**
 * 辐射机制的事件入口：维护方块索引、搬运/减半玩家辐射、把治疗换算成辐射衰减。
 * <p>
 * 索引维护走事件而不是每刻全图扫描：放置/破坏是增量，区块加载进队列等待扫描，
 * 区块卸载直接丢弃 —— 这些位置本来就不该再参与结算。
 */
@EventBusSubscriber(modid = Inspiration.MODID)
public final class RadiationEvents {

    /** 死亡后辐射减半的分母。 */
    private static final int DEATH_PENALTY_DIVISOR = 2;

    private RadiationEvents() {
    }

    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        int value = Radiation.of(event.getPlacedBlock());
        RadiationBlockTracker tracker = RadiationBlockTracker.get(level);
        if (value > 0) {
            tracker.put(event.getPos(), value);
        } else {
            tracker.forget(event.getPos());
        }
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            RadiationBlockTracker.get(level).forget(event.getPos());
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level && event.getChunk() instanceof LevelChunk chunk) {
            RadiationBlockTracker.get(level).queueChunk(chunk.getPos());
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            RadiationBlockTracker.get(level).forgetChunk(event.getChunk().getPos());
        }
    }

    /**
     * 玩家克隆：死亡时辐射减半，其余情况（从末地返回）原样搬运。
     * <p>
     * attachment 默认不随死亡拷贝，所以这里必须显式搬运一次。
     */
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player respawned = event.getEntity();
        if (original.level().isClientSide) {
            return;
        }
        int carried = RadiationAttachments.get(original);
        RadiationAttachments.set(respawned,
                event.isWasDeath() ? carried / DEATH_PENALTY_DIVISOR : carried);
    }

    /**
     * 100K 以上：本该回复的生命值按 1:10 换成玩家身上辐射值的衰减，
     * 也就是说重辐射状态下自然回复不再回血，而是「冲洗」掉一部分辐射。
     */
    @SubscribeEvent
    public static void onHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }
        int radiation = RadiationAttachments.get(player);
        if (!RadiationEffects.convertsHealing(radiation)) {
            return;
        }
        event.setCanceled(true);
        RadiationAttachments.add(player,
                -(int) (event.getAmount() * RadiationEffects.HEALING_CONVERSION_RATE));
    }

    /** 停服时丢弃全部索引：这些是派生数据，下次开服会重新扫描。 */
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        RadiationBlockTracker.clearAll();
    }
}
