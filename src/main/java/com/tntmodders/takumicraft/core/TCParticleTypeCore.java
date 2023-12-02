package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.particles.TCGlowingSquidParticleType;
import com.tntmodders.takumicraft.particles.TCParticleType;
import com.tntmodders.takumicraft.particles.TCSquidParticleType;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TCParticleTypeCore {
    public static final TCParticleType SQUID_INK = new TCSquidParticleType();
    public static final TCParticleType GLOWING_SQUID_INK = new TCGlowingSquidParticleType();

    public static final List<TCParticleType> PARTICLE_TYPES = new ArrayList<>();

    public static void register(final RegisterEvent event) {
        TCLoggingUtils.startRegistry("ParticleType");
        List<Field> fieldList = Arrays.asList(TCParticleTypeCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof TCParticleType type) {
                    event.register(ForgeRegistries.PARTICLE_TYPES.getRegistryKey(), helper -> helper.register(new ResourceLocation(TakumiCraftCore.MODID, type.getRegistryName()), type));
                    PARTICLE_TYPES.add(type);
                    TCLoggingUtils.entryRegistry("ParticleType", type.getRegistryName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        TCLoggingUtils.completeRegistry("ParticleType");
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerParticleEngine(RegisterParticleProvidersEvent event) {
        PARTICLE_TYPES.forEach(particle -> particle.registerParticleSprite(event, particle));
    }
}
