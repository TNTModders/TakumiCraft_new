package com.tntmodders.takumicraft.entity.ai.breeze;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.tntmodders.takumicraft.entity.mobs.TCBreezeCreeper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.LongJumpUtil;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public class LongJump extends Behavior<TCBreezeCreeper> {
    private static final int REQUIRED_AIR_BLOCKS_ABOVE = 4;
    private static final int JUMP_COOLDOWN_TICKS = 10;
    private static final int JUMP_COOLDOWN_WHEN_HURT_TICKS = 2;
    private static final int INHALING_DURATION_TICKS = Math.round(10.0F);
    private static final float MAX_JUMP_VELOCITY = 1.4F;
    private static final ObjectArrayList<Integer> ALLOWED_ANGLES = new ObjectArrayList<>(Lists.newArrayList(40, 55, 60, 75, 80));

    @VisibleForTesting
    public LongJump() {
        super(
                Map.of(
                        MemoryModuleType.ATTACK_TARGET,
                        MemoryStatus.VALUE_PRESENT,
                        MemoryModuleType.BREEZE_JUMP_COOLDOWN,
                        MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.BREEZE_JUMP_INHALING,
                        MemoryStatus.REGISTERED,
                        MemoryModuleType.BREEZE_JUMP_TARGET,
                        MemoryStatus.REGISTERED,
                        MemoryModuleType.BREEZE_SHOOT,
                        MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.WALK_TARGET,
                        MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.BREEZE_LEAVING_WATER,
                        MemoryStatus.REGISTERED
                ),
                200
        );
    }

    public static boolean canRun(ServerLevel p_328434_, TCBreezeCreeper p_330036_) {
        if (!p_330036_.onGround() && !p_330036_.isInWater()) {
            return false;
        } else if (Swim.shouldSwim(p_330036_)) {
            return false;
        } else if (p_330036_.getBrain().checkMemory(MemoryModuleType.BREEZE_JUMP_TARGET, MemoryStatus.VALUE_PRESENT)) {
            return true;
        } else {
            LivingEntity livingentity = p_330036_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
            if (livingentity == null) {
                return false;
            } else if (outOfAggroRange(p_330036_, livingentity)) {
                p_330036_.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                return false;
            } else if (tooCloseForJump(p_330036_, livingentity)) {
                return false;
            } else if (!canJumpFromCurrentPosition(p_328434_, p_330036_)) {
                return false;
            } else {
                BlockPos blockpos = snapToSurface(p_330036_, TCBreezeCreeperUtil.randomPointBehindTarget(livingentity, p_330036_.getRandom()));
                if (blockpos == null) {
                    return false;
                } else {
                    BlockState blockstate = p_328434_.getBlockState(blockpos.below());
                    if (p_330036_.getType().isBlockDangerous(blockstate)) {
                        return false;
                    } else if (!TCBreezeCreeperUtil.hasLineOfSight(p_330036_, blockpos.getCenter()) && !TCBreezeCreeperUtil.hasLineOfSight(p_330036_, blockpos.above(4).getCenter())) {
                        return false;
                    } else {
                        p_330036_.getBrain().setMemory(MemoryModuleType.BREEZE_JUMP_TARGET, blockpos);
                        return true;
                    }
                }
            }
        }
    }

    private static boolean isFinishedInhaling(TCBreezeCreeper p_330141_) {
        return p_330141_.getBrain().getMemory(MemoryModuleType.BREEZE_JUMP_INHALING).isEmpty() && p_330141_.getPose() == Pose.INHALING;
    }

    private static boolean isFinishedJumping(TCBreezeCreeper p_330755_) {
        boolean flag = p_330755_.getPose() == Pose.LONG_JUMPING;
        boolean flag1 = p_330755_.onGround();
        boolean flag2 = p_330755_.isInWater() && p_330755_.getBrain().checkMemory(MemoryModuleType.BREEZE_LEAVING_WATER, MemoryStatus.VALUE_ABSENT);
        return flag && (flag1 || flag2);
    }

    @Nullable
    private static BlockPos snapToSurface(LivingEntity p_312785_, Vec3 p_311613_) {
        ClipContext clipcontext = new ClipContext(
                p_311613_, p_311613_.relative(Direction.DOWN, 10.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p_312785_
        );
        HitResult hitresult = p_312785_.level().clip(clipcontext);
        if (hitresult.getType() == HitResult.Type.BLOCK) {
            return BlockPos.containing(hitresult.getLocation()).above();
        } else {
            ClipContext clipcontext1 = new ClipContext(
                    p_311613_, p_311613_.relative(Direction.UP, 10.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p_312785_
            );
            HitResult hitresult1 = p_312785_.level().clip(clipcontext1);
            return hitresult1.getType() == HitResult.Type.BLOCK ? BlockPos.containing(hitresult1.getLocation()).above() : null;
        }
    }

    private static boolean outOfAggroRange(TCBreezeCreeper p_310244_, LivingEntity p_309508_) {
        return !p_309508_.closerThan(p_310244_, 24.0);
    }

    private static boolean tooCloseForJump(TCBreezeCreeper p_310091_, LivingEntity p_311303_) {
        return p_311303_.distanceTo(p_310091_) - 4.0F <= 0.0F;
    }

    private static boolean canJumpFromCurrentPosition(ServerLevel p_312023_, TCBreezeCreeper p_313218_) {
        BlockPos blockpos = p_313218_.blockPosition();

        for (int i = 1; i <= 4; i++) {
            BlockPos blockpos1 = blockpos.relative(Direction.UP, i);
            if (!p_312023_.getBlockState(blockpos1).isAir() && !p_312023_.getFluidState(blockpos1).is(FluidTags.WATER)) {
                return false;
            }
        }

        return true;
    }

    private static Optional<Vec3> calculateOptimalJumpVector(TCBreezeCreeper p_310143_, RandomSource p_313023_, Vec3 p_309973_) {
        for (int i : Util.shuffledCopy(ALLOWED_ANGLES, p_313023_)) {
            Optional<Vec3> optional = LongJumpUtil.calculateJumpVectorForAngle(p_310143_, p_309973_, 1.4F, i, false);
            if (optional.isPresent()) {
                return optional;
            }
        }

        return Optional.empty();
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel p_312411_, TCBreezeCreeper p_309539_) {
        return canRun(p_312411_, p_309539_);
    }

    @Override
    protected boolean canStillUse(ServerLevel p_310673_, TCBreezeCreeper p_311330_, long p_310051_) {
        return p_311330_.getPose() != Pose.STANDING && !p_311330_.getBrain().hasMemoryValue(MemoryModuleType.BREEZE_JUMP_COOLDOWN);
    }

    @Override
    protected void start(ServerLevel p_310741_, TCBreezeCreeper p_312948_, long p_311377_) {
        if (p_312948_.getBrain().checkMemory(MemoryModuleType.BREEZE_JUMP_INHALING, MemoryStatus.VALUE_ABSENT)) {
            p_312948_.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_JUMP_INHALING, Unit.INSTANCE, INHALING_DURATION_TICKS);
        }

        p_312948_.setPose(Pose.INHALING);
        p_310741_.playSound(null, p_312948_, SoundEvents.BREEZE_CHARGE, SoundSource.HOSTILE, 1.0F, 1.0F);
        p_312948_.getBrain()
                .getMemory(MemoryModuleType.BREEZE_JUMP_TARGET)
                .ifPresent(p_311106_ -> p_312948_.lookAt(EntityAnchorArgument.Anchor.EYES, p_311106_.getCenter()));
    }

    @Override
    protected void tick(ServerLevel p_312629_, TCBreezeCreeper p_310204_, long p_313176_) {
        boolean flag = p_310204_.isInWater();
        if (!flag && p_310204_.getBrain().checkMemory(MemoryModuleType.BREEZE_LEAVING_WATER, MemoryStatus.VALUE_PRESENT)) {
            p_310204_.getBrain().eraseMemory(MemoryModuleType.BREEZE_LEAVING_WATER);
        }

        if (isFinishedInhaling(p_310204_)) {
            Vec3 vec3 = p_310204_.getBrain()
                    .getMemory(MemoryModuleType.BREEZE_JUMP_TARGET)
                    .flatMap(p_341451_ -> calculateOptimalJumpVector(p_310204_, p_310204_.getRandom(), Vec3.atBottomCenterOf(p_341451_)))
                    .orElse(null);
            if (vec3 == null) {
                p_310204_.setPose(Pose.STANDING);
                return;
            }

            if (flag) {
                p_310204_.getBrain().setMemory(MemoryModuleType.BREEZE_LEAVING_WATER, Unit.INSTANCE);
            }

            p_310204_.playSound(SoundEvents.BREEZE_JUMP, 1.0F, 1.0F);
            p_310204_.setPose(Pose.LONG_JUMPING);
            p_310204_.setYRot(p_310204_.yBodyRot);
            p_310204_.setDiscardFriction(true);
            p_310204_.setDeltaMovement(vec3);
        } else if (isFinishedJumping(p_310204_)) {
            p_310204_.playSound(SoundEvents.BREEZE_LAND, 1.0F, 1.0F);
            p_310204_.setPose(Pose.STANDING);
            p_310204_.setDiscardFriction(false);
            boolean flag1 = p_310204_.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY);
            p_310204_.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_JUMP_COOLDOWN, Unit.INSTANCE, flag1 ? 2L : 10L);
            p_310204_.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT, Unit.INSTANCE, 100L);
        }
    }

    @Override
    protected void stop(ServerLevel p_309511_, TCBreezeCreeper p_311681_, long p_312980_) {
        if (p_311681_.getPose() == Pose.LONG_JUMPING || p_311681_.getPose() == Pose.INHALING) {
            p_311681_.setPose(Pose.STANDING);
        }

        p_311681_.getBrain().eraseMemory(MemoryModuleType.BREEZE_JUMP_TARGET);
        p_311681_.getBrain().eraseMemory(MemoryModuleType.BREEZE_JUMP_INHALING);
        p_311681_.getBrain().eraseMemory(MemoryModuleType.BREEZE_LEAVING_WATER);
    }
}