package com.tntmodders.takumicraft.client.model;

import com.tntmodders.takumicraft.client.renderer.entity.state.TCSheepCreeperRenderState;
import com.tntmodders.takumicraft.entity.mobs.TCSheepCreeper;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class TCSheepCreeperModel<T extends TCSheepCreeperRenderState> extends SheepModel {
    public TCSheepCreeperModel(ModelPart p_170903_) {
        super(p_170903_);
    }
}
