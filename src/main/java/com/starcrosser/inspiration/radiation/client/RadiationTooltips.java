package com.starcrosser.inspiration.radiation.client;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.radiation.Radiation;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * 给带辐射的物品挂一行说明。
 * <p>
 * 数值来自数据地图，因此任何物品（包括别的模组的）只要在
 * {@code data_maps/item/radiation.json} 里登记过就会自动显示，无需改代码。
 * 客户端限定：tooltip 只在客户端绘制，别让服务端去碰这类事件。
 */
@EventBusSubscriber(modid = Inspiration.MODID, value = Dist.CLIENT)
public final class RadiationTooltips {

    private RadiationTooltips() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        int value = Radiation.of(event.getItemStack());
        if (value <= 0) {
            return;
        }
        event.getToolTip().add(Component.translatable("tooltip.inspiration.radiation", value)
                .withStyle(ChatFormatting.GREEN));
    }
}
