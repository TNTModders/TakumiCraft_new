package com.tntmodders.takumicraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCKingCreeperRenderState;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class TCKingCreeperLayer<S extends TCKingCreeperRenderState, M extends CreeperModel> extends RenderLayer<S, M> {
    public TCKingCreeperLayer(RenderLayerParent<S, M> p_117346_) {
        super(p_117346_);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource source, int p_117351_, S state, float p_117353_, float partialTicks) {
        if (state.swelling > 5 && state.swellDir > 0) {
            state.id.getAttack().clientTick(state, state.getCreeper(), state.swellDir, pose, source, partialTicks);
        }
    }
}
