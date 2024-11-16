package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCBreezeCreeperEyesLayer;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCBreezeCreeperWindLayer;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCBreezeCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCBreezeCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.animation.definitions.BreezeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCBreezeCreeperRenderer extends MobRenderer<TCBreezeCreeper, TCBreezeCreeperRenderState, TCBreezeCreeperRenderer.TCBreezeCreeperModel<TCBreezeCreeperRenderState>> {
    private static final ResourceLocation LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/breezecreeper.png");

    public TCBreezeCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new TCBreezeCreeperModel<>(p_173956_.bakeLayer(ModelLayers.BREEZE)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer<>(this, p_173956_.getModelSet(), new TCBreezeCreeperModel(p_173956_.bakeLayer(ModelLayers.BREEZE)), TCEntityCore.BLAZE, false));
        this.addLayer(new TCBreezeCreeperWindLayer(p_173956_, this));
        this.addLayer(new TCBreezeCreeperEyesLayer(this));
    }

    public static TCBreezeCreeperModel<TCBreezeCreeperRenderState> enable(TCBreezeCreeperModel<TCBreezeCreeperRenderState> p_328756_, ModelPart... p_332502_) {
        p_328756_.head().visible = false;
        p_328756_.eyes().visible = false;
        p_328756_.rods().visible = false;
        p_328756_.wind().visible = false;

        for (ModelPart modelpart : p_332502_) {
            modelpart.visible = true;
        }

        return p_328756_;
    }

    @Override
    public void render(TCBreezeCreeperRenderState p_334455_, PoseStack p_332688_, MultiBufferSource p_333828_, int p_331024_) {
        TCBreezeCreeperModel<TCBreezeCreeperRenderState> breezemodel = this.getModel();
        enable(breezemodel, breezemodel.head(), breezemodel.rods());
        super.render(p_334455_, p_332688_, p_333828_, p_331024_);
    }

    @Override
    public TCBreezeCreeperRenderState createRenderState() {
        return new TCBreezeCreeperRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(TCBreezeCreeperRenderState p_114482_) {
        return LOCATION;
    }

    @Override
    protected void scale(TCBreezeCreeperRenderState p_114046_, PoseStack p_114047_) {
        TCClientUtils.scaleSwelling(p_114046_.swelling, p_114047_);
    }

    @Override
    protected float getWhiteOverlayProgress(TCBreezeCreeperRenderState p_114043_) {
        float f = p_114043_.swelling;
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public void extractRenderState(TCBreezeCreeper creeper, TCBreezeCreeperRenderState state, float f) {
        super.extractRenderState(creeper, state, f);
        state.swelling = creeper.getSwelling(f);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.idle.copyFrom(creeper.idle);
        state.shoot.copyFrom(creeper.shoot);
        state.slide.copyFrom(creeper.slide);
        state.slideBack.copyFrom(creeper.slideBack);
        state.inhale.copyFrom(creeper.inhale);
        state.longJump.copyFrom(creeper.longJump);
        state.isOnBook = creeper.isOnBook();
    }

    @OnlyIn(Dist.CLIENT)
    public static class TCBreezeCreeperModel<T extends TCBreezeCreeperRenderState> extends EntityModel<T> {
        private static final float WIND_TOP_SPEED = 0.6F;
        private static final float WIND_MIDDLE_SPEED = 0.8F;
        private static final float WIND_BOTTOM_SPEED = 1.0F;
        private final ModelPart root;
        private final ModelPart head;
        private final ModelPart eyes;
        private final ModelPart wind;
        private final ModelPart windTop;
        private final ModelPart windMid;
        private final ModelPart windBottom;
        private final ModelPart rods;

        public TCBreezeCreeperModel(ModelPart p_309507_) {
            super(p_309507_.getChild("root"), RenderType::entityTranslucent);
            this.root = p_309507_;
            this.wind = p_309507_.getChild("wind_body");
            this.windBottom = this.wind.getChild("wind_bottom");
            this.windMid = this.windBottom.getChild("wind_mid");
            this.windTop = this.windMid.getChild("wind_top");
            this.head = p_309507_.getChild("body").getChild("head");
            this.eyes = this.head.getChild("eyes");
            this.rods = p_309507_.getChild("body").getChild("rods");
        }

        public static LayerDefinition createBodyLayer(int p_329286_, int p_330152_) {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();
            PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
            PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild("rods", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));
            partdefinition2.addOrReplaceChild(
                    "rod_1",
                    CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(2.5981F, -3.0F, 1.5F, -2.7489F, -1.0472F, 3.1416F)
            );
            partdefinition2.addOrReplaceChild(
                    "rod_2",
                    CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(-2.5981F, -3.0F, 1.5F, -2.7489F, 1.0472F, 3.1416F)
            );
            partdefinition2.addOrReplaceChild(
                    "rod_3",
                    CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(0.0F, -3.0F, -3.0F, 0.3927F, 0.0F, 0.0F)
            );
            PartDefinition partdefinition3 = partdefinition1.addOrReplaceChild(
                    "head",
                    CubeListBuilder.create()
                            .texOffs(4, 24)
                            .addBox(-5.0F, -5.0F, -4.2F, 10.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                            .texOffs(0, 0)
                            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
                    PartPose.offset(0.0F, 4.0F, 0.0F)
            );
            partdefinition3.addOrReplaceChild(
                    "eyes",
                    CubeListBuilder.create()
                            .texOffs(4, 24)
                            .addBox(-5.0F, -5.0F, -4.2F, 10.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                            .texOffs(0, 0)
                            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
                    PartPose.offset(0.0F, 0.0F, 0.0F)
            );
            PartDefinition partdefinition4 = partdefinition.addOrReplaceChild("wind_body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
            PartDefinition partdefinition5 = partdefinition4.addOrReplaceChild(
                    "wind_bottom",
                    CubeListBuilder.create().texOffs(1, 83).addBox(-2.5F, -7.0F, -2.5F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)),
                    PartPose.offset(0.0F, 24.0F, 0.0F)
            );
            PartDefinition partdefinition6 = partdefinition5.addOrReplaceChild(
                    "wind_mid",
                    CubeListBuilder.create()
                            .texOffs(74, 28)
                            .addBox(-6.0F, -6.0F, -6.0F, 12.0F, 6.0F, 12.0F, new CubeDeformation(0.0F))
                            .texOffs(78, 32)
                            .addBox(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
                            .texOffs(49, 71)
                            .addBox(-2.5F, -6.0F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)),
                    PartPose.offset(0.0F, -7.0F, 0.0F)
            );
            partdefinition6.addOrReplaceChild(
                    "wind_top",
                    CubeListBuilder.create()
                            .texOffs(0, 0)
                            .addBox(-9.0F, -8.0F, -9.0F, 18.0F, 8.0F, 18.0F, new CubeDeformation(0.0F))
                            .texOffs(6, 6)
                            .addBox(-6.0F, -8.0F, -6.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F))
                            .texOffs(105, 57)
                            .addBox(-2.5F, -8.0F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)),
                    PartPose.offset(0.0F, -6.0F, 0.0F)
            );
            return LayerDefinition.create(meshdefinition, p_329286_, p_330152_);
        }

        @Override
        public void setupAnim(T p_364657_) {
            super.setupAnim(p_364657_);
            this.animate(p_364657_.idle, BreezeAnimation.IDLE, p_364657_.ageInTicks);
            this.animate(p_364657_.shoot, BreezeAnimation.SHOOT, p_364657_.ageInTicks);
            this.animate(p_364657_.slide, BreezeAnimation.SLIDE, p_364657_.ageInTicks);
            this.animate(p_364657_.slideBack, BreezeAnimation.SLIDE_BACK, p_364657_.ageInTicks);
            this.animate(p_364657_.inhale, BreezeAnimation.INHALE, p_364657_.ageInTicks);
            this.animate(p_364657_.longJump, BreezeAnimation.JUMP, p_364657_.ageInTicks);
        }

        public ModelPart head() {
            return this.head;
        }

        public ModelPart eyes() {
            return this.eyes;
        }

        public ModelPart rods() {
            return this.rods;
        }

        public ModelPart wind() {
            return this.wind;
        }
    }
}