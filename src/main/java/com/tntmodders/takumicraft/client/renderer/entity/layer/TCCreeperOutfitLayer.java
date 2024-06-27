package com.tntmodders.takumicraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.entity.mobs.TCYukariCreeper;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class TCCreeperOutfitLayer<T extends AbstractTCCreeper, M extends CreeperModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation XMAS_YUKARI = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/seasons/xmas_yukari.png");
    private static final ResourceLocation NY_YUKARI = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/seasons/newyear_yukari.png");
    private static final ResourceLocation NY_CREEPER = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/seasons/newyear.png");

    private final M model;

    public TCCreeperOutfitLayer(RenderLayerParent parent, EntityModelSet modelSet, M model) {
        super(parent);
        this.model = model;
    }

    @Override
    protected ResourceLocation getTextureLocation(T creeper) {
        if (creeper instanceof TCYukariCreeper) {
            return TCEntityUtils.isXmas() ? XMAS_YUKARI : TCEntityUtils.isNewYear() ? NY_YUKARI : null;
        }
        return TCEntityUtils.isNewYear() ? NY_CREEPER : null;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource source, int p_116972_, T creeper, float p_116974_, float p_116975_, float p_116976_, float p_116977_, float p_116978_, float p_116979_) {
        ResourceLocation location = this.getTextureLocation(creeper);
        if (location != null) {
            poseStack.pushPose();
            EntityModel<T> entitymodel = this.model;
            entitymodel.prepareMobModel(creeper, p_116974_, p_116975_, p_116976_);
            this.getParentModel().copyPropertiesTo(entitymodel);
            VertexConsumer vertexconsumer = source.getBuffer(RenderType.armorCutoutNoCull(location));
            entitymodel.setupAnim(creeper, p_116974_, p_116975_, p_116977_, p_116978_, p_116979_);
            entitymodel.renderToBuffer(poseStack, vertexconsumer, p_116972_, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }
}