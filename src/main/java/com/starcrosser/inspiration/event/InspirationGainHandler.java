package com.starcrosser.inspiration.event;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.player.InspirationValue;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

@EventBusSubscriber(modid = Inspiration.MODID)
public final class InspirationGainHandler {

    private InspirationGainHandler() {
    }

    @SubscribeEvent
    public static void onCrafted(PlayerEvent.ItemCraftedEvent event) {
        InspirationValue.add(event.getEntity(), 1);
    }

    @SubscribeEvent
    public static void onEaten(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player && event.getItem().has(DataComponents.FOOD)) {
            InspirationValue.add(player, 1);
        }
    }

    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        InspirationValue.add(event.getEntity(), 1);
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        InspirationValue.sync(event.getEntity());
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        InspirationValue.set(event.getEntity(), InspirationValue.get(event.getOriginal()));
    }
}
