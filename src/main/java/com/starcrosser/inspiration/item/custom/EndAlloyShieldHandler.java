package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerDestroyItemEvent;

import java.util.List;

/**
 * 末地合金盾牌的破盾特效：盾牌被击碎的那一刻，把周围的敌对生物随机甩到远处。
 * <p>
 * 钩子用 {@link PlayerDestroyItemEvent} —— 原版在 {@code Player#hurtCurrentlyUsedShield}
 * 里把盾打碎时会调 {@code EventHooks.onPlayerDestroyItem}，正好是「被破盾」这一个时机
 * （末地合金盾牌只有格挡这一条消耗耐久的途径，不会误触发）。
 * <p>
 * 落点用 {@code LivingEntity#randomTeleport}：它会自己检查目标区块是否加载、
 * 有没有碰撞与液体，落不下来就返回 false，我们换个方向再试。
 */
@EventBusSubscriber(modid = Inspiration.MODID)
public final class EndAlloyShieldHandler {

    /** 搜索半径（格），以破盾的玩家为中心。 */
    private static final double RADIUS = 12.0;
    /** 传送落点距玩家的最近 / 最远距离（格）。 */
    private static final double MIN_DISTANCE = 16.0;
    private static final double MAX_DISTANCE = 32.0;
    /** 落点的垂直抖动范围（格）。 */
    private static final int VERTICAL_SPREAD = 4;
    /** 单个生物最多重试几次落点。 */
    private static final int ATTEMPTS = 8;
    /** 一次破盾最多传送多少个生物，避免怪堆里一次性搬走几十只卡顿。 */
    private static final int MAX_TARGETS = 16;

    private EndAlloyShieldHandler() {
    }

    @SubscribeEvent
    public static void onShieldBroken(PlayerDestroyItemEvent event) {
        if (!(event.getOriginal().getItem() instanceof EndAlloyShieldItem)) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !(player.level() instanceof ServerLevel level)) {
            return;
        }

        List<Monster> targets = level.getEntitiesOfClass(
                Monster.class, player.getBoundingBox().inflate(RADIUS));
        if (targets.isEmpty()) {
            return;
        }

        RandomSource random = player.getRandom();
        int sent = 0;
        for (Monster mob : targets) {
            if (sent >= MAX_TARGETS) {
                break;
            }
            if (scatter(level, player, mob, random)) {
                sent++;
            }
        }
    }

    /** 给一只生物找一个远处的落点，传走成功返回 true。 */
    private static boolean scatter(ServerLevel level, ServerPlayer player, Monster mob, RandomSource random) {
        for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double distance = MIN_DISTANCE + random.nextDouble() * (MAX_DISTANCE - MIN_DISTANCE);
            double x = player.getX() + Math.cos(angle) * distance;
            double z = player.getZ() + Math.sin(angle) * distance;
            double y = player.getY() + random.nextInt(VERTICAL_SPREAD * 2 + 1) - VERTICAL_SPREAD;

            // broadcastTeleport = true：原版会广播实体事件 46，在出发地撒一串末影粒子
            if (!mob.randomTeleport(x, y, z, true)) {
                continue;
            }

            level.sendParticles(ParticleTypes.PORTAL,
                    mob.getX(), mob.getY() + mob.getBbHeight() * 0.5, mob.getZ(),
                    24, 0.4, 0.6, 0.4, 0.15);
            level.playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.4F);
            return true;
        }
        return false;
    }
}
