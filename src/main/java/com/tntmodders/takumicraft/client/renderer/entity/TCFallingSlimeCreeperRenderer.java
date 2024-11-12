package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCCreeperRenderState;
import com.tntmodders.takumicraft.core.client.TCRenderCore;
import com.tntmodders.takumicraft.entity.mobs.TCFallingSlimeCreeper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TCFallingSlimeCreeperRenderer<T extends TCFallingSlimeCreeper, S extends TCCreeperRenderState> extends TCCreeperRenderer<T, S> {
    private static final ResourceLocation LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper.png");

    public TCFallingSlimeCreeperRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new TCFallingSlimeOuterLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(S state) {
        return LOCATION;
    }

    @OnlyIn(Dist.CLIENT)
    public static class TCFallingSlimeOuterLayer<T extends TCCreeperRenderState, S extends CreeperModel> extends RenderLayer<T, S> {
        private final EntityModel model;

        public TCFallingSlimeOuterLayer(RenderLayerParent<T, S> p_174536_, EntityModelSet p_174537_) {
            super(p_174536_);
            this.model = new CreeperModel(p_174537_.bakeLayer(TCRenderCore.FALLING_SLIME));
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int p_117472_, T state, float p_117474_, float p_117475_) {
            Minecraft minecraft = Minecraft.getInstance();
            boolean flag = state.appearsGlowing && state.isInvisible;
            if (!state.isInvisible || flag) {
                VertexConsumer vertexconsumer;
                if (flag) {
                    vertexconsumer = bufferSource.getBuffer(RenderType.outline(this.getTextureLocation(state)));
                } else {
                    vertexconsumer = bufferSource.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(state)));
                }
                poseStack.pushPose();
                this.model.setupAnim(state);
                this.model.renderToBuffer(poseStack, vertexconsumer, p_117472_, LivingEntityRenderer.getOverlayCoords(state, 0.0F));
                poseStack.popPose();
            }
        }

        public ResourceLocation getTextureLocation(T creeper) {
            return ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.context.entityType().toShortString() + ".png");
        }
    }
}
