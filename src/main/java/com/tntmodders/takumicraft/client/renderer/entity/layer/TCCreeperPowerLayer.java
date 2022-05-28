package com.tntmodders.takumicraft.client.renderer.entity.layer;

import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;

public class TCCreeperPowerLayer<T extends AbstractTCCreeper,M extends EntityModel<T>> extends EnergySwirlLayer<T,M> {

    private final M model;
    private final AbstractTCCreeper.TCCreeperContext context;

    public TCCreeperPowerLayer(RenderLayerParent p_174471_, EntityModelSet p_174472_, M model, AbstractTCCreeper.TCCreeperContext context) {
        super(p_174471_);
        this.model = model;
        this.context = context;
    }

    @Override
    protected float xOffset(float p_116683_) {
        return p_116683_ * 0.01F;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return context.getArmor();
    }

    @Override
    protected M model() {
        return this.model;
    }
}