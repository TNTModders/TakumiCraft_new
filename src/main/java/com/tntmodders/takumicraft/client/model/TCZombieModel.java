package com.tntmodders.takumicraft.client.model;

import com.tntmodders.takumicraft.entity.mobs.TCZombieCreeper;
import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelPart;

public class TCZombieModel<T extends TCZombieCreeper> extends AbstractZombieModel<T> {
    public TCZombieModel(ModelPart p_170337_) {
        super(p_170337_);
    }

    @Override
    public boolean isAggressive(T p_101999_) {
        return p_101999_.isAggressive();
    }

    @Override
    public void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {
        super.setupAnim(p_102618_, p_102619_, p_102620_, p_102621_, p_102622_, p_102623_);
        AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, this.isAggressive(p_102618_), this.attackTime, p_102621_);
    }
}
