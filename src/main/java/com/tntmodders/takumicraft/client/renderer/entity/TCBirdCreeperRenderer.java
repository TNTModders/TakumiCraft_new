package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCBirdCreeperModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCBirdCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCBirdCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCBirdCreeperRenderer<T extends TCBirdCreeper, S extends TCBirdCreeperRenderState> extends MobRenderer<T, S, TCBirdCreeperModel<S>> {
    private static final ResourceLocation LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/birdcreeper.png");

    public TCBirdCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new TCBirdCreeperModel<>(p_173956_.bakeLayer(ModelLayers.CHICKEN)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer(this, p_173956_.getModelSet(), new TCBirdCreeperModel(p_173956_.bakeLayer(ModelLayers.CHICKEN)), TCEntityCore.BIRD, true));
    }

    @Override
    public S createRenderState() {
        return (S) new TCBirdCreeperRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(S p_114482_) {
        return LOCATION;
    }

    @Override
    protected void scale(S p_114046_, PoseStack p_114047_) {
        TCClientUtils.scaleSwelling(p_114046_.swelling, p_114047_);
    }

    @Override
    protected float getWhiteOverlayProgress(S state) {
        float f = state.swelling;
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public void extractRenderState(T creeper, S state, float f) {
        super.extractRenderState(creeper, state, f);
        state.swelling = creeper.getSwelling(f);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.flap = Mth.lerp(f, creeper.oFlap, creeper.flap);
        state.flapSpeed = Mth.lerp(f, creeper.oFlapSpeed, creeper.flapSpeed);
        state.isOnBook = creeper.isOnBook();
    }
}