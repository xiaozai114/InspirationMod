package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.machine.plugin.MachinePluginType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 机器插件：装进机器右侧的插件槽后改变机器性能。
 * <p>
 * 插件本身只声明自己的种类，具体倍率与代价写在物品描述里，
 * 由 {@code PluginEffects} 在机器端统一读取。
 */
public class MachinePluginItem extends Item {

    private final MachinePluginType pluginType;

    public MachinePluginItem(MachinePluginType pluginType) {
        super(new Item.Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.UNCOMMON));
        this.pluginType = pluginType;
    }

    public MachinePluginType pluginType() {
        return pluginType;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.inspiration.plugin.slot")
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable(pluginType.descriptionKey())
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.inspiration.plugin.stack",
                        pluginType.multiplierText())
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
