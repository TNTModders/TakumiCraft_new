package com.tntmodders.takumicraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import net.minecraft.client.model.Model;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TCCreeperShulkerBoxRenderer extends ShulkerBoxRenderer {
    private final TCShulkerBoxModel model;

    public TCCreeperShulkerBoxRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        this.model = new TCShulkerBoxModel(context.bakeLayer(ModelLayers.SHULKER));
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
        this.model.animate(entity, partialTicks);
        VertexConsumer vertexconsumer = material.buffer(bufferSource, RenderType::entityCutoutNoCull);
        this.model.renderToBuffer(poseStack, vertexconsumer, x, z);
        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public static class TCShulkerBoxModel extends Model {
        private final ModelPart lid;

        public TCShulkerBoxModel(ModelPart p_366433_) {
            super(p_366433_, RenderType::entityCutoutNoCull);
            this.lid = p_366433_.getChild("lid");
        }

        public void animate(ShulkerBoxBlockEntity p_362661_, float p_363916_) {
            this.lid.setPos(0.0F, 24.0F - p_362661_.getProgress(p_363916_) * 0.5F * 16.0F, 0.0F);
            this.lid.yRot = 270.0F * p_362661_.getProgress(p_363916_) * (float) (Math.PI / 180.0);
        }
    }
}
