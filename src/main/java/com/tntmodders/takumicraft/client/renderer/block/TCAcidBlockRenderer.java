package com.tntmodders.takumicraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.entity.TCAcidBlockEntity;
import com.tntmodders.takumicraft.core.client.TCRenderCore;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TCAcidBlockRenderer implements BlockEntityRenderer<TCAcidBlockEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(TakumiCraftCore.MODID, "textures/block/acidblock.png");
    private final ModelPart modelPart;

    public TCAcidBlockRenderer(BlockEntityRendererProvider.Context p_173540_) {
        this.modelPart = p_173540_.bakeLayer(TCRenderCore.ACID);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("block", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 16, 16, 16), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void render(TCAcidBlockEntity acid, float p_112308_, PoseStack poseStack, MultiBufferSource source, int p_112311_, int p_112312_) {
        poseStack.pushPose();
        float tick = acid.getTick() / 2f;
        float white = (int) (tick / 4.5f) % 2 == 1 ? 0.0F : Mth.clamp(tick, 0.4F, 0.8F);
        int overlay = OverlayTexture.pack(OverlayTexture.u(white), OverlayTexture.v(false));
        float f1 = 1.0F + Mth.sin(tick * 100.0F) * tick * 0.001F;
        tick = Mth.clamp(tick, 0.0F, 1.0F);
        float f2 = (1.0F + tick * 0.1F) * f1;
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(f2, f2, f2);
        poseStack.translate(-0.5, -0.5, -0.5);
        VertexConsumer consumer = source.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        this.modelPart.render(poseStack, consumer, p_112311_, overlay);
        poseStack.popPose();
    }
}
