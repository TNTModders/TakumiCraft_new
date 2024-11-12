package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCPhantomCreeperModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCPhantomCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCPhantomCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCPhantomCreeperRenderer<T extends TCPhantomCreeper, S extends TCPhantomCreeperRenderState, M extends TCPhantomCreeperModel<S>> extends MobRenderer<T, S, M> {
    private static final ResourceLocation PHANTOM_LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/phantomcreeper.png");

    public TCPhantomCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, (M) new TCPhantomCreeperModel(context.bakeLayer(ModelLayers.PHANTOM)), 0.75F);
        this.addLayer(new TCPhantomCreeperEyesLayer(this));
        this.addLayer(new TCCreeperPowerLayer(this, context.getModelSet(), new TCPhantomCreeperModel<>(context.bakeLayer(ModelLayers.PHANTOM)), TCEntityCore.PHANTOM));
    }

    @Override
    public ResourceLocation getTextureLocation(S creeper) {
        return ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.context.entityType().toShortString() + ".png");
    }


    @Override
    protected void scale(S state, PoseStack poseStack) {
        float sizeFactor = state.context.getSizeFactor();
        poseStack.scale(sizeFactor, sizeFactor, sizeFactor);
        poseStack.translate(0.0D, 1.3125D, 0.1875D);
        TCClientUtils.scaleSwelling(state.swelling, poseStack);
    }

    @Override
    protected float getWhiteOverlayProgress(S state) {
        float f = state.swelling;
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public S createRenderState() {
        return (S) new TCPhantomCreeperRenderState();
    }

    @Override
    public void extractRenderState(T creeper, S state, float f) {
        super.extractRenderState(creeper, state, f);
        state.swelling = creeper.getSwelling(f);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.isOnBook = creeper.isOnBook();
        state.flapTime = (float) creeper.getUniqueFlapTickOffset() + state.ageInTicks;
        state.size = creeper.getPhantomSize();
    }

    @Override
    protected void setupRotations(S p_370120_, PoseStack p_115318_, float p_115319_, float p_115320_) {
        super.setupRotations(p_370120_, p_115318_, p_115319_, p_115320_);
        p_115318_.mulPose(Axis.XP.rotationDegrees(p_370120_.xRot));
    }

    @OnlyIn(Dist.CLIENT)
    public static class TCPhantomCreeperEyesLayer<T extends TCPhantomCreeperRenderState> extends EyesLayer<T, TCPhantomCreeperModel<T>> {
        private static final RenderType PHANTOM_EYES = RenderType.eyes(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/phantomcreeper_eyes.png"));

        public TCPhantomCreeperEyesLayer(RenderLayerParent<T, TCPhantomCreeperModel<T>> p_117342_) {
            super(p_117342_);
        }

        @Override
        public void render(PoseStack pose, MultiBufferSource source, int p_116985_, T state, float p_116987_, float p_116988_) {
            if (!state.isOnBook || TCClientUtils.checkSlayAdv(state.context.entityType())) {
                super.render(pose, source, p_116985_, state, p_116987_, p_116988_);
            }
        }

        @Override
        public RenderType renderType() {
            return PHANTOM_EYES;
        }
    }
}