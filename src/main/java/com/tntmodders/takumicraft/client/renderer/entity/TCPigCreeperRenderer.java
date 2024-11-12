package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCPigCreeperModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCPigCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCPigCreeperRenderer<T extends TCPigCreeper, S extends TCCreeperRenderState, M extends TCPigCreeperModel<S>> extends MobRenderer<T, S, M> {
    private static final ResourceLocation LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/pigcreeper.png");

    public TCPigCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, (M) new TCPigCreeperModel(p_173956_.bakeLayer(ModelLayers.PIG)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer(this, p_173956_.getModelSet(), new TCPigCreeperModel(p_173956_.bakeLayer(ModelLayers.PIG)), TCEntityCore.PIG, true));
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
        return (S) new TCCreeperRenderState();
    }

    @Override
    public void extractRenderState(T creeper, S state, float f) {
        super.extractRenderState(creeper, state, f);
        state.swelling = creeper.getSwelling(f);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.isOnBook = creeper.isOnBook();
    }
}