package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCRavagerCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.boss.TCRavagerCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.RavagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.RavagerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCRavagerCreeperRenderer<T extends TCRavagerCreeper, S extends TCRavagerCreeperRenderState, M extends TCRavagerCreeperRenderer.TCRavagerCreeperModel<S>> extends MobRenderer<T, S, M> {
    private static final ResourceLocation LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/ravagercreeper.png");

    public TCRavagerCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, (M) new TCRavagerCreeperModel(p_173956_.bakeLayer(ModelLayers.RAVAGER)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer(this, p_173956_.getModelSet(), new TCRavagerCreeperModel(p_173956_.bakeLayer(ModelLayers.RAVAGER)), TCEntityCore.RAVAGER, true));
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
        return (S) new TCRavagerCreeperRenderState();
    }

    @Override
    public void extractRenderState(T creeper, S state, float f) {
        super.extractRenderState(creeper, state, f);
        state.swelling = creeper.getSwelling(f);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.isOnBook = creeper.isOnBook();
    }

    public static class TCRavagerCreeperModel<T extends TCRavagerCreeperRenderState> extends RavagerModel {
        private final ModelPart root;
        private final ModelPart head;
        private final ModelPart mouth;
        private final ModelPart rightHindLeg;
        private final ModelPart leftHindLeg;
        private final ModelPart rightFrontLeg;
        private final ModelPart leftFrontLeg;
        private final ModelPart neck;

        public TCRavagerCreeperModel(ModelPart p_170889_) {
            super(p_170889_);
            this.root = p_170889_;
            this.neck = p_170889_.getChild("neck");
            this.head = this.neck.getChild("head");
            this.mouth = this.head.getChild("mouth");
            this.rightHindLeg = p_170889_.getChild("right_hind_leg");
            this.leftHindLeg = p_170889_.getChild("left_hind_leg");
            this.rightFrontLeg = p_170889_.getChild("right_front_leg");
            this.leftFrontLeg = p_170889_.getChild("left_front_leg");
        }

        @Override
        public void setupAnim(RavagerRenderState state) {
            this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
            this.head.yRot = state.yRot * (float) (Math.PI / 180.0);
            float f = 0.4F * state.walkAnimationSpeed;
            this.rightHindLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F) * f;
            this.leftHindLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F + (float) Math.PI) * f;
            this.rightFrontLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F + (float) Math.PI) * f;
            this.leftFrontLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F) * f;
            this.prepareMobModel(state);
        }

        public void prepareMobModel(RavagerRenderState state) {
            float i = state.stunnedTicksRemaining;
            float j = state.roarAnimation;
            int k = 20;
            float l = state.attackTicksRemaining;
            int i1 = 10;
            if (l > 0) {
                float f = Mth.triangleWave(l, 10.0F);
                float f1 = (1.0F + f) * 0.5F;
                float f2 = f1 * f1 * f1 * 12.0F;
                float f3 = f2 * Mth.sin(this.neck.xRot);
                this.neck.z = -6.5F + f2;
                this.neck.y = -7.0F - f3;
                float f4 = Mth.sin(l / 10.0F * (float) Math.PI * 0.25F);
                this.mouth.xRot = (float) (Math.PI / 2) * f4;
                if (l > 5) {
                    this.mouth.xRot = Mth.sin((-4 + l) / 4.0F) * (float) Math.PI * 0.4F;
                } else {
                    this.mouth.xRot = (float) (Math.PI / 20) * Mth.sin((float) Math.PI * l / 10.0F);
                }
            } else {
                float f5 = -1.0F;
                float f6 = -1.0F * Mth.sin(this.neck.xRot);
                this.neck.x = 0.0F;
                this.neck.y = -7.0F - f6;
                this.neck.z = 5.5F;
                boolean flag = i > 0;
                this.neck.xRot = flag ? 0.21991149F : 0.0F;
                this.mouth.xRot = (float) Math.PI * (flag ? 0.05F : 0.01F);
                if (flag) {
                    double d0 = (double) i / 40.0;
                    this.neck.x = (float) Math.sin(d0 * 10.0) * 3.0F;
                } else if (j > 0) {
                    float f7 = Mth.sin((20 - j) / 20.0F * (float) Math.PI * 0.25F);
                    this.mouth.xRot = (float) (Math.PI / 2) * f7;
                }
            }
        }
    }
}