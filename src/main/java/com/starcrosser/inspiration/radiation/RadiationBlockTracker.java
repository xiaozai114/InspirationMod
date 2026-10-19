package com.starcrosser.inspiration.radiation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 一个维度里「哪些位置摆着会辐射的方块」的索引。
 * <p>
 * 每秒都要判断这些方块是否接触空气，因此不能每次全图扫描 —— 这里维护一张位置表：
 * 区块加载时排队等扫描（每刻只扫有限个，避免加载卡顿），放置/破坏时增量增删，
 * 区块卸载时整块丢弃。
 * <p>
 * 扫描前先查区块段的调色板：只有段内确实出现了登记过的辐射方块，才逐格遍历这 16³ 格，
 * 因此绝大多数段的开销只是「把存储里的整数读一遍」。
 */
public final class RadiationBlockTracker {

    private static final Map<ResourceKey<Level>, RadiationBlockTracker> TRACKERS = new HashMap<>();

    private final Map<BlockPos, Integer> sources = new HashMap<>();
    private final Deque<ChunkPos> pending = new ArrayDeque<>();

    private RadiationBlockTracker() {
    }

    public static RadiationBlockTracker get(ServerLevel level) {
        return TRACKERS.computeIfAbsent(level.dimension(), key -> new RadiationBlockTracker());
    }

    /** 服务端停服时清空；这些是派生数据，重开世界会重新扫描。 */
    public static void clearAll() {
        TRACKERS.clear();
    }

    /** 区块加载：排进扫描队列，稍后由 {@link #tick} 分摊处理。 */
    public void queueChunk(ChunkPos pos) {
        if (!pending.contains(pos)) {
            pending.add(pos);
        }
    }

    /** 区块卸载：丢掉该区块内的全部记录，也从队列里撤掉。 */
    public void forgetChunk(ChunkPos pos) {
        pending.remove(pos);
        sources.keySet().removeIf(blockPos -> chunkOf(blockPos).equals(pos));
    }

    /** 重新读取某个位置的方块：有辐射就登记，没有就移除。 */
    public void refresh(ServerLevel level, BlockPos pos) {
        int value = Radiation.of(level.getBlockState(pos));
        if (value > 0) {
            put(pos, value);
        } else {
            forget(pos);
        }
    }

    /** 直接登记一个位置；用于已经知道新方块状态的场合（比如放置事件）。 */
    public void put(BlockPos pos, int value) {
        sources.put(pos.immutable(), value);
    }

    public void forget(BlockPos pos) {
        sources.remove(pos);
    }

    /** 每刻处理至多 {@code budget} 个待扫描区块。 */
    public void tick(ServerLevel level, int budget) {
        for (int processed = 0; processed < budget && !pending.isEmpty(); processed++) {
            ChunkPos pos = pending.poll();
            if (pos == null || !level.hasChunkAt(pos.x, pos.z)) {
                continue;
            }
            if (level.getChunk(pos.x, pos.z) instanceof LevelChunk chunk) {
                scanChunk(chunk);
            }
        }
    }

    /**
     * 把「正在接触空气」的辐射方块按区块汇总到 {@code out}。
     * 位置可能在队列里等扫描（刚加载的区块），或者所在区块已卸载，这两种都跳过。
     */
    public void collect(ServerLevel level, Map<ChunkPos, Integer> out) {
        if (sources.isEmpty()) {
            return;
        }
        sources.forEach((pos, value) -> {
            if (!level.isLoaded(pos)) {
                return;
            }
            if (isExposed(level, pos)) {
                out.merge(chunkOf(pos), value, Integer::sum);
            }
        });
    }

    /** 六面里只要有一面是空气就算暴露。 */
    private static boolean isExposed(ServerLevel level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (level.getBlockState(pos.relative(direction)).isAir()) {
                return true;
            }
        }
        return false;
    }

    private void scanChunk(LevelChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        LevelChunkSection[] sections = chunk.getSections();
        for (int index = 0; index < sections.length; index++) {
            LevelChunkSection section = sections[index];
            if (section == null || section.hasOnlyAir()) {
                continue;
            }
            PalettedContainer<BlockState> states = section.getStates();
            if (!hasRadiation(states)) {
                continue;
            }
            int baseY = SectionPos.sectionToBlockCoord(chunk.getSectionYFromSectionIndex(index));
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        int value = Radiation.of(states.get(x, y, z));
                        if (value > 0) {
                            sources.put(new BlockPos(chunkPos.getMinBlockX() + x, baseY + y,
                                    chunkPos.getMinBlockZ() + z), value);
                        }
                    }
                }
            }
        }
    }

    /** 段内是否出现过辐射方块：只看调色板里的去重状态，因此很便宜。 */
    private static boolean hasRadiation(PalettedContainer<BlockState> states) {
        boolean[] found = {false};
        states.getAll(state -> {
            if (!found[0] && Radiation.of(state) > 0) {
                found[0] = true;
            }
        });
        return found[0];
    }

    private static ChunkPos chunkOf(BlockPos pos) {
        return new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
    }

    /** 调试用：当前登记的辐射方块数量。 */
    public int trackedCount() {
        return sources.size();
    }

    /** 调试用：还在排队等扫描的区块数量。 */
    public int pendingCount() {
        return pending.size();
    }

    /** 调试用：把所有登记位置按区块分组（自检时不依赖加载状态直接核对索引内容）。 */
    public Map<ChunkPos, Integer> snapshot() {
        Map<ChunkPos, Integer> result = new HashMap<>();
        Iterator<Map.Entry<BlockPos, Integer>> iterator = sources.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, Integer> entry = iterator.next();
            result.merge(chunkOf(entry.getKey()), entry.getValue(), Integer::sum);
        }
        return result;
    }
}
