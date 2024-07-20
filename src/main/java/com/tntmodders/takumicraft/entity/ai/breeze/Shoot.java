package com.tntmodders.takumicraft.entity.ai.breeze;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.tntmodders.takumicraft.entity.mobs.TCBreezeCreeper;
import com.tntmodders.takumicraft.entity.projectile.TCBreezeCreeperWindCharge;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;

public class Shoot extends Behavior<TCBreezeCreeper> {
    private static final int ATTACK_RANGE_MIN_SQRT = 4;
    private static final int ATTACK_RANGE_MAX_SQRT = 256;
    private static final int UNCERTAINTY_BASE = 5;
    private static final int UNCERTAINTY_MULTIPLIER = 4;
    private static final float PROJECTILE_MOVEMENT_SCALE = 0.7F;
    private static final int SHOOT_INITIAL_DELAY_TICKS = Math.round(15.0F);
    private static final int SHOOT_RECOVER_DELAY_TICKS = Math.round(4.0F);
    private static final int SHOOT_COOLDOWN_TICKS = Math.round(10.0F);

    @VisibleForTesting
    public Shoot() {
        super(
                ImmutableMap.of(
                        MemoryModuleType.ATTACK_TARGET,
                        MemoryStatus.VALUE_PRESENT,
                        MemoryModuleType.BREEZE_SHOOT_COOLDOWN,
                        MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.BREEZE_SHOOT_CHARGING,
                        MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.BREEZE_SHOOT_RECOVERING,
                        MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.BREEZE_SHOOT,
                        MemoryStatus.VALUE_PRESENT,
                        MemoryModuleType.WALK_TARGET,
                        MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.BREEZE_JUMP_TARGET,
                        MemoryStatus.VALUE_ABSENT
                ),
                SHOOT_INITIAL_DELAY_TICKS + 1 + SHOOT_RECOVER_DELAY_TICKS
        );
    }

    @VisibleForTesting
    public static boolean isFacingTarget(TCBreezeCreeper p_311537_, LivingEntity p_310664_) {
        Vec3 vec3 = p_311537_.getViewVector(1.0F);
        Vec3 vec31 = p_310664_.position().subtract(p_311537_.position()).normalize();
        return vec3.dot(vec31) > 0.5;
    }

    private static boolean isTargetWithinRange(TCBreezeCreeper p_311470_, LivingEntity p_309385_) {
        double d0 = p_311470_.position().distanceToSqr(p_309385_.position());
        return d0 > 4.0 && d0 < 256.0;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel p_310608_, TCBreezeCreeper p_310203_) {
        return p_310203_.getPose() == Pose.STANDING && p_310203_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).map(p_311282_ -> isTargetWithinRange(p_310203_, p_311282_)).map(p_311912_ -> {
            if (!p_311912_) {
                p_310203_.getBrain().eraseMemory(MemoryModuleType.BREEZE_SHOOT);
            }

            return p_311912_;
        }).orElse(false);
    }

    @Override
    protected boolean canStillUse(ServerLevel p_309829_, TCBreezeCreeper p_312308_, long p_310493_) {
        return p_312308_.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && p_312308_.getBrain().hasMemoryValue(MemoryModuleType.BREEZE_SHOOT);
    }

    @Override
    protected void start(ServerLevel p_312287_, TCBreezeCreeper p_310847_, long p_311799_) {
        p_310847_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(p_312466_ -> p_310847_.setPose(Pose.SHOOTING));
        p_310847_.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT_CHARGING, Unit.INSTANCE, SHOOT_INITIAL_DELAY_TICKS);
        p_310847_.playSound(SoundEvents.BREEZE_INHALE, 1.0F, 1.0F);
    }

    @Override
    protected void stop(ServerLevel p_312573_, TCBreezeCreeper p_309852_, long p_310968_) {
        if (p_309852_.getPose() == Pose.SHOOTING) {
            p_309852_.setPose(Pose.STANDING);
        }

        p_309852_.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT_COOLDOWN, Unit.INSTANCE, SHOOT_COOLDOWN_TICKS);
        p_309852_.getBrain().eraseMemory(MemoryModuleType.BREEZE_SHOOT);
    }

    @Override
    protected void tick(ServerLevel level, TCBreezeCreeper creeper, long p_312577_) {
        Brain<TCBreezeCreeper> brain = creeper.getBrain();
        LivingEntity livingentity = brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        if (livingentity != null) {
            creeper.lookAt(EntityAnchorArgument.Anchor.EYES, livingentity.position());
            if (brain.getMemory(MemoryModuleType.BREEZE_SHOOT_CHARGING).isEmpty() && brain.getMemory(MemoryModuleType.BREEZE_SHOOT_RECOVERING).isEmpty()) {
                brain.setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT_RECOVERING, Unit.INSTANCE, SHOOT_RECOVER_DELAY_TICKS);
                if (isFacingTarget(creeper, livingentity)) {
                    double d0 = livingentity.getX() - creeper.getX();
                    double d1 = livingentity.getY(livingentity.isPassenger() ? 0.8 : 0.3) - creeper.getY(0.5);
                    double d2 = livingentity.getZ() - creeper.getZ();
                    TCBreezeCreeperWindCharge breezewindcharge = new TCBreezeCreeperWindCharge(creeper, level);
                    creeper.playSound(SoundEvents.BREEZE_SHOOT, 1.5F, 1.0F);
                    breezewindcharge.shoot(d0, d1, d2, 0.7F, (float) (5 - level.getDifficulty().getId() * 4));
                    if (level.addFreshEntity(breezewindcharge)) {
                        if (creeper.getRandom().nextBoolean() && creeper.distanceToSqr(livingentity) < 25) {
                            creeper.ignite();
                        }
                    }
                }
            }
        }
    }
}