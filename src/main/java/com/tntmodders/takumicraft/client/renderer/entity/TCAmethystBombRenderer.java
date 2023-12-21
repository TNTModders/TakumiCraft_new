package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.entity.projectile.TCAmethystBomb;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;

public class TCAmethystBombRenderer extends ThrownItemRenderer<TCAmethystBomb> {

    private final ItemRenderer itemRenderer;
    private static final float SCALE = 2f;

    public TCAmethystBombRenderer(EntityRendererProvider.Context p_174416_) {
        super(p_174416_, SCALE, true);
        this.itemRenderer = p_174416_.getItemRenderer();
    }

    @Override
    public void render(TCAmethystBomb bomb, float p_116086_, float p_116087_, PoseStack poseStack, MultiBufferSource source, int p_116090_) {
        if (bomb.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(bomb) < 12.25D)) {
            poseStack.pushPose();
            poseStack.scale(SCALE,SCALE,SCALE);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            this.itemRenderer.renderStatic(bomb.getItem(), ItemDisplayContext.GROUND, p_116090_, OverlayTexture.NO_OVERLAY, poseStack, source, bomb.level(), bomb.getId());
            poseStack.popPose();

            super.render(bomb, p_116086_, p_116087_, poseStack, source, p_116090_);
        }
    }
}
