package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.control.VecHelper;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PowerBeacon extends Item {
    public PowerBeacon(Properties props) {
        super(props);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        // 启动物品的使用动作
        player.startUsingItem(hand);
        // 返回 CONSUME 表示动作开始消耗
        return InteractionResultHolder.consume(itemStack);
    }
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 120000; // 一个很大的数字，代表可以无限使用
    }
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity,int time) {
        if(livingEntity instanceof Player player) {
            Vec3 dir=player.getLookAngle();
            double posx=player.position().x+dir.x;
            double posy=player.position().y+dir.y+1;
            double posz=player.position().z+dir.z;
            if (!level.isClientSide  && getUseDuration(stack, player)-time>20) {
                // 1. 在玩家所在位置生成生物

                WitherBoss wither = new WitherBoss(EntityType.WITHER,level);

                wither.setPos(posx,posy,posz);
                level.addFreshEntity(wither);
                if(!player.isCreative()){
                    // 2. 消耗当前物品，替换为新物品
                    ItemStack newItemStack = new ItemStack(ModItems.EMPTY_BEACON.get());

                    player.setItemInHand(player.getUsedItemHand(), newItemStack);
                }

            }
            if (level.isClientSide&& getUseDuration(stack, player)-time>20) {
                for (int i = 0; i < 40; i++) {
                    Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, level.random, .125f);
                    level.addParticle(ParticleTypes.SOUL, posx, posy, posz, motion.x, motion.y, motion.z);
                    Vec3 circle = motion.multiply(1, 0, 1)
                            .normalize()
                            .scale(.5f);
                    level.addParticle(ParticleTypes.SMOKE, circle.x, posy, circle.z, 0, -0.125, 0);
                }
                return;
            }
        }


    }
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW; // 关键：让动画生效
    }
}
