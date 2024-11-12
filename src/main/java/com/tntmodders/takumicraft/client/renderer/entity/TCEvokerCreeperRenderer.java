package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCEvokerCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCEvokerCreeper;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCEvokerCreeperRenderer<T extends TCEvokerCreeper, S extends TCEvokerCreeperRenderState> extends AbstractTCIllagerCreeperRenderer<T, S> {
    private static final ResourceLocation LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/evokercreeper.png");

    public TCEvokerCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new IllagerCreeperModel<>(context.bakeLayer(ModelLayers.EVOKER)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer<>(this, context.getModelSet(), new IllagerCreeperModel<>(context.bakeLayer(ModelLayers.EVOKER)), TCEntityCore.VINDICATOR, true));
        this.addLayer(
                new ItemInHandLayer<>(this, context.getItemRenderer()) {
                    @Override
                    public void render(PoseStack p_117204_, MultiBufferSource p_117205_, int p_117206_, S p_363689_, float p_117208_, float p_117209_) {
                        if (p_363689_.isPowered) {
                            super.render(p_117204_, p_117205_, p_117206_, p_363689_, p_117208_, p_117209_);
                        }
                    }
                }
        );
    }

    @Override
    public ResourceLocation getTextureLocation(S p_114482_) {
        return LOCATION;
    }

    @Override
    public void extractRenderState(T creeper, S state, float f) {
        super.extractRenderState(creeper, state, f);
        state.isCastingSpell = creeper.isCastingSpell();
    }
}