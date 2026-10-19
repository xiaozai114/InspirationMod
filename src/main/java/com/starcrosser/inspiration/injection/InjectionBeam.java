package com.starcrosser.inspiration.injection;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * 注入过程的视觉表现：注入枪到物质束缚器之间的激光束，以及成功 / 失败时的收尾粒子。
 * <p>
 * 全部走服务端的 {@code sendParticles}，客户端无需注册任何东西；粒子只在 32 格内可见，
 * 与结构本身的小体积相称。
 */
public final class InjectionBeam {

    /** 激光的采样段数。段数越多线越连续，14 段已经看不出断点。 */
    private static final int SEGMENTS = 14;

    /** 束体：偏白的翡翠绿。 */
    private static final DustParticleOptions BEAM =
            new DustParticleOptions(new Vector3f(0.55F, 1.0F, 0.72F), 0.75F);

    /** 束芯：接近纯白的细点，叠在束体上做出「能量流」的层次。 */
    private static final DustParticleOptions CORE =
            new DustParticleOptions(new Vector3f(0.92F, 1.0F, 0.95F), 0.35F);

    /** 在两点的连线上撒一串粒子。 */
    public static void beam(Level level, Vec3 from, Vec3 to) {
        ServerLevel server = serverLevel(level);
        if (server == null) {
            return;
        }
        Vec3 delta = to.subtract(from);
        for (int i = 0; i <= SEGMENTS; i++) {
            Vec3 point = from.add(delta.scale(i / (double) SEGMENTS));
            emit(server, BEAM, point);
            if (i % 2 == 0) {
                emit(server, CORE, point);
            }
        }
    }

    /** 注入成功：束缚器处一簇电火花。 */
    public static void flash(Level level, Vec3 at) {
        ServerLevel server = serverLevel(level);
        if (server == null) {
            return;
        }
        server.sendParticles(ParticleTypes.ELECTRIC_SPARK, at.x, at.y, at.z, 18, 0.3, 0.3, 0.3, 0.05);
        server.sendParticles(CORE, at.x, at.y, at.z, 12, 0.2, 0.2, 0.2, 0.0);
    }

    /** 能量不足：材料被烧掉，只剩一股烟。 */
    public static void fizzle(Level level, Vec3 at) {
        ServerLevel server = serverLevel(level);
        if (server == null) {
            return;
        }
        server.sendParticles(ParticleTypes.LARGE_SMOKE, at.x, at.y, at.z, 12, 0.25, 0.25, 0.25, 0.01);
        server.sendParticles(ParticleTypes.SMOKE, at.x, at.y, at.z, 12, 0.3, 0.3, 0.3, 0.02);
    }

    private static void emit(ServerLevel server, DustParticleOptions options, Vec3 point) {
        server.sendParticles(options, point.x, point.y, point.z, 1, 0.0, 0.0, 0.0, 0.0);
    }

    private static ServerLevel serverLevel(Level level) {
        return level instanceof ServerLevel server ? server : null;
    }

    private InjectionBeam() {
    }
}
