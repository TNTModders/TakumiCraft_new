package com.tntmodders.takumicraft.entity.ai.boss.king;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.entity.misc.TCKingStorm;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StormKingCreeperAttack extends AbstractKingCreeperAttack {
    @Override
    public void serverInit(TCKingCreeper creeper) {
    }

    @Override
    public void serverTick(TCKingCreeper creeper, int swell) {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(LivingEntityRenderState state, TCKingCreeper creeper, int swell, PoseStack pose, MultiBufferSource bufferSource, float renderTick) {
        for (double x = -2; x <= 2; x += 0.25) {
            for (double z = -2; z <= 2; z += 0.25) {
                if (x * x + z * z <= 4 && x * x + z * z >= 1.5 * 1.5 && creeper.getRandom().nextInt(50) == 0) {
                    this.spawnParticle(creeper, ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, 0, z,
                            Math.sin(Math.atan2(z, x) * creeper.tickCount / 10) * 0.2, 0.2,
                            Math.cos(Math.atan2(z, x) * creeper.tickCount / 10) * 0.2);
                }
            }
        }
    }

    @Override
    public void serverExp(TCKingCreeper creeper) {
        TCKingStorm storm = TCKingStorm.KING_STORM.create(creeper.level(), EntitySpawnReason.MOB_SUMMONED);
        storm.copyPosition(creeper);
        storm.setTarget(creeper.getTarget());
        storm.setInvulnerable(true);
        creeper.level().addFreshEntity(storm);
    }
}
