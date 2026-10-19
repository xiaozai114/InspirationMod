package com.starcrosser.inspiration.radiation.item;

import com.starcrosser.inspiration.radiation.Radiation;
import com.starcrosser.inspiration.radiation.RadiationAttachments;
import com.starcrosser.inspiration.radiation.RadiationEffects;
import com.starcrosser.inspiration.radiation.RadiationSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 盖革计数器：铯锭经注入打进铁锭后得到的测量工具。
 * <p>
 * 手持右键读出两件事 —— 脚下区块的辐射值，以及自己体内累积的辐射值。
 * 读数由服务端算出后以 action bar 形式回传，客户端不做任何推算。
 */
public class GeigerCounterItem extends Item {

    public GeigerCounterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            int chunk = RadiationSavedData.get(serverLevel).get(player.chunkPosition());
            int carried = RadiationAttachments.get(player);
            player.displayClientMessage(Component.translatable("item.inspiration.geiger_counter.reading",
                            Radiation.format(chunk),
                            Radiation.format(carried),
                            Component.translatable("radiation.inspiration.severity."
                                    + RadiationEffects.tierOf(carried)))
                    .withStyle(ChatFormatting.GREEN), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.inspiration.geiger_counter.desc")
                .withStyle(ChatFormatting.GRAY));
    }
}
