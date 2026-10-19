package com.starcrosser.inspiration.multiblock.client;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import com.starcrosser.inspiration.multiblock.item.ProjectorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * 结构投影仪的客户端状态：手持投影仪右键主方块后，记下「从哪一格、按哪个朝向投影」，
 * 以及什么时候失效，然后由 {@link SimulatorPreviewRenderer} 每帧去画。
 * <p>
 * 状态只在客户端保存：投影纯粹是视觉辅助，服务端不需要知道，也就不做任何网络同步。
 * 右键主方块时 {@code StructureSimulatorTile#onActivated} 会跳过开界面，
 * 所以投影期间不会弹 GUI。
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Inspiration.MODID, value = Dist.CLIENT)
public final class ProjectorPreviewHandler {

    /** 虚影持续 30 秒。 */
    private static final int PREVIEW_TICKS = 30 * 20;

    /** 正在投影的结构锚点（主方块所在格）；没有投影时为 null。 */
    private static BlockPos anchor;
    /** 投影用的结构朝向。 */
    private static Direction facing = Direction.NORTH;
    /** 到期时刻（客户端游戏刻）。 */
    private static long expiresAt;
    /** 投影发生在哪个维度；换维度后刻数会重算，必须一并作废。 */
    private static ClientLevel previewLevel;

    private ProjectorPreviewHandler() {
    }

    /** 当前是否需要画虚影；顺带处理过期。 */
    public static boolean isActive() {
        if (anchor == null) {
            return false;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || level != previewLevel || level.getGameTime() >= expiresAt) {
            anchor = null;
            previewLevel = null;
            return false;
        }
        return true;
    }

    public static BlockPos anchor() {
        return anchor;
    }

    public static Direction facing() {
        return facing;
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ClientLevel level)) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof ProjectorItem)) {
            return;
        }
        BlockPos pos = event.getPos();
        if (level.getBlockState(pos).getBlock() != ModMultiblocks.STRUCTURE_SIMULATOR.getBlock()) {
            return;
        }
        anchor = pos.immutable();
        // 朝向取玩家的水平朝向：结构从主方块朝**远离玩家**的方向展开，正面那一层正对玩家。
        facing = event.getEntity().getDirection();
        expiresAt = level.getGameTime() + PREVIEW_TICKS;
        previewLevel = level;
    }
}
