package com.tntmodders.takumicraft.entity.ai.allay;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.tntmodders.takumicraft.entity.mobs.TCAllayCreeper;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

public class TCAllayCreeperAi {
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_DEPOSIT_TARGET = 2.25F;
    private static final float SPEED_MULTIPLIER_WHEN_RETRIEVING_ITEM = 1.75F;
    private static final float SPEED_MULTIPLIER_WHEN_PANICKING = 2.5F;
    private static final int CLOSE_ENOUGH_TO_TARGET = 4;
    private static final int TOO_FAR_FROM_TARGET = 16;
    private static final int MAX_LOOK_DISTANCE = 6;
    private static final int MIN_WAIT_DURATION = 30;
    private static final int MAX_WAIT_DURATION = 60;
    private static final int TIME_TO_FORGET_NOTEBLOCK = 600;
    private static final int DISTANCE_TO_WANTED_ITEM = 32;
    private static final int GIVE_ITEM_TIMEOUT_DURATION = 20;

    public static Brain<?> makeBrain(Brain<TCAllayCreeper> p_218420_) {
        initCoreActivity(p_218420_);
        initIdleActivity(p_218420_);
        p_218420_.setCoreActivities(ImmutableSet.of(Activity.CORE));
        p_218420_.setDefaultActivity(Activity.IDLE);
        p_218420_.useDefaultActivity();
        return p_218420_;
    }

    private static void initCoreActivity(Brain<TCAllayCreeper> p_218426_) {
        p_218426_.addActivity(
                Activity.CORE,
                0,
                ImmutableList.of(
                        new Swim<>(0.8F),
                        new AnimalPanic<>(2.5F),
                        new LookAtTargetSink(45, 90),
                        new MoveToTargetSink(),
                        new CountDownCooldownTicks(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS),
                        new CountDownCooldownTicks(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS)
                )
        );
    }

