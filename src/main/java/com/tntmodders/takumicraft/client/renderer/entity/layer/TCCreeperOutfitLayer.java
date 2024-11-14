package com.tntmodders.takumicraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
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

import java.util.Objects;

public class TCCreeperOutfitLayer<T extends TCCreeperRenderState, M extends CreeperModel> extends RenderLayer<T, M> {

    private static final ResourceLocation XMAS_YUKARI = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/seasons/xmas_yukari.png");
    private static final ResourceLocation NY_YUKARI = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/seasons/newyear_yukari.png");
    private static final ResourceLocation NY_CREEPER = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/seasons/newyear.png");

    private final M model;

    public TCCreeperOutfitLayer(RenderLayerParent parent, EntityModelSet modelSet, M model) {
        super(parent);
        this.model = model;
    }

    protected ResourceLocation getTextureLocation(T creeper) {
        if (Objects.equals(creeper.context.getRegistryName(), TCEntityCore.YUKARI.getRegistryName())) {
            return TCEntityUtils.isXmas() ? XMAS_YUKARI : TCEntityUtils.isNewYear() ? NY_YUKARI : null;
        }
        return TCEntityUtils.isNewYear() ? NY_CREEPER : null;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource source, int p_116972_, T creeper, float p_116974_, float p_116975_) {
        ResourceLocation location = this.getTextureLocation(creeper);
        if (location != null) {
            poseStack.pushPose();
            EntityModel entitymodel = this.model;
            VertexConsumer vertexconsumer = source.getBuffer(RenderType.armorCutoutNoCull(location));
            entitymodel.setupAnim(creeper);
            entitymodel.renderToBuffer(poseStack, vertexconsumer, p_116972_, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }
}