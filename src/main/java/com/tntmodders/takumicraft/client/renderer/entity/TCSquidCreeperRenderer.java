package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCSquidCreeperModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCGlowingSquidCreeper;
import com.tntmodders.takumicraft.entity.mobs.TCSquidCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCSquidCreeperRenderer<T extends TCSquidCreeper> extends MobRenderer<T, TCSquidCreeperModel<T>> {

    public TCSquidCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new TCSquidCreeperModel<>(p_173956_.bakeLayer(ModelLayers.SQUID)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer<>(this, p_173956_.getModelSet(), new TCSquidCreeperModel<>(p_173956_.bakeLayer(ModelLayers.SQUID)), TCEntityCore.SQUID, true));
    }

    @Override
    protected void setupRotations(T p_116035_, PoseStack p_116036_, float p_116037_, float p_116038_, float p_116039_, float p_334101_) {
        float f = Mth.lerp(p_116039_, p_116035_.xBodyRotO, p_116035_.xBodyRot);
        float f1 = Mth.lerp(p_116039_, p_116035_.zBodyRotO, p_116035_.zBodyRot);
        p_116036_.translate(0.0F, 0.5F, 0.0F);
        p_116036_.mulPose(Axis.YP.rotationDegrees(180.0F - p_116038_));
        p_116036_.mulPose(Axis.XP.rotationDegrees(f));
        p_116036_.mulPose(Axis.YP.rotationDegrees(f1));
        p_116036_.translate(0.0F, -1.2F, 0.0F);
    }

    @Override
    protected float getBob(T p_116032_, float p_116033_) {
        return Mth.lerp(p_116033_, p_116032_.oldTentacleAngle, p_116032_.tentacleAngle);
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/creeper/" + p_114482_.getContext().getRegistryName() + ".png");
    }

    @Override
    protected void scale(T p_114046_, PoseStack p_114047_, float p_114048_) {
        TCClientUtils.scaleSwelling(p_114046_, p_114047_, p_114048_);
    }

    @Override
    protected float getWhiteOverlayProgress(T p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    protected int getBlockLightLevel(T p_174146_, BlockPos p_174147_) {
        if (p_174146_ instanceof TCGlowingSquidCreeper glowing) {
            int i = (int) Mth.clampedLerp(0.0F, 15.0F, 1.0F - (float) glowing.getDarkTicksRemaining() / 10.0F);
            return i == 15 ? 15 : Math.max(i, super.getBlockLightLevel(p_174146_, p_174147_));
        }
        return super.getBlockLightLevel(p_174146_, p_174147_);
    }
}