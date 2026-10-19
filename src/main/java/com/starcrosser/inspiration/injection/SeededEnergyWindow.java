package com.starcrosser.inspiration.injection;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 由**世界种子**推导出来的注入能量窗口。
 * <p>
 * 有些注入配方的能量窗口不写死在 JSON 里，而是每个存档各随机一次：
 * <ol>
 *   <li>按 50% / 25% / 12.5% / 6.25% / 6.25% 取一个 {@code i ∈ [0, 4]}；
 *       这正是「连续抛硬币正面直到反面」的截断分布，最大层取 4。</li>
 *   <li>在 {@code [0, 4 - i]} 上均匀取一个 {@code j}。</li>
 *   <li>{@code min = 2^i × (2/3)^(j+1) × 100K FE}，{@code max = 2^(i+1) × (2/3)^(j+1) × 100K FE}。</li>
 * </ol>
 * 于是窗口总是在 {@code [E, 2E]} 这个形状上，只是 E 从约 13K 一路跨到约 107 万 ——
 * 玩家第一次开档面对的是哪个量级，全看运气。
 * <p>
 * 结果按「世界种子 + 配方 id」缓存，同一个存档里无论问多少次都是同一组数；
 * 不同配方各自独立取，互不干扰（混入配方 id 再打散种子）。
 */
public final class SeededEnergyWindow {

    /** 层 i 的上限。 */
    public static final int MAX_LAYER = 4;

    /** 公式里的基准能量：100K FE。 */
    public static final int BASE_ENERGY = 100_000;

    /** 公式里的步进系数 2/3。 */
    public static final double STEP = 2.0 / 3.0;

    /** 种子打散用的常数，换个值就会换掉所有存档的窗口。 */
    private static final long SALT = 0x9E3779B97F4A7C15L;

    private static final Map<Key, SeededEnergyWindow> CACHE = new ConcurrentHashMap<>();

    private final int layerI;
    private final int layerJ;
    private final int minEnergy;
    private final int maxEnergy;

    private SeededEnergyWindow(int layerI, int layerJ, int minEnergy, int maxEnergy) {
        this.layerI = layerI;
        this.layerJ = layerJ;
        this.minEnergy = minEnergy;
        this.maxEnergy = maxEnergy;
    }

    /** 取某个存档里、某条配方对应的窗口。 */
    public static SeededEnergyWindow of(long worldSeed, ResourceLocation recipeId) {
        return CACHE.computeIfAbsent(new Key(worldSeed, recipeId), key -> roll(key.worldSeed(), key.recipeId()));
    }

    /**
     * 取当前世界对应的窗口；客户端拿不到世界种子，返回 {@code null}。
     * <p>
     * {@code ServerLevel#getSeed()} 读的是存档的世界种子，所有维度共用同一个值。
     */
    public static SeededEnergyWindow of(Level level, ResourceLocation recipeId) {
        if (level instanceof ServerLevel serverLevel) {
            return of(serverLevel.getSeed(), recipeId);
        }
        return null;
    }

    private static SeededEnergyWindow roll(long worldSeed, ResourceLocation recipeId) {
        long mixed = worldSeed ^ ((long) recipeId.hashCode() * SALT) ^ SALT;
        Random random = new Random(mixed);
        // 先抽两下，把线性同余的前几个数丢掉，相邻的存档种子因此不会给出相近序列
        random.nextLong();
        random.nextLong();

        int layerI = 0;
        while (layerI < MAX_LAYER && random.nextBoolean()) {
            layerI++;
        }
        int layerJ = random.nextInt(MAX_LAYER - layerI + 1);

        double factor = (1 << layerI) * Math.pow(STEP, layerJ + 1);
        int min = (int) Math.floor(factor * BASE_ENERGY);
        int max = (int) Math.floor(factor * 2 * BASE_ENERGY);
        return new SeededEnergyWindow(layerI, layerJ, Math.max(1, min), Math.max(1, max));
    }

    public int minEnergy() {
        return minEnergy;
    }

    public int maxEnergy() {
        return maxEnergy;
    }

    public int layerI() {
        return layerI;
    }

    public int layerJ() {
        return layerJ;
    }

    /** 形如 {@code 13,168 ~ 26,337 FE}，供物品与调试输出复用。 */
    public String describeRange() {
        return String.format("%,d ~ %,d FE", minEnergy, maxEnergy);
    }

    /** 缓存键：世界种子 + 配方 id。 */
    private record Key(long worldSeed, ResourceLocation recipeId) {
    }
}
