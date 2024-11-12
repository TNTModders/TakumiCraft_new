package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCKingBlockRenderState;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.entity.misc.TCKingBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Block;

public class TCKingBlockRenderer<T extends TCKingBlock, S extends TCKingBlockRenderState> extends EntityRenderer<T, S> {

    private final Block block;
    private final BlockRenderDispatcher blockRenderDispatcher;

    public TCKingBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.block = TCBlockCore.TAKUMI_ALTAR;
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(S state, PoseStack pose, MultiBufferSource source, int p_114490_) {
        pose.pushPose();
        pose.rotateAround(Axis.YP.rotationDegrees(-90.0F), 0.0F, 1.0F, 0.0F);
        pose.translate(-0.5F, 0F, -0.5F);
        pose.scale(state.glowingSize, state.glowingSize, state.glowingSize);
        this.blockRenderDispatcher.renderSingleBlock(this.block.defaultBlockState(), pose, source, p_114490_, OverlayTexture.NO_OVERLAY);
        pose.popPose();

        super.render(state, pose, source, p_114490_);
    }

    @Override
    public S createRenderState() {
        return (S) new TCKingBlockRenderState();
    }

    @Override
    public void extractRenderState(T entity, S state, float p_363243_) {
        super.extractRenderState(entity, state, p_363243_);
        state.glowingSize = entity.getGlowingSize();
    }
}
