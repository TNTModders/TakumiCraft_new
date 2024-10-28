package com.tntmodders.takumicraft.entity.ai.boss.king;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ExplosionKingCreeperAttack extends AbstractKingCreeperAttack {

    private final boolean isFire;
    private final boolean isRandom;

    public ExplosionKingCreeperAttack(boolean isFire, boolean isRandom) {
        this.isFire = isFire;
        this.isRandom = isRandom;
    }

    @Override
    public void serverInit(TCKingCreeper creeper) {
    }

    @Override
    public void serverTick(TCKingCreeper creeper, int swell) {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(LivingEntityRenderState state, TCKingCreeper creeper, int swell, PoseStack pose, MultiBufferSource bufferSource, float renderTick) {
        if (this.isRandom) {
            for (double x = -5; x <= 5; x += 0.25) {
                for (double z = -5; z <= 5; z += 0.25) {
                    if (x * x + z * z <= 25 && x * x + z * z >= 4.5 * 4.5 && creeper.getRandom().nextInt(10) == 0) {
                        this.spawnParticle(creeper, this.getParticle(), x, 0, z, Math.sin(Math.atan2(z, x) * creeper.tickCount / 10) * 0.2, 0.4, Math.cos(Math.atan2(z, x) * creeper.tickCount / 10) * 0.2);
                    }
                }
            }
        } else {
            for (double x = -5; x <= 5; x += 0.5) {
                for (double z = -5; z <= 5; z += 0.5) {
                    for (double y = -5; y <= 5; y += 0.5) {
                        double cubic = x * x + z * z + y * y;
                        if (cubic <= 25 && cubic >= 4.5 * 4.5 && creeper.getRandom().nextInt(10) == 0) {
                            this.spawnParticle(creeper, this.getParticle(), x, y, z,
                                    Math.sin(Math.atan2(z, x) * creeper.tickCount / 10) * 0.01, 0,
                                    Math.cos(Math.atan2(z, x) * creeper.tickCount / 10) * 0.01);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void serverExp(TCKingCreeper creeper) {
        if (this.isRandom) {
            for (int i = 0; i < (creeper.isPowered() ? 15 : 10); i++) {
                BlockPos pos = this.getRandomPos(creeper, 9, 4, 9);
                float factor = creeper.isPowered() ? 16 : 10;
                if (this.isFire) {
                    factor = factor / 2;
                }
                TCExplosionUtils.createExplosion(creeper.level(), creeper, pos.getX(), pos.getY(), pos.getZ(), factor, this.isFire);
            }
        } else {
            for (double x = -5; x <= 5; x += 1) {
                for (double z = -5; z <= 5; z += 1) {
                    for (double y = -5; y <= 5; y += 1) {
                        double cubic = x * x + z * z + y * y;
                        if (cubic <= 25 && cubic >= 4.5 * 4.5 && creeper.getRandom().nextBoolean()) {
                            float factor = creeper.isPowered() ? 5 : 3;
                            if (this.isFire) {
                                factor = factor / 2;
                            }
                            TCExplosionUtils.createExplosion(creeper.level(), creeper, creeper.getX(x), creeper.getY(y), creeper.getZ(z), factor, this.isFire);
                        }
                    }
                }
            }
        }
    }

    private ParticleOptions getParticle() {
        return this.isFire ? ParticleTypes.FLAME : ParticleTypes.LARGE_SMOKE;
    }
}
