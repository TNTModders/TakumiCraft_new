package com.tntmodders.takumicraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCBreezeCreeperRenderer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCBreezeCreeperRenderState;
import com.tntmodders.takumicraft.entity.mobs.TCBreezeCreeper;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BreezeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.BreezeRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCBreezeCreeperWindLayer extends RenderLayer<TCBreezeCreeperRenderState, TCBreezeCreeperRenderer.TCBreezeCreeperModel<TCBreezeCreeperRenderState>> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/breezecreeper_wind.png");
    private final TCBreezeCreeperRenderer.TCBreezeCreeperModel<TCBreezeCreeperRenderState> model;

    public TCBreezeCreeperWindLayer(EntityRendererProvider.Context p_343821_, RenderLayerParent<TCBreezeCreeperRenderState, TCBreezeCreeperRenderer.TCBreezeCreeperModel<TCBreezeCreeperRenderState>> p_312719_) {
        super(p_312719_);
        this.model = new TCBreezeCreeperRenderer.TCBreezeCreeperModel<>(p_343821_.bakeLayer(ModelLayers.BREEZE_WIND));
    }

    @Override
    public void render(PoseStack p_312401_, MultiBufferSource p_310855_, int p_312784_, TCBreezeCreeperRenderState p_362639_, float p_311307_, float p_312259_) {
        VertexConsumer vertexconsumer = p_310855_.getBuffer(RenderType.breezeWind(TEXTURE_LOCATION, this.xOffset(p_362639_.ageInTicks) % 1.0F, 0.0F));
        this.model.setupAnim(p_362639_);
        TCBreezeCreeperRenderer.enable(this.model, this.model.wind()).renderToBuffer(p_312401_, vertexconsumer, p_312784_, OverlayTexture.NO_OVERLAY);
    }

    private float xOffset(float p_310525_) {
        return p_310525_ * 0.02F;
    }
}