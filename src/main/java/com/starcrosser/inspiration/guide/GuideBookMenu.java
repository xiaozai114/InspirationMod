package com.starcrosser.inspiration.guide;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

/**
 * 指导书的空菜单：只用把界面「开起来」，不搬运任何物品。
 * <p>
 * 内容全在客户端的 {@code GuideBookScreen} 里，服务端不需要知道玩家翻到了第几章，
 * 所以这里除了「一直有效、不搬东西」之外没有别的逻辑。
 */
public class GuideBookMenu extends AbstractContainerMenu {

    public GuideBookMenu(int containerId, Inventory playerInventory) {
        super(ModGuideMenus.GUIDE_BOOK.get(), containerId);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
