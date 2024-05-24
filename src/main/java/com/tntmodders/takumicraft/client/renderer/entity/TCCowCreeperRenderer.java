package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCCowCreeperModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCCowCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCCowCreeperRenderer extends MobRenderer<TCCowCreeper, TCCowCreeperModel<TCCowCreeper>> {
    private static final ResourceLocation LOCATION = new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/creeper/cowcreeper.png");

    public TCCowCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new TCCowCreeperModel<>(p_173956_.bakeLayer(ModelLayers.COW)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer<>(this, p_173956_.getModelSet(), new TCCowCreeperModel<>(p_173956_.bakeLayer(ModelLayers.COW)), TCEntityCore.COW, true));
    }

    @Override
    public ResourceLocation getTextureLocation(TCCowCreeper p_114482_) {
        return LOCATION;
    }

    @Override
    protected void scale(TCCowCreeper p_114046_, PoseStack p_114047_, float p_114048_) {
        TCClientUtils.scaleSwelling(p_114046_, p_114047_, p_114048_);
    }

    @Override
    protected float getWhiteOverlayProgress(TCCowCreeper p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }
}