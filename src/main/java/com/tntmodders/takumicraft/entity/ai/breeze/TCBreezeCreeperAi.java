package com.tntmodders.takumicraft.entity.ai.breeze;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.breeze.BreezeAi;
import net.minecraft.world.entity.monster.breeze.BreezeUtil;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TCBreezeCreeperAi {
    public static final float SPEED_MULTIPLIER_WHEN_SLIDING = 0.6F;
    public static final float JUMP_CIRCLE_INNER_RADIUS = 4.0F;
    public static final float JUMP_CIRCLE_MIDDLE_RADIUS = 8.0F;
    public static final float JUMP_CIRCLE_OUTER_RADIUS = 20.0F;
    public static final List<SensorType<? extends Sensor<? super TCBreezeCreeper>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.NEAREST_PLAYERS);
    public static final List<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.BREEZE_JUMP_COOLDOWN, MemoryModuleType.BREEZE_JUMP_INHALING, MemoryModuleType.BREEZE_SHOOT, MemoryModuleType.BREEZE_SHOOT_CHARGING, MemoryModuleType.BREEZE_SHOOT_RECOVERING, MemoryModuleType.BREEZE_SHOOT_COOLDOWN, MemoryModuleType.BREEZE_JUMP_TARGET, MemoryModuleType.BREEZE_LEAVING_WATER, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.PATH);

    public static Brain<?> makeBrain(TCBreezeCreeper p_342303_, Brain<TCBreezeCreeper> p_311919_) {
        initCoreActivity(p_311919_);
        initIdleActivity(p_311919_);
        initFightActivity(p_342303_, p_311919_);
        p_311919_.setCoreActivities(Set.of(Activity.CORE));
        p_311919_.setDefaultActivity(Activity.FIGHT);
        p_311919_.useDefaultActivity();
        return p_311919_;
    }

    public static boolean hasLineOfSight(TCBreezeCreeper p_334566_, Vec3 p_329106_) {
        Vec3 vec3 = new Vec3(p_334566_.getX(), p_334566_.getY(), p_334566_.getZ());
        return !(p_329106_.distanceTo(vec3) > getMaxLineOfSightTestRange(p_334566_)) && p_334566_.level().clip(new ClipContext(vec3, p_329106_, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p_334566_)).getType()
                == HitResult.Type.MISS;
    }

    private static double getMaxLineOfSightTestRange(TCBreezeCreeper p_363657_) {
        return Math.max(50.0, p_363657_.getAttributeValue(Attributes.FOLLOW_RANGE));
    }

    private static void initCoreActivity(Brain<TCBreezeCreeper> p_312238_) {
        p_312238_.addActivity(Activity.CORE, 0, ImmutableList.of(new Swim<>(0.8F), new LookAtTargetSink(45, 90)));
    }

    private static void initIdleActivity(Brain<TCBreezeCreeper> p_335718_) {
        p_335718_.addActivity(
                Activity.IDLE,
                ImmutableList.of(
                        Pair.of(0, StartAttacking.create((p_369589_, p_312068_) -> p_312068_.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE))),
                        Pair.of(1, StartAttacking.create((p_359265_, p_359266_) -> p_359266_.getHurtBy())),
                        Pair.of(2, new BreezeAi.SlideToTargetSink(20, 40)),
                        Pair.of(3, new RunOne<>(ImmutableList.of(Pair.of(new DoNothing(20, 100), 1), Pair.of(RandomStroll.stroll(0.6F), 2))))
                )
        );
    }

    private static void initFightActivity(TCBreezeCreeper p_344626_, Brain<TCBreezeCreeper> p_310469_) {
        p_310469_.addActivityWithConditions(
                Activity.FIGHT,
                ImmutableList.of(
                        Pair.of(0, StopAttackingIfTargetInvalid.create(Sensor.wasEntityAttackableLastNTicks(p_344626_, 100).negate()::test)),
                        Pair.of(1, new TCShoot()),
                        Pair.of(2, new TCLongJump()),
                        Pair.of(3, new TCShootWhenStuck()),
                        Pair.of(4, new TCSlide())
                ),
                ImmutableSet.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT))
        );
    }

    public static void updateActivity(TCBreezeCreeper p_331608_) {
        p_331608_.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
    }

    public static class SlideToTargetSink extends MoveToTargetSink {
        @VisibleForTesting
        public SlideToTargetSink(int p_309679_, int p_309866_) {
            super(p_309679_, p_309866_);
        }

        @Override
        protected void start(ServerLevel p_312379_, Mob p_312744_, long p_311813_) {
            super.start(p_312379_, p_312744_, p_311813_);
            p_312744_.playSound(SoundEvents.BREEZE_SLIDE);
            p_312744_.setPose(Pose.SLIDING);
        }

        @Override
        protected void stop(ServerLevel p_311146_, Mob p_310932_, long p_312981_) {
            super.stop(p_311146_, p_310932_, p_312981_);
            p_310932_.setPose(Pose.STANDING);
            if (p_310932_.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
                p_310932_.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT, Unit.INSTANCE, 60L);
            }
        }
    }

    public static class TCSlide extends Behavior<TCBreezeCreeper> {
        public TCSlide() {
            super(
                    Map.of(
                            MemoryModuleType.ATTACK_TARGET,
                            MemoryStatus.VALUE_PRESENT,
                            MemoryModuleType.WALK_TARGET,
                            MemoryStatus.VALUE_ABSENT,
                            MemoryModuleType.BREEZE_JUMP_COOLDOWN,
                            MemoryStatus.VALUE_ABSENT,
                            MemoryModuleType.BREEZE_SHOOT,
                            MemoryStatus.VALUE_ABSENT
                    )
            );
        }

        private static Vec3 randomPointInMiddleCircle(TCBreezeCreeper p_310635_, LivingEntity p_312574_) {
            Vec3 vec3 = p_312574_.position().subtract(p_310635_.position());
            double d0 = vec3.length() - Mth.lerp(p_310635_.getRandom().nextDouble(), 8.0, 4.0);
            Vec3 vec31 = vec3.normalize().multiply(d0, d0, d0);
            return p_310635_.position().add(vec31);
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel p_312721_, TCBreezeCreeper p_311782_) {
            return p_311782_.onGround() && !p_311782_.isInWater() && p_311782_.getPose() == Pose.STANDING;
        }

        @Override
        protected void start(ServerLevel p_312079_, TCBreezeCreeper p_310251_, long p_310596_) {
            LivingEntity livingentity = p_310251_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
            if (livingentity != null) {
                boolean flag = p_310251_.withinInnerCircleRange(livingentity.position());
                Vec3 vec3 = null;
                if (flag) {
                    Vec3 vec31 = DefaultRandomPos.getPosAway(p_310251_, 5, 5, livingentity.position());
                    if (vec31 != null
                            && hasLineOfSight(p_310251_, vec31)
                            && livingentity.distanceToSqr(vec31.x, vec31.y, vec31.z) > livingentity.distanceToSqr(p_310251_)) {
                        vec3 = vec31;
                    }
                }

                if (vec3 == null) {
                    vec3 = p_310251_.getRandom().nextBoolean() ? BreezeUtil.randomPointBehindTarget(livingentity, p_310251_.getRandom()) : randomPointInMiddleCircle(p_310251_, livingentity);
                }

                p_310251_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(BlockPos.containing(vec3), 0.6F, 1));
            }
        }
    }

    public static class TCShootWhenStuck extends Behavior<TCBreezeCreeper> {
        public TCShootWhenStuck() {
            super(
                    Map.of(
                            MemoryModuleType.ATTACK_TARGET,
                            MemoryStatus.VALUE_PRESENT,
                            MemoryModuleType.BREEZE_JUMP_INHALING,
                            MemoryStatus.VALUE_ABSENT,
                            MemoryModuleType.BREEZE_JUMP_TARGET,
                            MemoryStatus.VALUE_ABSENT,
                            MemoryModuleType.WALK_TARGET,
                            MemoryStatus.VALUE_ABSENT,
                            MemoryModuleType.BREEZE_SHOOT,
                            MemoryStatus.VALUE_ABSENT
                    )
            );
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel p_312625_, TCBreezeCreeper p_311731_) {
            return p_311731_.isPassenger() || p_311731_.isInWater() || p_311731_.getEffect(MobEffects.LEVITATION) != null;
        }

        @Override
        public boolean canStillUse(ServerLevel p_310843_, TCBreezeCreeper p_311345_, long p_311650_) {
            return false;
        }

        @Override
        protected void start(ServerLevel p_311028_, TCBreezeCreeper p_309885_, long p_313079_) {
            p_309885_.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT, Unit.INSTANCE, 60L);
        }
    }

    public static class TCShoot extends Behavior<TCBreezeCreeper> {
        private static final int ATTACK_RANGE_MAX_SQRT = 256;
        private static final int UNCERTAINTY_BASE = 5;
        private static final int UNCERTAINTY_MULTIPLIER = 4;
        private static final float PROJECTILE_MOVEMENT_SCALE = 0.7F;
        private static final int SHOOT_INITIAL_DELAY_TICKS = Math.round(15.0F);
        private static final int SHOOT_RECOVER_DELAY_TICKS = Math.round(4.0F);
        private static final int SHOOT_COOLDOWN_TICKS = Math.round(10.0F);

        @VisibleForTesting
        public TCShoot() {
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

        private static boolean isTargetWithinRange(TCBreezeCreeper p_311470_, LivingEntity p_309385_) {
            double d0 = p_311470_.position().distanceToSqr(p_309385_.position());
            return d0 < 256.0;
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
        protected void tick(ServerLevel p_312469_, TCBreezeCreeper p_309721_, long p_312577_) {
            Brain<TCBreezeCreeper> brain = p_309721_.getBrain();
            LivingEntity livingentity = brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
            if (livingentity != null) {
                p_309721_.lookAt(EntityAnchorArgument.Anchor.EYES, livingentity.position());
                if (brain.getMemory(MemoryModuleType.BREEZE_SHOOT_CHARGING).isEmpty() && brain.getMemory(MemoryModuleType.BREEZE_SHOOT_RECOVERING).isEmpty()) {
                    brain.setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT_RECOVERING, Unit.INSTANCE, SHOOT_RECOVER_DELAY_TICKS);
                    double d0 = livingentity.getX() - p_309721_.getX();
                    double d1 = livingentity.getY(livingentity.isPassenger() ? 0.8 : 0.3) - p_309721_.getFiringYPosition();
                    double d2 = livingentity.getZ() - p_309721_.getZ();
                    Projectile.spawnProjectileUsingShoot(
                            //@TODO solve the tag-error
                            /*new TCBreezeCreeperWindCharge(TCBreezeCreeperWindCharge.BREEZE_WIND_CHARGE, p_312469_),*/new BreezeWindCharge(EntityType.BREEZE_WIND_CHARGE, p_312469_),
                            p_312469_,
                            ItemStack.EMPTY,
                            d0,
                            d1,
                            d2,
                            0.7F,
                            (float) (5 - p_312469_.getDifficulty().getId() * 4)
                    );
                    p_309721_.playSound(SoundEvents.BREEZE_SHOOT, 1.5F, 1.0F);
                }
            }
        }
    }

    public static class TCLongJump extends Behavior<TCBreezeCreeper> {
        private static final int REQUIRED_AIR_BLOCKS_ABOVE = 4;
        private static final int JUMP_COOLDOWN_TICKS = 10;
        private static final int JUMP_COOLDOWN_WHEN_HURT_TICKS = 2;
        private static final int INHALING_DURATION_TICKS = Math.round(10.0F);
        private static final float DEFAULT_FOLLOW_RANGE = 24.0F;
        private static final float DEFAULT_MAX_JUMP_VELOCITY = 1.4F;
        private static final float MAX_JUMP_VELOCITY_MULTIPLIER = 0.058333334F;
        private static final ObjectArrayList<Integer> ALLOWED_ANGLES = new ObjectArrayList<>(Lists.newArrayList(40, 55, 60, 75, 80));

        @VisibleForTesting
        public TCLongJump() {
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
                    BlockPos blockpos = snapToSurface(p_330036_, BreezeUtil.randomPointBehindTarget(livingentity, p_330036_.getRandom()));
                    if (blockpos == null) {
                        return false;
                    } else {
                        BlockState blockstate = p_328434_.getBlockState(blockpos.below());
                        if (p_330036_.getType().isBlockDangerous(blockstate)) {
                            return false;
                        } else if (!hasLineOfSight(p_330036_, blockpos.getCenter()) && !hasLineOfSight(p_330036_, blockpos.above(4).getCenter())) {
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
            return !p_309508_.closerThan(p_310244_, p_310244_.getAttributeValue(Attributes.FOLLOW_RANGE));
        }

        private static boolean tooCloseForJump(TCBreezeCreeper p_310091_, LivingEntity p_311303_) {
            return p_311303_.distanceTo(p_310091_) - 4.0F <= 0.0F;
        }

        private static boolean canJumpFromCurrentPosition(ServerLevel p_312023_, TCBreezeCreeper p_313218_) {
            BlockPos blockpos = p_313218_.blockPosition();
            if (p_312023_.getBlockState(blockpos).is(Blocks.HONEY_BLOCK)) {
                return false;
            } else {
                for (int i = 1; i <= 4; i++) {
                    BlockPos blockpos1 = blockpos.relative(Direction.UP, i);
                    if (!p_312023_.getBlockState(blockpos1).isAir() && !p_312023_.getFluidState(blockpos1).is(FluidTags.WATER)) {
                        return false;
                    }
                }

                return true;
            }
        }

        private static Optional<Vec3> calculateOptimalJumpVector(TCBreezeCreeper p_310143_, RandomSource p_313023_, Vec3 p_309973_) {
            for (int i : Util.shuffledCopy(ALLOWED_ANGLES, p_313023_)) {
                float f = 0.058333334F * (float) p_310143_.getAttributeValue(Attributes.FOLLOW_RANGE);
                Optional<Vec3> optional = LongJumpUtil.calculateJumpVectorForAngle(p_310143_, p_309973_, f, i, false);
                if (optional.isPresent()) {
                    if (p_310143_.hasEffect(MobEffects.JUMP)) {
                        double d0 = optional.get().normalize().y * (double) p_310143_.getJumpBoostPower();
                        return optional.map(p_359268_ -> p_359268_.add(0.0, d0, 0.0));
                    }

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
                        .flatMap(p_359270_ -> calculateOptimalJumpVector(p_310204_, p_310204_.getRandom(), Vec3.atBottomCenterOf(p_359270_)))
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
}
