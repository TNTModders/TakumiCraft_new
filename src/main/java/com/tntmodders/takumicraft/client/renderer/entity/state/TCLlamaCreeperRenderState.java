package com.tntmodders.takumicraft.client.renderer.entity.state;

import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import net.minecraft.client.renderer.entity.LlamaSpitRenderer;
import net.minecraft.client.renderer.entity.state.LlamaRenderState;

public class TCLlamaCreeperRenderState extends LlamaRenderState implements ITCRenderState {
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
