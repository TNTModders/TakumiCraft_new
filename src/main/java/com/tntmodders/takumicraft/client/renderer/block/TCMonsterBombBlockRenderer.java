package com.tntmodders.takumicraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.TCMonsterBombBlock;
import com.tntmodders.takumicraft.block.entity.TCMonsterBombBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public class TCMonsterBombBlockRenderer implements BlockEntityRenderer<TCMonsterBombBlockEntity> {

    public TCMonsterBombBlockRenderer(BlockEntityRendererProvider.Context p_173540_) {
    }

    public static void renderBomb(TCMonsterBombBlock bomb, PoseStack poseStack, MultiBufferSource source, int p_112311_, int p_112312_) {
        if (Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(bomb.getContext().entityType().create(Minecraft.getInstance().level)) instanceof LivingEntityRenderer renderer) {
            if (renderer.getModel() instanceof HierarchicalModel<?> model) {
                try {
                    ModelPart part = model.root().getChild("head");
                    poseStack.pushPose();
                    poseStack.translate(0.5, 0.75, 0.5);
                    poseStack.rotateAround(Axis.XN.rotationDegrees(180), 1, 0, 0);
                    poseStack.scale(2, 2, 2);
                    VertexConsumer consumer = source.getBuffer(RenderType.entityCutoutNoCull(new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/creeper/" + bomb.getContext().getRegistryName() + ".png")));
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
