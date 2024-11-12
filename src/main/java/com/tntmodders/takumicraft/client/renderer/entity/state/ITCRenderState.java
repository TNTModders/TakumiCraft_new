package com.tntmodders.takumicraft.client.renderer.entity.state;

import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;

public interface ITCRenderState {
    float getSwelling();

    boolean isPowered();

    AbstractTCCreeper.TCCreeperContext getContext();

    boolean isOnBook();
}
