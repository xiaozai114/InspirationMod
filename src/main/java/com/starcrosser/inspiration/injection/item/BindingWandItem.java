package com.starcrosser.inspiration.injection.item;

import com.starcrosser.inspiration.injection.InjectionBeam;
import com.starcrosser.inspiration.injection.InjectionStructure;
import com.starcrosser.inspiration.injection.blockentity.InjectionGunTile;
import com.starcrosser.inspiration.injection.blockentity.MatterBinderTile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 绑定器：把注入枪与物质束缚器连起来的工具。
 * <p>
 * 两步操作 —— 先右键物质束缚器记下它的位置（存在物品的 {@code CustomData} 里），
 * 再右键注入枪完成绑定；绑定要求两者同维且在 5×5×5 范围内。
 * 潜行右键注入枪可以解除绑定。
 * <p>
 * 注入枪与束缚器都不会拦下这次右键（它们对绑定器返回
 * {@code SKIP_DEFAULT_BLOCK_INTERACTION}），因此逻辑只写在这里一处。
 */
public class BindingWandItem extends Item {

    /** 选中坐标在物品数据里的键名。 */
    private static final String KEY_POS = "BinderPos";

    public BindingWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        if (player == null) {
            return InteractionResult.PASS;
        }

        if (level.getBlockEntity(pos) instanceof MatterBinderTile) {
            if (!level.isClientSide) {
                select(stack, pos);
                player.displayClientMessage(message("selected", pos.getX(), pos.getY(), pos.getZ())
                        .withStyle(ChatFormatting.AQUA), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!(level.getBlockEntity(pos) instanceof InjectionGunTile gun)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player.isSecondaryUseActive()) {
            boolean wasBound = gun.isBound();
            gun.unbind();
            player.displayClientMessage(message(wasBound ? "unbound" : "not_bound"), true);
            return InteractionResult.SUCCESS;
        }

        BlockPos target = selectedPos(stack);
        if (target == null) {
            player.displayClientMessage(message("no_selection"), true);
            return InteractionResult.SUCCESS;
        }
        if (!InjectionStructure.isInRange(pos, target)) {
            player.displayClientMessage(
                    message("out_of_range", InjectionStructure.sideLabel()), true);
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(target) instanceof MatterBinderTile)) {
            clear(stack);
            player.displayClientMessage(message("target_lost"), true);
            return InteractionResult.SUCCESS;
        }

        gun.bind(target);
        // 连一条粒子线，直观地确认「连上了哪一台」
        InjectionBeam.beam(level, Vec3.atCenterOf(pos), Vec3.atCenterOf(target));
        player.displayClientMessage(message("bound", target.getX(), target.getY(), target.getZ())
                .withStyle(ChatFormatting.GREEN), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.inspiration.binding_wand.description")
                .withStyle(ChatFormatting.GRAY));
        BlockPos selected = selectedPos(stack);
        if (selected == null) {
            tooltipComponents.add(Component.translatable("tooltip.inspiration.binding_wand.none")
                    .withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.inspiration.binding_wand.selected",
                            selected.getX(), selected.getY(), selected.getZ())
                    .withStyle(ChatFormatting.DARK_AQUA));
        }
    }

    /* ============================ 选中坐标的读写 ============================ */

    private static void select(ItemStack stack, BlockPos pos) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        tag.putIntArray(KEY_POS, new int[]{pos.getX(), pos.getY(), pos.getZ()});
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static void clear(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return;
        }
        CompoundTag tag = data.copyTag();
        tag.remove(KEY_POS);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /** 当前选中的物质束缚器位置；未选中时为 null。 */
    @Nullable
    public static BlockPos selectedPos(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return null;
        }
        int[] array = data.copyTag().getIntArray(KEY_POS);
        return array.length == 3 ? new BlockPos(array[0], array[1], array[2]) : null;
    }

    private static MutableComponent message(String key, Object... args) {
        return Component.translatable("message.inspiration.binding_wand." + key, args);
    }
}
