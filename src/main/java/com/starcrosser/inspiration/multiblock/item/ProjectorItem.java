package com.starcrosser.inspiration.multiblock.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 结构投影仪：手持对着结构模拟机的主方块右键，就在原地把 4×3×3 的结构投影出来，
 * 照着虚影搭即可。虚影本身不消耗物品，也不改任何方块。
 * <p>
 * 真正的投影逻辑在客户端（{@code ProjectorPreviewHandler} + {@code SimulatorPreviewRenderer}），
 * 这里只是物品本体与说明文字。
 */
public class ProjectorItem extends Item {

    public ProjectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.inspiration.projector.usage")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.inspiration.projector.duration")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
