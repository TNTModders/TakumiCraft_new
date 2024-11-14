package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCWitchCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCWitchCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.VillagerHeadModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCWitchCreeperRenderer<T extends TCWitchCreeper, S extends TCWitchCreeperRenderState, M extends TCWitchCreeperRenderer.TCWitchCreeperModel<S>> extends MobRenderer<T, S, M> {
    private static final ResourceLocation LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/witchcreeper.png");

    public TCWitchCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, (M) new TCWitchCreeperRenderer.TCWitchCreeperModel<>(context.bakeLayer(ModelLayers.WITCH)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer(this, context.getModelSet(), new TCWitchCreeperModel(context.bakeLayer(ModelLayers.WITCH)), TCEntityCore.WITCH, true));
        this.addLayer(new TCWitchCreeperItemLayer(this, context.getItemRenderer()));
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
        return (S) new TCWitchCreeperRenderState();
    }

    @Override
    public void extractRenderState(T creeper, S state, float f) {
        super.extractRenderState(creeper, state, f);
        state.swelling = creeper.getSwelling(f);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.isOnBook = creeper.isOnBook();

        state.entityId = creeper.getId();
        state.isHoldingItem = !creeper.getMainHandItem().isEmpty();
    }

    @OnlyIn(Dist.CLIENT)
    public static class TCWitchCreeperItemLayer<S extends TCWitchCreeperRenderState, M extends TCWitchCreeperModel<S>> extends CrossedArmsItemLayer<S, M> {
        public TCWitchCreeperItemLayer(RenderLayerParent<S, M> p_234926_, ItemRenderer p_364299_) {
            super(p_234926_, p_364299_);
        }

        @Override
        public void render(PoseStack p_362411_, MultiBufferSource p_368708_, int p_368191_, S p_367958_, float p_367022_, float p_362108_) {
            p_362411_.pushPose();
            if (p_367958_.rightHandItem.is(Items.POTION)) {
                this.getParentModel().root().translateAndRotate(p_362411_);
                this.getParentModel().getHead().translateAndRotate(p_362411_);
                this.getParentModel().getNose().translateAndRotate(p_362411_);
                p_362411_.translate(0.0625F, 0.25F, 0.0F);
                p_362411_.mulPose(Axis.ZP.rotationDegrees(180.0F));
                p_362411_.mulPose(Axis.XP.rotationDegrees(140.0F));
                p_362411_.mulPose(Axis.ZP.rotationDegrees(10.0F));
                p_362411_.translate(0.0F, -0.4F, 0.4F);
            }

            super.render(p_362411_, p_368708_, p_368191_, p_367958_, p_367022_, p_362108_);
            p_362411_.popPose();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class TCWitchCreeperModel<T extends TCWitchCreeperRenderState> extends EntityModel<T> implements HeadedModel, VillagerHeadModel {
        protected final ModelPart nose;
        private final ModelPart head;
        private final ModelPart hat;
        private final ModelPart hatRim;
        private final ModelPart rightLeg;
        private final ModelPart leftLeg;

        public TCWitchCreeperModel(ModelPart p_171055_) {
            super(p_171055_);
            this.head = p_171055_.getChild("head");
            this.hat = this.head.getChild("hat");
            this.hatRim = this.hat.getChild("hat_rim");
            this.nose = this.head.getChild("nose");
            this.rightLeg = p_171055_.getChild("right_leg");
            this.leftLeg = p_171055_.getChild("left_leg");
        }

        @Override
        public void setupAnim(T p_366406_) {
            super.setupAnim(p_366406_);
            this.head.yRot = p_366406_.yRot * (float) (Math.PI / 180.0);
            this.head.xRot = p_366406_.xRot * (float) (Math.PI / 180.0);
            this.rightLeg.xRot = Mth.cos(p_366406_.walkAnimationPos * 0.6662F) * 1.4F * p_366406_.walkAnimationSpeed * 0.5F;
            this.leftLeg.xRot = Mth.cos(p_366406_.walkAnimationPos * 0.6662F + (float) Math.PI) * 1.4F * p_366406_.walkAnimationSpeed * 0.5F;
            float f = 0.01F * (float) (p_366406_.entityId % 10);
            this.nose.xRot = Mth.sin(p_366406_.ageInTicks * f) * 4.5F * (float) (Math.PI / 180.0);
            this.nose.zRot = Mth.cos(p_366406_.ageInTicks * f) * 2.5F * (float) (Math.PI / 180.0);
            if (p_366406_.isHoldingItem) {
                this.nose.setPos(0.0F, 1.0F, -1.5F);
                this.nose.xRot = -0.9F;
            }
        }

        public ModelPart getNose() {
            return this.nose;
        }

        @Override
        public ModelPart getHead() {
            return this.head;
        }

        @Override
        public void hatVisible(boolean p_363566_) {
            this.head.visible = p_363566_;
            this.hat.visible = p_363566_;
            this.hatRim.visible = p_363566_;
        }
    }
}