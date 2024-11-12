package com.tntmodders.takumicraft.client.model;

import com.tntmodders.takumicraft.client.renderer.entity.state.TCZombieCreeperRenderState;
import com.tntmodders.takumicraft.entity.mobs.TCZombieCreeper;
import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelPart;

public class TCZombieModel<T extends TCZombieCreeperRenderState> extends AbstractZombieModel<T> {
    public TCZombieModel(ModelPart p_170337_) {
        super(p_170337_);
    }
}
