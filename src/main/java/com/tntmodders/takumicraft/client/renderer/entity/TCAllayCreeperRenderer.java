package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCAllayCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCAllayCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCAllayCreeperRenderer<T extends TCAllayCreeper, S extends TCAllayCreeperRenderState> extends MobRenderer<T, S, TCAllayCreeperRenderer.TCAllayCreeperModel<S>> {

    public TCAllayCreeperRenderer(EntityRendererProvider.Context p_234551_) {
        super(p_234551_, new TCAllayCreeperModel(p_234551_.bakeLayer(ModelLayers.ALLAY)), 0.4F);
        this.addLayer(new ItemInHandLayer<>(this, p_234551_.getItemRenderer()));
        this.addLayer(new TCCreeperPowerLayer<>(this, p_234551_.getModelSet(), new TCAllayCreeperModel(p_234551_.bakeLayer(ModelLayers.ALLAY)), TCEntityCore.ALLAY, true));
    }

    @Override
    public S createRenderState() {
        return (S) new TCAllayCreeperRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(S creeper) {
        return ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.context.entityType().toShortString() + ".png");
    }

    @Override
    protected int getBlockLightLevel(T p_234560_, BlockPos p_234561_) {
        return 15;
    }

    @Override
    protected void scale(S creeper, PoseStack poseStack) {
        TCClientUtils.scaleSwelling(creeper.swelling, poseStack);
    }

    @Override
    protected float getWhiteOverlayProgress(S state) {
        float f = state.swelling;
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public void extractRenderState(T creeper, S state, float p_362065_) {
        super.extractRenderState(creeper, state, p_362065_);
        state.isDancing = creeper.isDancing();
        state.isSpinning = creeper.isSpinning();
        state.spinningProgress = creeper.getSpinningProgress(p_362065_);
        state.holdingAnimationProgress = creeper.getHoldingItemAnimationProgress(p_362065_);
        state.swelling = creeper.getSwelling(p_362065_);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.isOnBook = creeper.isOnBook();
    }


    @OnlyIn(Dist.CLIENT)
    public static class TCAllayCreeperModel<T extends TCAllayCreeperRenderState> extends EntityModel<T> implements ArmedModel {
        private static final float FLYING_ANIMATION_X_ROT = (float) (Math.PI / 4);
        private static final float MAX_HAND_HOLDING_ITEM_X_ROT_RAD = -1.134464F;
        private static final float MIN_HAND_HOLDING_ITEM_X_ROT_RAD = (float) (-Math.PI / 3);
        private final ModelPart root;
        private final ModelPart head;
        private final ModelPart body;
        private final ModelPart right_arm;
        private final ModelPart left_arm;
        private final ModelPart right_wing;
        private final ModelPart left_wing;

        public TCAllayCreeperModel(ModelPart p_233312_) {
            super(p_233312_.getChild("root"), RenderType::entityTranslucent);
            this.root = p_233312_.getChild("root");
            this.head = this.root.getChild("head");
            this.body = this.root.getChild("body");
            this.right_arm = this.body.getChild("right_arm");
            this.left_arm = this.body.getChild("left_arm");
            this.right_wing = this.body.getChild("right_wing");
            this.left_wing = this.body.getChild("left_wing");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();
            PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 23.5F, 0.0F));
            partdefinition1.addOrReplaceChild(
                    "head",
                    CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
                    PartPose.offset(0.0F, -3.99F, 0.0F)
            );
            PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild(
                    "body",
                    CubeListBuilder.create()
                            .texOffs(0, 10)
                            .addBox(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                            .texOffs(0, 16)
                            .addBox(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(-0.2F)),
                    PartPose.offset(0.0F, -4.0F, 0.0F)
            );
            partdefinition2.addOrReplaceChild(
                    "right_arm",
                    CubeListBuilder.create().texOffs(23, 0).addBox(-0.75F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(-0.01F)),
                    PartPose.offset(-1.75F, 0.5F, 0.0F)
            );
            partdefinition2.addOrReplaceChild(
                    "left_arm",
                    CubeListBuilder.create().texOffs(23, 6).addBox(-0.25F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(-0.01F)),
                    PartPose.offset(1.75F, 0.5F, 0.0F)
            );
            partdefinition2.addOrReplaceChild(
                    "right_wing",
                    CubeListBuilder.create().texOffs(16, 14).addBox(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
                    PartPose.offset(-0.5F, 0.0F, 0.6F)
            );
            partdefinition2.addOrReplaceChild(
                    "left_wing",
                    CubeListBuilder.create().texOffs(16, 14).addBox(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
                    PartPose.offset(0.5F, 0.0F, 0.6F)
            );
            return LayerDefinition.create(meshdefinition, 32, 32);
        }

        @Override
        public void setupAnim(TCAllayCreeperRenderState state) {
            super.setupAnim((T) state);
            float f = state.walkAnimationSpeed;
            float f1 = state.walkAnimationPos;
            float f2 = state.ageInTicks * 20.0F * (float) (Math.PI / 180.0) + f1;
            float f3 = Mth.cos(f2) * (float) Math.PI * 0.15F + f;
            float f4 = state.ageInTicks * 9.0F * (float) (Math.PI / 180.0);
            float f5 = Math.min(f / 0.3F, 1.0F);
            float f6 = 1.0F - f5;
            float f7 = state.holdingAnimationProgress;
            if (state.isDancing) {
                float f8 = state.ageInTicks * 8.0F * (float) (Math.PI / 180.0) + f;
                float f9 = Mth.cos(f8) * 16.0F * (float) (Math.PI / 180.0);
                float f10 = state.spinningProgress;
                float f11 = Mth.cos(f8) * 14.0F * (float) (Math.PI / 180.0);
                float f12 = Mth.cos(f8) * 30.0F * (float) (Math.PI / 180.0);
                this.root.yRot = state.isSpinning ? (float) (Math.PI * 4) * f10 : this.root.yRot;
                this.root.zRot = f9 * (1.0F - f10);
                this.head.yRot = f12 * (1.0F - f10);
                this.head.zRot = f11 * (1.0F - f10);
            } else {
                this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
                this.head.yRot = state.yRot * (float) (Math.PI / 180.0);
            }

            this.right_wing.xRot = 0.43633232F * (1.0F - f5);
            this.right_wing.yRot = (float) (-Math.PI / 4) + f3;
            this.left_wing.xRot = 0.43633232F * (1.0F - f5);
            this.left_wing.yRot = (float) (Math.PI / 4) - f3;
            this.body.xRot = f5 * (float) (Math.PI / 4);
            float f13 = f7 * Mth.lerp(f5, (float) (-Math.PI / 3), -1.134464F);
            this.root.y = this.root.y + (float) Math.cos(f4) * 0.25F * f6;
            this.right_arm.xRot = f13;
            this.left_arm.xRot = f13;
            float f14 = f6 * (1.0F - f7);
            float f15 = 0.43633232F - Mth.cos(f4 + (float) (Math.PI * 3.0 / 2.0)) * (float) Math.PI * 0.075F * f14;
            this.left_arm.zRot = -f15;
            this.right_arm.zRot = f15;
            this.right_arm.yRot = 0.27925268F * f7;
            this.left_arm.yRot = -0.27925268F * f7;
        }

        @Override
        public void translateToHand(HumanoidArm p_233322_, PoseStack p_233323_) {
            float f = 1.0F;
            float f1 = 3.0F;
            this.root.translateAndRotate(p_233323_);
            this.body.translateAndRotate(p_233323_);
            p_233323_.translate(0.0F, 0.0625F, 0.1875F);
            p_233323_.mulPose(Axis.XP.rotation(this.right_arm.xRot));
            p_233323_.scale(0.7F, 0.7F, 0.7F);
            p_233323_.translate(0.0625F, 0.0F, 0.0F);
        }
    }
}

