package com.tntmodders.takumicraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class TCKingCreeperLayer<T extends TCKingCreeper, M extends CreeperModel<T>> extends RenderLayer<T, M> {
    public TCKingCreeperLayer(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int p_117351_, T creeper, float p_117353_, float p_117354_, float partialTicks, float p_117356_, float p_117357_, float p_117358_) {
        if (creeper.getSwell() > 0) {
            creeper.getAttackID().getAttack().clientTick(creeper, creeper.getSwell(), pose, bufferSource, partialTicks);
        }
    }
}
