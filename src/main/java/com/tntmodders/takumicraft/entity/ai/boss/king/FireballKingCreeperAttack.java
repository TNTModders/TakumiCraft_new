package com.tntmodders.takumicraft.entity.ai.boss.king;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireballKingCreeperAttack extends AbstractKingCreeperAttack {
    @Override
    public void serverInit(TCKingCreeper creeper) {
    }

    @Override
    public void serverTick(TCKingCreeper creeper, int swell) {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(LivingEntityRenderState state, TCKingCreeper creeper, int swell, PoseStack pose, MultiBufferSource bufferSource, float renderTick) {
        for (double x = -5; x <= 5; x += 0.5) {
            for (double z = -5; z <= 5; z += 0.5) {
                if (x * x + z * z <= 25 && creeper.getRandom().nextInt(20) == 0) {
                    this.spawnParticle(creeper, ParticleTypes.LAVA, x, 0, z, (creeper.getRandom().nextDouble() - 0.5) * 0.3, 0.5, (creeper.getRandom().nextDouble() - 0.5) * 0.3);
                }
            }
        }
    }

    @Override
    public void serverExp(TCKingCreeper creeper) {
        for (int t = 0; t < 20 * (creeper.isPowered() ? 2 : 1); t++) {
            RandomSource rand = creeper.getRandom();
            BlockPos pos = this.getRandomPos(creeper, 20, 2, 20).above(50);
            LargeFireball fireball = new LargeFireball(creeper.level(), creeper, new Vec3(0, -1 - rand.nextGaussian() * 2, 0), creeper.isPowered() ? 6 : 4);
            fireball.setPos(pos.getCenter());
            fireball.setDeltaMovement(0, -0.3 - rand.nextGaussian() / 4, 0);
            creeper.level().addFreshEntity(fireball);
        }
        TCExplosionUtils.createExplosion(creeper.level(), creeper, creeper.getX(), creeper.getY(), creeper.getZ(), 3f, true);
    }
}
