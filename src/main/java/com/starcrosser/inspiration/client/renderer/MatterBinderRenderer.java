package com.starcrosser.inspiration.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.starcrosser.inspiration.injection.blockentity.MatterBinderTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 物质束缚器上方悬浮的那一份物质。
 * <p>
 * 台座本身由方块模型画出来，这里只负责顶面之上那件「被束缚的东西」：绕竖轴慢慢自转，
 * 同时上下轻微浮动。玩家不用打开 GUI 就能看出束缚器里有没有东西、放的是什么。
 */
public class MatterBinderRenderer implements BlockEntityRenderer<MatterBinderTile> {

    /** 台座顶面在 10/16 处，物品再往上抬一点，看起来才是浮着的。 */
    private static final float BASE_HEIGHT = 0.72F;
    private static final float BOB_AMPLITUDE = 0.03F;
    private static final float BOB_PERIOD = 12.0F;
    private static final float SPIN_SPEED = 1.5F;
    private static final float SCALE = 0.5F;

    public MatterBinderRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MatterBinderTile tile, float partialTick, PoseStack pose,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemStack stack = tile.getBase();
        Level level = tile.getLevel();
        if (stack.isEmpty() || level == null) {
            return;
        }

        float time = level.getGameTime() + partialTick;
        pose.pushPose();
        pose.translate(0.5F, BASE_HEIGHT + Mth.sin(time / BOB_PERIOD) * BOB_AMPLITUDE, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(time * SPIN_SPEED));
        pose.scale(SCALE, SCALE, SCALE);

        // 用物品所在那一格的光照，避免台座阴影里的物品发黑
        int light = LevelRenderer.getLightColor(level, tile.getBlockPos().above());
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, light, packedOverlay, pose, buffer, level, 0);
        pose.popPose();
    }

    /** 台座很矮，超出这个距离就没必要再画悬浮物品了。 */
    @Override
    public int getViewDistance() {
        return 24;
    }
}
