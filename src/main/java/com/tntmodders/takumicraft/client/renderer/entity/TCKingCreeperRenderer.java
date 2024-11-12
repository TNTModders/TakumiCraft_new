package com.tntmodders.takumicraft.client.renderer.entity;

import com.tntmodders.takumicraft.client.renderer.entity.layer.TCKingCreeperLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCKingCreeperRenderState;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class TCKingCreeperRenderer<T extends TCKingCreeper, S extends TCKingCreeperRenderState> extends TCCreeperRenderer<T, S> {
    public TCKingCreeperRenderer(EntityRendererProvider.Context context, AbstractTCCreeper.TCCreeperContext creeperContext) {
        super(context, true, creeperContext);
        this.addLayer(new TCKingCreeperLayer(this));
    }

    @Override
    public void extractRenderState(T creeper, S state, float f) {
        super.extractRenderState(creeper, state, f);
        state.id = creeper.getAttackID();
        state.swellDir = creeper.getSwellDir();
        //@TODO alt method w/o extract creeper itself
        state.setCreeper(creeper);
    }

    @Override
    public S createRenderState() {
        return (S) new TCKingCreeperRenderState();
    }
}
