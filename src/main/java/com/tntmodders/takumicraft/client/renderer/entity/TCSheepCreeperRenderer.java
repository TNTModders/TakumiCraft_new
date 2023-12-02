package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCSheepCreeperModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCSheepCreeperFurLayer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCSheepCreeper;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCSheepCreeperRenderer extends MobRenderer<TCSheepCreeper, TCSheepCreeperModel<TCSheepCreeper>> {
    private static final ResourceLocation LOCATION = new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/creeper/sheepcreeper.png");

    public TCSheepCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new TCSheepCreeperModel<>(p_173956_.bakeLayer(ModelLayers.SHEEP)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer<>(this, p_173956_.getModelSet(), new TCSheepCreeperModel<>(p_173956_.bakeLayer(ModelLayers.SHEEP_FUR)), TCEntityCore.SHEEP, true));
        this.addLayer(new TCSheepCreeperFurLayer(this, p_173956_.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(TCSheepCreeper p_114482_) {
        return LOCATION;
    }

    @Override
    protected void scale(TCSheepCreeper p_114046_, PoseStack p_114047_, float p_114048_) {
        float f = p_114046_.getSwelling(p_114048_);
        float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        f *= f;
        f *= f;
        float f2 = (1.0F + f * 0.4F) * f1;
        float f3 = (1.0F + f * 0.1F) / f1;
        p_114047_.scale(f2, f3, f2);
    }

    @Override
    protected float getWhiteOverlayProgress(TCSheepCreeper p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }
}