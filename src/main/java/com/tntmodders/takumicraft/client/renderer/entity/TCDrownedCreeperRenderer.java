package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCDrownedModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCDrownedCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TCDrownedCreeperRenderer<T extends TCDrownedCreeper, M extends TCDrownedModel<T>> extends HumanoidMobRenderer<T, M> {

    public TCDrownedCreeperRenderer(EntityRendererProvider.Context context) {
        super(context,
                (M) new TCDrownedModel(context.bakeLayer(ModelLayers.DROWNED)), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(this, new TCDrownedModel<>(context.bakeLayer(ModelLayers.DROWNED_INNER_ARMOR)),
                new TCDrownedModel<>(context.bakeLayer(ModelLayers.DROWNED_OUTER_ARMOR)), context.getModelManager()));
        this.addLayer(new TCDrownedOuterLayer(this, context.getModelSet()));
        this.addLayer(new TCCreeperPowerLayer(this, context.getModelSet(), new TCDrownedModel<>(context.bakeLayer(ModelLayers.DROWNED_OUTER_ARMOR)), TCEntityCore.DROWNED));
    }

    @Override
    public ResourceLocation getTextureLocation(T creeper) {
        return ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.getType().toShortString() + ".png");
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
    protected void setupRotations(T p_114109_, PoseStack p_114110_, float p_114111_, float p_114112_,
                                  float p_114113_, float p_114114_) {
        super.setupRotations(p_114109_, p_114110_, p_114111_, p_114112_, p_114113_, p_114114_);
        float f = p_114109_.getSwimAmount(p_114113_);
        if (f > 0.0F) {
            float f1 = -10.0F - p_114109_.getXRot();
            float f2 = Mth.lerp(f, 0.0F, f1);
            p_114110_.rotateAround(Axis.XP.rotationDegrees(f2), 0.0F, p_114109_.getBbHeight() / 2.0F, 0.0F);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static class TCDrownedOuterLayer<T extends TCDrownedCreeper> extends RenderLayer<T, TCDrownedModel<T>> {
        private static final ResourceLocation DROWNED_OUTER_LAYER_LOCATION =
                ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/drownedcreeper_outer_layer.png");
        private final TCDrownedModel<T> model;

        public TCDrownedOuterLayer(RenderLayerParent<T, TCDrownedModel<T>> p_174490_, EntityModelSet p_174491_) {
            super(p_174490_);
            this.model = new TCDrownedModel<>(p_174491_.bakeLayer(ModelLayers.DROWNED_OUTER_LAYER));
        }

        @Override
        public void render(PoseStack p_116924_, MultiBufferSource p_116925_, int p_116926_, T p_116927_, float p_116928_, float p_116929_, float p_116930_, float p_116931_, float p_116932_, float p_116933_) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, DROWNED_OUTER_LAYER_LOCATION, p_116924_, p_116925_, p_116926_, p_116927_, p_116928_, p_116929_, p_116931_, p_116932_, p_116933_, p_116930_, -1);
        }
    }
}