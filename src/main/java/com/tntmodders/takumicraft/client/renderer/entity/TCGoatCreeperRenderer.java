package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCGoatCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCGoatCreeperRenderer extends MobRenderer<TCGoatCreeper, TCGoatCreeperRenderer.TCGoatCreeperModel<TCGoatCreeper>> {
    private static final ResourceLocation LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/goatcreeper.png");

    public TCGoatCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new TCGoatCreeperModel<>(p_173956_.bakeLayer(ModelLayers.GOAT)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer<>(this, p_173956_.getModelSet(), new TCGoatCreeperModel<>(p_173956_.bakeLayer(ModelLayers.GOAT)), TCEntityCore.GOAT, true));
    }

    @Override
    public ResourceLocation getTextureLocation(TCGoatCreeper p_114482_) {
        return LOCATION;
    }

    @Override
    protected void scale(TCGoatCreeper p_114046_, PoseStack p_114047_, float p_114048_) {
        TCClientUtils.scaleSwelling(p_114046_, p_114047_, p_114048_);
    }

    @Override
    protected float getWhiteOverlayProgress(TCGoatCreeper p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    public static class TCGoatCreeperModel<T extends TCGoatCreeper> extends QuadrupedModel<T> {
        public TCGoatCreeperModel(ModelPart p_170578_) {
            super(p_170578_, true, 19.0F, 1.0F, 2.5F, 2.0F, 24);
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();
            PartDefinition partdefinition1 = partdefinition.addOrReplaceChild(
                    "head",
                    CubeListBuilder.create()
                            .texOffs(2, 61)
                            .addBox("right ear", -6.0F, -11.0F, -10.0F, 3.0F, 2.0F, 1.0F)
                            .texOffs(2, 61)
                            .mirror()
                            .addBox("left ear", 2.0F, -11.0F, -10.0F, 3.0F, 2.0F, 1.0F)
                            .texOffs(23, 52)
                            .addBox("goatee", -0.5F, -3.0F, -14.0F, 0.0F, 7.0F, 5.0F),
                    PartPose.offset(1.0F, 14.0F, 0.0F)
            );
            partdefinition1.addOrReplaceChild(
                    "left_horn",
                    CubeListBuilder.create().texOffs(12, 55).addBox(-0.01F, -16.0F, -10.0F, 2.0F, 7.0F, 2.0F),
                    PartPose.offset(0.0F, 0.0F, 0.0F)
            );
            partdefinition1.addOrReplaceChild(
                    "right_horn",
                    CubeListBuilder.create().texOffs(12, 55).addBox(-2.99F, -16.0F, -10.0F, 2.0F, 7.0F, 2.0F),
                    PartPose.offset(0.0F, 0.0F, 0.0F)
            );
            partdefinition1.addOrReplaceChild(
                    "nose",
                    CubeListBuilder.create().texOffs(34, 46).addBox(-3.0F, -4.0F, -8.0F, 5.0F, 7.0F, 10.0F),
                    PartPose.offsetAndRotation(0.0F, -8.0F, -8.0F, 0.9599F, 0.0F, 0.0F)
            );
            partdefinition.addOrReplaceChild(
                    "body",
                    CubeListBuilder.create()
                            .texOffs(1, 1)
                            .addBox(-4.0F, -17.0F, -7.0F, 9.0F, 11.0F, 16.0F)
                            .texOffs(0, 28)
                            .addBox(-5.0F, -18.0F, -8.0F, 11.0F, 14.0F, 11.0F),
                    PartPose.offset(0.0F, 24.0F, 0.0F)
            );
            partdefinition.addOrReplaceChild(
                    "left_hind_leg", CubeListBuilder.create().texOffs(36, 29).addBox(0.0F, 4.0F, 0.0F, 3.0F, 6.0F, 3.0F), PartPose.offset(1.0F, 14.0F, 4.0F)
            );
            partdefinition.addOrReplaceChild(
                    "right_hind_leg",
                    CubeListBuilder.create().texOffs(49, 29).addBox(0.0F, 4.0F, 0.0F, 3.0F, 6.0F, 3.0F),
                    PartPose.offset(-3.0F, 14.0F, 4.0F)
            );
            partdefinition.addOrReplaceChild(
                    "left_front_leg",
                    CubeListBuilder.create().texOffs(49, 2).addBox(0.0F, 0.0F, 0.0F, 3.0F, 10.0F, 3.0F),
                    PartPose.offset(1.0F, 14.0F, -6.0F)
            );
            partdefinition.addOrReplaceChild(
                    "right_front_leg",
                    CubeListBuilder.create().texOffs(35, 2).addBox(0.0F, 0.0F, 0.0F, 3.0F, 10.0F, 3.0F),
                    PartPose.offset(-3.0F, 14.0F, -6.0F)
            );
            return LayerDefinition.create(meshdefinition, 64, 64);
        }

        @Override
        public void setupAnim(T p_170587_, float p_170588_, float p_170589_, float p_170590_, float p_170591_, float p_170592_) {
            this.head.getChild("left_horn").visible = p_170587_.hasLeftHorn();
            this.head.getChild("right_horn").visible = p_170587_.hasRightHorn();
            super.setupAnim(p_170587_, p_170588_, p_170589_, p_170590_, p_170591_, p_170592_);
            float f = p_170587_.getRammingXHeadRot();
            if (f != 0.0F) {
                this.head.xRot = f;
            }
        }
    }
}
