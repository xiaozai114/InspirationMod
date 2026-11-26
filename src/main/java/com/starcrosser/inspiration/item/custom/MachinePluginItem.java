package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.machine.plugin.MachinePluginType;
import com.starcrosser.inspiration.machine.plugin.PluginEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 机器插件：装进机器右侧的插件槽后改变机器性能。
 * <p>
 * 插件本身只声明自己的种类，具体倍率与代价写在物品描述里，
 * 由 {@code PluginEffects} 在机器端统一读取。
 */
public class MachinePluginItem extends Item {

    public static final float HIGH_ENERGY_MULTIPLIER = 4F;

    private final MachinePluginType pluginType;
    private final float multiplier;

    public MachinePluginItem(MachinePluginType pluginType) {
        this(pluginType, pluginType.multiplier(), Rarity.UNCOMMON);
    }

    private MachinePluginItem(MachinePluginType pluginType, float multiplier, Rarity rarity) {
        super(new Item.Properties().stacksTo(1).rarity(rarity));
        this.pluginType = pluginType;
        this.multiplier = multiplier;
    }

    public static MachinePluginItem highEnergy(MachinePluginType type) {
        return new MachinePluginItem(type, HIGH_ENERGY_MULTIPLIER, Rarity.RARE);
    }

    public float multiplier() {
        return multiplier;
    }

    public MachinePluginType pluginType() {
        return pluginType;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.inspiration.plugin.slot")
                .withStyle(ChatFormatting.DARK_GRAY));
        String factor = MachinePluginType.formatMultiplier(multiplier);
        MutableComponent description = pluginType == MachinePluginType.SPEED
                ? Component.translatable(pluginType.descriptionKey(), factor,
                        MachinePluginType.formatMultiplier(PluginEffects.speedEnergyUse(multiplier)))
                : Component.translatable(pluginType.descriptionKey(), factor);
        tooltip.add(description.withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.inspiration.plugin.stack",
                        factor)
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
