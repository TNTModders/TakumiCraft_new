package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCParrotCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCParrotCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCParrotCreeperRenderer<T extends TCParrotCreeper, S extends TCParrotCreeperRenderState, M extends TCParrotCreeperRenderer.TCParrotCreeperModel> extends MobRenderer<T, S, M> {
    private static final ResourceLocation LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/parrotcreeper.png");

    public TCParrotCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, (M) new TCParrotCreeperModel(p_173956_.bakeLayer(ModelLayers.PARROT)), 0.3F);
        this.addLayer(new TCCreeperPowerLayer(this, p_173956_.getModelSet(), new TCParrotCreeperModel(p_173956_.bakeLayer(ModelLayers.PARROT)), TCEntityCore.PARROT, true));
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
        return (S) new TCParrotCreeperRenderState();
    }

    @Override
    public void extractRenderState(T creeper, S state, float f) {
        super.extractRenderState(creeper, state, f);
        state.swelling = creeper.getSwelling(f);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.isOnBook = creeper.isOnBook();

        state.variant = Parrot.Variant.GREEN;
        float f0 = Mth.lerp(f, creeper.oFlap, creeper.flap);
        float f1 = Mth.lerp(f, creeper.oFlapSpeed, creeper.flapSpeed);
        state.flapAngle = (Mth.sin(f0) + 1.0F) * f1;
        state.pose = ParrotModel.Pose.FLYING;
    }

    public static class TCParrotCreeperModel extends ParrotModel {

        private static final String FEATHER = "feather";
        private final ModelPart body;
        private final ModelPart tail;
        private final ModelPart leftWing;
        private final ModelPart rightWing;
        private final ModelPart head;
        private final ModelPart leftLeg;
        private final ModelPart rightLeg;

        public TCParrotCreeperModel(ModelPart p_170780_) {
            super(p_170780_);
            this.body = p_170780_.getChild("body");
            this.tail = p_170780_.getChild("tail");
            this.leftWing = p_170780_.getChild("left_wing");
            this.rightWing = p_170780_.getChild("right_wing");
            this.head = p_170780_.getChild("head");
            this.leftLeg = p_170780_.getChild("left_leg");
            this.rightLeg = p_170780_.getChild("right_leg");
        }

        @Override
        public void setupAnim(ParrotRenderState p_364395_) {
            super.setupAnim(p_364395_);
            this.prepare(p_364395_.pose);
            this.head.xRot = p_364395_.xRot * (float) (Math.PI / 180.0);
            this.head.yRot = p_364395_.yRot * (float) (Math.PI / 180.0);

            float f2 = p_364395_.flapAngle * 0.3F;
            this.head.y += f2;
            this.tail.xRot = this.tail.xRot + Mth.cos(p_364395_.walkAnimationPos * 0.6662F) * 0.3F * p_364395_.walkAnimationSpeed;
            this.tail.y += f2;
            this.body.y += f2;
            this.leftWing.zRot = -0.0873F - p_364395_.flapAngle;
            this.leftWing.y += f2;
            this.rightWing.zRot = 0.0873F + p_364395_.flapAngle;
            this.rightWing.y += f2;
            this.leftLeg.y += f2;
            this.rightLeg.y += f2;

            float f = Mth.cos(p_364395_.ageInTicks);
            float f1 = Mth.sin(p_364395_.ageInTicks);
            this.head.x += f;
            this.head.y += f1;
            this.head.xRot = 0.0F;
            this.head.yRot = 0.0F;
            this.head.zRot = Mth.sin(p_364395_.ageInTicks) * 0.4F;
            this.body.x += f;
            this.body.y += f1;
            this.leftWing.zRot = -0.0873F - p_364395_.flapAngle;
            this.leftWing.x += f;
            this.leftWing.y += f1;
            this.rightWing.zRot = 0.0873F + p_364395_.flapAngle;
            this.rightWing.x += f;
            this.rightWing.y += f1;
            this.tail.x += f;
            this.tail.y += f1;
        }

        private void prepare(ParrotModel.Pose p_360920_) {
            this.leftLeg.xRot += (float) (Math.PI * 2.0 / 9.0);
            this.rightLeg.xRot += (float) (Math.PI * 2.0 / 9.0);
            this.leftLeg.zRot = (float) (-Math.PI / 9);
            this.rightLeg.zRot = (float) (Math.PI / 9);
        }
    }
}