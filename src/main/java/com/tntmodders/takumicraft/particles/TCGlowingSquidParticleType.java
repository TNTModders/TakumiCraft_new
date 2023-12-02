package com.tntmodders.takumicraft.particles;

import com.tntmodders.takumicraft.client.particle.TCSquidParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;

public class TCGlowingSquidParticleType extends TCParticleType {
    public TCGlowingSquidParticleType() {
        super("glowingsquidcreeper_ink", true);
    }

    @Override
    public void registerParticleSprite(RegisterParticleProvidersEvent event, ParticleType type) {
        event.registerSpriteSet(type, TCSquidParticle.GlowInkProvider::new);
    }
}
