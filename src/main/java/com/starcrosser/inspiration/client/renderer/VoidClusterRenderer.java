package com.starcrosser.inspiration.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.voidenergy.entity.VoidClusterEntity;
import com.starcrosser.inspiration.item.armor.VoidGogglesItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

/**
 * 团簇的渲染：一张始终朝向摄像机的发光面片，颜色取当前阶段，尺寸随能量缓慢长大。
 * <p>
 * 不开剔除、两面都画 —— 面片在摄像机方向上会翻转，靠背面剔除会周期性闪没。
 */
public class VoidClusterRenderer extends EntityRenderer<VoidClusterEntity> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "textures/entity/void_cluster.png");
    private static final RenderType LAYER = RenderType.entityTranslucentEmissive(TEXTURE);

    public VoidClusterRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(VoidClusterEntity entity, float yaw, float partialTick, PoseStack pose,
                       MultiBufferSource buffers, int light) {
        int color = entity.stage().color();
        float alpha = 0.55F + 0.35F * Math.min(1F, entity.energy() / 36_000F);
        float size = 0.45F + 0.35F * Math.min(1F, entity.energy() / 36_000F);

        pose.pushPose();
        pose.mulPose(entityRenderDispatcher.cameraOrientation());
        Matrix4f matrix = pose.last().pose();
        VertexConsumer consumer = buffers.getBuffer(LAYER);

        quad(consumer, matrix, -size, size, color, alpha);
        quad(consumer, matrix, size, -size, color, alpha);

        renderGogglesLabel(entity, pose, buffers);

        pose.popPose();
    }

    /**
     * 戴着虚空护目镜时，把团簇的具体能量写在它上方。
     * <p>
     * 只在本地玩家戴着头盔时绘制 —— 免得别人看不到、自己却以为所有人都能看到。
     */
    private void renderGogglesLabel(VoidClusterEntity entity, PoseStack pose, MultiBufferSource buffers) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null
                || !(minecraft.player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof VoidGogglesItem)) {
            return;
        }
        Component label = Component.literal(entity.energy() + " ve");
        pose.pushPose();
        pose.translate(0.0F, 0.75F, 0.0F);
        pose.mulPose(entityRenderDispatcher.cameraOrientation());
        pose.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix = pose.last().pose();
        minecraft.font.drawInBatch(label, -minecraft.font.width(label) / 2.0F, 0.0F,
                0xFFFFFFFF, false, matrix, buffers, Font.DisplayMode.SEE_THROUGH, 0, 0xF000F0);
        pose.popPose();
    }

    private void quad(VertexConsumer consumer, Matrix4f matrix, float left, float right, int color, float alpha) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = Math.round(alpha * 255F);
        float top = -right;
        float bottom = -left;

        vertex(consumer, matrix, left, bottom, 0F, 0F, 1F, r, g, b, a);
        vertex(consumer, matrix, right, bottom, 0F, 1F, 1F, r, g, b, a);
        vertex(consumer, matrix, right, top, 0F, 1F, 0F, r, g, b, a);
        vertex(consumer, matrix, left, top, 0F, 0F, 0F, r, g, b, a);
    }

    private void vertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z,
                        float u, float v, int r, int g, int b, int a) {
        consumer.addVertex(matrix, x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0F, 0F, 1F);
    }

    @Override
    public ResourceLocation getTextureLocation(VoidClusterEntity entity) {
        return TEXTURE;
    }
}
