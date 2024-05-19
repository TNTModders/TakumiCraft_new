package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.entity.misc.TCKingBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Block;

public class TCKingBlockRenderer<T extends TCKingBlock> extends EntityRenderer<T> {

    private final Block block;
    private final BlockRenderDispatcher blockRenderDispatcher;

    public TCKingBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.block = TCBlockCore.TAKUMI_ALTAR;
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack pose, MultiBufferSource bufferSource, int p_114490_) {
        pose.pushPose();
        pose.rotateAround(Axis.YP.rotationDegrees(-90.0F), 0.0F, 1.0F, 0.0F);
        pose.translate(-0.5F, 0F, -0.5F);
        pose.scale(entity.getGlowingSize(), entity.getGlowingSize(), entity.getGlowingSize());
        this.blockRenderDispatcher.renderSingleBlock(this.block.defaultBlockState(), pose, bufferSource, p_114490_, OverlayTexture.NO_OVERLAY);
        pose.popPose();

        super.render(entity, entityYaw, partialTicks, pose, bufferSource, p_114490_);
    }


    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
