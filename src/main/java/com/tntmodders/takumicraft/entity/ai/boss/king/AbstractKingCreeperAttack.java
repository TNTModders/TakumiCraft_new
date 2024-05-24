package com.tntmodders.takumicraft.entity.ai.boss.king;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.level.ExplosionEvent;

public abstract class AbstractKingCreeperAttack {

    public abstract void serverInit(TCKingCreeper creeper);

    public abstract void serverTick(TCKingCreeper creeper, int swell);

    @OnlyIn(Dist.CLIENT)
    public abstract void clientTick(TCKingCreeper creeper, int swell, PoseStack pose, MultiBufferSource bufferSource, float renderTick);

    public abstract void serverExp(TCKingCreeper creeper);

    public void clientExp(TCKingCreeper creeper, int swell, PoseStack pose, float renderTick) {
    }

    public void serverExpEvent(TCKingCreeper creeper, ExplosionEvent.Detonate event) {
    }

    protected void spawnParticle(TCKingCreeper creeper, ParticleOptions particle, double x, double y, double z, double motionX, double motionY, double motionZ) {
        creeper.level().addAlwaysVisibleParticle(particle, creeper.getX() + x, creeper.getY() + y, creeper.getZ() + z, motionX, motionY, motionZ);
    }

    protected BlockPos getRandomPos(TCKingCreeper creeper, int xRange, int yRange, int zRange) {
        return creeper.getOnPos().offset(creeper.getRandom().nextInt(xRange * 2 + 1) - xRange, creeper.getRandom().nextInt(yRange * 2 + 1) - yRange, creeper.getRandom().nextInt(zRange * 2 + 1) - zRange);
    }
}
