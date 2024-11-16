package com.tntmodders.takumicraft.entity.ai.goat;

import com.google.common.collect.ImmutableMap;
import com.tntmodders.takumicraft.entity.mobs.TCGoatCreeper;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public class TCGoatRamTarget extends Behavior<TCGoatCreeper> {
    public static final int TIME_OUT_DURATION = 200;
    public static final float RAM_SPEED_FORCE_FACTOR = 1.65F;
    private final Function<TCGoatCreeper, UniformInt> getTimeBetweenRams;
    private final TargetingConditions ramTargeting;
    private final float speed;
    private final ToDoubleFunction<TCGoatCreeper> getKnockbackForce;
    private final Function<TCGoatCreeper, SoundEvent> getImpactSound;
    private final Function<TCGoatCreeper, SoundEvent> getHornBreakSound;
    private Vec3 ramDirection;

    public TCGoatRamTarget(
            Function<TCGoatCreeper, UniformInt> p_217342_,
            TargetingConditions p_217343_,
            float p_217344_,
            ToDoubleFunction<TCGoatCreeper> p_217345_,
            Function<TCGoatCreeper, SoundEvent> p_217346_,
            Function<TCGoatCreeper, SoundEvent> p_217347_
    ) {
        super(ImmutableMap.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.RAM_TARGET, MemoryStatus.VALUE_PRESENT), 200);
        this.getTimeBetweenRams = p_217342_;
        this.ramTargeting = p_217343_;
        this.speed = p_217344_;
        this.getKnockbackForce = p_217345_;
        this.getImpactSound = p_217346_;
        this.getHornBreakSound = p_217347_;
        this.ramDirection = Vec3.ZERO;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel p_217349_, TCGoatCreeper p_217350_) {
        return p_217350_.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
    }

    @Override
    protected boolean canStillUse(ServerLevel p_217352_, TCGoatCreeper p_217353_, long p_217354_) {
        return p_217353_.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
    }

    @Override
    protected void start(ServerLevel level, TCGoatCreeper creeper, long p_217361_) {
        BlockPos blockpos = creeper.blockPosition();
        Brain<?> brain = creeper.getBrain();
        Vec3 vec3 = brain.getMemory(MemoryModuleType.RAM_TARGET).get();
        this.ramDirection = new Vec3((double) blockpos.getX() - vec3.x(), 0.0, (double) blockpos.getZ() - vec3.z()).normalize();
        brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3, this.speed, 0));
    }

    @Override
    protected void tick(ServerLevel level, TCGoatCreeper creeper, long p_217368_) {
        List<LivingEntity> list = level.getNearbyEntities(LivingEntity.class, this.ramTargeting, creeper, creeper.getBoundingBox());
        Brain<?> brain = creeper.getBrain();
        TCExplosionUtils.createExplosion(level, creeper, creeper.getX(), creeper.getY() + 0.5, creeper.getZ(), 0f, false);
        if (!list.isEmpty() && list.getFirst().level() instanceof ServerLevel serverLevel) {
            LivingEntity livingentity = list.getFirst();
            DamageSource damagesource = level.damageSources().noAggroMobAttack(creeper);
            if (livingentity.hurtServer(serverLevel, damagesource, (float) creeper.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                EnchantmentHelper.doPostAttackEffects(level, livingentity, damagesource);
            }

            int i = creeper.hasEffect(MobEffects.MOVEMENT_SPEED) ? creeper.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1 : 0;
            int j = creeper.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) ? creeper.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1 : 0;
            float f = 0.25F * (float) (i - j);
            float f1 = Mth.clamp(creeper.getSpeed() * 1.65F, 0.2F, 3.0F) + f;
            float f2 = livingentity.isDamageSourceBlocked(level.damageSources().mobAttack(creeper)) ? 0.5F : 1.0F;
            float f3 = creeper.isPowered() ? 4 : 2;
            livingentity.knockback((double) (f2 * f1 * f3) * this.getKnockbackForce.applyAsDouble(creeper), this.ramDirection.x(), this.ramDirection.z());
            TCExplosionUtils.createExplosion(level, creeper, livingentity.getOnPos(), f3 * 2f);
            this.finishRam(level, creeper);
            level.playSound(null, creeper, this.getImpactSound.apply(creeper), SoundSource.NEUTRAL, 1.0F, 1.0F);
        } else if (this.hasRammedHornBreakingBlock(level, creeper)) {
            level.playSound(null, creeper, this.getImpactSound.apply(creeper), SoundSource.NEUTRAL, 1.0F, 1.0F);
            boolean flag = creeper.dropHorn();
            if (flag) {
                level.playSound(null, creeper, this.getHornBreakSound.apply(creeper), SoundSource.NEUTRAL, 1.0F, 1.0F);
            }

            this.finishRam(level, creeper);
        } else {
            Optional<WalkTarget> optional = brain.getMemory(MemoryModuleType.WALK_TARGET);
            Optional<Vec3> optional1 = brain.getMemory(MemoryModuleType.RAM_TARGET);
            boolean flag1 = optional.isEmpty() || optional1.isEmpty() || optional.get().getTarget().currentPosition().closerThan(optional1.get(), 0.25);
            if (flag1) {
                this.finishRam(level, creeper);
            }
        }
    }

    private boolean hasRammedHornBreakingBlock(ServerLevel level, TCGoatCreeper creeper) {
        Vec3 vec3 = creeper.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize();
        BlockPos blockpos = BlockPos.containing(creeper.position().add(vec3));
        return level.getBlockState(blockpos).is(BlockTags.SNAPS_GOAT_HORN) || level.getBlockState(blockpos.above()).is(BlockTags.SNAPS_GOAT_HORN);
    }

    protected void finishRam(ServerLevel level, TCGoatCreeper creeper) {
        level.broadcastEntityEvent(creeper, (byte) 59);
        creeper.getBrain().setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, this.getTimeBetweenRams.apply(creeper).sample(level.random));
        creeper.getBrain().eraseMemory(MemoryModuleType.RAM_TARGET);
    }
}
