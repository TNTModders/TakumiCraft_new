package com.tntmodders.takumicraft.client.model;

import com.tntmodders.takumicraft.client.renderer.entity.state.TCChildCreeperRenderState;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.util.Mth;

public class TCChildCreeperModel extends CreeperModel {

    public final ModelPart root;
    public final ModelPart head;
    public final ModelPart rightHindLeg;
    public final ModelPart leftHindLeg;
    public final ModelPart rightFrontLeg;
    public final ModelPart leftFrontLeg;

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
    public void setupAnim(CreeperRenderState state) {
        if (state instanceof TCChildCreeperRenderState childState && childState.isChild) {
            this.head.visible = false;
        } else {
            this.head.visible = true;
            this.head.yRot = state.yRot * ((float) Math.PI / 180F);
            this.head.xRot = state.xRot * ((float) Math.PI / 180F);
            this.rightHindLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F) * 1.4F * state.walkAnimationSpeed;
            this.leftHindLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F + (float) Math.PI) * 1.4F * state.walkAnimationSpeed;
            this.rightFrontLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F + (float) Math.PI) * 1.4F * state.walkAnimationSpeed;
            this.leftFrontLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F) * 1.4F * state.walkAnimationSpeed;
        }

        this.parent_head.yRot = state.yRot * ((float) Math.PI / 180F);
        this.parent_head.xRot = state.xRot * ((float) Math.PI / 180F);
        this.parent_rightHindLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F) * 1.4F * state.walkAnimationSpeed;
        this.parent_leftHindLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F + (float) Math.PI) * 1.4F * state.walkAnimationSpeed;
        this.parent_rightFrontLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F + (float) Math.PI) * 1.4F * state.walkAnimationSpeed;
        this.parent_leftFrontLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F) * 1.4F * state.walkAnimationSpeed;
    }
}
