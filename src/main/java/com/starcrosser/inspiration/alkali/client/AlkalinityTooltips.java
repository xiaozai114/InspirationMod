package com.starcrosser.inspiration.alkali.client;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.alkali.Alkalinity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * 给带碱性的物品在名字下面挂一行说明。
 * <p>
 * 与辐射 tooltip 同一套做法（见 {@code radiation/client/RadiationTooltips}）：
 * 数值来自数据地图，任何物品只要在 {@code data_maps/item/alkalinity.json} 里登记过
 * 就会自动显示，无需改代码。客户端限定，别让服务端去碰这类事件。
 */
@EventBusSubscriber(modid = Inspiration.MODID, value = Dist.CLIENT)
public final class AlkalinityTooltips {

    private AlkalinityTooltips() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        int value = Alkalinity.of(event.getItemStack());
        if (value <= 0) {
            return;
        }
        event.getToolTip().add(Component.translatable("tooltip.inspiration.alkalinity", value)
                .withStyle(ChatFormatting.AQUA));
    }
}
