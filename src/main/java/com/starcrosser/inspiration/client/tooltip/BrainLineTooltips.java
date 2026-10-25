package com.starcrosser.inspiration.client.tooltip;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.LinkedHashMap;
import java.util.Map;

@EventBusSubscriber(modid = Inspiration.MODID, value = Dist.CLIENT)
public final class BrainLineTooltips {

    private BrainLineTooltips() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        String key = keys().get(event.getItemStack().getItem());
        if (key == null) {
            return;
        }
        for (String line : Component.translatable(key).getString().split("\n")) {
            event.getToolTip().add(Component.literal(line).withStyle(ChatFormatting.GRAY));
        }
    }

    private static Map<Item, String> keys() {
        Map<Item, String> keys = new LinkedHashMap<>();
        keys.put(ModItems.VILLAGER_BRAIN.get(), "tooltip.inspiration.brain.villager");
        keys.put(ModItems.ENDERMAN_BRAIN.get(), "tooltip.inspiration.brain.enderman");
        keys.put(ModItems.INACTIVE_BRAIN.get(), "tooltip.inspiration.brain.inactive");
        keys.put(ModItems.PARASITE.get(), "tooltip.inspiration.parasite");
        keys.put(ModItems.MECHANICAL_PARASITE.get(), "tooltip.inspiration.mechanical_parasite");
        return keys;
    }
}
