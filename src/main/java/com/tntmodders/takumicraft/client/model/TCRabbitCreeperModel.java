package com.tntmodders.takumicraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCRabbitCreeperRenderState;
import com.tntmodders.takumicraft.entity.mobs.TCRabbitCreeper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class TCRabbitCreeperModel<T extends TCRabbitCreeperRenderState> extends RabbitModel {

    public TCRabbitCreeperModel(ModelPart p_170881_) {
        super(p_170881_);
    }
}
