package com.tntmodders.takumicraft.particles;

import com.tntmodders.takumicraft.client.particle.TCSquidParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;

public class TCSquidParticleType extends TCParticleType {
    public TCSquidParticleType() {
        super("squidcreeper_ink", true);
    }

    @Override
    public void registerParticleSprite(RegisterParticleProvidersEvent event, ParticleType type) {
        event.registerSpriteSet(type, TCSquidParticle.TCSquidParticleProvider::new);
    }
}
