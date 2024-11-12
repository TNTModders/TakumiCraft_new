package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCZombieModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCZombieCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCZombieCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TCZombieCreeperRenderer<T extends TCZombieCreeper, S extends TCZombieCreeperRenderState, M extends TCZombieModel<S>> extends HumanoidMobRenderer<T, S, M> {

    public TCZombieCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, (M) new TCZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
        this.addLayer(new HumanoidArmorLayer(this, new TCZombieModel(context.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
                new TCZombieModel(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)), context.getEquipmentRenderer()));
        this.addLayer(new TCCreeperPowerLayer(this, context.getModelSet(), new TCZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)), TCEntityCore.ZOMBIE));
    }

    @Override
    public S createRenderState() {
        return (S) new TCZombieCreeperRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(S creeper) {
        return ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.context.entityType().toShortString() + ".png");
    }

    @Override
    protected int getBlockLightLevel(T p_234560_, BlockPos p_234561_) {
        return 15;
    }

    @Override
    protected void scale(S creeper, PoseStack poseStack) {
        TCClientUtils.scaleSwelling(creeper.swelling, poseStack);
    }

    @Override
    protected float getWhiteOverlayProgress(S state) {
        float f = state.swelling;
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public void extractRenderState(T creeper, S state, float p_362065_) {
        super.extractRenderState(creeper, state, p_362065_);
        state.isAggressive = creeper.isAggressive();
        state.isConverting = creeper.isUnderWaterConverting();
        state.swelling = creeper.getSwelling(p_362065_);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.isOnBook = creeper.isOnBook();
    }

    @Override
    protected boolean isShaking(S p_113773_) {
        return super.isShaking(p_113773_) || p_113773_.isConverting;
    }
}