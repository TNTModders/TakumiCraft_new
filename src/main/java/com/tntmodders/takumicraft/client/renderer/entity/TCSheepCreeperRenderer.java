package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCSheepCreeperModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCSheepCreeperFurLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCSheepCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCSheepCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCSheepCreeperRenderer<T extends TCSheepCreeper, S extends TCSheepCreeperRenderState, M extends TCSheepCreeperModel<S>> extends MobRenderer<T, S, M> {
    private static final ResourceLocation LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/sheepcreeper.png");

    public TCSheepCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, (M) new TCSheepCreeperModel(p_173956_.bakeLayer(ModelLayers.SHEEP)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer(this, p_173956_.getModelSet(), new TCSheepCreeperModel<>(p_173956_.bakeLayer(ModelLayers.SHEEP_WOOL)), TCEntityCore.SHEEP, true));
        this.addLayer(new TCSheepCreeperFurLayer(this, p_173956_.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(S creeper) {
        return ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.context.entityType().toShortString() + ".png");
    }


    @Override
    protected void scale(S state, PoseStack poseStack) {
        float sizeFactor = state.context.getSizeFactor();
        poseStack.scale(sizeFactor, sizeFactor, sizeFactor);
        TCClientUtils.scaleSwelling(state.swelling, poseStack);
    }

    @Override
    protected float getWhiteOverlayProgress(S state) {
        float f = state.swelling;
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public S createRenderState() {
        return (S) new TCSheepCreeperRenderState();
    }

    @Override
    public void extractRenderState(T creeper, S state, float f) {
        super.extractRenderState(creeper, state, f);
        state.swelling = creeper.getSwelling(f);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.isOnBook = creeper.isOnBook();
        state.isRainbow = creeper.isRainbow();

        state.headEatAngleScale = creeper.getHeadEatAngleScale(f);
        state.headEatPositionScale = creeper.getHeadEatPositionScale(f);
        state.isSheared = false;
        state.woolColor = creeper.getColor();
        state.id = creeper.getId();
    }
}