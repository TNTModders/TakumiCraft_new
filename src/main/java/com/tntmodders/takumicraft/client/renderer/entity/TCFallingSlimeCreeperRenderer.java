package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.entity.mobs.TCFallingSlimeCreeper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TCFallingSlimeCreeperRenderer<T extends TCFallingSlimeCreeper> extends TCCreeperRenderer<T> {
    private static final ResourceLocation LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper.png");

    public TCFallingSlimeCreeperRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new TCFallingSlimeOuterLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(T creeper) {
        return LOCATION;
    }

    @OnlyIn(Dist.CLIENT)
    public static class TCFallingSlimeOuterLayer<T extends LivingEntity> extends RenderLayer<T, CreeperModel<T>> {
        private final EntityModel<T> model;

        public TCFallingSlimeOuterLayer(RenderLayerParent<T, CreeperModel<T>> p_174536_, EntityModelSet p_174537_) {
            super(p_174536_);
            this.model = new CreeperModel<>(p_174537_.bakeLayer(ModelLayers.CREEPER));
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int p_117472_, T creeper, float p_117474_, float p_117475_, float p_117476_, float p_117477_, float p_117478_, float p_117479_) {
            Minecraft minecraft = Minecraft.getInstance();
            boolean flag = minecraft.shouldEntityAppearGlowing(creeper) && creeper.isInvisible();
            if (!creeper.isInvisible() || flag) {
                VertexConsumer vertexconsumer;
                if (flag) {
                    vertexconsumer = bufferSource.getBuffer(RenderType.outline(this.getTextureLocation(creeper)));
                } else {
                    vertexconsumer = bufferSource.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(creeper)));
                }
                poseStack.pushPose();
                poseStack.scale(1.2f, 1.2f, 1.2f);
                this.getParentModel().copyPropertiesTo(this.model);
                this.model.prepareMobModel(creeper, p_117474_, p_117475_, p_117476_);
                this.model.setupAnim(creeper, p_117474_, p_117475_, p_117477_, p_117478_, p_117479_);
                this.model.renderToBuffer(poseStack, vertexconsumer, p_117472_, LivingEntityRenderer.getOverlayCoords(creeper, 0.0F));
                poseStack.popPose();
            }
        }

        @Override
        public ResourceLocation getTextureLocation(T creeper) {
            return ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.getType().toShortString() + ".png");
        }
    }
}
