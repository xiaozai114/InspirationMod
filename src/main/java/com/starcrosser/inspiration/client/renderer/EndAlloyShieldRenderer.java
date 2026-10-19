package com.starcrosser.inspiration.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.starcrosser.inspiration.Inspiration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * 末地合金盾牌的物品渲染：直接用原版那套盾牌模型，只换贴图。
 * <p>
 * 为什么必须自己写：原版把「盾牌长什么样」硬编码在
 * {@code BlockEntityWithoutLevelRenderer#renderByItem} 里（判的是 {@code stack.is(Items.SHIELD)}），
 * 自定义盾牌走到那里会掉进 else 分支、什么都不画。所以这里照抄那一段，把 atlas 素材换成
 * 我们自己的一张独立贴图，其余（plate / handle 的模型、姿态、发光附魔的光泽）一模一样。
 * <p>
 * 贴图是原版盾牌展开图改的（{@code tools/textures/gen_end_alloys.py}），所以模型的 UV 天然对得上；
 * 又因为走独立贴图而不是盾牌纹饰 atlas，不需要往 atlas 里塞 sprite。
 */
public class EndAlloyShieldRenderer extends BlockEntityWithoutLevelRenderer {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            Inspiration.MODID, "textures/entity/end_alloy_shield.png");

    private static EndAlloyShieldRenderer instance;

    private final ShieldModel model;

    private EndAlloyShieldRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
        this.model = new ShieldModel(
                Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SHIELD));
    }

    /**
     * 懒加载的单例。
     * <p>
     * 扩展注册事件跑在客户端初始化之前，那时 {@code Minecraft.getInstance()} 还没影，
     * 所以不能在注册的时候就把渲染器造出来；而 {@code getCustomRenderer()} 又是每帧都问一次，
     * 也不能每次都新建。
     */
    public static EndAlloyShieldRenderer get() {
        if (instance == null) {
            instance = new EndAlloyShieldRenderer();
        }
        return instance;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        // 原版盾牌模型是「面朝 -Z」建的，渲染前先翻一下 y、z
        poseStack.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(
                buffer, this.model.renderType(TEXTURE), true, stack.hasFoil());
        this.model.handle().render(poseStack, consumer, packedLight, packedOverlay);
        this.model.plate().render(poseStack, consumer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
