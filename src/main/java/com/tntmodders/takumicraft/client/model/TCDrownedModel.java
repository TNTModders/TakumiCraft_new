package com.tntmodders.takumicraft.client.model;

import com.tntmodders.takumicraft.client.renderer.entity.state.TCZombieCreeperRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TCDrownedModel<T extends TCZombieCreeperRenderState> extends TCZombieModel<T> {
    public TCDrownedModel(ModelPart p_170337_) {
        super(p_170337_);
    }

    public static LayerDefinition createBodyLayer(CubeDeformation p_170536_) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(p_170536_, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170536_),
                PartPose.offset(5.0F, 2.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170536_),
                PartPose.offset(1.9F, 12.0F, 0.0F)
        );
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    protected HumanoidModel.ArmPose getArmPose(T p_362447_, HumanoidArm p_364107_) {
        ItemStack itemstack = p_364107_ == HumanoidArm.RIGHT ? p_362447_.rightHandItem : p_362447_.leftHandItem;
        return itemstack.is(Items.TRIDENT) && p_362447_.isAggressive && p_362447_.mainArm == p_364107_
                ? HumanoidModel.ArmPose.THROW_SPEAR
                : HumanoidModel.ArmPose.EMPTY;
    }

    @Override
    public void setupAnim(T p_368669_) {
        super.setupAnim(p_368669_);
        if (this.getArmPose(p_368669_, HumanoidArm.LEFT) == HumanoidModel.ArmPose.THROW_SPEAR) {
            this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float) Math.PI;
            this.leftArm.yRot = 0.0F;
        }

        if (this.getArmPose(p_368669_, HumanoidArm.RIGHT) == HumanoidModel.ArmPose.THROW_SPEAR) {
            this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float) Math.PI;
            this.rightArm.yRot = 0.0F;
        }

        float f = p_368669_.swimAmount;
        if (f > 0.0F) {
            this.rightArm.xRot = Mth.rotLerpRad(f, this.rightArm.xRot, (float) (-Math.PI * 4.0 / 5.0))
                    + f * 0.35F * Mth.sin(0.1F * p_368669_.ageInTicks);
            this.leftArm.xRot = Mth.rotLerpRad(f, this.leftArm.xRot, (float) (-Math.PI * 4.0 / 5.0))
                    - f * 0.35F * Mth.sin(0.1F * p_368669_.ageInTicks);
            this.rightArm.zRot = Mth.rotLerpRad(f, this.rightArm.zRot, -0.15F);
            this.leftArm.zRot = Mth.rotLerpRad(f, this.leftArm.zRot, 0.15F);
            this.leftLeg.xRot = this.leftLeg.xRot - f * 0.55F * Mth.sin(0.1F * p_368669_.ageInTicks);
            this.rightLeg.xRot = this.rightLeg.xRot + f * 0.55F * Mth.sin(0.1F * p_368669_.ageInTicks);
            this.head.xRot = 0.0F;
        }
    }
}
