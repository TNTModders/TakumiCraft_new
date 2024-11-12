package com.tntmodders.takumicraft.client.renderer.entity.state;

import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCCreeperRenderState extends CreeperRenderState implements ITCRenderState {
    public AbstractTCCreeper.TCCreeperContext context;
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