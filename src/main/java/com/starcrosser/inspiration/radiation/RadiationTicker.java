package com.starcrosser.inspiration.radiation;

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 辐射机制的时间驱动：每秒结算一次、每小时结算一次。
 * <p>
 * 每秒：
 * <ul>
 *     <li>暴露在空气里的辐射方块向所在区块注入辐射；</li>
 *     <li>玩家背包里的辐射物品向玩家所在区块注入辐射；</li>
 *     <li>玩家吸收所在区块的辐射，并按当前剂量刷新负面效果。</li>
 * </ul>
 * 每小时：
 * <ul>
 *     <li>区块向外传播并自发衰减（见 {@link RadiationSavedData#tickHourly()}）；</li>
 *     <li>玩家自身衰减 {@code x/10}，并把同样多的 {@code x/10} 倒回所在区块。</li>
 * </ul>
 */
@EventBusSubscriber(modid = Inspiration.MODID)
public final class RadiationTicker {

    private static final int TICKS_PER_SECOND = 20;
    private static final int TICKS_PER_HOUR = 72_000;

    /** 每刻最多扫描几个待处理的区块，避免区块集中加载时卡顿。 */
    private static final int SCANS_PER_TICK = 2;

    /** 玩家每小时的自发衰减与倒回区块的分母。 */
    private static final int PLAYER_DECAY_DIVISOR = 10;

    private static int elapsed = 0;

    private RadiationTicker() {
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        elapsed++;
        boolean second = elapsed % TICKS_PER_SECOND == 0;
        boolean hour = elapsed % TICKS_PER_HOUR == 0;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            RadiationBlockTracker tracker = RadiationBlockTracker.get(level);
            // 扫描队列每刻都要消耗，否则刚加载的区块会一直等不到登记
            tracker.tick(level, SCANS_PER_TICK);
            if (second) {
                tickSecond(level, tracker);
            }
            if (hour) {
                tickHour(level);
            }
        }
    }

    private static void tickSecond(ServerLevel level, RadiationBlockTracker tracker) {
        RadiationSavedData data = RadiationSavedData.get(level);
        Map<ChunkPos, Integer> gains = new HashMap<>();

        tracker.collect(level, gains);

        for (ServerPlayer player : level.players()) {
            tickPlayerSecond(data, player, gains);
        }

        data.addAll(gains);
    }

    /**
     * 单个玩家的每秒结算：把携带的辐射注入所在区块，再吸收该区块的辐射并刷新效果。
     * <p>
     * 区块值取「本轮方块结算之前」的值，避免同一秒内自己携带的辐射立刻反弹回自己身上。
     */
    public static void tickPlayerSecond(RadiationSavedData data, ServerPlayer player,
                                        Map<ChunkPos, Integer> gains) {
        ChunkPos pos = player.chunkPosition();
        int carried = carriedRadiation(player);
        if (carried > 0) {
            gains.merge(pos, carried, Integer::sum);
        }
        int ambient = data.get(pos);
        if (ambient > 0) {
            RadiationAttachments.add(player, ambient);
        }
        RadiationEffects.apply(player, RadiationAttachments.get(player));
    }

    private static void tickHour(ServerLevel level) {
        RadiationSavedData data = RadiationSavedData.get(level);
        data.tickHourly();

        for (ServerPlayer player : level.players()) {
            tickPlayerHour(data, player);
        }
    }

    /** 单个玩家的每小时结算：自身衰减 x/10，并把另外 x/10 倒回所在区块。 */
    public static void tickPlayerHour(RadiationSavedData data, ServerPlayer player) {
        int value = RadiationAttachments.get(player);
        if (value <= 0) {
            return;
        }
        int step = value / PLAYER_DECAY_DIVISOR;
        RadiationAttachments.set(player, value - 2 * step);
        if (step > 0) {
            data.add(player.chunkPosition(), step);
        }
    }

    /** 玩家身上携带的全部辐射：背包 + 装备栏 + 副手，按件数累计。 */
    public static int carriedRadiation(ServerPlayer player) {
        int total = 0;
        for (List<ItemStack> compartment : List.of(
                player.getInventory().items,
                player.getInventory().armor,
                player.getInventory().offhand)) {
            for (ItemStack stack : compartment) {
                total += Radiation.ofStack(stack);
            }
        }
        return total;
    }
}
