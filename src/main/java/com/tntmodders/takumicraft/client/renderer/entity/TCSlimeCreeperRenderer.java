package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCSlimeCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCSlimeCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCSlimeCreeperRenderer extends MobRenderer<TCSlimeCreeper, TCSlimeCreeperRenderState, SlimeModel> {

    public TCSlimeCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new SlimeModel(context.bakeLayer(ModelLayers.SLIME)), 0.25F);
        this.addLayer(new TCSlimeCreeperOuterLayer(this, context.getModelSet()));
        this.addLayer(new TCCreeperPowerLayer(this, context.getModelSet(), new SlimeModel(context.bakeLayer(ModelLayers.SLIME_OUTER)), TCEntityCore.SLIME));
    }

    @Override
    public void render(TCSlimeCreeperRenderState p_364434_, PoseStack p_115979_, MultiBufferSource p_115980_, int p_115981_) {
        this.shadowRadius = 0.25F * (float) p_364434_.size;
        super.render(p_364434_, p_115979_, p_115980_, p_115981_);
    }

    @Override
    protected void scale(TCSlimeCreeperRenderState p_115983_, PoseStack p_115984_) {
        float f = 0.999F;
        p_115984_.scale(0.999F, 0.999F, 0.999F);
        p_115984_.translate(0.0D, 0.001F, 0.0D);
        float f1 = p_115983_.scale;
        float f2 = p_115983_.squish / (f1 * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        p_115984_.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
        TCClientUtils.scaleSwelling(p_115983_.swelling, p_115984_);
    }

    @Override
    public ResourceLocation getTextureLocation(TCSlimeCreeperRenderState creeper) {
        return ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.context.entityType().toShortString() + ".png");
    }

    @Override
    protected float getWhiteOverlayProgress(TCSlimeCreeperRenderState state) {
        float f = state.swelling;
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public TCSlimeCreeperRenderState createRenderState() {
        return new TCSlimeCreeperRenderState();
    }

    @Override
    public void extractRenderState(TCSlimeCreeper creeper, TCSlimeCreeperRenderState state, float f) {
        super.extractRenderState(creeper, state, f);
        state.swelling = creeper.getSwelling(f);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.isOnBook = creeper.isOnBook();
        state.squish = Mth.lerp(f, creeper.oSquish, creeper.squish);
        state.size = creeper.getSize();
    }

    public static class TCSlimeCreeperOuterLayer<T extends TCSlimeCreeperRenderState, S extends SlimeModel> extends SlimeOuterLayer {

        public TCSlimeCreeperOuterLayer(RenderLayerParent p_174536_, EntityModelSet p_174537_) {
            super(p_174536_, p_174537_);
        }
    }
}
