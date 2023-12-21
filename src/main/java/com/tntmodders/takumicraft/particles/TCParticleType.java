package com.tntmodders.takumicraft.particles;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;

public abstract class TCParticleType extends SimpleParticleType {
    private final String registerName;

    public TCParticleType(String name, boolean flg) {
        super(flg);
        this.registerName = name;
    }

    public String getRegistryName() {
        return this.registerName;
    }

    public abstract void registerParticleSprite(RegisterParticleProvidersEvent event, ParticleType type);
}
