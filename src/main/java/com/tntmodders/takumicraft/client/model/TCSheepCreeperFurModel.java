package com.tntmodders.takumicraft.client.model;

import com.tntmodders.takumicraft.client.renderer.entity.state.TCSheepCreeperRenderState;
import com.tntmodders.takumicraft.entity.mobs.TCSheepCreeper;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCSheepCreeperFurModel<T extends TCSheepCreeperRenderState> extends SheepFurModel {

    public TCSheepCreeperFurModel(ModelPart p_170900_) {
        super(p_170900_);
    }
}
