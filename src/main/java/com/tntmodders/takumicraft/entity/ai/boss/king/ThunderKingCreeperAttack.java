package com.tntmodders.takumicraft.entity.ai.boss.king;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;

public class ThunderKingCreeperAttack extends AbstractKingCreeperAttack {
    private final boolean isRandom;
    private BlockPos target;

    public ThunderKingCreeperAttack(boolean isRandom) {
        this.isRandom = isRandom;
    }

    @Override
    public void serverInit(TCKingCreeper creeper) {
        target = creeper.getOnPos();
    }

    @Override
    public void serverTick(TCKingCreeper creeper, int swell) {
        if (!this.isRandom) {
            if (swell < creeper.maxSwell / 2) {
                this.searchTarget(creeper);
            } else if (this.target != null && swell % 10 == 0) {
                for (int i = 0; i < (creeper.isPowered() ? 4 : 2); i++) {
                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(creeper.level());
                    bolt.setPos(this.target.getX() + 0.5, this.target.getY(), this.target.getZ() + 0.5);
                    creeper.level().addFreshEntity(bolt);
                }
            }
        }
    }

    @Override
    public void clientTick(TCKingCreeper creeper, int swell, PoseStack pose, MultiBufferSource bufferSource, float renderTick) {
        if (this.isRandom) {
            for (double x = -5; x <= 5; x += 0.5) {
                for (double z = -5; z <= 5; z += 0.5) {
                    if (x * x + z * z <= 25 && creeper.getRandom().nextInt(20) == 0) {
                        this.spawnParticle(creeper, ParticleTypes.END_ROD, x, 0, z, (creeper.getRandom().nextDouble() - 0.5) * 0.3, 0.5, (creeper.getRandom().nextDouble() - 0.5) * 0.3);
                    }
                }
            }
        } else {
            if (swell < creeper.maxSwell / 2) {
                this.searchTarget(creeper);
            }
            for (double x = -2; x <= 2; x += 0.5) {
                for (double z = -2; z <= 2; z += 0.5) {
                    if (x * x + z * z <= 4 && creeper.getRandom().nextInt(4) == 0) {
                        if (target != null) {
                            creeper.level().addAlwaysVisibleParticle(ParticleTypes.END_ROD, x + target.getX(), target.getY() + creeper.getRandom().nextDouble(), z + target.getZ(), (creeper.getRandom().nextDouble() - 0.5) * 0.3, 0.5, (creeper.getRandom().nextDouble() - 0.5) * 0.3);
                        }
                    }
                }
            }
        }
    }

    private void searchTarget(TCKingCreeper creeper) {
        Player player = creeper.level().getNearestPlayer(creeper, 10);
        if (player != null) {
            this.target = player.getOnPos();
        }
    }

    @Override
    public void serverExp(TCKingCreeper creeper) {
        if (this.isRandom) {
            for (int i = 0; i < (creeper.isPowered() ? 100 : 70); i++) {
                BlockPos pos = this.getRandomPos(creeper, 2, 1, 2);
                if (creeper.getRandom().nextInt(5) == 0) {
                    TCExplosionUtils.createExplosion(creeper.level(), creeper, pos, 1.5f);
                }
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(creeper.level());
                bolt.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                creeper.level().addFreshEntity(bolt);
            }
        } else {
            TCExplosionUtils.createExplosion(creeper.level(), creeper, creeper.getOnPos(), 1.5f);
            if (this.target != null) {
                for (int i = 0; i < (creeper.isPowered() ? 50 : 30); i++) {
                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(creeper.level());
                    bolt.setPos(this.target.getX() + 0.5, this.target.getY(), this.target.getZ() + 0.5);
                    creeper.level().addFreshEntity(bolt);
                }
            }
        }
    }
}
