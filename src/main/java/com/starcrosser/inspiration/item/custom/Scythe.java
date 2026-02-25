package com.starcrosser.inspiration.item.custom;

import com.google.common.collect.Multimap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.List;
import java.util.Objects;



public class Scythe extends HoeItem {
    public Scythe(Tier tier, float attackDamage,float attackSpeed,Properties properties) {
        super(tier, properties.attributes(AxeItem.createAttributes(tier, attackDamage, attackSpeed)));
    }
    /**
     * 计算伤害
     */
    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            Level level = player.level();

            // 只在服务端执行
            if (!level.isClientSide) {
                // 阻止暴击
                boolean flag = false;
                if(player.isSprinting()) {
                    flag = true;
                }
                // 计算基础伤害（无暴击）
                float damage = calculateDamage(player, stack,flag);
                // 执行横扫攻击
                performCompleteSweep(player, target, level, damage);
                // 播放音效
                player.playSound(net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_SWEEP,
                        1.0F, 1.0F);
                // 消耗耐久
                if (!player.isCreative()) {
                    stack.hurtAndBreak(1, player,
                            Objects.requireNonNull(player.getUseItem().getEquipmentSlot()));
                }

            }
        }

    }
    private float calculateDamage(Player player, ItemStack sword, boolean isCritical) {
        // 基础攻击伤害
        float damage = (float)player.getAttributeValue(
                net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE
        );
        return Math.max(1.0F, damage);
    }

    private void performCompleteSweep(Player player, LivingEntity primaryTarget, Level level, float baseDamage) {
        // 获取横扫范围内的目标
        List<LivingEntity> targets = getSweepTargetsComplete(player, level);
        // 对每个目标造成伤害
        for (LivingEntity target : targets) {
            if (target.isAlive()) {
                // 应用横扫伤害（比主目标略低）
                target.hurt(level.damageSources().mobAttack(player), baseDamage);

                // 击退效果
                applySweepKnockbackComplete(player, target);
                double x=target.getX();
                double y=target.getY();
                double z=target.getZ();
                Vec3 vec3=player.getLookAngle();
                for (int i=1;i<=10;i++ ){
                    target.level().addParticle(
                            net.minecraft.core.particles.ParticleTypes.DRAGON_BREATH,
                            x, y, z,
                            vec3.x,
                            vec3.y,
                            vec3.z
                    );
                }
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,60));
                target.addEffect(new MobEffectInstance(MobEffects.WITHER,60));
            }
        }

        // 生成粒子效果
        spawnSweepParticlesComplete(player, level);
    }
    /**
     * 获取横扫范围内的所有目标
     */
    private List<LivingEntity> getSweepTargetsComplete(Player player, Level level) {
        Vec3 pos = player.position();
        double range = 3.5; // 更大的横扫范围

        net.minecraft.world.phys.AABB area = new net.minecraft.world.phys.AABB(
                pos.x - range, pos.y - 1.0, pos.z - range,
                pos.x + range, pos.y + 2.0, pos.z + range
        );

        // 添加前方扩展
        Vec3 look = player.getLookAngle();
        area = area.expandTowards(look.x * 1.5, 0, look.z * 1.5);

        return level.getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity != player && entity.isAlive());
    }

    /**
     * 应用横扫击退
     */
    private void applySweepKnockbackComplete(Player player, LivingEntity target) {
        Vec3 toTarget = target.position().subtract(player.position());
        double distance = toTarget.length();

        if (distance > 0) {
            Vec3 knockback = toTarget.normalize().scale(0.6);
            target.push(knockback.x, 0.4, knockback.z);
        }
    }

    /**
     * 生成横扫粒子
     */
    private void spawnSweepParticlesComplete(Player player, Level level) {
        if (level.isClientSide) {
            Vec3 center = player.position().add(0, 1.0, 0);
            Vec3 look = player.getLookAngle();

            // 生成弧形粒子
            for (int i = 0; i < 20; i++) {
                float angle = (float)i / 20 * (float)Math.PI * 2;
                float radius = 2.0F;

                // 计算粒子位置
                double x = center.x + Math.cos(angle) * radius * look.z +
                        Math.sin(angle) * radius * look.x;
                double z = center.z + Math.cos(angle) * radius * look.x -
                        Math.sin(angle) * radius * look.z;
                double y = center.y + Math.sin(angle) * 0.5;

                player.level().addParticle(ParticleTypes.SWEEP_ATTACK, x, y, z,look.x , look.y,  look.z );

            }
        }
    }

}
