package com.tntmodders.takumicraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.entity.TCCreeperSuperBlockEntity;
import com.tntmodders.takumicraft.core.TCBlockCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;

import java.util.Arrays;

public class TCSuperBlockRenderer<T extends TCCreeperSuperBlockEntity> implements BlockEntityRenderer<T> {
    public static final ResourceLocation POWERED_TEXTURE = new ResourceLocation(TakumiCraftCore.MODID, "textures/takumiblock/takumiblock_overlay.png");
    private final BlockEntityRendererProvider.Context context;
    private final BlockRenderDispatcher blockRenderer;

    public TCSuperBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(T blockentity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int p_112311_, int p_112312_) {
        try {
            ModelManager modelmanager = this.blockRenderer.getBlockModelShaper().getModelManager();
            BlockState state = blockentity.getState();
            BakedModel model;
            if (state == null || state.isAir()) {
                model = this.blockRenderer.getBlockModel(TCBlockCore.SUPER_BLOCK.defaultBlockState());
                state = TCBlockCore.SUPER_BLOCK.defaultBlockState();
            } else {
                model = this.blockRenderer.getBlockModel(state);
                if (model == null || model.equals(this.blockRenderer.getBlockModelShaper().getModelManager().getMissingModel())) {
                    model = this.blockRenderer.getBlockModel(TCBlockCore.SUPER_BLOCK.defaultBlockState());
                }
                poseStack.pushPose();
                BlockColors colors = BlockColors.createDefault();
                int i = colors.getColor(state, null, null, 0);
                float factorR = (float) (i >> 16 & 255) / 255.0F;
                float factorG = (float) (i >> 8 & 255) / 255.0F;
                float factorB = (float) (i & 255) / 255.0F;
                final BakedModel setModel = model;
                final BlockState setState = state;
                if (model.getQuads(state, null, Minecraft.getInstance().level.getRandom()).isEmpty() && Arrays.stream(Direction.values()).allMatch(direction -> setModel.getQuads(setState, direction, Minecraft.getInstance().level.getRandom()).isEmpty())) {
                    state = TCBlockCore.SUPER_BLOCK.defaultBlockState();
                    model = this.blockRenderer.getBlockModel(TCBlockCore.SUPER_BLOCK.defaultBlockState());
                    blockentity.setState(Blocks.AIR.defaultBlockState());
                } else {
                    ChunkRenderTypeSet set = model.getRenderTypes(state, Minecraft.getInstance().level.getRandom(), null);
                    this.blockRenderer.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(set.asList().getFirst()), state, model, factorR, factorG, factorB, p_112311_, OverlayTexture.NO_OVERLAY);
                }
                poseStack.popPose();
            }
            if (!blockentity.isHideOverlay() || state.getBlock().equals(TCBlockCore.SUPER_BLOCK)) {
                poseStack.pushPose();
                float overrap = 0.01f;
                poseStack.scale(1 + overrap, 1 + overrap, 1 + overrap);
                poseStack.translate(-overrap / 2, 0, -overrap / 2);
                float f = Minecraft.getInstance().player.tickCount * 0.0005f;
                VertexConsumer consumer = bufferSource.getBuffer(RenderType.energySwirl(POWERED_TEXTURE, f % 1f, f % 1f));
                this.blockRenderer.getModelRenderer().renderModel(poseStack.last(), consumer, state, model, 0.5f, 0.5f, 0.5f, p_112311_, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
        } catch (Exception ignored) {
        }
    }
}
