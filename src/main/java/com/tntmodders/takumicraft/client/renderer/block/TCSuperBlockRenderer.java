package com.tntmodders.takumicraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.entity.TCCreeperSuperBlockEntity;
import com.tntmodders.takumicraft.core.TCBlockCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

public class TCSuperBlockRenderer<T extends TCCreeperSuperBlockEntity> implements BlockEntityRenderer<T> {
    public static final ResourceLocation POWERED_TEXTURE = new ResourceLocation(TakumiCraftCore.MODID, "textures/block/takumiblock_r.png");
    private final BlockEntityRendererProvider.Context context;
    private final BlockRenderDispatcher blockRenderer;

    public TCSuperBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(T blockentity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int p_112311_, int p_112312_) {
        ModelManager modelmanager = this.blockRenderer.getBlockModelShaper().getModelManager();
        BakedModel model = this.blockRenderer.getBlockModel(TCBlockCore.CREEPER_BOMB.defaultBlockState());
        poseStack.pushPose();
        float f = Minecraft.getInstance().player.tickCount * 0.0005f;
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.energySwirl(POWERED_TEXTURE, f % 1f, f % 1f));
        this.blockRenderer.getModelRenderer().renderModel(poseStack.last(), consumer, null, model, 1f, 1f, 1f, p_112311_, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
