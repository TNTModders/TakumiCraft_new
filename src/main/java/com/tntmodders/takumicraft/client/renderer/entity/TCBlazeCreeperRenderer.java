package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCBlazeCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class TCBlazeCreeperRenderer<T extends TCBlazeCreeper, S extends TCCreeperRenderState> extends MobRenderer<T, S, BlazeModel> {
    private static final ResourceLocation LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/blazecreeper.png");

    public TCBlazeCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new BlazeModel(p_173956_.bakeLayer(ModelLayers.BLAZE)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer(this, p_173956_.getModelSet(), new TCBlazeCreeperModel(p_173956_.bakeLayer(ModelLayers.BLAZE)), TCEntityCore.BLAZE, true));
    }

    @Override
    public ResourceLocation getTextureLocation(TCCreeperRenderState p_114482_) {
        return LOCATION;
    }

    @Override
    protected int getBlockLightLevel(T p_113910_, BlockPos p_113911_) {
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
        return (S) new TCCreeperRenderState();
    }

    @Override
    public void extractRenderState(T creeper, S state, float f) {
        super.extractRenderState(creeper, state, f);
        state.swelling = creeper.getSwelling(f);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.isOnBook = creeper.isOnBook();
    }

    @OnlyIn(Dist.CLIENT)
    public static class TCBlazeCreeperModel extends EntityModel<TCCreeperRenderState> {
        private final ModelPart[] upperBodyParts;
        private final ModelPart head;

        public TCBlazeCreeperModel(ModelPart p_170443_) {
            super(p_170443_);
            this.head = p_170443_.getChild("head");
            this.upperBodyParts = new ModelPart[12];
            Arrays.setAll(this.upperBodyParts, p_170449_ -> p_170443_.getChild(getPartName(p_170449_)));
        }

        private static String getPartName(int p_170446_) {
            return "part" + p_170446_;
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();
            partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
            float f = 0.0F;
            CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F);

            for (int i = 0; i < 4; i++) {
                float f1 = Mth.cos(f) * 9.0F;
                float f2 = -2.0F + Mth.cos((float) (i * 2) * 0.25F);
                float f3 = Mth.sin(f) * 9.0F;
                partdefinition.addOrReplaceChild(getPartName(i), cubelistbuilder, PartPose.offset(f1, f2, f3));
                f++;
            }

            f = (float) (Math.PI / 4);

            for (int j = 4; j < 8; j++) {
                float f4 = Mth.cos(f) * 7.0F;
                float f6 = 2.0F + Mth.cos((float) (j * 2) * 0.25F);
                float f8 = Mth.sin(f) * 7.0F;
                partdefinition.addOrReplaceChild(getPartName(j), cubelistbuilder, PartPose.offset(f4, f6, f8));
                f++;
            }

            f = 0.47123894F;

            for (int k = 8; k < 12; k++) {
                float f5 = Mth.cos(f) * 5.0F;
                float f7 = 11.0F + Mth.cos((float) k * 1.5F * 0.5F);
                float f9 = Mth.sin(f) * 5.0F;
                partdefinition.addOrReplaceChild(getPartName(k), cubelistbuilder, PartPose.offset(f5, f7, f9));
                f++;
            }

            return LayerDefinition.create(meshdefinition, 64, 32);
        }

        @Override
        public void setupAnim(TCCreeperRenderState p_364987_) {
            super.setupAnim(p_364987_);
            float f = p_364987_.ageInTicks * (float) Math.PI * -0.1F;

            for (int i = 0; i < 4; i++) {
                this.upperBodyParts[i].y = -2.0F + Mth.cos(((float) (i * 2) + p_364987_.ageInTicks) * 0.25F);
                this.upperBodyParts[i].x = Mth.cos(f) * 9.0F;
                this.upperBodyParts[i].z = Mth.sin(f) * 9.0F;
                f++;
            }

            f = (float) (Math.PI / 4) + p_364987_.ageInTicks * (float) Math.PI * 0.03F;

            for (int j = 4; j < 8; j++) {
                this.upperBodyParts[j].y = 2.0F + Mth.cos(((float) (j * 2) + p_364987_.ageInTicks) * 0.25F);
                this.upperBodyParts[j].x = Mth.cos(f) * 7.0F;
                this.upperBodyParts[j].z = Mth.sin(f) * 7.0F;
                f++;
            }

            f = 0.47123894F + p_364987_.ageInTicks * (float) Math.PI * -0.05F;

            for (int k = 8; k < 12; k++) {
                this.upperBodyParts[k].y = 11.0F + Mth.cos(((float) k * 1.5F + p_364987_.ageInTicks) * 0.5F);
                this.upperBodyParts[k].x = Mth.cos(f) * 5.0F;
                this.upperBodyParts[k].z = Mth.sin(f) * 5.0F;
                f++;
            }

            this.head.yRot = p_364987_.yRot * (float) (Math.PI / 180.0);
            this.head.xRot = p_364987_.xRot * (float) (Math.PI / 180.0);
        }
    }
}