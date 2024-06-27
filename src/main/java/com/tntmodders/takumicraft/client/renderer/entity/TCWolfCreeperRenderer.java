package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCWolfCreeperModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCWolfCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCWolfCreeperRenderer extends MobRenderer<TCWolfCreeper, TCWolfCreeperModel<TCWolfCreeper>> {
    private static final ResourceLocation LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/wolfcreeper.png");

    public TCWolfCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new TCWolfCreeperModel<>(p_173956_.bakeLayer(ModelLayers.WOLF)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer<>(this, p_173956_.getModelSet(), new TCWolfCreeperModel<>(p_173956_.bakeLayer(ModelLayers.WOLF)), TCEntityCore.WOLF, true));
    }

    @Override
    protected float getBob(TCWolfCreeper p_116528_, float p_116529_) {
        return p_116528_.getTailAngle();
    }

    @Override
    public void render(TCWolfCreeper p_116531_, float p_116532_, float p_116533_, PoseStack p_116534_, MultiBufferSource p_116535_, int p_116536_) {
        if (p_116531_.isWet()) {
            float f = p_116531_.getWetShade(p_116533_);
            this.model.setColor(FastColor.ARGB32.colorFromFloat(1f, f, f, f));
        }
        super.render(p_116531_, p_116532_, p_116533_, p_116534_, p_116535_, p_116536_);
        if (p_116531_.isWet()) {
            this.model.setColor(-1);
        }

    }

    @Override
    public ResourceLocation getTextureLocation(TCWolfCreeper p_114482_) {
        return LOCATION;
    }

    @Override
    protected void scale(TCWolfCreeper p_114046_, PoseStack p_114047_, float p_114048_) {
        TCClientUtils.scaleSwelling(p_114046_, p_114047_, p_114048_);
    }

    @Override
    protected float getWhiteOverlayProgress(TCWolfCreeper p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }
}