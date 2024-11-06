package com.tntmodders.takumicraft.entity.ai.boss.king;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.entity.misc.TCKingBlock;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class KingblockKingCreeperAttack extends AbstractKingCreeperAttack {
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
                if (x * x + z * z <= 25 && creeper.getRandom().nextInt(10) == 0) {
                    this.spawnParticle(creeper, ParticleTypes.ENCHANT, x, creeper.getRandom().nextDouble() + 0.5, z, (creeper.getRandom().nextDouble() - 0.5) * 0.3, 0.5, (creeper.getRandom().nextDouble() - 0.5) * 0.3);
                }
            }
        }
    }

    @Override
    public void serverExp(TCKingCreeper creeper) {
        TCExplosionUtils.createExplosion(creeper.level(), creeper, creeper.getOnPos(), 0.5f);
        LivingEntity target = creeper.getTarget() == null ? creeper.level().getNearestPlayer(creeper, 32) : creeper.getTarget();

        if (target != null) {
            double d0 = Math.min(target.getY(), creeper.getY());
            double d1 = Math.max(target.getY(), creeper.getY()) + 1.0D;
            float f = (float) Math.atan2(target.getZ() - creeper.getZ(),
                    target.getX() - creeper.getX());

            if (creeper.distanceToSqr(target) < 64) {
                for (int i = 0; i < 5; ++i) {
                    float f1 = f + (float) i * (float) Math.PI * 0.4F;
                    this.spawnFangs(creeper, creeper.getX() + Math.cos(f1) * 3D,
                            creeper.getZ() + Math.sin(f1) * 3D, d0, d1, f1, 0);
                }

                for (int k = 0; k < 8; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 2.0F / 8.0F + (float) Math.PI * 2F / 5F;
                    this.spawnFangs(creeper, creeper.getX() + Math.cos(f2) * 7D,
                            creeper.getZ() + Math.sin(f2) * 7D, d0, d1, f2, 3);
                }
            } else {
                for (int l = 0; l < 16; ++l) {
                    double d2 = 2D * (double) (l + 1);
                    this.spawnFangs(creeper, creeper.getX() + Math.cos(f) * d2,
                            creeper.getZ() + Math.sin(f) * d2, d0, d1, f, (double) l / 2);
                }
            }
        }
    }

    private void spawnFangs(TCKingCreeper creeper, double x, double z, double minY, double maxY, float yRot, double delay) {
        BlockPos blockpos = BlockPos.containing(x, maxY, z);
        boolean flag = false;
        double d0 = 0;
        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = creeper.level().getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(creeper.level(), blockpos1, Direction.UP)) {
                if (!creeper.level().isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = creeper.level().getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(creeper.level(), blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }
                flag = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= Mth.floor(minY) - 1);

        if (flag) {
            creeper.level().addFreshEntity(new TCKingBlock(creeper.level(), x, (double) blockpos.getY() + d0 + 5, z, (int) (delay * 10 + 20), creeper));
            creeper.level().gameEvent(GameEvent.ENTITY_PLACE, new Vec3(x, (double) blockpos.getY() + d0, z), GameEvent.Context.of(creeper));
        }
    }

}