package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCBatCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.animation.definitions.BatAnimation;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCBatCreeperRenderer<T extends TCBatCreeper> extends MobRenderer<T, TCBatCreeperRenderer.TCBatCreeperModel<T>> {

    public TCBatCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new TCBatCreeperModel(p_173956_.bakeLayer(ModelLayers.BAT)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer<>(this, p_173956_.getModelSet(), new TCBatCreeperModel<>(p_173956_.bakeLayer(ModelLayers.BAT)), TCEntityCore.BAT, true));
    }

    @Override
    public ResourceLocation getTextureLocation(T creeper) {
        return ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.getType().toShortString() + ".png");
    }

    @Override
    protected void scale(T creeper, PoseStack poseStack, float p_114048_) {
        float f = creeper.getContext().getSizeFactor();
        poseStack.scale(f, f, f);
        TCClientUtils.scaleSwelling(creeper, poseStack, p_114048_);
    }

    @Override
    protected float getWhiteOverlayProgress(T p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    public static class TCBatCreeperModel<T extends TCBatCreeper> extends HierarchicalModel<T> {
        private final ModelPart root;
        private final ModelPart head;
        private final ModelPart body;
        private final ModelPart rightWing;
        private final ModelPart leftWing;
        private final ModelPart rightWingTip;
        private final ModelPart leftWingTip;
        private final ModelPart feet;

        public TCBatCreeperModel(ModelPart p_170427_) {
            super(RenderType::entityCutout);
            this.root = p_170427_;
            this.body = p_170427_.getChild("body");
            this.head = p_170427_.getChild("head");
            this.rightWing = this.body.getChild("right_wing");
            this.rightWingTip = this.rightWing.getChild("right_wing_tip");
            this.leftWing = this.body.getChild("left_wing");
            this.leftWingTip = this.leftWing.getChild("left_wing_tip");
            this.feet = this.body.getChild("feet");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();
            PartDefinition partdefinition1 = partdefinition.addOrReplaceChild(
                    "body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F), PartPose.offset(0.0F, 17.0F, 0.0F)
            );
            PartDefinition partdefinition2 = partdefinition.addOrReplaceChild(
                    "head", CubeListBuilder.create().texOffs(0, 7).addBox(-2.0F, -3.0F, -1.0F, 4.0F, 3.0F, 2.0F), PartPose.offset(0.0F, 17.0F, 0.0F)
            );
            partdefinition2.addOrReplaceChild(
                    "right_ear", CubeListBuilder.create().texOffs(1, 15).addBox(-2.5F, -4.0F, 0.0F, 3.0F, 5.0F, 0.0F), PartPose.offset(-1.5F, -2.0F, 0.0F)
            );
            partdefinition2.addOrReplaceChild(
                    "left_ear", CubeListBuilder.create().texOffs(8, 15).addBox(-0.1F, -3.0F, 0.0F, 3.0F, 5.0F, 0.0F), PartPose.offset(1.1F, -3.0F, 0.0F)
            );
            PartDefinition partdefinition3 = partdefinition1.addOrReplaceChild(
                    "right_wing", CubeListBuilder.create().texOffs(12, 0).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 7.0F, 0.0F), PartPose.offset(-1.5F, 0.0F, 0.0F)
            );
            partdefinition3.addOrReplaceChild(
                    "right_wing_tip",
                    CubeListBuilder.create().texOffs(16, 0).addBox(-6.0F, -2.0F, 0.0F, 6.0F, 8.0F, 0.0F),
                    PartPose.offset(-2.0F, 0.0F, 0.0F)
            );
            PartDefinition partdefinition4 = partdefinition1.addOrReplaceChild(
                    "left_wing", CubeListBuilder.create().texOffs(12, 7).addBox(0.0F, -2.0F, 0.0F, 2.0F, 7.0F, 0.0F), PartPose.offset(1.5F, 0.0F, 0.0F)
            );
            partdefinition4.addOrReplaceChild(
                    "left_wing_tip", CubeListBuilder.create().texOffs(16, 8).addBox(0.0F, -2.0F, 0.0F, 6.0F, 8.0F, 0.0F), PartPose.offset(2.0F, 0.0F, 0.0F)
            );
            partdefinition1.addOrReplaceChild(
                    "feet", CubeListBuilder.create().texOffs(16, 16).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 2.0F, 0.0F), PartPose.offset(0.0F, 5.0F, 0.0F)
            );
            return LayerDefinition.create(meshdefinition, 32, 32);
        }

        @Override
        public ModelPart root() {
            return this.root;
        }

        @Override
        public void setupAnim(T p_102200_, float p_102201_, float p_102202_, float p_102203_, float p_102204_, float p_102205_) {
            this.root().getAllParts().forEach(ModelPart::resetPose);
            if (p_102200_.isResting()) {
                this.applyHeadRotation(p_102204_);
            }

            this.animate(p_102200_.flyAnimationState, BatAnimation.BAT_FLYING, p_102203_, 1.0F);
            this.animate(p_102200_.restAnimationState, BatAnimation.BAT_RESTING, p_102203_, 1.0F);
        }

        private void applyHeadRotation(float p_310053_) {
            this.head.yRot = p_310053_ * (float) (Math.PI / 180.0);
        }
    }
}