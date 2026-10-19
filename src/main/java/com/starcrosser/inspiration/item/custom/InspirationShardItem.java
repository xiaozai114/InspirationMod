package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.machine.charge.InspirationCharge;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 灵感碎片：稀释灵感结晶的掉落物，是灵感的零碎形态。
 * <p>
 * 它和「灵感」一样可以直接放进机器的充能槽，只是折算出的充能量只有
 * {@link InspirationCharge#MB_PER_SHARD} mB。这条信息在物品描述里写明，
 * 否则玩家很难猜到碎片还有这个用途。
 */
public class InspirationShardItem extends Item {

    public InspirationShardItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.inspiration.inspiration_shard.description",
                        InspirationCharge.MB_PER_SHARD)
                .withStyle(ChatFormatting.GRAY));
    }
}
