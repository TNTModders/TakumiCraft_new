package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCZombieVillagerModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCZombieVillagerCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCZombieVillagerCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TCZombieVillagerCreeperRenderer<T extends TCZombieVillagerCreeper, S extends TCZombieVillagerCreeperRenderState, M extends TCZombieVillagerModel<S>> extends HumanoidMobRenderer<T, S, M> {

    public TCZombieVillagerCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, (M) new TCZombieVillagerModel<S>(context.bakeLayer(ModelLayers.ZOMBIE_VILLAGER)), 0.5F);
        this.addLayer(new HumanoidArmorLayer(this, new TCZombieVillagerModel<>(context.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)),
                new TCZombieVillagerModel<>(context.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR)), context.getEquipmentRenderer()));
        this.addLayer(new VillagerProfessionLayer<>(this, context.getResourceManager(), "zombie_villager"));
        this.addLayer(new TCCreeperPowerLayer(this, context.getModelSet(), new TCZombieVillagerModel<>(context.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR)),
                TCEntityCore.ZOMBIE_VILLAGER));
    }

    @Override
    public S createRenderState() {
        return (S) new TCZombieVillagerCreeperRenderState();
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
        state.villagerData = creeper.getVillagerData();
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