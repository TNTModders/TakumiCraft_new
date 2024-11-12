package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCVexCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCVexCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCVexCreeperRenderer<T extends TCVexCreeper, S extends TCVexCreeperRenderState, M extends TCVexCreeperRenderer.TCVexCreeperModel<S>> extends MobRenderer<T, S, M> {
    private static final ResourceLocation LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/vexcreeper.png");
    private static final ResourceLocation LOCATION_CHARGING = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/vexcreeper.png");

    public TCVexCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, (M) new TCVexCreeperModel(p_173956_.bakeLayer(ModelLayers.VEX)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer(this, p_173956_.getModelSet(), new TCVexCreeperModel<>(p_173956_.bakeLayer(ModelLayers.VEX)), TCEntityCore.VEX, true));
        this.addLayer(new ItemInHandLayer<>(this, p_173956_.getItemRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(S p_114482_) {
        return p_114482_.isCharging ? LOCATION_CHARGING : LOCATION;
    }

    @Override
    protected int getBlockLightLevel(T p_116298_, BlockPos p_116299_) {
        return 15;
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
        return (S) new TCVexCreeperRenderState();
    }

    @Override
    public void extractRenderState(T creeper, S state, float f) {
        super.extractRenderState(creeper, state, f);
        state.swelling = creeper.getSwelling(f);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.isOnBook = creeper.isOnBook();
        state.isCharging = creeper.isCharging();
    }

    public static class TCVexCreeperModel<T extends TCVexCreeperRenderState> extends EntityModel<T> implements ArmedModel {
        private final ModelPart root;
        private final ModelPart body;
        private final ModelPart rightArm;
        private final ModelPart leftArm;
        private final ModelPart rightWing;
        private final ModelPart leftWing;
        private final ModelPart head;

        public TCVexCreeperModel(ModelPart p_171045_) {
            super(p_171045_.getChild("root"), RenderType::entityTranslucent);
            this.root = p_171045_.getChild("root");
            this.body = this.root.getChild("body");
            this.rightArm = this.body.getChild("right_arm");
            this.leftArm = this.body.getChild("left_arm");
            this.rightWing = this.body.getChild("right_wing");
            this.leftWing = this.body.getChild("left_wing");
            this.head = this.root.getChild("head");
        }

        @Override
        public void setupAnim(T state) {
            this.root().getAllParts().forEach(ModelPart::resetPose);
            this.head.yRot = state.yRot * (float) (Math.PI / 180.0);
            this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
            float f = Mth.cos(state.ageInTicks * 5.5F * (float) (Math.PI / 180.0)) * 0.1F;
            this.rightArm.zRot = (float) (Math.PI / 5) + f;
            this.leftArm.zRot = -((float) (Math.PI / 5) + f);
            if (state.isCharging) {
                this.body.xRot = 0.0F;
                this.setArmsCharging(state.rightHandItem, state.leftHandItem, f);
            } else {
                this.body.xRot = (float) (Math.PI / 20);
            }

            this.leftWing.yRot = 1.0995574F + Mth.cos(state.ageInTicks * 45.836624F * (float) (Math.PI / 180.0)) * (float) (Math.PI / 180.0) * 16.2F;
            this.rightWing.yRot = -this.leftWing.yRot;
            this.leftWing.xRot = 0.47123888F;
            this.leftWing.zRot = -0.47123888F;
            this.rightWing.xRot = 0.47123888F;
            this.rightWing.zRot = 0.47123888F;
        }

        private void setArmsCharging(ItemStack p_265484_, ItemStack p_265329_, float p_265125_) {
            if (p_265484_.isEmpty() && p_265329_.isEmpty()) {
                this.rightArm.xRot = -1.2217305F;
                this.rightArm.yRot = (float) (Math.PI / 12);
                this.rightArm.zRot = -0.47123888F - p_265125_;
                this.leftArm.xRot = -1.2217305F;
                this.leftArm.yRot = (float) (-Math.PI / 12);
                this.leftArm.zRot = 0.47123888F + p_265125_;
            } else {
                if (!p_265484_.isEmpty()) {
                    this.rightArm.xRot = (float) (Math.PI * 7.0 / 6.0);
                    this.rightArm.yRot = (float) (Math.PI / 12);
                    this.rightArm.zRot = -0.47123888F - p_265125_;
                }

                if (!p_265329_.isEmpty()) {
                    this.leftArm.xRot = (float) (Math.PI * 7.0 / 6.0);
                    this.leftArm.yRot = (float) (-Math.PI / 12);
                    this.leftArm.zRot = 0.47123888F + p_265125_;
                }
            }
        }

        @Override
        public void translateToHand(HumanoidArm p_259770_, PoseStack p_260351_) {
            boolean flag = p_259770_ == HumanoidArm.RIGHT;
            ModelPart modelpart = flag ? this.rightArm : this.leftArm;
            this.root.translateAndRotate(p_260351_);
            this.body.translateAndRotate(p_260351_);
            modelpart.translateAndRotate(p_260351_);
            p_260351_.scale(0.55F, 0.55F, 0.55F);
            this.offsetStackPosition(p_260351_, flag);
        }

        private void offsetStackPosition(PoseStack p_263343_, boolean p_263414_) {
            if (p_263414_) {
                p_263343_.translate(0.046875, -0.15625, 0.078125);
            } else {
                p_263343_.translate(-0.046875, -0.15625, 0.078125);
            }
        }
    }
}