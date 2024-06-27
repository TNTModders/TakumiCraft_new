package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCSkeletonCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TCSkeletonCreeperRenderer<T extends AbstractTCSkeletonCreeper, M extends SkeletonModel<T>> extends HumanoidMobRenderer<T, M> {

    public TCSkeletonCreeperRenderer(EntityRendererProvider.Context context, AbstractTCCreeper.TCCreeperContext creeperContext) {
        this(context, ModelLayers.SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR, creeperContext);
    }

    public TCSkeletonCreeperRenderer(EntityRendererProvider.Context p_174382_, ModelLayerLocation p_174383_, ModelLayerLocation p_174384_, ModelLayerLocation p_174385_,
                                     AbstractTCCreeper.TCCreeperContext creeperContext) {
        super(p_174382_, (M) new SkeletonModel(p_174382_.bakeLayer(p_174383_)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new SkeletonModel(p_174382_.bakeLayer(p_174384_)), new SkeletonModel(p_174382_.bakeLayer(p_174385_)), p_174382_.getModelManager()));
        this.addLayer(new TCCreeperPowerLayer(this, p_174382_.getModelSet(), new SkeletonModel(p_174382_.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)), creeperContext));
    }

    @Override
    public ResourceLocation getTextureLocation(T creeper) {
        return ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.getType().toShortString() + ".png");
    }

    @Override
    protected void scale(T p_114046_, PoseStack p_114047_, float p_114048_) {
        TCClientUtils.scaleSwelling(p_114046_, p_114047_, p_114048_);
    }

    @Override
    protected float getWhiteOverlayProgress(T p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    protected boolean isShaking(T p_113773_) {
        return p_113773_.isShaking();
    }
}