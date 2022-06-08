package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCSlimeCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCSlimeCreeperRenderer extends MobRenderer<TCSlimeCreeper, SlimeModel<TCSlimeCreeper>> {

    public TCSlimeCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new SlimeModel<>(context.bakeLayer(ModelLayers.SLIME)), 0.25F);
        this.addLayer(new SlimeOuterLayer<>(this, context.getModelSet()));
        this.addLayer(new TCCreeperPowerLayer(this, context.getModelSet(), new SlimeModel<>(context.bakeLayer(ModelLayers.SLIME_OUTER)), TCEntityCore.SLIME));
    }

    @Override
    public void render(TCSlimeCreeper p_115976_, float p_115977_, float p_115978_, PoseStack p_115979_, MultiBufferSource p_115980_, int p_115981_) {
        this.shadowRadius = 0.25F * (float) p_115976_.getSize();
        super.render(p_115976_, p_115977_, p_115978_, p_115979_, p_115980_, p_115981_);
    }

    @Override
    protected void scale(TCSlimeCreeper p_115983_, PoseStack p_115984_, float p_115985_) {
        float f = 0.999F;
        p_115984_.scale(0.999F, 0.999F, 0.999F);
        p_115984_.translate(0.0D, 0.001F, 0.0D);
        float f1 = (float) p_115983_.getSize();
        float f2 = Mth.lerp(p_115985_, p_115983_.oSquish, p_115983_.squish) / (f1 * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        p_115984_.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
        TCClientUtils.scaleSwelling(p_115983_, p_115984_, p_115985_);
    }

    @Override
    protected float getWhiteOverlayProgress(TCSlimeCreeper p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(TCSlimeCreeper creeper) {
        return new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.getType().toShortString() + ".png");
    }
}
