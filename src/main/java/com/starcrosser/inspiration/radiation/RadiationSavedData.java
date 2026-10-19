package com.starcrosser.inspiration.radiation;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

/**
 * 一个维度里所有区块的辐射值。
 * <p>
 * 只有「有辐射」的区块才会留在表里，值归零就会被剔除，避免表无限膨胀。
 * 数据挂在维度的 {@code DimensionDataStorage} 上，随存档落盘。
 * <p>
 * 每小时的结算（见 {@link #tickHourly()}）：
 * 向四个正方向中辐射值严格更低的邻区块各传播 {@code x/100} 点（源区块扣除这部分），
 * 并额外自发衰减 {@code x/10} 点，所有除法向下取整。
 */
public class RadiationSavedData extends SavedData {

    private static final String ID = "inspiration_radiation";
    private static final String KEY_CHUNKS = "chunks";

    /** 传播与衰减的分母：向每个更低的邻区块传出 x/100，自身额外衰减 x/10。 */
    private static final int SPREAD_DIVISOR = 100;
    private static final int DECAY_DIVISOR = 10;

    /** 只向四个正方向传播（不含斜角）。 */
    private static final Direction[] SPREAD_DIRECTIONS =
            {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    private static final SavedData.Factory<RadiationSavedData> FACTORY =
            new SavedData.Factory<>(RadiationSavedData::new, RadiationSavedData::load);

    private final Map<ChunkPos, Integer> radiation = new HashMap<>();

    public static RadiationSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, ID);
    }

    public int get(ChunkPos pos) {
        return radiation.getOrDefault(pos, 0);
    }

    /** 给某个区块加辐射；负值允许，结果归零或更低时会把该区块从表里剔除。 */
    public void add(ChunkPos pos, int amount) {
        if (amount == 0) {
            return;
        }
        int updated = get(pos) + amount;
        if (updated <= 0) {
            radiation.remove(pos);
        } else {
            radiation.put(pos, updated);
        }
        setDirty();
    }

    public void addAll(Map<ChunkPos, Integer> amounts) {
        if (amounts.isEmpty()) {
            return;
        }
        amounts.forEach(this::add);
    }

    /** 表内区块数量，自检与调试用。 */
    public int size() {
        return radiation.size();
    }

    /**
     * 每小时结算：先按「结算前」的快照算出全部增减，再统一写回，
     * 这样传播次序不会影响结果。
     */
    public void tickHourly() {
        if (radiation.isEmpty()) {
            return;
        }
        Map<ChunkPos, Integer> snapshot = new HashMap<>(radiation);
        Map<ChunkPos, Integer> deltas = new HashMap<>();

        snapshot.forEach((pos, value) -> {
            if (value <= 0) {
                return;
            }
            int spread = value / SPREAD_DIVISOR;
            int lost = value / DECAY_DIVISOR;
            if (spread > 0) {
                for (Direction direction : SPREAD_DIRECTIONS) {
                    ChunkPos neighbour = new ChunkPos(pos.x + direction.getStepX(), pos.z + direction.getStepZ());
                    if (snapshot.getOrDefault(neighbour, 0) < value) {
                        deltas.merge(neighbour, spread, Integer::sum);
                        lost += spread;
                    }
                }
            }
            if (lost > 0) {
                deltas.merge(pos, -lost, Integer::sum);
            }
        });

        if (deltas.isEmpty()) {
            return;
        }
        deltas.forEach(this::add);
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        int[] flat = new int[radiation.size() * 3];
        int index = 0;
        for (Map.Entry<ChunkPos, Integer> entry : radiation.entrySet()) {
            flat[index++] = entry.getKey().x;
            flat[index++] = entry.getKey().z;
            flat[index++] = entry.getValue();
        }
        tag.put(KEY_CHUNKS, new IntArrayTag(flat));
        return tag;
    }

    public static RadiationSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        RadiationSavedData data = new RadiationSavedData();
        int[] flat = tag.getIntArray(KEY_CHUNKS);
        for (int index = 0; index + 2 < flat.length; index += 3) {
            int value = flat[index + 2];
            if (value > 0) {
                data.radiation.put(new ChunkPos(flat[index], flat[index + 1]), value);
            }
        }
        return data;
    }
}
