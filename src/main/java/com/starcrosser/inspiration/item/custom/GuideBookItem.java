package com.starcrosser.inspiration.item.custom;

import com.starcrosser.inspiration.guide.GuideBookMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 灵感指导书：右键翻开。
 * <p>
 * 不走原版成书（{@code Items.WRITTEN_BOOK}）那条路 —— 原版的开书包里两岸都写死了
 * 「手里拿的是不是原版成书」，自定义物品塞不进去。这里改成打开一个空菜单，
 * 由客户端界面自己渲染章节内容。
 */
public class GuideBookItem extends Item {

    public GuideBookItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            // SimpleMenuProvider 的构造器要 (id, 背包, 玩家) 三个参数，这里用不到玩家
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, inventory, player2) -> new GuideBookMenu(containerId, inventory),
                    Component.translatable("container.inspiration.guide_book")));
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
