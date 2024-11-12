package com.tntmodders.takumicraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCCreeperRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class TCMuscleCreeperArmLayer<T extends TCCreeperRenderState, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation WINGS_LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/musclecreeperarm.png");
    private final MuscleCreeperArmModel<T> elytraModel;

    public TCMuscleCreeperArmLayer(RenderLayerParent p_174493_, EntityModelSet p_174494_) {
        super(p_174493_);
        this.elytraModel = new MuscleCreeperArmModel(p_174494_.bakeLayer(ModelLayers.ELYTRA));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource source, int p_364275_, T state, float p_368401_, float p_362513_) {
        ResourceLocation resourcelocation = WINGS_LOCATION;
        MuscleCreeperArmModel elytramodel = this.elytraModel;
        pose.pushPose();
        pose.translate(0.0F, 0.0F, 0.125F);
        elytramodel.setupAnim(state);
        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(source, RenderType.armorCutoutNoCull(resourcelocation), false);
        this.elytraModel.renderToBuffer(pose, vertexconsumer, p_364275_, OverlayTexture.NO_OVERLAY);
        pose.popPose();
    }

    protected static class MuscleCreeperArmModel<T extends TCCreeperRenderState> extends EntityModel<T> {
        private final ModelPart rightWing;
        private final ModelPart leftWing;

        public MuscleCreeperArmModel(ModelPart p_170538_) {
            super(p_170538_);
            this.leftWing = p_170538_.getChild("left_wing");
            this.rightWing = p_170538_.getChild("right_wing");
        }

        @Override
        public void setupAnim(T creeper) {
            int i = creeper.isOnBook() ? (int) System.currentTimeMillis() / 500 : (int) (creeper.ageInTicks / 10);
            if (i % 2 == 0) {
                this.leftWing.y = 8f;
                this.leftWing.x = 14f;
                this.leftWing.z = 0f;
                this.leftWing.xRot = (float) Math.PI;
            } else {
                this.leftWing.y = 4f;
                this.leftWing.x = 12f;
                this.leftWing.z = -6f;
                this.leftWing.xRot = 0;
            }
            this.rightWing.y = this.leftWing.y;
            this.rightWing.x = -this.leftWing.x;
            this.rightWing.z = this.leftWing.z;
            this.rightWing.xRot = this.leftWing.xRot;
        }
    }
}
