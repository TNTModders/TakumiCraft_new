package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
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
public class TCEvokerCreeperRenderer<T extends TCEvokerCreeper> extends AbstractTCIllagerCreeperRenderer<T> {
    private static final ResourceLocation LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/evokercreeper.png");

    public TCEvokerCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new IllagerCreeperModel<>(context.bakeLayer(ModelLayers.EVOKER)), 0.7F);
        this.addLayer(new TCCreeperPowerLayer<>(this, context.getModelSet(), new IllagerCreeperModel<>(context.bakeLayer(ModelLayers.EVOKER)), TCEntityCore.VINDICATOR, true));
        this.addLayer(
                new ItemInHandLayer<>(this, context.getItemInHandRenderer()) {
                    @Override
                    public void render(
                            PoseStack p_114569_,
                            MultiBufferSource p_114570_,
                            int p_114571_,
                            T p_114572_,
                            float p_114573_,
                            float p_114574_,
                            float p_114575_,
                            float p_114576_,
                            float p_114577_,
                            float p_114578_
                    ) {
                        if (p_114572_.isCastingSpell()) {
                            super.render(p_114569_, p_114570_, p_114571_, p_114572_, p_114573_, p_114574_, p_114575_, p_114576_, p_114577_, p_114578_);
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