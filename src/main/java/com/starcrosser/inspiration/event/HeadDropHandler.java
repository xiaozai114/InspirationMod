package com.starcrosser.inspiration.event;

import com.starcrosser.inspiration.Inspiration;


import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
@EventBusSubscriber(modid = Inspiration.MODID)
public class HeadDropHandler {
    private static final Map<Class<? extends LivingEntity>, ItemStack> HEAD_MAPPINGS = new HashMap<>();

    static {
        // 初始化怪物头颅映射
        initializeHeadMappings();
    }
    private static void initializeHeadMappings() {
        HEAD_MAPPINGS.put(Zombie.class, new ItemStack(Items.ZOMBIE_HEAD,1));
        HEAD_MAPPINGS.put(Creeper.class, new ItemStack(Items.CREEPER_HEAD,1));
        HEAD_MAPPINGS.put(Skeleton.class, new ItemStack(Items.SKELETON_SKULL,1));
        HEAD_MAPPINGS.put(WitherSkeleton.class, new ItemStack(Items.WITHER_SKELETON_SKULL,1));
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        Vec3 pos=victim.position();
        // 检查伤害来源
        if (victim.level().isClientSide) return;

        if (event.getSource().getEntity() instanceof Player player) {
            ItemStack heldItem = player.getMainHandItem();

            // 检查是否持有猎首之斧
            if (heldItem.getItem() == ModItems.HEAD_HUNTER_AXE.get()) {
                // 处理头颅掉落
                Level level= victim.level();
                ItemStack headStack = getHeadForEntity(victim);
                ItemEntity headEntity = new ItemEntity(
                        level,
                        pos.x+0.5,
                        pos.y+0.5+1,
                        pos.z+0.5,
                        headStack
                );
                // 添加到世界中
                level.addFreshEntity(headEntity);

            }
        }
    }
    private static ItemStack getHeadForEntity(LivingEntity entity) {
        for (Map.Entry<Class<? extends LivingEntity>, ItemStack> entry : HEAD_MAPPINGS.entrySet()) {
            if (entry.getKey().isInstance(entity)) {
                return entry.getValue().copy();
            }
        }

        // 对于没有映射的实体，返回默认物品
        return new ItemStack(Items.AIR,1); // 或者返回空堆栈
    }
}
