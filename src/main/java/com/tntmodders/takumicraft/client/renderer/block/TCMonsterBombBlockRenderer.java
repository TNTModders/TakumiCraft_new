package com.tntmodders.takumicraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.TCMonsterBombBlock;
import com.tntmodders.takumicraft.block.entity.TCMonsterBombBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntitySpawnReason;

public class TCMonsterBombBlockRenderer implements BlockEntityRenderer<TCMonsterBombBlockEntity> {

    public TCMonsterBombBlockRenderer(BlockEntityRendererProvider.Context p_173540_) {
    }

    public static void renderBomb(TCMonsterBombBlock bomb, PoseStack poseStack, MultiBufferSource source, int p_112311_, int p_112312_) {
        var creeper = bomb.getContext().entityType().create(Minecraft.getInstance().level, EntitySpawnReason.LOAD);
        if (Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(creeper) instanceof LivingEntityRenderer renderer) {
            if (renderer.getModel() instanceof EntityModel model) {
                try {
                    ModelPart part = model.root().getChild("head");
                    part.xRot = 0f;
                    part.yRot = 0f;
                    part.zRot = 0f;
                    poseStack.pushPose();
                    poseStack.translate(0.5, 0.75, 0.5);
                    poseStack.rotateAround(Axis.XN.rotationDegrees(180), 1, 0, 0);
                    poseStack.scale(2, 2, 2);
                    VertexConsumer consumer = source.getBuffer(RenderType.entityCutoutNoCull(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/" + bomb.getContext().getRegistryName() + ".png")));
                    part.render(poseStack, consumer, p_112311_, p_112312_);
                    poseStack.popPose();
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public void render(TCMonsterBombBlockEntity blockEntity, float p_112308_, PoseStack poseStack, MultiBufferSource source, int p_112311_, int p_112312_) {
        if (blockEntity.getBlockState().getBlock() instanceof TCMonsterBombBlock bombBlock) {
            renderBomb(bombBlock, poseStack, source, p_112311_, p_112312_);
        }
    }
}
