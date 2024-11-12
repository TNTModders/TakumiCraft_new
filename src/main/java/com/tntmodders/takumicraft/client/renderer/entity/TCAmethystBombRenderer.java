package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.entity.projectile.TCAmethystBomb;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;

public class TCAmethystBombRenderer extends ThrownItemRenderer<TCAmethystBomb> {

    private static final float SCALE = 2f;
    private final ItemRenderer itemRenderer;

    public TCAmethystBombRenderer(EntityRendererProvider.Context p_174416_) {
        super(p_174416_, SCALE, true);
        this.itemRenderer = p_174416_.getItemRenderer();
    }

    @Override
    public void render(ThrownItemRenderState p_362153_, PoseStack p_367133_, MultiBufferSource p_369201_, int p_366531_) {
        p_367133_.pushPose();
        p_367133_.scale(SCALE, SCALE, SCALE);
        p_367133_.mulPose(this.entityRenderDispatcher.cameraOrientation());
        if (p_362153_.itemModel != null) {
            this.itemRenderer
                    .render(
                            p_362153_.item, ItemDisplayContext.GROUND, false, p_367133_, p_369201_, p_366531_, OverlayTexture.NO_OVERLAY, p_362153_.itemModel
                    );
        }

        p_367133_.popPose();
        super.render(p_362153_, p_367133_, p_369201_, p_366531_);
    }
}
