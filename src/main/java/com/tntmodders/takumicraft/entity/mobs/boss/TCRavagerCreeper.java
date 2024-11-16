package com.tntmodders.takumicraft.entity.mobs.boss;

import com.tntmodders.takumicraft.client.renderer.entity.TCRavagerCreeperRenderer;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class TCRavagerCreeper extends AbstractTCBossCreeper {

    protected final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(false).setCreateWorldFog(false).setPlayBossMusic(false);

    private static final Predicate<Entity> NO_RAVAGER_AND_ALIVE = p_33346_ -> p_33346_.isAlive() && !(p_33346_ instanceof TCRavagerCreeper);
    private static final double BASE_MOVEMENT_SPEED = 0.5;
    private static final double ATTACK_MOVEMENT_SPEED = BASE_MOVEMENT_SPEED * 1.2;
    private static final int STUNNED_COLOR = 8356754;
    private static final float STUNNED_COLOR_BLUE = 0.57254905F;
    private static final float STUNNED_COLOR_GREEN = 0.5137255F;
    private static final float STUNNED_COLOR_RED = 0.49803922F;
    private static final int ATTACK_DURATION = 10;
    public static final int STUN_DURATION = 40;
    private int attackTick;
    private int stunnedTick;
    private int roarTick;

    public TCRavagerCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 50;
        this.explosionRadius = 10;
        this.setPathfindingMalus(PathType.LEAVES, 0.0F);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.RAVAGER;
    }

    @Override
    public ServerBossEvent getBossEvent() {
        return this.bossEvent;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.4));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, (p_199899_, p_364954_) -> !p_199899_.isBaby()));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    protected void updateControlFlags() {
        boolean flag = !(this.getControllingPassenger() instanceof Mob) || this.getControllingPassenger().getType().is(EntityTypeTags.RAIDERS);
        boolean flag1 = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, flag);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, flag && flag1);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, flag);
        this.goalSelector.setControlFlag(Goal.Flag.TARGET, flag);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.MOVEMENT_SPEED, BASE_MOVEMENT_SPEED)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_33353_) {
        super.addAdditionalSaveData(p_33353_);
        p_33353_.putInt("AttackTick", this.attackTick);
        p_33353_.putInt("StunTick", this.stunnedTick);
        p_33353_.putInt("RoarTick", this.roarTick);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_33344_) {
        super.readAdditionalSaveData(p_33344_);
        this.attackTick = p_33344_.getInt("AttackTick");
        this.stunnedTick = p_33344_.getInt("StunTick");
        this.roarTick = p_33344_.getInt("RoarTick");
    }

    @Override
    public int getMaxHeadYRot() {
        return 45;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getHealth() < this.getMaxHealth() / 10) {
            this.ignite();
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isAlive()) {
            if (this.isImmobile()) {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
            } else {
                double d0 = this.getTarget() != null ? ATTACK_MOVEMENT_SPEED : BASE_MOVEMENT_SPEED;
                double d1 = this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(Mth.lerp(0.1, d1, d0));
            }

            if (this.level() instanceof ServerLevel serverLevel && this.getTarget() != null && this.horizontalCollision && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(serverLevel, this)) {
                boolean flag = false;
                AABB aabb = this.getBoundingBox().inflate(0.2);

                for (BlockPos blockpos : BlockPos.betweenClosed(
                        Mth.floor(aabb.minX),
                        Mth.floor(aabb.minY),
                        Mth.floor(aabb.minZ),
                        Mth.floor(aabb.maxX),
                        Mth.floor(aabb.maxY),
                        Mth.floor(aabb.maxZ)
                )) {
                    BlockState blockstate = this.level().getBlockState(blockpos);
                    Block block = blockstate.getBlock();
                    if (!blockstate.is(TCBlockCore.ANTI_EXPLOSION) && blockstate.getExplosionResistance(this.level(), blockpos, null) < 5f) {
                        boolean destFlag = this.level().destroyBlock(blockpos, true, this);
                        if (destFlag) {
                            TCExplosionUtils.createExplosion(serverLevel, this, blockpos, 0.5f);
                            if (this.getRandom().nextInt(5) == 0) {
                                this.hurt(this.damageSources().explosion(this, this), (float) (this.getRandom().nextGaussian() * 5));
                            }
                        }
                        flag = destFlag || flag;

                    }
                }

                if (!flag && this.onGround()) {
                    this.jumpFromGround();
                }
            }

            if (this.roarTick > 0) {
                this.roarTick--;
                if (this.roarTick == 10) {
                    this.roar();
                }
            }

            if (this.attackTick > 0) {
                this.attackTick--;
            }

            if (this.stunnedTick > 0) {
                this.stunnedTick--;
                this.stunEffect();
                if (this.stunnedTick == 0) {
                    this.playSound(SoundEvents.RAVAGER_ROAR, 1.0F, 1.0F);
                    this.roarTick = 20;
                }
            }
        }
    }

    private void stunEffect() {
        if (this.random.nextInt(6) == 0) {
            double d0 = this.getX()
                    - (double) this.getBbWidth() * Math.sin(this.yBodyRot * (float) (Math.PI / 180.0))
                    + (this.random.nextDouble() * 0.6 - 0.3);
            double d1 = this.getY() + (double) this.getBbHeight() - 0.3;
            double d2 = this.getZ()
                    + (double) this.getBbWidth() * Math.cos(this.yBodyRot * (float) (Math.PI / 180.0))
                    + (this.random.nextDouble() * 0.6 - 0.3);
            this.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.49803922F, 0.5137255F, 0.57254905F), d0, d1, d2, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.attackTick > 0 || this.stunnedTick > 0 || this.roarTick > 0;
    }

    @Override
    public boolean hasLineOfSight(Entity p_149755_) {
        return this.stunnedTick <= 0 && this.roarTick <= 0 && super.hasLineOfSight(p_149755_);
    }

    @Override
    protected void blockedByShield(LivingEntity p_33361_) {
        if (this.roarTick == 0) {
            if (this.random.nextDouble() < 0.5) {
                this.stunnedTick = 40;
                this.playSound(SoundEvents.RAVAGER_STUNNED, 1.0F, 1.0F);
                this.level().broadcastEntityEvent(this, (byte) 39);
                p_33361_.push(this);
            } else {
                this.strongKnockback(p_33361_);
            }

            p_33361_.hurtMarked = true;
        }
    }

    private void roar() {
        if (this.isAlive()) {
            for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0), NO_RAVAGER_AND_ALIVE)) {
                if (!(livingentity instanceof AbstractIllager)) {
                    livingentity.hurt(this.damageSources().mobAttack(this), 6.0F);
                    TCExplosionUtils.createExplosion(this.level(), this, livingentity.getX(), livingentity.getY(), livingentity.getZ(), this.isPowered() ? 5 : 3, false);
                }

                this.strongKnockback(livingentity);
            }

            Vec3 vec3 = this.getBoundingBox().getCenter();

            for (int i = 0; i < 40; i++) {
                double d0 = this.random.nextGaussian() * 0.2;
                double d1 = this.random.nextGaussian() * 0.2;
                double d2 = this.random.nextGaussian() * 0.2;
                this.level().addParticle(ParticleTypes.POOF, vec3.x, vec3.y, vec3.z, d0, d1, d2);
            }

            this.gameEvent(GameEvent.ENTITY_ACTION);
        }
    }

    private void strongKnockback(Entity p_33340_) {
        double d0 = p_33340_.getX() - this.getX();
        double d1 = p_33340_.getZ() - this.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
        p_33340_.push(d0 / d2 * 4.0, 0.2, d1 / d2 * 4.0);
    }

    @Override
    public void handleEntityEvent(byte p_33335_) {
        if (p_33335_ == 4) {
            this.attackTick = 10;
            this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
        } else if (p_33335_ == 39) {
            this.stunnedTick = 40;
        }

        super.handleEntityEvent(p_33335_);
    }

    public int getAttackTick() {
        return this.attackTick;
    }

    public int getStunnedTick() {
        return this.stunnedTick;
    }

    public int getRoarTick() {
        return this.roarTick;
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity target) {
        this.attackTick = 10;
        TCExplosionUtils.createExplosion(this.level(), this, target.getX(), target.getY(), target.getZ(), this.isPowered() ? 5 : 3, false);
        if (this.isPowered() && target.isAlive() && this.level() instanceof ServerLevel level) {
            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            bolt.moveTo(target.position());
            bolt.setVisualOnly(true);
            bolt.setDamage(0f);
            level.addFreshEntity(bolt);
            target.thunderHit(level, bolt);
            target.extinguishFire();
        }
        this.level().broadcastEntityEvent(this, (byte) 4);
        this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
        return super.doHurtTarget(serverLevel, target);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33359_) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos p_33350_, BlockState p_33351_) {
        this.playSound(SoundEvents.RAVAGER_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader p_33342_) {
        return !p_33342_.containsAnyLiquid(this.getBoundingBox());
    }

    @Override
    protected AABB getAttackBoundingBox() {
        AABB aabb = super.getAttackBoundingBox();
        return aabb.deflate(0.05, 0.0, 0.05);
    }

    @Override
    public float getBlockExplosionResistance(Explosion p_19992_, BlockGetter p_19993_, BlockPos p_19994_, BlockState p_19995_, FluidState p_19996_, float p_19997_) {
        return super.getBlockExplosionResistance(p_19992_, p_19993_, p_19994_, p_19995_, p_19996_, p_19997_) / 10f;
    }

    @Override
    public void onExplosionHit(@org.jetbrains.annotations.Nullable Entity p_331940_) {
        this.stunEffect();
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor levelAccessor, EntitySpawnReason spawnType) {
        return super.checkSpawnRules(levelAccessor, spawnType) && levelAccessor.getRandom().nextInt(20) == 0;
    }

    public static class TCRavagerCreeperContext implements TCCreeperContext<TCRavagerCreeper> {
        private static final String NAME = "ravagercreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCRavagerCreeper::new, MobCategory.MONSTER).sized(1.95F, 2.2F).passengerAttachments(new Vec3(0.0, 2.2625, -0.0625)).clientTrackingRange(10).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "らゔぇじゃーたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Rage, savage, destructive beast here.";
        }

        @Override
        public String getJaJPDesc() {
            return "憤怒を、野性を、破壊を解き放つ魔獣が暴れだす。";
        }

        @Override
        public String getEnUSName() {
            return "Ravager Creeper";
        }

        @Override
        public String getJaJPName() {
            return "ラヴェジャー匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0x88ff00;
        }

        @Override
        public int getPrimaryColor() {
            return 0x888888;
        }

        @Override
        public boolean alterSpawn() {
            return true;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return TCRavagerCreeper.createAttributes();
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCRavagerCreeper>) type, TCRavagerCreeperRenderer::new);
        }

        @Override
        public int getMaxSpawn() {
            return 1;
        }

        @Override
        public int getSpawnWeight() {
            return TCCreeperContext.super.getSpawnWeight() / 10;
        }
    }
}
