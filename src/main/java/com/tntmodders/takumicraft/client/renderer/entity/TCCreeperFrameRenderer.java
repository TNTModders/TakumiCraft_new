package com.tntmodders.takumicraft.client.renderer.entity;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.client.TCRenderCore;
import com.tntmodders.takumicraft.entity.decoration.TCCreeperFrame;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.resources.ResourceLocation;

public class TCCreeperFrameRenderer<T extends TCCreeperFrame> extends ItemFrameRenderer<T> {
    public static final ResourceLocation CREEPER_FRAME_LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/block/creeperframe.png");
    public static final ResourceLocation CREEPER_GLOW_FRAME_LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/block/creeperframe_glowing.png");

    private final ModelPart modelPart;

    public TCCreeperFrameRenderer(EntityRendererProvider.Context p_174204_) {
        super(p_174204_);
        this.modelPart = p_174204_.bakeLayer(TCRenderCore.FRAME);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-8, -8, -0.6f, 16, 16, 0), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 16, 16);
    }
}
