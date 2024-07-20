package com.tntmodders.takumicraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCBreezeCreeperRenderer;
import com.tntmodders.takumicraft.entity.mobs.TCBreezeCreeper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCBreezeCreeperEyesLayer extends RenderLayer<TCBreezeCreeper, TCBreezeCreeperRenderer.TCBreezeCreeperModel<TCBreezeCreeper>> {
    private static final RenderType BREEZE_EYES = RenderType.breezeEyes(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/breezecreeper_eyes.png"));

    public TCBreezeCreeperEyesLayer(RenderLayerParent<TCBreezeCreeper, TCBreezeCreeperRenderer.TCBreezeCreeperModel<TCBreezeCreeper>> p_310165_) {
        super(p_310165_);
    }

    @Override
    public void render(PoseStack p_312911_, MultiBufferSource p_312666_, int p_311532_, TCBreezeCreeper p_311391_, float p_311193_, float p_309423_, float p_310215_, float p_311406_, float p_311840_, float p_312197_) {
        VertexConsumer vertexconsumer = p_312666_.getBuffer(BREEZE_EYES);
        TCBreezeCreeperRenderer.TCBreezeCreeperModel<TCBreezeCreeper> breezemodel = this.getParentModel();
        TCBreezeCreeperRenderer.enable(breezemodel, breezemodel.head(), breezemodel.eyes()).renderToBuffer(p_312911_, vertexconsumer, p_311532_, OverlayTexture.NO_OVERLAY);
    }
}