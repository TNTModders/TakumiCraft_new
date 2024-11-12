package com.tntmodders.takumicraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCSheepCreeperFurModel;
import com.tntmodders.takumicraft.client.model.TCSheepCreeperModel;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCSheepCreeperRenderState;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCSheepCreeperFurLayer<T extends TCSheepCreeperRenderState, S extends TCSheepCreeperModel<T>> extends RenderLayer<T, S> {
    private static final ResourceLocation SHEEP_FUR_LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/sheepcreeper_fur.png");
    private final TCSheepCreeperFurModel<TCSheepCreeperRenderState> model;

    public TCSheepCreeperFurLayer(RenderLayerParent<T, S> p_174533_, EntityModelSet p_174534_) {
        super(p_174533_);
        this.model = new TCSheepCreeperFurModel<>(p_174534_.bakeLayer(ModelLayers.SHEEP_WOOL));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource source, int p_117423_, T state, float p_117425_, float p_117426_) {
        if (state.isInvisible) {
            boolean flag = state.appearsGlowing;
            if (flag) {
                this.model.setupAnim(state);
                VertexConsumer vertexconsumer = source.getBuffer(RenderType.outline(SHEEP_FUR_LOCATION));
                this.model.renderToBuffer(pose, vertexconsumer, p_117423_, LivingEntityRenderer.getOverlayCoords(state, 0.0F), -16777216);
            }
        } else {
            int i;
            if (state.isRainbow) {
                int j = 25;
                int k = Mth.floor(state.ageInTicks);
                int l = DyeColor.values().length;
                int i1 = k % l;
                int j1 = (k + 1) % l;
                float f = ((float) (k % 25) + Mth.frac(state.ageInTicks)) / 25.0F;
                int k1 = Sheep.getColor(DyeColor.byId(i1));
                int l1 = Sheep.getColor(DyeColor.byId(j1));
                i = ARGB.lerp(f, k1, l1);
            } else {
                i = Sheep.getColor(state.woolColor);
            }

            coloredCutoutModelCopyLayerRender(model, SHEEP_FUR_LOCATION, pose, source, p_117423_, state, i);
        }
    }
}
