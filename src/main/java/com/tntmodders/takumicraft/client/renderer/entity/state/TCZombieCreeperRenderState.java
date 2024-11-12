package com.tntmodders.takumicraft.client.renderer.entity.state;

import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;

public class TCZombieCreeperRenderState extends ZombieRenderState implements ITCRenderState {
    public AbstractTCCreeper.TCCreeperContext context;
    public float swelling;
    public boolean isPowered;
    public boolean isOnBook;

    @Override
    public float getSwelling() {
        return this.swelling;
    }

    @Override
    public boolean isPowered() {
        return this.isPowered;
    }

    @Override
    public AbstractTCCreeper.TCCreeperContext getContext() {
        return this.context;
    }

    @Override
    public boolean isOnBook() {
        return this.isOnBook;
    }
}
