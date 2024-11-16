package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCDrownedModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCZombieCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCDrownedCreeper;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TCDrownedCreeperRenderer<T extends TCDrownedCreeper, S extends TCZombieCreeperRenderState, M extends TCDrownedModel<S>> extends AbstractTCZombieCreeperRenderer<T, S, M> {

    public TCDrownedCreeperRenderer(EntityRendererProvider.Context context) {
        super(context,
                (M) new TCDrownedModel(context.bakeLayer(ModelLayers.DROWNED)),
                (M) new TCDrownedModel(context.bakeLayer(ModelLayers.DROWNED_BABY)),
                (M) new TCDrownedModel(context.bakeLayer(ModelLayers.DROWNED_INNER_ARMOR)),
                (M) new TCDrownedModel(context.bakeLayer(ModelLayers.DROWNED_OUTER_ARMOR)),
                (M) new TCDrownedModel(context.bakeLayer(ModelLayers.DROWNED_BABY_INNER_ARMOR)),
                (M) new TCDrownedModel(context.bakeLayer(ModelLayers.DROWNED_BABY_OUTER_ARMOR)));
        this.addLayer(new TCDrownedCreeperOuterLayer(this, context.getModelSet()));
        this.addLayer(new TCCreeperPowerLayer(this, context.getModelSet(), new TCDrownedModel<>(context.bakeLayer(ModelLayers.DROWNED_OUTER_ARMOR)), TCEntityCore.DROWNED));
    }


    @Override
    public ResourceLocation getTextureLocation(S creeper) {
        return ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.context.entityType().toShortString() + ".png");
    }

    @OnlyIn(Dist.CLIENT)
    public static class TCDrownedCreeperOuterLayer<T extends TCZombieCreeperRenderState, M extends TCDrownedModel<T>> extends RenderLayer<T, M> {
        private static final ResourceLocation DROWNED_OUTER_LAYER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/drowned_outer_layer.png");
        private final DrownedModel model;
        private final DrownedModel babyModel;

        public TCDrownedCreeperOuterLayer(RenderLayerParent<T, M> p_174490_, EntityModelSet p_174491_) {
            super(p_174490_);
            this.model = new DrownedModel(p_174491_.bakeLayer(ModelLayers.DROWNED_OUTER_LAYER));
            this.babyModel = new DrownedModel(p_174491_.bakeLayer(ModelLayers.DROWNED_BABY_OUTER_LAYER));
        }

        @Override
        public void render(PoseStack p_116924_, MultiBufferSource p_116925_, int p_116926_, T p_369850_, float p_116928_, float p_116929_) {
            DrownedModel drownedmodel = p_369850_.isBaby ? this.babyModel : this.model;
            coloredCutoutModelCopyLayerRender(drownedmodel, DROWNED_OUTER_LAYER_LOCATION, p_116924_, p_116925_, p_116926_, p_369850_, -1);
        }
    }
}