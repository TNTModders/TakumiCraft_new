package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCAllayCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
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
public class TCAllayCreeperRenderer<T extends TCAllayCreeper> extends MobRenderer<T, TCAllayCreeperRenderer.TCAllayCreeperModel<T>> {

    public TCAllayCreeperRenderer(EntityRendererProvider.Context p_234551_) {
        super(p_234551_, new TCAllayCreeperModel(p_234551_.bakeLayer(ModelLayers.ALLAY)), 0.4F);
        this.addLayer(new ItemInHandLayer<>(this, p_234551_.getItemInHandRenderer()));
        this.addLayer(new TCCreeperPowerLayer<>(this, p_234551_.getModelSet(), new TCAllayCreeperModel<>(p_234551_.bakeLayer(ModelLayers.ALLAY)), TCEntityCore.ALLAY, true));
    }

    @Override
    public ResourceLocation getTextureLocation(T creeper) {
        return ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.getType().toShortString() + ".png");
    }

    @Override
    protected int getBlockLightLevel(T p_234560_, BlockPos p_234561_) {
        return 15;
    }

    @Override
    protected void scale(T creeper, PoseStack poseStack, float p_114048_) {
        TCClientUtils.scaleSwelling(creeper, poseStack, p_114048_);
    }

    @Override
    protected float getWhiteOverlayProgress(T p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    public static class TCAllayCreeperModel<T extends TCAllayCreeper> extends HierarchicalModel<T> implements ArmedModel {
        private final ModelPart root;
        private final ModelPart head;
        private final ModelPart body;
        private final ModelPart right_arm;
        private final ModelPart left_arm;
        private final ModelPart right_wing;
        private final ModelPart left_wing;
        private static final float FLYING_ANIMATION_X_ROT = (float) (Math.PI / 4);
        private static final float MAX_HAND_HOLDING_ITEM_X_ROT_RAD = -1.134464F;
        private static final float MIN_HAND_HOLDING_ITEM_X_ROT_RAD = (float) (-Math.PI / 3);

        public TCAllayCreeperModel(ModelPart p_233312_) {
            super(RenderType::entityTranslucent);
            this.root = p_233312_.getChild("root");
            this.head = this.root.getChild("head");
            this.body = this.root.getChild("body");
            this.right_arm = this.body.getChild("right_arm");
            this.left_arm = this.body.getChild("left_arm");
            this.right_wing = this.body.getChild("right_wing");
            this.left_wing = this.body.getChild("left_wing");
        }

        @Override
        public ModelPart root() {
            return this.root;
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
        public void setupAnim(T p_233325_, float p_233326_, float p_233327_, float p_233328_, float p_233329_, float p_233330_) {
            this.root().getAllParts().forEach(ModelPart::resetPose);
            float f = p_233328_ * 20.0F * (float) (Math.PI / 180.0) + p_233326_;
            float f1 = Mth.cos(f) * (float) Math.PI * 0.15F + p_233327_;
            float f2 = p_233328_ - (float) p_233325_.tickCount;
            float f3 = p_233328_ * 9.0F * (float) (Math.PI / 180.0);
            float f4 = Math.min(p_233327_ / 0.3F, 1.0F);
            float f5 = 1.0F - f4;
            float f6 = p_233325_.getHoldingItemAnimationProgress(f2);
            if (p_233325_.isDancing()) {
                float f7 = p_233328_ * 8.0F * (float) (Math.PI / 180.0) + p_233327_;
                float f8 = Mth.cos(f7) * 16.0F * (float) (Math.PI / 180.0);
                float f9 = p_233325_.getSpinningProgress(f2);
                float f10 = Mth.cos(f7) * 14.0F * (float) (Math.PI / 180.0);
                float f11 = Mth.cos(f7) * 30.0F * (float) (Math.PI / 180.0);
                this.root.yRot = p_233325_.isSpinning() ? (float) (Math.PI * 4) * f9 : this.root.yRot;
                this.root.zRot = f8 * (1.0F - f9);
                this.head.yRot = f11 * (1.0F - f9);
                this.head.zRot = f10 * (1.0F - f9);
            } else {
                this.head.xRot = p_233330_ * (float) (Math.PI / 180.0);
                this.head.yRot = p_233329_ * (float) (Math.PI / 180.0);
            }

            this.right_wing.xRot = 0.43633232F * (1.0F - f4);
            this.right_wing.yRot = (float) (-Math.PI / 4) + f1;
            this.left_wing.xRot = 0.43633232F * (1.0F - f4);
            this.left_wing.yRot = (float) (Math.PI / 4) - f1;
            this.body.xRot = f4 * (float) (Math.PI / 4);
            float f12 = f6 * Mth.lerp(f4, (float) (-Math.PI / 3), -1.134464F);
            this.root.y = this.root.y + (float) Math.cos(f3) * 0.25F * f5;
            this.right_arm.xRot = f12;
            this.left_arm.xRot = f12;
            float f13 = f5 * (1.0F - f6);
            float f14 = 0.43633232F - Mth.cos(f3 + (float) (Math.PI * 3.0 / 2.0)) * (float) Math.PI * 0.075F * f13;
            this.left_arm.zRot = -f14;
            this.right_arm.zRot = f14;
            this.right_arm.yRot = 0.27925268F * f6;
            this.left_arm.yRot = -0.27925268F * f6;
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

