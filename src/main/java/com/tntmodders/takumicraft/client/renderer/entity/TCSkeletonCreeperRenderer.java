package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCSkeletonCreeperRenderState;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCSkeletonCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TCSkeletonCreeperRenderer<T extends AbstractTCSkeletonCreeper, S extends TCSkeletonCreeperRenderState, M extends SkeletonModel<S>> extends HumanoidMobRenderer<T, S, M> {

    public TCSkeletonCreeperRenderer(EntityRendererProvider.Context context, AbstractTCCreeper.TCCreeperContext creeperContext) {
        this(context, ModelLayers.SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR, creeperContext);
    }

    public TCSkeletonCreeperRenderer(EntityRendererProvider.Context p_174382_, ModelLayerLocation p_174383_, ModelLayerLocation p_174384_, ModelLayerLocation p_174385_,
                                     AbstractTCCreeper.TCCreeperContext creeperContext) {
        super(p_174382_, (M) new SkeletonModel(p_174382_.bakeLayer(p_174383_)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new SkeletonModel(p_174382_.bakeLayer(p_174383_)), new SkeletonModel(p_174382_.bakeLayer(p_174383_)), p_174382_.getEquipmentRenderer()));
        this.addLayer(new TCCreeperPowerLayer(this, p_174382_.getModelSet(), new SkeletonModel(p_174382_.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)), creeperContext));
    }

    @Override
    public ResourceLocation getTextureLocation(S creeper) {
        return ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.context.entityType().toShortString() + ".png");
    }


    @Override
    protected void scale(S state, PoseStack poseStack) {
        float sizeFactor = state.context.getSizeFactor();
        poseStack.scale(sizeFactor, sizeFactor, sizeFactor);
        TCClientUtils.scaleSwelling(state.swelling, poseStack);
    }

    @Override
    protected float getWhiteOverlayProgress(S state) {
        float f = state.swelling;
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public S createRenderState() {
        return (S) new TCSkeletonCreeperRenderState();
    }

    @Override
    public void extractRenderState(T creeper, S state, float f) {
        super.extractRenderState(creeper, state, f);
        state.swelling = creeper.getSwelling(f);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.isOnBook = creeper.isOnBook();
        state.isAggressive = creeper.isAggressive();
        state.isShaking = creeper.isShaking();
    }
}