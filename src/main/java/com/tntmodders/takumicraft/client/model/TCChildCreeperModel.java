package com.tntmodders.takumicraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.entity.mobs.TCChildCreeper;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class TCChildCreeperModel<T extends TCChildCreeper> extends CreeperModel<T> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

    private final ModelPart parent;
    private final ModelPart parent_head;
    private final ModelPart parent_rightHindLeg;
    private final ModelPart parent_leftHindLeg;
    private final ModelPart parent_rightFrontLeg;
    private final ModelPart parent_leftFrontLeg;

    public TCChildCreeperModel(ModelPart modelPart) {
        super(modelPart);
        this.root = modelPart;
        this.head = modelPart.getChild("head");
        this.leftHindLeg = modelPart.getChild("right_hind_leg");
        this.rightHindLeg = modelPart.getChild("left_hind_leg");
        this.leftFrontLeg = modelPart.getChild("right_front_leg");
        this.rightFrontLeg = modelPart.getChild("left_front_leg");

        this.parent = modelPart.getChild("parent");
        this.parent_head = this.parent.getChild("parent_head");
        this.parent_leftHindLeg = this.parent.getChild("parent_right_hind_leg");
        this.parent_rightHindLeg = this.parent.getChild("parent_left_hind_leg");
        this.parent_leftFrontLeg = this.parent.getChild("parent_right_front_leg");
        this.parent_rightFrontLeg = this.parent.getChild("parent_left_front_leg");
    }

    public static LayerDefinition createBodyLayer(CubeDeformation p_170526_) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_170526_), PartPose.offset(0.0F, 6.0F, 0.0F));
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p_170526_), PartPose.offset(0.0F, 6.0F, 0.0F));
        CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, p_170526_);
        partdefinition.addOrReplaceChild("right_hind_leg", cubelistbuilder, PartPose.offset(-2.0F, 18.0F, 4.0F));
        partdefinition.addOrReplaceChild("left_hind_leg", cubelistbuilder, PartPose.offset(2.0F, 18.0F, 4.0F));
        partdefinition.addOrReplaceChild("right_front_leg", cubelistbuilder, PartPose.offset(-2.0F, 18.0F, -4.0F));
        partdefinition.addOrReplaceChild("left_front_leg", cubelistbuilder, PartPose.offset(2.0F, 18.0F, -4.0F));

        PartDefinition parent = partdefinition.addOrReplaceChild("parent", CubeListBuilder.create(), PartPose.ZERO);
        parent.addOrReplaceChild("parent_head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_170526_), PartPose.offset(0.0F, 6.0F, 0.0F));
        parent.addOrReplaceChild("parent_body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p_170526_), PartPose.offset(0.0F, 6.0F, 0.0F));
        CubeListBuilder cubelistbuilder1 = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, p_170526_);
        parent.addOrReplaceChild("parent_right_hind_leg", cubelistbuilder1, PartPose.offset(-2.0F, 18.0F, 4.0F));
        parent.addOrReplaceChild("parent_left_hind_leg", cubelistbuilder1, PartPose.offset(2.0F, 18.0F, 4.0F));
        parent.addOrReplaceChild("parent_right_front_leg", cubelistbuilder1, PartPose.offset(-2.0F, 18.0F, -4.0F));
        parent.addOrReplaceChild("parent_left_front_leg", cubelistbuilder1, PartPose.offset(2.0F, 18.0F, -4.0F));
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T p_102463_, float p_102464_, float p_102465_, float p_102466_, float p_102467_, float p_102468_) {
        this.head.yRot = p_102467_ * ((float) Math.PI / 180F);
        this.head.xRot = p_102468_ * ((float) Math.PI / 180F);
        this.rightHindLeg.xRot = Mth.cos(p_102464_ * 0.6662F) * 1.4F * p_102465_;
        this.leftHindLeg.xRot = Mth.cos(p_102464_ * 0.6662F + (float) Math.PI) * 1.4F * p_102465_;
        this.rightFrontLeg.xRot = Mth.cos(p_102464_ * 0.6662F + (float) Math.PI) * 1.4F * p_102465_;
        this.leftFrontLeg.xRot = Mth.cos(p_102464_ * 0.6662F) * 1.4F * p_102465_;

        this.parent_head.yRot = p_102467_ * ((float) Math.PI / 180F);
        this.parent_head.xRot = p_102468_ * ((float) Math.PI / 180F);
        this.parent_rightHindLeg.xRot = Mth.cos(p_102464_ * 0.6662F) * 1.4F * p_102465_;
        this.parent_leftHindLeg.xRot = Mth.cos(p_102464_ * 0.6662F + (float) Math.PI) * 1.4F * p_102465_;
        this.parent_rightFrontLeg.xRot = Mth.cos(p_102464_ * 0.6662F + (float) Math.PI) * 1.4F * p_102465_;
        this.parent_leftFrontLeg.xRot = Mth.cos(p_102464_ * 0.6662F) * 1.4F * p_102465_;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer p_170626_, int p_170627_, int p_170628_, float p_170629_, float p_170630_, float p_170631_, float p_170632_) {
        boolean flg = true;
        if (flg) {
            this.head.render(poseStack, p_170626_, p_170627_, p_170628_, p_170629_, p_170630_, p_170631_, p_170632_);
            this.root.render(poseStack, p_170626_, p_170627_, p_170628_, p_170629_, p_170630_, p_170631_, p_170632_);
            this.leftFrontLeg.render(poseStack, p_170626_, p_170627_, p_170628_, p_170629_, p_170630_, p_170631_, p_170632_);
            this.rightFrontLeg.render(poseStack, p_170626_, p_170627_, p_170628_, p_170629_, p_170630_, p_170631_, p_170632_);
            this.leftHindLeg.render(poseStack, p_170626_, p_170627_, p_170628_, p_170629_, p_170630_, p_170631_, p_170632_);
            this.rightHindLeg.render(poseStack, p_170626_, p_170627_, p_170628_, p_170629_, p_170630_, p_170631_, p_170632_);
        }

        poseStack.pushPose();
        if (flg) {
            poseStack.translate(0f, 0.375f, 0.05f);
            poseStack.rotateAround(Axis.XP.rotationDegrees(20),0,1,0);
        } else {
            poseStack.translate(0f, 1f, 0f);
        }
        poseStack.scale(0.5f, 0.5f, 0.5f);
        this.parent.render(poseStack, p_170626_, p_170627_, p_170628_, p_170629_, p_170630_, p_170631_, p_170632_);
        poseStack.popPose();
    }
}
