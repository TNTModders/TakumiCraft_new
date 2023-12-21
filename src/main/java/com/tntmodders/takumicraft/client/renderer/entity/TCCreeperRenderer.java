package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperOutfitLayer;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TCCreeperRenderer<T extends AbstractTCCreeper> extends MobRenderer<T, CreeperModel<T>> {
    private final boolean isBright;

    public TCCreeperRenderer(EntityRendererProvider.Context p_173958_) {
        this(p_173958_, false);
    }

    public TCCreeperRenderer(EntityRendererProvider.Context p_173958_, boolean isBright) {
        this(p_173958_, isBright, null);
    }

    public TCCreeperRenderer(EntityRendererProvider.Context p_173958_, boolean isBright, AbstractTCCreeper.TCCreeperContext creeperContext) {
        super(p_173958_, new CreeperModel<>(p_173958_.bakeLayer(ModelLayers.CREEPER)), 0.5F);
        this.addLayer(new TCCreeperPowerLayer<>(this, p_173958_.getModelSet(), new CreeperModel<>(p_173958_.bakeLayer(ModelLayers.CREEPER_ARMOR)), creeperContext));
        if (TCEntityUtils.isXmas() || TCEntityUtils.isNewYear()) {
            this.addLayer(new TCCreeperOutfitLayer<>(this, p_173958_.getModelSet(), new CreeperModel<>(p_173958_.bakeLayer(ModelLayers.CREEPER))));
        }
        this.isBright = isBright;
    }

    @Override
    protected void scale(T creeper, PoseStack poseStack, float size) {

        float sizeFactor = creeper.getContext().getSizeFactor();
        poseStack.scale(sizeFactor, sizeFactor, sizeFactor);

        float f = creeper.getSwelling(size);
        float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        f *= f;
        f *= f;
        float f2 = (1.0F + f * 0.4F) * f1;
        float f3 = (1.0F + f * 0.1F) / f1;
        poseStack.scale(f2, f3, f2);
    }

    @Override
    protected float getWhiteOverlayProgress(T p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(T creeper) {
        return new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.getType().toShortString() + ".png");
    }

    @Override
    protected int getBlockLightLevel(T p_114496_, BlockPos p_114497_) {
        return this.isBright ? 15 : super.getBlockLightLevel(p_114496_, p_114497_);
    }
}
