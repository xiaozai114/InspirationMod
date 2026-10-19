package com.starcrosser.inspiration.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class StormSword extends SwordItem {
    /** 冷却按玩家记在 persistentData 里：Item 是单例，用实例字段会让所有玩家共用一个冷却。 */
    private static final String LEFT_KEY = "inspiration:storm_sword_left";
    private static final String RIGHT_KEY = "inspiration:storm_sword_right";

    private final int LeftClickCooldown;
    private final int RightClickCooldown;
    public StormSword(Tier tier, int attack_dmg, float attack_spd , int lcc, int rcc,Properties properties) {
        super(tier, properties.attributes(SwordItem.createAttributes(tier, attack_dmg, attack_spd)));
        this.LeftClickCooldown = lcc;
        this.RightClickCooldown = rcc;
    }
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player) {
            long currentTime = player.level().getGameTime();
            Level level = target.level();
            if (currentTime - player.getPersistentData().getLong(LEFT_KEY) >= LeftClickCooldown) {
                player.getPersistentData().putLong(LEFT_KEY, currentTime);
                for (int i=1;i<=10;i++){
                    // 发射雪球
                    Snowball snowball = new Snowball(player.level(), player);
                    // 设置方向和速度
                    Vec3 look = player.getLookAngle();
                    snowball.shoot(look.x, look.y, look.z, 1.5F, 10.0F);
                    player.level().addFreshEntity(snowball);
                }

                // 播放发射音效
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (player.getRandom().nextFloat() * 0.4F + 0.8F));
            }
        }

        return super.hurtEnemy(stack, target, attacker);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            long currentTime = level.getGameTime();

            // 检查冷却（按玩家记录，避免多人服互相串）
            if (currentTime - player.getPersistentData().getLong(RIGHT_KEY) >= RightClickCooldown) {
                player.getPersistentData().putLong(RIGHT_KEY, currentTime);
                WindCharge windCharge = new WindCharge(player, level, player.getX(), player.getY()+1, player.getZ());
                windCharge.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 0F);
                level.addFreshEntity(windCharge);
                level.playSound(player, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WIND_CHARGE_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.getCooldowns().addCooldown(this, RightClickCooldown);

            }


        }
        return InteractionResultHolder.success(stack);
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 添加描述文本
        tooltip.add(Component.translatable("tooltip.inspiration.storm_sowrd.description"));
    }
}
