package com.tntmodders.takumicraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;

public class TCCreeperPowerLayer<T extends AbstractTCCreeper, M extends EntityModel<T>> extends EnergySwirlLayer<T, M> {

    private final M model;
    private final AbstractTCCreeper.TCCreeperContext context;
    private final boolean doExpand;

    public TCCreeperPowerLayer(RenderLayerParent p_174471_, EntityModelSet p_174472_, M model, AbstractTCCreeper.TCCreeperContext context) {
        this(p_174471_, p_174472_, model, context, false);
    }

    public TCCreeperPowerLayer(RenderLayerParent p_174471_, EntityModelSet p_174472_, M model, AbstractTCCreeper.TCCreeperContext context, boolean expand) {
        super(p_174471_);
        this.model = model;
        this.context = context;
        this.doExpand = expand;
    }

    @Override
    protected float xOffset(float p_116683_) {
        return p_116683_ * 0.01F;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return this.context == null ? TCClientUtils.POWER_LOCATION : this.context.getArmor();
    }

    @Override
    protected M model() {
        return this.model;
    }

    @Override
    public void render(PoseStack p_116970_, MultiBufferSource p_116971_, int p_116972_, T p_116973_, float p_116974_, float p_116975_, float p_116976_, float p_116977_, float p_116978_, float p_116979_) {
        p_116970_.pushPose();
        if (this.doExpand) {
            p_116970_.scale(1.1f, 1.1f, 1.1f);
            p_116970_.translate(0, -0.1, 0);
        }
        super.render(p_116970_, p_116971_, p_116972_, p_116973_, p_116974_, p_116975_, p_116976_, p_116977_, p_116978_, p_116979_);
        p_116970_.popPose();
    }
}