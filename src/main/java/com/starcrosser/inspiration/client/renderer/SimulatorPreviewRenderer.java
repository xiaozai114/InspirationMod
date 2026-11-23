package com.starcrosser.inspiration.client.renderer;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.client.extensions.ProjectorPreviewHandler;
import com.starcrosser.inspiration.multiblock.pattern.SimulatorPattern;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * 把结构投影仪的虚影画在主方块周围。
 * <p>
 * 锚点与朝向由 {@link ProjectorPreviewHandler} 决定（手持投影仪右键主方块时记下），
 * 这里只负责画：每格缩到 {@link #SCALE}（和 MBD2 自己的结构预览同一个做法），
 * 缩小的方块与四周的实体方块之间会留出一条缝，一眼就能看出这是预览而不是真摆出来的。
 * <p>
 * 画在 {@code AFTER_TRANSLUCENT_BLOCKS}：不透明方块已经写完深度，虚影能正常被地形遮挡。
 * 用的是独立的 {@link MultiBufferSource#immediate}，不去动 Minecraft 那一份共享缓冲，
 * 免得把别人还没画完的批次提前冲掉。
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Inspiration.MODID, value = Dist.CLIENT)
public final class SimulatorPreviewRenderer {

    private static final float SCALE = 0.8F;

    /** 满亮度，免得虚影跟着环境光忽明忽暗。 */
    private static final int LIGHT = 0xF000F0;

    private static final ByteBufferBuilder BYTES =
            new ByteBufferBuilder(RenderType.solid().bufferSize());

    private SimulatorPreviewRenderer() {
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || !ProjectorPreviewHandler.isActive()) {
            return;
        }
        BlockPos anchor = ProjectorPreviewHandler.anchor();

        PoseStack pose = event.getPoseStack();
        Camera camera = event.getCamera();
        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();
        MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(BYTES);

        pose.pushPose();
        Vec3 view = camera.getPosition();
        pose.translate(anchor.getX() - view.x, anchor.getY() - view.y, anchor.getZ() - view.z);
        for (com.starcrosser.inspiration.multiblock.MultiblockKind.Cell cell
                : ProjectorPreviewHandler.kind().cells(ProjectorPreviewHandler.facing())) {
            BlockPos offset = cell.offset();
            pose.pushPose();
            pose.translate(offset.getX() + 0.5F, offset.getY() + 0.5F, offset.getZ() + 0.5F);
            pose.scale(SCALE, SCALE, SCALE);
            pose.translate(-0.5F, -0.5F, -0.5F);
            dispatcher.renderSingleBlock(cell.state(), pose, buffers, LIGHT, OverlayTexture.NO_OVERLAY);
            pose.popPose();
        }
        pose.popPose();
        buffers.endBatch();
    }
}
