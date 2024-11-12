package com.tntmodders.takumicraft.entity.ai.boss.king;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TPKingCreeperAttack extends AbstractKingCreeperAttack {
    private BlockPos target = BlockPos.ZERO;

    @Override
    public void serverInit(TCKingCreeper creeper) {
    }

    @Override
    public void serverTick(TCKingCreeper creeper, int swell) {
        if (swell < 30) {
            if (creeper.getTarget() != null) {
                target = new BlockPos(creeper.getTarget().getBlockX(), creeper.getTarget().getBlockY(), creeper.getTarget().getBlockZ());
            } else {
                Player player = creeper.level().getNearestPlayer(creeper, 50);
                if (player != null) {
                    this.target = new BlockPos(player.getBlockX(), player.getBlockY(), player.getBlockZ());
                }
            }
        } else if (swell == 30) {
            TCExplosionUtils.createExplosion(creeper.level(), creeper, creeper.getX(), creeper.getY(), creeper.getZ(), 2f, false);
            if (!BlockPos.ZERO.equals(this.target)) {
                creeper.setPos(this.target.getX(), this.target.getY() + 0.25, this.target.getZ());
                creeper.playSound(SoundEvents.ENDERMAN_TELEPORT);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(LivingEntityRenderState state, TCKingCreeper creeper, int swell, PoseStack pose, MultiBufferSource bufferSource, float renderTick) {
        if (swell < 30) {
            for (double x = -2; x <= 2; x += 0.25) {
                for (double z = -2; z <= 2; z += 0.25) {
                    if (x * x + z * z <= 4 && x * x + z * z >= 1.5 * 1.5 && creeper.getRandom().nextInt(2) == 0) {
                        this.spawnParticle(creeper, ParticleTypes.PORTAL, x, 0, z, Math.sin(Math.atan2(z, x) * creeper.tickCount / 10) * 0.2, 0.1, Math.cos(Math.atan2(z, x) * creeper.tickCount / 10) * 0.2);
                    }
                }
            }
        } else if (swell == 30) {
            creeper.playSound(SoundEvents.ENDERMAN_TELEPORT);
        }
    }

    @Override
    public void serverExp(TCKingCreeper creeper) {
        TCExplosionUtils.createExplosion(creeper.level(), creeper, creeper.getX(), creeper.getY(), creeper.getZ(), creeper.isPowered() ? 14f : 7f, false);
    }
}
