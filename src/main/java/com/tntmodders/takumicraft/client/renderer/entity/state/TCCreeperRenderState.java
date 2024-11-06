package com.tntmodders.takumicraft.client.renderer.entity.state;

import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCCreeperRenderState extends CreeperRenderState {
    public float swelling;
    public boolean isPowered;
    public AbstractTCCreeper.TCCreeperContext context;
}