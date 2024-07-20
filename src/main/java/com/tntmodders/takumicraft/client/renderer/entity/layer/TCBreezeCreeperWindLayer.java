package com.tntmodders.takumicraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCBreezeCreeperRenderer;
import com.tntmodders.takumicraft.entity.mobs.TCBreezeCreeper;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCBreezeCreeperWindLayer extends RenderLayer<TCBreezeCreeper, TCBreezeCreeperRenderer.TCBreezeCreeperModel<TCBreezeCreeper>> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/breezecreeper_wind.png");
    private final TCBreezeCreeperRenderer.TCBreezeCreeperModel<TCBreezeCreeper> model;

    public TCBreezeCreeperWindLayer(EntityRendererProvider.Context p_343821_, RenderLayerParent<TCBreezeCreeper, TCBreezeCreeperRenderer.TCBreezeCreeperModel<TCBreezeCreeper>> p_312719_) {
        super(p_312719_);
        this.model = new TCBreezeCreeperRenderer.TCBreezeCreeperModel<>(p_343821_.bakeLayer(ModelLayers.BREEZE_WIND));
    }

    @Override
    public void render(PoseStack p_312401_, MultiBufferSource p_310855_, int p_312784_, TCBreezeCreeper p_309942_, float p_311307_, float p_312259_, float p_311774_, float p_312816_, float p_312844_, float p_313068_) {
        float f = (float) p_309942_.tickCount + p_311774_;
        VertexConsumer vertexconsumer = p_310855_.getBuffer(RenderType.breezeWind(TEXTURE_LOCATION, this.xOffset(f) % 1.0F, 0.0F));
        this.model.setupAnim(p_309942_, p_311307_, p_312259_, p_312816_, p_312844_, p_313068_);
        TCBreezeCreeperRenderer.enable(this.model, this.model.wind()).renderToBuffer(p_312401_, vertexconsumer, p_312784_, OverlayTexture.NO_OVERLAY);
    }

    private float xOffset(float p_310525_) {
        return p_310525_ * 0.02F;
    }
}