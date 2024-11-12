package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCBeeCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCBeeCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCBeeCreeperRenderer<T extends TCBeeCreeper, S extends TCBeeCreeperRenderState> extends MobRenderer<T, S, TCBeeCreeperRenderer.TCBeeCreeperModel<S>> {
    private static final ResourceLocation LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/beecreeper.png");

    public TCBeeCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new TCBeeCreeperModel(p_173956_.bakeLayer(ModelLayers.BEE)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer<>(this, p_173956_.getModelSet(), new TCBeeCreeperModel(p_173956_.bakeLayer(ModelLayers.BEE)), TCEntityCore.BEE, true));
    }

    @Override
    public S createRenderState() {
        return (S) new TCBeeCreeperRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(S p_114482_) {
        return LOCATION;
    }

    @Override
    protected void scale(S state, PoseStack poseStack) {
        TCClientUtils.scaleSwelling(state.swelling, poseStack);
    }

    @Override
    protected float getWhiteOverlayProgress(S state) {
        float f = state.swelling;
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public void extractRenderState(T creeper, S state, float f) {
        super.extractRenderState(creeper, state, f);
        state.rollAmount = creeper.getRollAmount(f);
        state.hasStinger = true;
        state.isOnGround = creeper.onGround() && creeper.getDeltaMovement().lengthSqr() < 1.0E-7;
        state.isAngry = true;
        state.hasNectar = true;
        state.swelling = creeper.getSwelling(f);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.isOnBook = creeper.isOnBook();
    }

    public static class TCBeeCreeperModel<T extends TCBeeCreeperRenderState> extends EntityModel<T> {
        private static final float BEE_Y_BASE = 19.0F;
        private static final String BONE = "bone";
        private static final String STINGER = "stinger";
        private static final String LEFT_ANTENNA = "left_antenna";
        private static final String RIGHT_ANTENNA = "right_antenna";
        private static final String FRONT_LEGS = "front_legs";
        private static final String MIDDLE_LEGS = "middle_legs";
        private static final String BACK_LEGS = "back_legs";
        private final ModelPart bone;
        private final ModelPart rightWing;
        private final ModelPart leftWing;
        private final ModelPart frontLeg;
        private final ModelPart midLeg;
        private final ModelPart backLeg;
        private final ModelPart stinger;
        private final ModelPart leftAntenna;
        private final ModelPart rightAntenna;
        private float rollAmount;

        public TCBeeCreeperModel(ModelPart p_170439_) {
            super(p_170439_);
            this.bone = p_170439_.getChild("bone");
            ModelPart modelpart = this.bone.getChild("body");
            this.stinger = modelpart.getChild("stinger");
            this.leftAntenna = modelpart.getChild("left_antenna");
            this.rightAntenna = modelpart.getChild("right_antenna");
            this.rightWing = this.bone.getChild("right_wing");
            this.leftWing = this.bone.getChild("left_wing");
            this.frontLeg = this.bone.getChild("front_legs");
            this.midLeg = this.bone.getChild("middle_legs");
            this.backLeg = this.bone.getChild("back_legs");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();
            PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 19.0F, 0.0F));
            PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild(
                    "body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F), PartPose.ZERO
            );
            partdefinition2.addOrReplaceChild("stinger", CubeListBuilder.create().texOffs(26, 7).addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F), PartPose.ZERO);
            partdefinition2.addOrReplaceChild(
                    "left_antenna", CubeListBuilder.create().texOffs(2, 0).addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F), PartPose.offset(0.0F, -2.0F, -5.0F)
            );
            partdefinition2.addOrReplaceChild(
                    "right_antenna",
                    CubeListBuilder.create().texOffs(2, 3).addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F),
                    PartPose.offset(0.0F, -2.0F, -5.0F)
            );
            CubeDeformation cubedeformation = new CubeDeformation(0.001F);
            partdefinition1.addOrReplaceChild(
                    "right_wing",
                    CubeListBuilder.create().texOffs(0, 18).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, cubedeformation),
                    PartPose.offsetAndRotation(-1.5F, -4.0F, -3.0F, 0.0F, -0.2618F, 0.0F)
            );
            partdefinition1.addOrReplaceChild(
                    "left_wing",
                    CubeListBuilder.create().texOffs(0, 18).mirror().addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, cubedeformation),
                    PartPose.offsetAndRotation(1.5F, -4.0F, -3.0F, 0.0F, 0.2618F, 0.0F)
            );
            partdefinition1.addOrReplaceChild(
                    "front_legs", CubeListBuilder.create().addBox("front_legs", -5.0F, 0.0F, 0.0F, 7, 2, 0, 26, 1), PartPose.offset(1.5F, 3.0F, -2.0F)
            );
            partdefinition1.addOrReplaceChild(
                    "middle_legs", CubeListBuilder.create().addBox("middle_legs", -5.0F, 0.0F, 0.0F, 7, 2, 0, 26, 3), PartPose.offset(1.5F, 3.0F, 0.0F)
            );
            partdefinition1.addOrReplaceChild(
                    "back_legs", CubeListBuilder.create().addBox("back_legs", -5.0F, 0.0F, 0.0F, 7, 2, 0, 26, 5), PartPose.offset(1.5F, 3.0F, 2.0F)
            );
            return LayerDefinition.create(meshdefinition, 64, 64);
        }

        @Override
        public void setupAnim(T state) {
            this.rollAmount = state.rollAmount;
            this.stinger.visible = true;
            this.rightWing.xRot = 0.0F;
            this.leftAntenna.xRot = 0.0F;
            this.rightAntenna.xRot = 0.0F;
            this.bone.xRot = 0.0F;
            boolean flag = state.isOnGround;
            if (flag) {
                this.rightWing.yRot = -0.2618F;
                this.rightWing.zRot = 0.0F;
                this.leftWing.xRot = 0.0F;
                this.leftWing.yRot = 0.2618F;
                this.leftWing.zRot = 0.0F;
                this.frontLeg.xRot = 0.0F;
                this.midLeg.xRot = 0.0F;
                this.backLeg.xRot = 0.0F;
            } else {
                float f = state.ageInTicks * 120.32113F * (float) (Math.PI / 180.0);
                this.rightWing.yRot = 0.0F;
                this.rightWing.zRot = Mth.cos(f) * (float) Math.PI * 0.15F;
                this.leftWing.xRot = this.rightWing.xRot;
                this.leftWing.yRot = this.rightWing.yRot;
                this.leftWing.zRot = -this.rightWing.zRot;
                this.frontLeg.xRot = (float) (Math.PI / 4);
                this.midLeg.xRot = (float) (Math.PI / 4);
                this.backLeg.xRot = (float) (Math.PI / 4);
                this.bone.xRot = 0.0F;
                this.bone.yRot = 0.0F;
                this.bone.zRot = 0.0F;
            }

            if (this.rollAmount > 0.0F) {
                this.bone.xRot = Mth.rotLerpRad(this.bone.xRot, 3.0915928F, this.rollAmount);
            }
        }
    }
}