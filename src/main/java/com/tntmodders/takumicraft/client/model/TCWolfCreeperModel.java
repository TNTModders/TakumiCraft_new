package com.tntmodders.takumicraft.client.model;

import com.google.common.collect.ImmutableList;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCWolfCreeperRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class TCWolfCreeperModel<T extends TCWolfCreeperRenderState> extends EntityModel<T> {
    private static final String REAL_HEAD = "real_head";
    private static final String UPPER_BODY = "upper_body";
    private static final String REAL_TAIL = "real_tail";
    private static final int LEG_SIZE = 8;
    private final ModelPart head;
    private final ModelPart realHead;
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private final ModelPart realTail;
    private final ModelPart upperBody;

    public TCWolfCreeperModel(ModelPart p_171087_) {
        super(p_171087_);
        this.head = p_171087_.getChild("head");
        this.realHead = this.head.getChild("real_head");
        this.body = p_171087_.getChild("body");
        this.upperBody = p_171087_.getChild("upper_body");
        this.rightHindLeg = p_171087_.getChild("right_hind_leg");
        this.leftHindLeg = p_171087_.getChild("left_hind_leg");
        this.rightFrontLeg = p_171087_.getChild("right_front_leg");
        this.leftFrontLeg = p_171087_.getChild("left_front_leg");
        this.tail = p_171087_.getChild("tail");
        this.realTail = this.tail.getChild("real_tail");
    }


    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.head);
    }


    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.tail, this.upperBody);
    }

    @Override
    public void setupAnim(T p_363855_) {
        super.setupAnim(p_363855_);
        float f = p_363855_.walkAnimationPos;
        float f1 = p_363855_.walkAnimationSpeed;
        if (p_363855_.isAngry) {
            this.tail.yRot = 0.0F;
        } else {
            this.tail.yRot = Mth.cos(f * 0.6662F) * 1.4F * f1;
        }

        if (p_363855_.isSitting) {
            float f2 = p_363855_.ageScale;
            this.upperBody.y += 2.0F * f2;
            this.upperBody.xRot = (float) (Math.PI * 2.0 / 5.0);
            this.upperBody.yRot = 0.0F;
            this.body.y += 4.0F * f2;
            this.body.z -= 2.0F * f2;
            this.body.xRot = (float) (Math.PI / 4);
            this.tail.y += 9.0F * f2;
            this.tail.z -= 2.0F * f2;
            this.rightHindLeg.y += 6.7F * f2;
            this.rightHindLeg.z -= 5.0F * f2;
            this.rightHindLeg.xRot = (float) (Math.PI * 3.0 / 2.0);
            this.leftHindLeg.y += 6.7F * f2;
            this.leftHindLeg.z -= 5.0F * f2;
            this.leftHindLeg.xRot = (float) (Math.PI * 3.0 / 2.0);
            this.rightFrontLeg.xRot = 5.811947F;
            this.rightFrontLeg.x += 0.01F * f2;
            this.rightFrontLeg.y += f2;
            this.leftFrontLeg.xRot = 5.811947F;
            this.leftFrontLeg.x -= 0.01F * f2;
            this.leftFrontLeg.y += f2;
        } else {
            this.rightHindLeg.xRot = Mth.cos(f * 0.6662F) * 1.4F * f1;
            this.leftHindLeg.xRot = Mth.cos(f * 0.6662F + (float) Math.PI) * 1.4F * f1;
            this.rightFrontLeg.xRot = Mth.cos(f * 0.6662F + (float) Math.PI) * 1.4F * f1;
            this.leftFrontLeg.xRot = Mth.cos(f * 0.6662F) * 1.4F * f1;
        }

        this.realHead.zRot = p_363855_.headRollAngle + p_363855_.getBodyRollAngle(0.0F);
        this.upperBody.zRot = p_363855_.getBodyRollAngle(-0.08F);
        this.body.zRot = p_363855_.getBodyRollAngle(-0.16F);
        this.realTail.zRot = p_363855_.getBodyRollAngle(-0.2F);
        this.head.xRot = p_363855_.xRot * (float) (Math.PI / 180.0);
        this.head.yRot = p_363855_.yRot * (float) (Math.PI / 180.0);
        this.tail.xRot = p_363855_.tailAngle;
    }
}
