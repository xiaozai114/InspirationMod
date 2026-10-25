package com.starcrosser.inspiration.event;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.effect.ModEffects;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Random;

@EventBusSubscriber(modid = Inspiration.MODID)
public final class ParasiteHandler {

    private static final float HEAD_HUNTER_CHANCE = 0.2F;
    private static final float PARASITE_CHANCE = 0.8F;
    private static final Random RANDOM = new Random();

    private ParasiteHandler() {
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        Level level = victim.level();
        if (level.isClientSide) {
            return;
        }

        boolean mechanical = victim.hasEffect(ModEffects.MECHANICAL_PARASITE);
        boolean parasitized = victim.hasEffect(ModEffects.PARASITE);
        boolean beheaded = event.getSource().getEntity() instanceof Player player
                && player.getMainHandItem().is(ModItems.HEAD_HUNTER_AXE.get());

        if (victim instanceof Villager) {
            float chance = Math.max(mechanical || parasitized ? PARASITE_CHANCE : 0F,
                    beheaded ? HEAD_HUNTER_CHANCE : 0F);
            if (RANDOM.nextFloat() < chance) {
                drop(level, victim, new ItemStack(ModItems.VILLAGER_BRAIN.get()));
            }
        } else if (victim instanceof EnderMan) {
            if (mechanical) {
                drop(level, victim, new ItemStack(ModItems.ENDERMAN_BRAIN.get()));
            } else if (beheaded && RANDOM.nextFloat() < HEAD_HUNTER_CHANCE) {
                drop(level, victim, new ItemStack(ModItems.ENDERMAN_BRAIN.get()));
            }
        }

        if (mechanical && victim instanceof Villager) {
            drop(level, victim, new ItemStack(ModItems.MECHANICAL_PARASITE.get()));
        }
    }

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();
        ItemStack held = event.getItemStack();
        if (!(target instanceof LivingEntity living) || player.level().isClientSide) {
            return;
        }

        if (held.is(ModItems.PARASITE.get()) && living instanceof Villager) {
            infect(living, ModEffects.PARASITE, player, held);
            event.setCanceled(true);
        } else if (held.is(ModItems.MECHANICAL_PARASITE.get())
                && (living instanceof Villager || living instanceof EnderMan)) {
            infect(living, ModEffects.MECHANICAL_PARASITE, player, held);
            event.setCanceled(true);
        }
    }

    private static void infect(LivingEntity target, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect,
                               Player player, ItemStack held) {
        if (target.hasEffect(effect)) {
            return;
        }
        target.addEffect(new MobEffectInstance(effect, ModEffects.PARASITE_DURATION, 0));
        if (!player.isCreative()) {
            held.shrink(1);
        }
    }

    private static void drop(Level level, LivingEntity victim, ItemStack stack) {
        victim.spawnAtLocation(stack);
    }
}
