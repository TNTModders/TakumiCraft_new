package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCSkeletonCreeper;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class TCWitherSkeletonCreeperRenderer extends TCSkeletonCreeperRenderer {
    public TCWitherSkeletonCreeperRenderer(EntityRendererProvider.Context context, AbstractTCCreeper.TCCreeperContext creeperContext) {
        super(context, ModelLayers.WITHER_SKELETON, ModelLayers.WITHER_SKELETON_INNER_ARMOR, ModelLayers.WITHER_SKELETON_OUTER_ARMOR, creeperContext);
    }

    @Override
    protected void scale(AbstractTCSkeletonCreeper p_114046_, PoseStack p_114047_, float p_114048_) {
        p_114047_.scale(1.2F, 1.2F, 1.2F);
        super.scale(p_114046_, p_114047_, p_114048_);
    }
}
