package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCSquidCreeperModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCSquidCreeperRenderState;
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
public class TCSquidCreeperRenderer<T extends TCSquidCreeper, S extends TCSquidCreeperRenderState, M extends TCSquidCreeperModel<S>> extends MobRenderer<T, S, M> {

    public TCSquidCreeperRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, (M) new TCSquidCreeperModel<S>(p_173956_.bakeLayer(ModelLayers.SQUID)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer(this, p_173956_.getModelSet(), new TCSquidCreeperModel<>(p_173956_.bakeLayer(ModelLayers.SQUID)), TCEntityCore.SQUID, true));
    }

    @Override
    protected void setupRotations(S p_116035_, PoseStack p_116036_, float p_116037_, float p_116038_) {
        float f = p_116035_.xBodyRot;
        float f1 = p_116035_.zBodyRot;
        p_116036_.translate(0.0F, 0.5F, 0.0F);
        p_116036_.mulPose(Axis.YP.rotationDegrees(180.0F - p_116038_));
        p_116036_.mulPose(Axis.XP.rotationDegrees(f));
        p_116036_.mulPose(Axis.YP.rotationDegrees(f1));
        p_116036_.translate(0.0F, -1.2F, 0.0F);
    }

    @Override
    protected int getBlockLightLevel(T p_174146_, BlockPos p_174147_) {
        if (p_174146_ instanceof TCGlowingSquidCreeper glowing) {
            int i = (int) Mth.clampedLerp(0.0F, 15.0F, 1.0F - (float) glowing.getDarkTicksRemaining() / 10.0F);
            return i == 15 ? 15 : Math.max(i, super.getBlockLightLevel(p_174146_, p_174147_));
        }
        return super.getBlockLightLevel(p_174146_, p_174147_);
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
        return (S) new TCSquidCreeperRenderState();
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