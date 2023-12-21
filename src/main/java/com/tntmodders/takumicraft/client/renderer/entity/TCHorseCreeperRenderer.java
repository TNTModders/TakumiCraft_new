package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCHorseCreeperModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCHorseCreeper;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCHorseCreeperRenderer extends MobRenderer<TCHorseCreeper, TCHorseCreeperModel<TCHorseCreeper>> {
    private static final ResourceLocation LOCATION = new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/creeper/horsecreeper.png");

    public TCHorseCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new TCHorseCreeperModel<>(p_173956_.bakeLayer(ModelLayers.HORSE)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer<>(this, p_173956_.getModelSet(), new TCHorseCreeperModel<>(p_173956_.bakeLayer(ModelLayers.HORSE)), TCEntityCore.HORSE, true));
    }

    @Override
    public ResourceLocation getTextureLocation(TCHorseCreeper p_114482_) {
        return LOCATION;
    }

    @Override
    protected void scale(TCHorseCreeper p_114046_, PoseStack p_114047_, float p_114048_) {
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
    protected float getWhiteOverlayProgress(TCHorseCreeper p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }
}