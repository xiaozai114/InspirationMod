package com.starcrosser.inspiration.injection.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.starcrosser.inspiration.injection.InjectionTiers;
import com.starcrosser.inspiration.injection.ModInjection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 物质束缚器的方块实体。
 * <p>
 * 它本身不耗电、不参与红石，只做两件事：承放一份基底物质，以及读取正下方方块提供的注入等级。
 * 槽位限量为 1（「束缚器内只能放一个物品」），注入成功后产物也放回同一个槽位。
 * 真正的放电与计时由注入枪负责。
 */
public class MatterBinderTile extends ActiveTile<MatterBinderTile> {

    /** 唯一槽位在 GUI 中的位置。 */
    private static final int SLOT_X = 80;
    private static final int SLOT_Y = 35;

    @Save
    private SidedInventoryComponent<MatterBinderTile> baseSlot;

    public MatterBinderTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<MatterBinderTile>) ModInjection.MATTER_BINDER.getBlock(),
                ModInjection.MATTER_BINDER.type().get(), pos, state);

        SidedInventoryComponent<MatterBinderTile> slot =
                new SidedInventoryComponent<>("base", SLOT_X, SLOT_Y, 1, 0);
        slot.setRange(1, 1);
        // 只能放一个物品，无论玩家还是漏斗都塞不进第二份
        slot.setSlotLimit(1);
        slot.setColor(DyeColor.PURPLE);
        slot.setFacingHandlerPos(8, 84);
        this.baseSlot = slot;
        addInventory(this.baseSlot);
    }

    @Override
    public MatterBinderTile getSelf() {
        return this;
    }

    /** 右键打开 GUI。Titanium 只在 {@code MachineTile} 里开 GUI，这里得自己接上。 */
    @Override
    public ItemInteractionResult onActivated(Player player, InteractionHand hand, Direction facing,
                                             double hitX, double hitY, double hitZ) {
        if (super.onActivated(player, hand, facing, hitX, hitY, hitZ) == ItemInteractionResult.SUCCESS) {
            return ItemInteractionResult.SUCCESS;
        }
        openGui(player);
        return ItemInteractionResult.SUCCESS;
    }

    /** 当前承放的基底物质。 */
    public ItemStack getBase() {
        return baseSlot.getStackInSlot(0);
    }

    /** 正下方方块提供的注入等级；没有合格的底座时为 0。 */
    public int structureTier() {
        return level == null ? 0 : InjectionTiers.tierBelow(level, worldPosition);
    }

    /** 注入启动时取走基底物质；数量不足返回 false。 */
    public boolean extractBase(int count) {
        if (baseSlot.getStackInSlot(0).getCount() < count) {
            return false;
        }
        baseSlot.extractItem(0, count, false);
        markForUpdate();
        return true;
    }

    /**
     * 玩家右键放入一份物质。槽位限量为 1，所以一次只会收下一份。
     *
     * @return 实际收下的数量（0 表示槽里已经有东西，放不进去）
     */
    public int insert(ItemStack stack) {
        if (stack.isEmpty() || !baseSlot.getStackInSlot(0).isEmpty()) {
            return 0;
        }
        ItemStack leftover = baseSlot.insertItem(0, stack.copyWithCount(1), false);
        int inserted = 1 - leftover.getCount();
        if (inserted > 0) {
            markForUpdate();
        }
        return inserted;
    }

    /**
     * 把承放的物质交还给玩家：空手时直接给到手上，手上拿着东西就塞进背包。
     *
     * @return 是否成功交还（背包满时会失败，物品留在槽里）
     */
    public boolean extractToPlayer(Player player) {
        ItemStack stored = baseSlot.getStackInSlot(0);
        if (stored.isEmpty()) {
            return false;
        }
        ItemStack copy = stored.copy();
        if (player.getMainHandItem().isEmpty()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, copy);
        } else if (!player.getInventory().add(copy)) {
            return false;
        }
        baseSlot.setStackInSlot(0, ItemStack.EMPTY);
        markForUpdate();
        return true;
    }

    /**
     * 注入成功时把产物放回同一个槽位。
     *
     * @return 槽位放不下时返回 false，由调用方决定改成掉落
     */
    public boolean depositResult(ItemStack result) {
        ItemStack current = baseSlot.getStackInSlot(0);
        if (current.isEmpty()) {
            baseSlot.setStackInSlot(0, result);
        } else if (ItemStack.isSameItemSameComponents(current, result)) {
            baseSlot.setStackInSlot(0, current.copyWithCount(current.getCount() + result.getCount()));
        } else {
            return false;
        }
        markForUpdate();
        return true;
    }
}
