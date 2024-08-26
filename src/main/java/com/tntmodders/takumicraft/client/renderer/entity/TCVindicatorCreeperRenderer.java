package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCVindicatorCreeper;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCVindicatorCreeperRenderer<T extends TCVindicatorCreeper> extends AbstractTCIllagerCreeperRenderer<T> {
    private static final ResourceLocation LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/vindicatorcreeper.png");

    public TCVindicatorCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new IllagerCreeperModel<>(context.bakeLayer(ModelLayers.VINDICATOR)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer<>(this, context.getModelSet(), new IllagerCreeperModel<>(context.bakeLayer(ModelLayers.VINDICATOR)), TCEntityCore.VINDICATOR, true));
        this.addLayer(
                new ItemInHandLayer<>(this, context.getItemInHandRenderer()) {
                    @Override
                    public void render(
                            PoseStack p_116352_,
                            MultiBufferSource p_116353_,
                            int p_116354_,
                            T p_116355_,
                            float p_116356_,
                            float p_116357_,
                            float p_116358_,
                            float p_116359_,
                            float p_116360_,
                            float p_116361_
                    ) {
                        if (p_116355_.isAggressive()) {
                            super.render(p_116352_, p_116353_, p_116354_, p_116355_, p_116356_, p_116357_, p_116358_, p_116359_, p_116360_, p_116361_);
                        }
                    }
                }
        );
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return LOCATION;
    }

}