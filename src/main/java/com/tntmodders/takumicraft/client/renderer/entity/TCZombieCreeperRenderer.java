package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCZombieModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCZombieCreeper;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TCZombieCreeperRenderer<T extends TCZombieCreeper, M extends TCZombieModel<T>> extends HumanoidMobRenderer<T, M> {

    public TCZombieCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, (M) new TCZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, (M) new TCZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
                (M) new TCZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR))));
        this.addLayer(new TCCreeperPowerLayer(this, context.getModelSet(), new TCZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)), TCEntityCore.ZOMBIE));
    }

    @Override
    public ResourceLocation getTextureLocation(T creeper) {
        return new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.getType().toShortString() + ".png");
    }

    @Override
    protected void scale(T p_114046_, PoseStack p_114047_, float p_114048_) {
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
    protected float getWhiteOverlayProgress(T p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    protected boolean isShaking(T p_113773_) {
        return super.isShaking(p_113773_) || p_113773_.isUnderWaterConverting();
    }
}