package com.starcrosser.inspiration.client.tooltip;

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.Set;

/**
 * 给所有机器挂说明：加工类写明配方类型，发电类写明发电机制。
 * <p>
 * 机器方块由 Titanium 一次注册成型（方块 + 方块物品），拿不到自定义物品类来覆写
 * {@code appendHoverText}，所以走 tooltip 事件按物品 ID 匹配。
 * 键是 {@code tooltip.inspiration.machine.<机器名>}，新增机器只要往 {@link #MACHINES}
 * 里加一行、再补两种语言的文案。
 * <p>
 * 一个键可以写**多行**：语言值里用 {@code \n} 分隔，这里拆开逐行挂上去
 * （Minecraft 自己不会把语言值里的换行折成多行，必须手动切）。
 * 文案风格：加工类首行固定是「配方类型：xxx」，发电类是「投入……发电。」+ 数值行 +
 * 变量说明行，额外特性各占一行。
 * <p>
 * 客户端限定：tooltip 只在客户端绘制，别让服务端去碰这类事件。
 */
@EventBusSubscriber(modid = Inspiration.MODID, value = Dist.CLIENT)
public final class MachineTooltips {

    /** 所有机器的物品 ID（与 {@code ModMachines} 里注册用的名字一致）。 */
    private static final Set<String> MACHINES = Set.of(
            "matter_fusion_machine",
            "energized_matter_fusion_machine",
            "matter_reshaper",
            "mineral_twister",
            "inspiration_assembler",
            "core_driller",
            "high_energy_mineral_twister",
            "high_energy_inspiration_assembler",
            "high_energy_matter_reshaper",
            "alkaline_reactor",
            "rtg_generator",
            "structure_simulator",
            "fluid_extractor",
            "raw_refiner",
            "culture_tank");

    private MachineTooltips() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
        if (!Inspiration.MODID.equals(key.getNamespace()) || !MACHINES.contains(key.getPath())) {
            return;
        }
        String raw = Component.translatable("tooltip.inspiration.machine." + key.getPath()).getString();
        for (String line : raw.split("\n")) {
            event.getToolTip().add(Component.literal(line).withStyle(ChatFormatting.GRAY));
        }
    }
}
