package com.tntmodders.takumicraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCZombieCreeperRenderState;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.VillagerLikeModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCZombieVillagerModel<T extends TCZombieCreeperRenderState> extends HumanoidModel<T> implements VillagerLikeModel {
    private final ModelPart hatRim = this.hat.getChild("hat_rim");

    public TCZombieVillagerModel(ModelPart p_171092_) {
        super(p_171092_);
    }

    @Override
    public void setupAnim(T p_364685_) {
        super.setupAnim(p_364685_);
        float f = p_364685_.attackTime;
        AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, p_364685_.isAggressive, f, p_364685_.ageInTicks);
    }

    @Override
    public void hatVisible(boolean p_104182_) {
        this.head.visible = p_104182_;
        this.hat.visible = p_104182_;
        this.hatRim.visible = p_104182_;
    }

    @Override
    public void translateToArms(PoseStack p_378511_) {
        this.translateToHand(HumanoidArm.RIGHT, p_378511_);
    }
}
