package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.boss.TCRavagerCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.HierarchicalModel;
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
public class TCRavagerCreeperRenderer<T extends TCRavagerCreeper> extends MobRenderer<T, TCRavagerCreeperRenderer.TCRavagerCreeperModel<T>> {
    private static final ResourceLocation LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/ravagercreeper.png");

    public TCRavagerCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new TCRavagerCreeperModel<T>(p_173956_.bakeLayer(ModelLayers.RAVAGER)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer<>(this, p_173956_.getModelSet(), new TCRavagerCreeperModel<>(p_173956_.bakeLayer(ModelLayers.RAVAGER)), TCEntityCore.RAVAGER, true));
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return LOCATION;
    }

    @Override
    protected void scale(T p_114046_, PoseStack p_114047_, float p_114048_) {
        TCClientUtils.scaleSwelling(p_114046_, p_114047_, p_114048_);
    }

    @Override
    protected float getWhiteOverlayProgress(T p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    public static class TCRavagerCreeperModel<T extends TCRavagerCreeper> extends HierarchicalModel<T> {
        private final ModelPart root;
        private final ModelPart head;
        private final ModelPart mouth;
        private final ModelPart rightHindLeg;
        private final ModelPart leftHindLeg;
        private final ModelPart rightFrontLeg;
        private final ModelPart leftFrontLeg;
        private final ModelPart neck;

        public TCRavagerCreeperModel(ModelPart p_170889_) {
            this.root = p_170889_;
            this.neck = p_170889_.getChild("neck");
            this.head = this.neck.getChild("head");
            this.mouth = this.head.getChild("mouth");
            this.rightHindLeg = p_170889_.getChild("right_hind_leg");
            this.leftHindLeg = p_170889_.getChild("left_hind_leg");
            this.rightFrontLeg = p_170889_.getChild("right_front_leg");
            this.leftFrontLeg = p_170889_.getChild("left_front_leg");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();
            int i = 16;
            PartDefinition partdefinition1 = partdefinition.addOrReplaceChild(
                    "neck", CubeListBuilder.create().texOffs(68, 73).addBox(-5.0F, -1.0F, -18.0F, 10.0F, 10.0F, 18.0F), PartPose.offset(0.0F, -7.0F, 5.5F)
            );
            PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild(
                    "head",
                    CubeListBuilder.create()
                            .texOffs(0, 0)
                            .addBox(-8.0F, -20.0F, -14.0F, 16.0F, 20.0F, 16.0F)
                            .texOffs(0, 0)
                            .addBox(-2.0F, -6.0F, -18.0F, 4.0F, 8.0F, 4.0F),
                    PartPose.offset(0.0F, 16.0F, -17.0F)
            );
            partdefinition2.addOrReplaceChild(
                    "right_horn",
                    CubeListBuilder.create().texOffs(74, 55).addBox(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F),
                    PartPose.offsetAndRotation(-10.0F, -14.0F, -8.0F, 1.0995574F, 0.0F, 0.0F)
            );
            partdefinition2.addOrReplaceChild(
                    "left_horn",
                    CubeListBuilder.create().texOffs(74, 55).mirror().addBox(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F),
                    PartPose.offsetAndRotation(8.0F, -14.0F, -8.0F, 1.0995574F, 0.0F, 0.0F)
            );
            partdefinition2.addOrReplaceChild(
                    "mouth", CubeListBuilder.create().texOffs(0, 36).addBox(-8.0F, 0.0F, -16.0F, 16.0F, 3.0F, 16.0F), PartPose.offset(0.0F, -2.0F, 2.0F)
            );
            partdefinition.addOrReplaceChild(
                    "body",
                    CubeListBuilder.create()
                            .texOffs(0, 55)
                            .addBox(-7.0F, -10.0F, -7.0F, 14.0F, 16.0F, 20.0F)
                            .texOffs(0, 91)
                            .addBox(-6.0F, 6.0F, -7.0F, 12.0F, 13.0F, 18.0F),
                    PartPose.offsetAndRotation(0.0F, 1.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
            );
            partdefinition.addOrReplaceChild(
                    "right_hind_leg",
                    CubeListBuilder.create().texOffs(96, 0).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F),
                    PartPose.offset(-8.0F, -13.0F, 18.0F)
            );
            partdefinition.addOrReplaceChild(
                    "left_hind_leg",
                    CubeListBuilder.create().texOffs(96, 0).mirror().addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F),
                    PartPose.offset(8.0F, -13.0F, 18.0F)
            );
            partdefinition.addOrReplaceChild(
                    "right_front_leg",
                    CubeListBuilder.create().texOffs(64, 0).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F),
                    PartPose.offset(-8.0F, -13.0F, -5.0F)
            );
            partdefinition.addOrReplaceChild(
                    "left_front_leg",
                    CubeListBuilder.create().texOffs(64, 0).mirror().addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F),
                    PartPose.offset(8.0F, -13.0F, -5.0F)
            );
            return LayerDefinition.create(meshdefinition, 128, 128);
        }

        @Override
        public ModelPart root() {
            return this.root;
        }

        @Override
        public void setupAnim(T p_103626_, float p_103627_, float p_103628_, float p_103629_, float p_103630_, float p_103631_) {
            this.head.xRot = p_103631_ * (float) (Math.PI / 180.0);
            this.head.yRot = p_103630_ * (float) (Math.PI / 180.0);
            float f = 0.4F * p_103628_;
            this.rightHindLeg.xRot = Mth.cos(p_103627_ * 0.6662F) * f;
            this.leftHindLeg.xRot = Mth.cos(p_103627_ * 0.6662F + (float) Math.PI) * f;
            this.rightFrontLeg.xRot = Mth.cos(p_103627_ * 0.6662F + (float) Math.PI) * f;
            this.leftFrontLeg.xRot = Mth.cos(p_103627_ * 0.6662F) * f;
        }

        @Override
        public void prepareMobModel(T p_103621_, float p_103622_, float p_103623_, float p_103624_) {
            super.prepareMobModel(p_103621_, p_103622_, p_103623_, p_103624_);
            int i = p_103621_.getStunnedTick();
            int j = p_103621_.getRoarTick();
            int k = 20;
            int l = p_103621_.getAttackTick();
            int i1 = 10;
            if (l > 0) {
                float f = Mth.triangleWave((float) l - p_103624_, 10.0F);
                float f1 = (1.0F + f) * 0.5F;
                float f2 = f1 * f1 * f1 * 12.0F;
                float f3 = f2 * Mth.sin(this.neck.xRot);
                this.neck.z = -6.5F + f2;
                this.neck.y = -7.0F - f3;
                float f4 = Mth.sin(((float) l - p_103624_) / 10.0F * (float) Math.PI * 0.25F);
                this.mouth.xRot = (float) (Math.PI / 2) * f4;
                if (l > 5) {
                    this.mouth.xRot = Mth.sin(((float) (-4 + l) - p_103624_) / 4.0F) * (float) Math.PI * 0.4F;
                } else {
                    this.mouth.xRot = (float) (Math.PI / 20) * Mth.sin((float) Math.PI * ((float) l - p_103624_) / 10.0F);
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
                    float f7 = Mth.sin(((float) (20 - j) - p_103624_) / 20.0F * (float) Math.PI * 0.25F);
                    this.mouth.xRot = (float) (Math.PI / 2) * f7;
                }
            }
        }
    }
}