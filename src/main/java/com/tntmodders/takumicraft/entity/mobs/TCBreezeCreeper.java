package com.tntmodders.takumicraft.entity.mobs;

import com.mojang.serialization.Dynamic;
import com.tntmodders.takumicraft.client.renderer.entity.TCBreezeCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.ai.breeze.TCBreezeCreeperAi;
import com.tntmodders.takumicraft.entity.projectile.TCBreezeCreeperWindCharge;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityRenderersEvent;

import javax.annotation.Nullable;
import java.util.Optional;

public class TCBreezeCreeper extends AbstractTCCreeper {
    private static final int SLIDE_PARTICLES_AMOUNT = 20;
    private static final int IDLE_PARTICLES_AMOUNT = 1;
    private static final int JUMP_DUST_PARTICLES_AMOUNT = 20;
    private static final int JUMP_TRAIL_PARTICLES_AMOUNT = 3;
    private static final int JUMP_TRAIL_DURATION_TICKS = 5;
    private static final int JUMP_CIRCLE_DISTANCE_Y = 10;
    private static final float FALL_DISTANCE_SOUND_TRIGGER_THRESHOLD = 3.0F;
    private static final int WHIRL_SOUND_FREQUENCY_MIN = 1;
    private static final int WHIRL_SOUND_FREQUENCY_MAX = 80;
    public AnimationState idle = new AnimationState();
    public AnimationState slide = new AnimationState();
    public AnimationState slideBack = new AnimationState();
    public AnimationState longJump = new AnimationState();
    public AnimationState shoot = new AnimationState();
    public AnimationState inhale = new AnimationState();
    private int jumpTrailStartedTick = 0;
    private int soundTick = 0;
    private static final ProjectileDeflection PROJECTILE_DEFLECTION = (p_341445_, p_341446_, p_341447_) -> {
        p_341446_.level().playSound(null, p_341446_, SoundEvents.BREEZE_DEFLECT, p_341446_.getSoundSource(), 1.0F, 1.0F);
        ProjectileDeflection.REVERSE.deflect(p_341445_, p_341446_, p_341447_);
    };

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.63F).add(Attributes.MAX_HEALTH, 30.0).add(Attributes.FOLLOW_RANGE, 24.0).add(Attributes.ATTACK_DAMAGE, 3.0);
    }

    public TCBreezeCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.DANGER_TRAPDOOR, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
        this.xpReward = 20;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.BREEZE;
    }

    @Override
    public void explodeCreeper() {
        if (!this.level().isClientSide()) {
            float f = this.isPowered() ? 2.0F : 1.0F;
            this.dead = true;
            this.level().explode(this, null, AbstractWindCharge.EXPLOSION_DAMAGE_CALCULATOR, this.getX(), this.getY(), this.getZ(), (float) this.explosionRadius * f, false, Level.ExplosionInteraction.TRIGGER, ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.BREEZE_WIND_CHARGE_BURST);
            this.spawnLingeringCloud();

            for (int i = 0; i < (this.isPowered() ? 8 : 4); i++) {
                double d0 = this.getRandomX(10) - this.getX();
                double d2 = this.getRandomZ(10) - this.getZ();
                TCBreezeCreeperWindCharge breezewindcharge = new TCBreezeCreeperWindCharge(this, this.level());
                this.playSound(SoundEvents.BREEZE_SHOOT, 1.5F, 1.0F);
                breezewindcharge.shoot(d0, -this.getRandom().nextDouble() * 0.25, d2, 0.7F, (float) (5 - this.level().getDifficulty().getId() * 4));
                this.level().addFreshEntity(breezewindcharge);
            }

            this.triggerOnDeathMobEffects(Entity.RemovalReason.KILLED);
            this.discard();
        }
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> p_311857_) {
        return TCBreezeCreeperAi.makeBrain(this, this.brainProvider().makeBrain(p_311857_));
    }

    @Override
    public Brain<TCBreezeCreeper> getBrain() {
        return (Brain<TCBreezeCreeper>) super.getBrain();
    }

    @Override
    protected Brain.Provider<TCBreezeCreeper> brainProvider() {
        return Brain.provider(TCBreezeCreeperAi.MEMORY_TYPES, TCBreezeCreeperAi.SENSOR_TYPES);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_309800_) {
        if (this.level().isClientSide() && DATA_POSE.equals(p_309800_)) {
            this.resetAnimations();
            Pose pose = this.getPose();
            switch (pose) {
                case SHOOTING:
                    this.shoot.startIfStopped(this.tickCount);
                    break;
                case INHALING:
                    this.longJump.startIfStopped(this.tickCount);
                    break;
                case SLIDING:
                    this.slide.startIfStopped(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(p_309800_);
    }

    private void resetAnimations() {
        this.shoot.stop();
        this.idle.stop();
        this.inhale.stop();
        this.longJump.stop();
    }

    @Override
    public void tick() {
        Pose pose = this.getPose();
        switch (pose) {
            case SHOOTING:
            case INHALING:
            case STANDING:
                this.resetJumpTrail().emitGroundParticles(1 + this.getRandom().nextInt(1));
                break;
            case SLIDING:
                this.emitGroundParticles(20);
                break;
            case LONG_JUMPING:
                this.emitJumpTrailParticles();
        }

        if (pose != Pose.SLIDING && this.slide.isStarted()) {
            this.slideBack.start(this.tickCount);
            this.slide.stop();
        }

        this.soundTick = this.soundTick == 0 ? this.random.nextIntBetweenInclusive(1, 80) : this.soundTick - 1;
        if (this.soundTick == 0) {
            this.playWhirlSound();
        }

        super.tick();
    }

    public TCBreezeCreeper resetJumpTrail() {
        this.jumpTrailStartedTick = 0;
        return this;
    }

    public void emitJumpTrailParticles() {
        if (++this.jumpTrailStartedTick <= 5) {
            BlockState blockstate = !this.getInBlockState().isAir() ? this.getInBlockState() : this.getBlockStateOn();
            Vec3 vec3 = this.getDeltaMovement();
            Vec3 vec31 = this.position().add(vec3).add(0.0, 0.1F, 0.0);

            for (int i = 0; i < 3; i++) {
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), vec31.x, vec31.y, vec31.z, 0.0, 0.0, 0.0);
            }
        }
    }

    public void emitGroundParticles(int p_310885_) {
        if (!this.isPassenger()) {
            Vec3 vec3 = this.getBoundingBox().getCenter();
            Vec3 vec31 = new Vec3(vec3.x, this.position().y, vec3.z);
            BlockState blockstate = !this.getInBlockState().isAir() ? this.getInBlockState() : this.getBlockStateOn();
            if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                for (int i = 0; i < p_310885_; i++) {
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), vec31.x, vec31.y, vec31.z, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Override
    public void playAmbientSound() {
        if (this.getTarget() == null || !this.onGround()) {
            this.level().playLocalSound(this, this.getAmbientSound(), this.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public void playWhirlSound() {
        float f = 0.7F + 0.4F * this.random.nextFloat();
        float f1 = 0.8F + 0.2F * this.random.nextFloat();
        this.level().playLocalSound(this, SoundEvents.BREEZE_WHIRL, this.getSoundSource(), f1, f);
    }

    @Override
    public ProjectileDeflection deflection(Projectile p_335920_) {
        if (p_335920_.getType() != EntityType.BREEZE_WIND_CHARGE && p_335920_.getType() != EntityType.WIND_CHARGE) {
            return this.getType().is(EntityTypeTags.DEFLECTS_PROJECTILES) ? PROJECTILE_DEFLECTION : ProjectileDeflection.NONE;
        } else {
            return ProjectileDeflection.NONE;
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BREEZE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_311322_) {
        return SoundEvents.BREEZE_HURT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.onGround() ? SoundEvents.BREEZE_IDLE_GROUND : SoundEvents.BREEZE_IDLE_AIR;
    }

    public Optional<LivingEntity> getHurtBy() {
        return this.getBrain().getMemory(MemoryModuleType.HURT_BY).map(DamageSource::getEntity).filter(p_333499_ -> p_333499_ instanceof LivingEntity).map(p_332795_ -> (LivingEntity) p_332795_);
    }

    public boolean withinInnerCircleRange(Vec3 p_311473_) {
        Vec3 vec3 = this.blockPosition().getCenter();
        return p_311473_.closerThan(vec3, 4.0, 10.0);
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("breezeBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().popPush("breezeActivityUpdate");
        TCBreezeCreeperAi.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public boolean canAttackType(EntityType<?> p_310232_) {
        return p_310232_ == EntityType.PLAYER || p_310232_ == EntityType.IRON_GOLEM;
    }

    @Override
    public int getMaxHeadYRot() {
        return 30;
    }

    @Override
    public int getHeadRotSpeed() {
        return 25;
    }

    public double getSnoutYPosition() {
        return this.getEyeY() - 0.4;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource p_309859_) {
        return p_309859_.getEntity() instanceof TCBreezeCreeper || super.isInvulnerableTo(p_309859_);
    }

    @Override
    public double getFluidJumpThreshold() {
        return this.getEyeHeight();
    }

    @Override
    public boolean causeFallDamage(float p_310250_, float p_310041_, DamageSource p_311921_) {
        if (p_310250_ > 3.0F) {
            this.playSound(SoundEvents.BREEZE_LAND, 1.0F, 1.0F);
        }

        return super.causeFallDamage(p_310250_, p_310041_, p_311921_);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    @Nullable
    @Override
    public LivingEntity getTarget() {
        return this.getTargetFromBrain();
    }

    public static class TCBreezeCreeperContext implements TCCreeperContext<TCBreezeCreeper> {
        private static final String NAME = "breezecreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCBreezeCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.77F).eyeHeight(1.3452F).clientTrackingRange(10).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "ぶりーずたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "The farther, the more windcharge. The nearer, the more explosion.";
        }

        @Override
        public String getJaJPDesc() {
            return "近づけば爆発、遠ざかれば風圧。狂飆を漂う匠は、スティーブを嵐中へ誘う。";
        }

        @Override
        public String getEnUSName() {
            return "Breeze Creeper";
        }

        @Override
        public String getJaJPName() {
            return "ブリーズ匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0xaaccff;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }


        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return TCBreezeCreeper.createAttributes();
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCBreezeCreeper>) type, TCBreezeCreeperRenderer::new);
        }

        @Override
        public ItemLike getMainDropItem() {
            return Items.BREEZE_ROD;
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(2f, 8f);
        }

        @Override
        public int getSpawnWeight() {
            return TCCreeperContext.super.getSpawnWeight() / 4;
        }
    }
}
