package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.client.model.TCZombieModel;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCZombieCreeperRenderState;
import com.tntmodders.takumicraft.entity.mobs.TCZombieCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractTCZombieCreeperRenderer<T extends TCZombieCreeper, S extends TCZombieCreeperRenderState, M extends TCZombieModel<S>> extends HumanoidMobRenderer<T, S, M> {
    private static final ResourceLocation ZOMBIE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png");

    protected AbstractTCZombieCreeperRenderer(EntityRendererProvider.Context p_173910_, M p_173911_, M p_173912_, M p_173913_, M p_369784_, M p_360801_, M p_367849_) {
        super(p_173910_, p_173911_, p_173912_, 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, p_173913_, p_369784_, p_360801_, p_367849_, p_173910_.getEquipmentRenderer()));
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
        return (S) new TCZombieCreeperRenderState();
    }

    @Override
    public void extractRenderState(T creeper, S state, float f) {
        super.extractRenderState(creeper, state, f);
        state.swelling = creeper.getSwelling(f);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.isAggressive = creeper.isAggressive();
        state.isConverting = creeper.isUnderWaterConverting();
        state.isOnBook = creeper.isOnBook();
    }

    @Override
    protected boolean isShaking(S p_361791_) {
        return super.isShaking(p_361791_) || p_361791_.isConverting;
    }
}