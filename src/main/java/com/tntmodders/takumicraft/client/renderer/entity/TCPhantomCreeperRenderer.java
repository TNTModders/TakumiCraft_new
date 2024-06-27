package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCPhantomCreeperModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
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
public class TCPhantomCreeperRenderer extends MobRenderer<TCPhantomCreeper, TCPhantomCreeperModel<TCPhantomCreeper>> {
    private static final ResourceLocation PHANTOM_LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/phantomcreeper.png");

    public TCPhantomCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new TCPhantomCreeperModel<>(context.bakeLayer(ModelLayers.PHANTOM)), 0.75F);
        this.addLayer(new TCPhantomCreeperEyesLayer<>(this));
        this.addLayer(new TCCreeperPowerLayer(this, context.getModelSet(), new TCPhantomCreeperModel<>(context.bakeLayer(ModelLayers.PHANTOM)), TCEntityCore.PHANTOM));
    }

    @Override
    public ResourceLocation getTextureLocation(TCPhantomCreeper p_115679_) {
        return PHANTOM_LOCATION;
    }

    @Override
    protected void scale(TCPhantomCreeper p_115681_, PoseStack p_115682_, float p_115683_) {
        int i = p_115681_.getPhantomSize();
        float f = 1.0F + 0.15F * (float) i;
        p_115682_.scale(f, f, f);
        p_115682_.translate(0.0D, 1.3125D, 0.1875D);
        TCClientUtils.scaleSwelling(p_115681_, p_115682_, p_115683_);
    }

    @Override
    protected float getWhiteOverlayProgress(TCPhantomCreeper p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    protected void setupRotations(TCPhantomCreeper p_115685_, PoseStack p_115686_, float p_115687_, float p_115688_, float p_115689_, float f) {
        super.setupRotations(p_115685_, p_115686_, p_115687_, p_115688_, p_115689_, f);
        p_115686_.mulPose(Axis.XP.rotationDegrees(p_115685_.getXRot()));
    }

    @OnlyIn(Dist.CLIENT)
    public static class TCPhantomCreeperEyesLayer<T extends TCPhantomCreeper> extends EyesLayer<T, TCPhantomCreeperModel<T>> {
        private static final RenderType PHANTOM_EYES = RenderType.eyes(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/phantomcreeper_eyes.png"));

        public TCPhantomCreeperEyesLayer(RenderLayerParent<T, TCPhantomCreeperModel<T>> p_117342_) {
            super(p_117342_);
        }

        @Override
        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T creeper, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (!creeper.isOnBook() || TCClientUtils.checkSlayAdv(creeper.getType())) {
                super.render(p_116983_, p_116984_, p_116985_, creeper, p_116987_, p_116988_, p_116989_, p_116990_, p_116991_, p_116992_);
            }
        }

        @Override
        public RenderType renderType() {
            return PHANTOM_EYES;
        }
    }
}