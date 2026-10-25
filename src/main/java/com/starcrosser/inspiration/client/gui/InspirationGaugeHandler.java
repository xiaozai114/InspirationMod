package com.starcrosser.inspiration.client.gui;

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = Inspiration.MODID, value = Dist.CLIENT)
public final class InspirationGaugeHandler {

    private InspirationGaugeHandler() {
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen screen) {
            event.addListener(new InspirationGauge(screen.getGuiLeft() + 180, screen.getGuiTop() + 4));
        }
    }
}
