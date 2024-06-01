package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCChildCreeperModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.client.TCRenderCore;
import com.tntmodders.takumicraft.entity.mobs.TCChildCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCChildCreeperRenderer extends MobRenderer<TCChildCreeper, TCChildCreeperModel<TCChildCreeper>> {
    private static final ResourceLocation CREEPER_LOCATION = new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/creeper/childcreeper.png");

    public TCChildCreeperRenderer(EntityRendererProvider.Context p_173958_) {
        super(p_173958_, new TCChildCreeperModel(p_173958_.bakeLayer(TCRenderCore.CHILD)), 0.5F);
        this.addLayer(new TCCreeperPowerLayer(this, p_173958_.getModelSet(), this.model, TCEntityCore.CHILD, true));
    }

    @Override
    protected void scale(TCChildCreeper p_114046_, PoseStack p_114047_, float p_114048_) {
        TCClientUtils.scaleSwelling(p_114046_, p_114047_, p_114048_);
    }

    @Override
    public void render(TCChildCreeper p_115455_, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource p_115459_, int p_115460_) {
        super.render(p_115455_, p_115456_, p_115457_, p_115458_, p_115459_, p_115460_);
    }

    @Override
    protected float getWhiteOverlayProgress(TCChildCreeper p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(TCChildCreeper p_114041_) {
        return CREEPER_LOCATION;
    }
}