    private static void initIdleActivity(Brain<TCAllayCreeper> p_218432_) {
        p_218432_.addActivityWithConditions(
                Activity.IDLE,
                ImmutableList.of(
                        Pair.of(0, GoToWantedItem.create(p_218428_ -> true, 1.75F, true, 32)),
                        Pair.of(1, new GoAndGiveTNTsToTarget<>(TCAllayCreeperAi::getItemDepositPosition, 2.25F, 20)),
                        Pair.of(2, StayCloseToTarget.create(TCAllayCreeperAi::getItemDepositPosition, Predicate.not(TCAllayCreeperAi::hasWantedItem), 4, 16, 2.25F)),
                        Pair.of(3, SetEntityLookTargetSometimes.create(6.0F, UniformInt.of(30, 60))),
                        Pair.of(
                                4,
                                new RunOne<>(
                                        ImmutableList.of(
                                                Pair.of(RandomStroll.fly(1.0F), 2),
                                                Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 2),
                                                Pair.of(new DoNothing(30, 60), 1)
                                        )
                                )
                        )
                ),
                ImmutableSet.of()
        );
    }

    public static void updateActivity(TCAllayCreeper p_218422_) {
        p_218422_.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }

    public static void hearNoteblock(LivingEntity p_218417_, BlockPos p_218418_) {
        Brain<?> brain = p_218417_.getBrain();
        GlobalPos globalpos = GlobalPos.of(p_218417_.level().dimension(), p_218418_);
        Optional<GlobalPos> optional = brain.getMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
        if (optional.isEmpty()) {
            brain.setMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION, globalpos);
            brain.setMemory(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, 600);
        } else if (optional.get().equals(globalpos)) {
            brain.setMemory(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, 600);
        }
    }

    private static Optional<PositionTracker> getItemDepositPosition(LivingEntity p_218424_) {
        Brain<?> brain = p_218424_.getBrain();
        Optional<GlobalPos> optional = brain.getMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
        if (optional.isPresent()) {
            GlobalPos globalpos = optional.get();
            if (shouldDepositItemsAtLikedNoteblock(p_218424_, brain, globalpos)) {
                return Optional.of(new BlockPosTracker(globalpos.pos().above()));
            }

            brain.eraseMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
        }

        return getLikedPlayerPositionTracker(p_218424_);
    }

    private static boolean hasWantedItem(LivingEntity p_273346_) {
        Brain<?> brain = p_273346_.getBrain();
        return brain.hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
    }

    private static boolean shouldDepositItemsAtLikedNoteblock(LivingEntity p_218413_, Brain<?> p_218414_, GlobalPos p_218415_) {
        Optional<Integer> optional = p_218414_.getMemory(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS);
        Level level = p_218413_.level();
        return level.dimension() == p_218415_.dimension() && level.getBlockState(p_218415_.pos()).is(Blocks.NOTE_BLOCK) && optional.isPresent();
    }

    private static Optional<PositionTracker> getLikedPlayerPositionTracker(LivingEntity p_218430_) {
        return getLikedPlayer(p_218430_).map(p_218409_ -> new EntityTracker(p_218409_, true));
    }

    public static Optional<ServerPlayer> getLikedPlayer(LivingEntity p_218411_) {
        Level level = p_218411_.level();
        if (!level.isClientSide() && level instanceof ServerLevel serverlevel) {
            Optional<UUID> optional = p_218411_.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
            if (optional.isPresent()) {
                if (serverlevel.getEntity(optional.get()) instanceof ServerPlayer serverplayer
                        && (serverplayer.gameMode.isSurvival() || serverplayer.gameMode.isCreative())
                        && serverplayer.closerThan(p_218411_, 64.0)) {
                    return Optional.of(serverplayer);
                }

                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    public static class GoAndGiveTNTsToTarget<E extends LivingEntity & InventoryCarrier> extends Behavior<E> {
        private static final int CLOSE_ENOUGH_DISTANCE_TO_TARGET = 3;
        private static final int ITEM_PICKUP_COOLDOWN_AFTER_THROWING = 60;
        private final Function<LivingEntity, Optional<PositionTracker>> targetPositionGetter;
        private final float speedModifier;

        public GoAndGiveTNTsToTarget(Function<LivingEntity, Optional<PositionTracker>> p_249894_, float p_249937_, int p_249620_) {
            super(
                    Map.of(
                            MemoryModuleType.LOOK_TARGET,
                            MemoryStatus.REGISTERED,
                            MemoryModuleType.WALK_TARGET,
                            MemoryStatus.REGISTERED,
                            MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
                            MemoryStatus.REGISTERED
                    ),
                    p_249620_
            );
            this.targetPositionGetter = p_249894_;
            this.speedModifier = p_249937_;
        }

        private static Vec3 getThrowPosition(PositionTracker p_217212_) {
            return p_217212_.currentPosition().add(0.0, 1.0, 0.0);
        }

        public static void throwItem(LivingEntity p_217208_, ItemStack p_217209_, Vec3 p_217210_) {
            Vec3 vec3 = new Vec3(0.2F, 0.3F, 0.2F);
            throwTNT(p_217208_, p_217209_, p_217210_, vec3, 0.2F);
            Level level = p_217208_.level();
            if (level.getGameTime() % 7L == 0L && level.random.nextDouble() < 0.9) {
                float f = Util.getRandom(TCAllayCreeper.THROW_SOUND_PITCHES, level.getRandom());
                level.playSound(null, p_217208_, SoundEvents.ALLAY_THROW, SoundSource.NEUTRAL, 1.0F, f);
            }
        }

        public static void throwTNT(LivingEntity p_217134_, ItemStack p_217135_, Vec3 p_217136_, Vec3 p_217137_, float p_217138_) {
            double d0 = p_217134_.getEyeY() - (double) p_217138_;
            PrimedTnt tnt = new PrimedTnt(p_217134_.level(), p_217134_.getX(), d0, p_217134_.getZ(), p_217134_);
            Vec3 vec3 = p_217136_.subtract(p_217134_.position());
            vec3 = vec3.normalize().multiply(p_217137_.x, p_217137_.y, p_217137_.z);
            tnt.setDeltaMovement(vec3);
            tnt.setFuse(30);
            p_217134_.level().addFreshEntity(tnt);
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel p_217196_, E p_217197_) {
            return this.canThrowItemToTarget(p_217197_);
        }

        @Override
        protected boolean canStillUse(ServerLevel p_217218_, E p_217219_, long p_217220_) {
            return this.canThrowItemToTarget(p_217219_);
        }

        @Override
        protected void start(ServerLevel p_217199_, E p_217200_, long p_217201_) {
            this.targetPositionGetter.apply(p_217200_).ifPresent(p_217206_ -> BehaviorUtils.setWalkAndLookTargetMemories(p_217200_, p_217206_, this.speedModifier, 3));
        }

        @Override
        protected void tick(ServerLevel p_217226_, E p_217227_, long p_217228_) {
            Optional<PositionTracker> optional = this.targetPositionGetter.apply(p_217227_);
            if (optional.isPresent()) {
                PositionTracker positiontracker = optional.get();
                double d0 = positiontracker.currentPosition().distanceTo(p_217227_.getEyePosition());
                if (d0 < 3.0) {
                    ItemStack itemstack = p_217227_.getInventory().removeItem(0, 1 + p_217227_.getRandom().nextInt(4));
                    if (!itemstack.isEmpty()) {
                        throwItem(p_217227_, itemstack, getThrowPosition(positiontracker));
                        if (p_217227_ instanceof TCAllayCreeper allay) {
                            TCAllayCreeperAi.getLikedPlayer(allay).ifPresent(p_217224_ -> this.triggerDropItemOnBlock(positiontracker, itemstack, p_217224_));
                        }

                        p_217227_.getBrain().setMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, 60);
                    }
                }
            }
        }

        private void triggerDropItemOnBlock(PositionTracker p_217214_, ItemStack p_217215_, ServerPlayer p_217216_) {
            BlockPos blockpos = p_217214_.currentBlockPosition().below();
            CriteriaTriggers.ALLAY_DROP_ITEM_ON_BLOCK.trigger(p_217216_, blockpos, p_217215_);
        }

        private boolean canThrowItemToTarget(E p_217203_) {
            if (p_217203_.getInventory().isEmpty()) {
                return false;
            } else {
                Optional<PositionTracker> optional = this.targetPositionGetter.apply(p_217203_);
                return optional.isPresent();
            }
        }
    }
}
