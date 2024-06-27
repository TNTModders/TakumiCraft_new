package com.tntmodders.takumicraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TCCreeperShulkerBoxRenderer extends ShulkerBoxRenderer {
    private final ShulkerModel<?> model;

    public TCCreeperShulkerBoxRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        this.model = new ShulkerModel(context.bakeLayer(ModelLayers.SHULKER));
    }

    @Override
    public void render(ShulkerBoxBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int x, int z) {
        Direction direction = Direction.UP;
        if (entity.hasLevel()) {
            BlockState blockstate = entity.getLevel().getBlockState(entity.getBlockPos());
            if (blockstate.getBlock() instanceof ShulkerBoxBlock) {
                direction = blockstate.getValue(ShulkerBoxBlock.FACING);
            }
        }
        Material material = new Material(Sheets.SHULKER_SHEET, ResourceLocation.tryBuild(TakumiCraftCore.MODID, "entity/shulker/creepershulkerbox"));
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        float f = 0.9995F;
        poseStack.scale(0.9995F, 0.9995F, 0.9995F);
        poseStack.mulPose(direction.getRotation());
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.0F, -1.0F, 0.0F);
        ModelPart modelpart = this.model.getLid();
        modelpart.setPos(0.0F, 24.0F - entity.getProgress(partialTicks) * 0.5F * 16.0F, 0.0F);
        modelpart.yRot = 270.0F * entity.getProgress(partialTicks) * (float) (Math.PI / 180.0);
        VertexConsumer vertexconsumer = material.buffer(bufferSource, RenderType::entityCutoutNoCull);
        this.model.renderToBuffer(poseStack, vertexconsumer, x, z);
        poseStack.popPose();
    }
}
