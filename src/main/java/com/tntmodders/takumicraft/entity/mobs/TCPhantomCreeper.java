package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.client.renderer.entity.TCPhantomCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class TCPhantomCreeper extends AbstractTCCreeper {
    public static final float FLAP_DEGREES_PER_TICK = 7.448451F;
    public static final int TICKS_PER_FLAP = Mth.ceil(24.166098F);
    private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(TCPhantomCreeper.class, EntityDataSerializers.INT);
    Vec3 moveTargetPoint = Vec3.ZERO;

    BlockPos anchorPoint = BlockPos.ZERO;
    TCPhantomCreeper.AttackPhase attackPhase = TCPhantomCreeper.AttackPhase.CIRCLE;

    private int summonCooldown;

    public TCPhantomCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5;
        this.moveControl = new TCPhantomCreeper.PhantomMoveControl(this);
        this.lookControl = new PhantomLookControl(this);
    }

    @Override
    public boolean causeFallDamage(float p_147105_, float p_147106_, DamageSource p_147107_) {
        return false;
    }

    @Override
    protected void checkFallDamage(double p_20809_, boolean p_20810_, BlockState p_20811_, BlockPos p_20812_) {
    }

    @Override
    public void travel(Vec3 p_20818_) {
        if (this.isInWater()) {
            this.moveRelative(0.02F, p_20818_);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
        } else if (this.isInLava()) {
            this.moveRelative(0.02F, p_20818_);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
        } else {
            BlockPos ground = this.getOnPos().below(1);
            float f = 0.91F;
            if (this.onGround()) {
                f = this.level().getBlockState(ground).getFriction(this.level(), ground, this) * 0.91F;
            }

            float f1 = 0.16277137F / (f * f * f);
            f = 0.91F;
            if (this.onGround()) {
                f = this.level().getBlockState(ground).getFriction(this.level(), ground, this) * 0.91F;
            }

            this.moveRelative(this.onGround() ? 0.1F * f1 : 0.02F, p_20818_);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(f));
        }

        this.calculateEntityAnimation(false);
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    public boolean isFlapping() {
        return (this.getUniqueFlapTickOffset() + this.tickCount) % TICKS_PER_FLAP == 0;
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new TCPhantomCreeper.PhantomBodyRotationControl(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwellGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(1, new TCPhantomCreeper.PhantomAttackStrategyGoal());
        this.goalSelector.addGoal(2, new TCPhantomCreeper.PhantomSweepAttackGoal());
        this.goalSelector.addGoal(3, new TCPhantomCreeper.PhantomCircleAroundAnchorGoal());
        this.targetSelector.addGoal(1, new TCPhantomCreeper.PhantomAttackPlayerTargetGoal());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_331815_) {
        super.defineSynchedData(p_331815_);
        p_331815_.define(ID_SIZE, 0);
    }

    private void updatePhantomSizeInfo() {
        this.refreshDimensions();
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6 + this.getPhantomSize());
    }

    public int getPhantomSize() {
        return this.entityData.get(ID_SIZE);
    }

    public void setPhantomSize(int p_33109_) {
        this.entityData.set(ID_SIZE, Mth.clamp(p_33109_, 0, 64));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33134_) {
        if (ID_SIZE.equals(p_33134_)) {
            this.updatePhantomSizeInfo();
        }

        super.onSyncedDataUpdated(p_33134_);
    }

    public int getUniqueFlapTickOffset() {
        return this.getId() * 3;
    }

    @Override
    public void aiStep() {
        if (this.isAlive() && this.isSunBurnTick()) {
            this.igniteForSeconds(8);
        }

        super.aiStep();
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33126_, DifficultyInstance p_33127_, EntitySpawnReason p_33128_, @Nullable SpawnGroupData p_33129_) {
        this.anchorPoint = this.blockPosition().above(5);
        this.setPhantomSize(0);
        return super.finalizeSpawn(p_33126_, p_33127_, p_33128_, p_33129_);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_33132_) {
        super.readAdditionalSaveData(p_33132_);
        if (p_33132_.contains("AX")) {
            this.anchorPoint = new BlockPos(p_33132_.getInt("AX"), p_33132_.getInt("AY"), p_33132_.getInt("AZ"));
        }

        this.setPhantomSize(p_33132_.getInt("Size"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_33141_) {
        super.addAdditionalSaveData(p_33141_);
        p_33141_.putInt("AX", this.anchorPoint.getX());
        p_33141_.putInt("AY", this.anchorPoint.getY());
        p_33141_.putInt("AZ", this.anchorPoint.getZ());
        p_33141_.putInt("Size", this.getPhantomSize());
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_33107_) {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PHANTOM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33152_) {
        return SoundEvents.PHANTOM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
    }

    @Override
    public boolean canAttackType(EntityType<?> p_33111_) {
        return true;
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose p_333771_) {
        int i = this.getPhantomSize();
        EntityDimensions entitydimensions = super.getDefaultDimensions(p_333771_);
        return entitydimensions.scale(1.0F + 0.15F * (float) i);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.PHANTOM;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getSwellDir() > 0) {
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 5));
        } else if (this.getTarget() != null && this.distanceToSqr(this.getTarget()) < 16) {
            this.setSwellDir(1);
        } else if (this.getTarget() != null && Math.abs(this.getTarget().getX() - this.getX()) + Math.abs(this.getTarget().getZ() - this.getZ()) < 6 && this.getY() > this.getTarget().getY()
                && !this.hasEffect(MobEffects.MOVEMENT_SPEED) && this.level() instanceof ServerLevel) {
            TCZombieCreeper creeper = (TCZombieCreeper) TCEntityCore.ZOMBIE.entityType().create(this.level(), EntitySpawnReason.MOB_SUMMONED);
            creeper.copyPosition(this);
            creeper.finalizeSpawn((ServerLevel) this.level(), this.level().getCurrentDifficultyAt(this.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
            creeper.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.ELYTRA));
            ((ServerLevel) this.level()).addFreshEntityWithPassengers(creeper);
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60));
        }

        if (this.level().isClientSide) {
            float f = Mth.cos((float) (this.getUniqueFlapTickOffset() + this.tickCount) * 7.448451F * ((float) Math.PI / 180F) + (float) Math.PI);
            float f1 = Mth.cos((float) (this.getUniqueFlapTickOffset() + this.tickCount + 1) * 7.448451F * ((float) Math.PI / 180F) + (float) Math.PI);
            if (f > 0.0F && f1 <= 0.0F) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.PHANTOM_FLAP, this.getSoundSource(), 0.95F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
            }

            int i = this.getPhantomSize();
            float f2 = Mth.cos(this.getYRot() * ((float) Math.PI / 180F)) * (1.3F + 0.21F * (float) i);
            float f3 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F)) * (1.3F + 0.21F * (float) i);
            float f4 = (0.3F + f * 0.45F) * ((float) i * 0.2F + 1.0F);
            this.level().addParticle(ParticleTypes.MYCELIUM, this.getX() + (double) f2, this.getY() + (double) f4, this.getZ() + (double) f3, 0.0D, 0.0D, 0.0D);
            this.level().addParticle(ParticleTypes.MYCELIUM, this.getX() - (double) f2, this.getY() + (double) f4, this.getZ() - (double) f3, 0.0D, 0.0D, 0.0D);
        }
    }

    enum AttackPhase {
        CIRCLE,
        SWOOP
    }

    static class PhantomLookControl extends LookControl {
        public PhantomLookControl(Mob p_33235_) {
            super(p_33235_);
        }

        @Override
        public void tick() {
        }
    }

    public static class TCPhantomCreeperContext implements TCCreeperContext<TCPhantomCreeper> {
        private static final String NAME = "phantomcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCPhantomCreeper::new, MobCategory.MONSTER).sized(0.9F, 0.5F).clientTrackingRange(8)
                .build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "ふぁんとむたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "A flying nightmare, shows a phantom of fighter from the air.";
        }

        @Override
        public String getJaJPDesc() {
            return "幻影より生まれし、異なる貌を持つ、空より来る新たなる脅威。";
        }

        @Override
        public String getEnUSName() {
            return "Phantom Creeper";
        }

        @Override
        public String getJaJPName() {
            return "ファントム匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x333366;
        }

        @Override
        public int getSecondaryColor() {
            return 0x336633;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCPhantomCreeper>) type, TCPhantomCreeperRenderer::new);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }
    }

    class PhantomAttackPlayerTargetGoal extends Goal {
        private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0D);
        private int nextScanTick = reducedTickDelay(20);

        @Override
        public boolean canUse() {
            if (this.nextScanTick > 0) {
                --this.nextScanTick;
            } else {
                this.nextScanTick = reducedTickDelay(60);
                List<Entity> list = TCPhantomCreeper.this.level().getEntities(TCPhantomCreeper.this, TCPhantomCreeper.this.getBoundingBox().inflate(16, 64, 16), entity -> entity instanceof Player player && !player.isInvulnerable());
                if (!list.isEmpty()) {
                    list.sort(Comparator.<Entity, Double>comparing(Entity::getY).reversed());

                    for (Entity entity : list) {
                        if (entity instanceof Player player) {
                            if (TCPhantomCreeper.this.canAttack(player)) {
                                TCPhantomCreeper.this.setTarget(player);
                                return true;
                            }
                        }
                    }
                }

            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingentity = TCPhantomCreeper.this.getTarget();
            return livingentity != null && TCPhantomCreeper.this.canAttack(livingentity);
        }
    }

    class PhantomAttackStrategyGoal extends Goal {
        private int nextSweepTick;

        @Override
        public boolean canUse() {
            LivingEntity livingentity = TCPhantomCreeper.this.getTarget();
            return livingentity != null && TCPhantomCreeper.this.canAttack(livingentity);
        }

        @Override
        public void start() {
            this.nextSweepTick = this.adjustedTickDelay(10);
            TCPhantomCreeper.this.attackPhase = TCPhantomCreeper.AttackPhase.CIRCLE;
            this.setAnchorAboveTarget();
        }

        @Override
        public void stop() {
            TCPhantomCreeper.this.anchorPoint = TCPhantomCreeper.this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, TCPhantomCreeper.this.anchorPoint).above(10 + TCPhantomCreeper.this.random.nextInt(20));
        }

        @Override
        public void tick() {
            if (TCPhantomCreeper.this.attackPhase == TCPhantomCreeper.AttackPhase.CIRCLE) {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0) {
                    TCPhantomCreeper.this.attackPhase = TCPhantomCreeper.AttackPhase.SWOOP;
                    this.setAnchorAboveTarget();
                    this.nextSweepTick = this.adjustedTickDelay((8 + TCPhantomCreeper.this.random.nextInt(4)) * 20);
                    TCPhantomCreeper.this.playSound(SoundEvents.PHANTOM_SWOOP, 10.0F, 0.95F + TCPhantomCreeper.this.random.nextFloat() * 0.1F);
                }
            }

        }

        private void setAnchorAboveTarget() {
            TCPhantomCreeper.this.anchorPoint = TCPhantomCreeper.this.getTarget().blockPosition().above(20 + TCPhantomCreeper.this.random.nextInt(20));
            if (TCPhantomCreeper.this.anchorPoint.getY() < TCPhantomCreeper.this.level().getSeaLevel()) {
                TCPhantomCreeper.this.anchorPoint = new BlockPos(TCPhantomCreeper.this.anchorPoint.getX(), TCPhantomCreeper.this.level().getSeaLevel() + 1, TCPhantomCreeper.this.anchorPoint.getZ());
            }

        }
    }

    class PhantomBodyRotationControl extends BodyRotationControl {
        public PhantomBodyRotationControl(Mob p_33216_) {
            super(p_33216_);
        }

        @Override
        public void clientTick() {
            TCPhantomCreeper.this.yHeadRot = TCPhantomCreeper.this.yBodyRot;
            TCPhantomCreeper.this.yBodyRot = TCPhantomCreeper.this.getYRot();
        }
    }

    class PhantomCircleAroundAnchorGoal extends TCPhantomCreeper.PhantomMoveTargetGoal {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        @Override
        public boolean canUse() {
            return TCPhantomCreeper.this.getTarget() == null || TCPhantomCreeper.this.attackPhase == TCPhantomCreeper.AttackPhase.CIRCLE;
        }

        @Override
        public void start() {
            this.distance = 5.0F + TCPhantomCreeper.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + TCPhantomCreeper.this.random.nextFloat() * 9.0F;
            this.clockwise = TCPhantomCreeper.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        @Override
        public void tick() {
            if (TCPhantomCreeper.this.random.nextInt(this.adjustedTickDelay(350)) == 0) {
                this.height = -4.0F + TCPhantomCreeper.this.random.nextFloat() * 9.0F;
            }

            if (TCPhantomCreeper.this.random.nextInt(this.adjustedTickDelay(250)) == 0) {
                ++this.distance;
                if (this.distance > 15.0F) {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (TCPhantomCreeper.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
                this.angle = TCPhantomCreeper.this.random.nextFloat() * 2.0F * (float) Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget()) {
                this.selectNext();
            }

            if (TCPhantomCreeper.this.moveTargetPoint.y < TCPhantomCreeper.this.getY() && !TCPhantomCreeper.this.level().isEmptyBlock(TCPhantomCreeper.this.blockPosition().below(1))) {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (TCPhantomCreeper.this.moveTargetPoint.y > TCPhantomCreeper.this.getY() && !TCPhantomCreeper.this.level().isEmptyBlock(TCPhantomCreeper.this.blockPosition().above(1))) {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }

        }

        private void selectNext() {
            if (BlockPos.ZERO.equals(TCPhantomCreeper.this.anchorPoint)) {
                TCPhantomCreeper.this.anchorPoint = TCPhantomCreeper.this.blockPosition();
            }

            this.angle += this.clockwise * 15.0F * ((float) Math.PI / 180F);
            TCPhantomCreeper.this.moveTargetPoint = Vec3.atLowerCornerOf(TCPhantomCreeper.this.anchorPoint).add(this.distance * Mth.cos(this.angle), -4.0F + this.height, this.distance * Mth.sin(this.angle));
        }
    }

    class PhantomMoveControl extends MoveControl {
        private float speed = 0.1F;

        public PhantomMoveControl(Mob p_33241_) {
            super(p_33241_);
        }

        @Override
        public void tick() {
            if (TCPhantomCreeper.this.horizontalCollision) {
                TCPhantomCreeper.this.setYRot(TCPhantomCreeper.this.getYRot() + 180.0F);
                this.speed = 0.1F;
            }

            double d0 = TCPhantomCreeper.this.moveTargetPoint.x - TCPhantomCreeper.this.getX();
            double d1 = TCPhantomCreeper.this.moveTargetPoint.y - TCPhantomCreeper.this.getY();
            double d2 = TCPhantomCreeper.this.moveTargetPoint.z - TCPhantomCreeper.this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            if (Math.abs(d3) > (double) 1.0E-5F) {
                double d4 = 1.0D - Math.abs(d1 * (double) 0.7F) / d3;
                d0 *= d4;
                d2 *= d4;
                d3 = Math.sqrt(d0 * d0 + d2 * d2);
                double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
                float f = TCPhantomCreeper.this.getYRot();
                float f1 = (float) Mth.atan2(d2, d0);
                float f2 = Mth.wrapDegrees(TCPhantomCreeper.this.getYRot() + 90.0F);
                float f3 = Mth.wrapDegrees(f1 * (180F / (float) Math.PI));
                TCPhantomCreeper.this.setYRot(Mth.approachDegrees(f2, f3, 4.0F) - 90.0F);
                TCPhantomCreeper.this.yBodyRot = TCPhantomCreeper.this.getYRot();
                if (Mth.degreesDifferenceAbs(f, TCPhantomCreeper.this.getYRot()) < 3.0F) {
                    this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
                } else {
                    this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
                }

                float f4 = (float) -(Mth.atan2(-d1, d3) * (double) (180F / (float) Math.PI));
                TCPhantomCreeper.this.setXRot(f4);
                float f5 = TCPhantomCreeper.this.getYRot() + 90.0F;
                double d6 = (double) (this.speed * Mth.cos(f5 * ((float) Math.PI / 180F))) * Math.abs(d0 / d5);
                double d7 = (double) (this.speed * Mth.sin(f5 * ((float) Math.PI / 180F))) * Math.abs(d2 / d5);
                double d8 = (double) (this.speed * Mth.sin(f4 * ((float) Math.PI / 180F))) * Math.abs(d1 / d5);
                Vec3 vec3 = TCPhantomCreeper.this.getDeltaMovement();
                TCPhantomCreeper.this.setDeltaMovement(vec3.add(new Vec3(d6, d8, d7).subtract(vec3).scale(0.2D)));
            }

        }
    }

    abstract class PhantomMoveTargetGoal extends Goal {
        public PhantomMoveTargetGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean touchingTarget() {
            return TCPhantomCreeper.this.moveTargetPoint.distanceToSqr(TCPhantomCreeper.this.getX(), TCPhantomCreeper.this.getY(), TCPhantomCreeper.this.getZ()) < 4.0D;
        }
    }

    class PhantomSweepAttackGoal extends TCPhantomCreeper.PhantomMoveTargetGoal {
        private static final int CAT_SEARCH_TICK_DELAY = 20;
        private boolean isScaredOfCat;
        private int catSearchTick;

        @Override
        public boolean canUse() {
            return TCPhantomCreeper.this.getTarget() != null && TCPhantomCreeper.this.attackPhase == TCPhantomCreeper.AttackPhase.SWOOP;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingentity = TCPhantomCreeper.this.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                if (livingentity instanceof Player player) {
                    if (livingentity.isSpectator() || player.isCreative()) {
                        return false;
                    }
                }

                if (!this.canUse()) {
                    return false;
                } else {
                    if (TCPhantomCreeper.this.tickCount > this.catSearchTick) {
                        this.catSearchTick = TCPhantomCreeper.this.tickCount + 20;
                        List<Cat> list = TCPhantomCreeper.this.level().getEntitiesOfClass(Cat.class, TCPhantomCreeper.this.getBoundingBox().inflate(16.0D), EntitySelector.ENTITY_STILL_ALIVE);

                        for (Cat cat : list) {
                            cat.hiss();
                        }

                        this.isScaredOfCat = !list.isEmpty();
                    }

                    return !this.isScaredOfCat;
                }
            }
        }

        @Override
        public void stop() {
            TCPhantomCreeper.this.setTarget(null);
            TCPhantomCreeper.this.attackPhase = TCPhantomCreeper.AttackPhase.CIRCLE;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = TCPhantomCreeper.this.getTarget();
            if (livingentity != null && level() instanceof ServerLevel serverLevel) {
                TCPhantomCreeper.this.moveTargetPoint = new Vec3(livingentity.getX(), livingentity.getY(0.5D), livingentity.getZ());
                if (TCPhantomCreeper.this.getBoundingBox().inflate(0.2F).intersects(livingentity.getBoundingBox())) {
                    TCPhantomCreeper.this.doHurtTarget(serverLevel, livingentity);
                    TCPhantomCreeper.this.attackPhase = TCPhantomCreeper.AttackPhase.CIRCLE;
                    if (!TCPhantomCreeper.this.isSilent()) {
                        TCPhantomCreeper.this.level().levelEvent(1039, TCPhantomCreeper.this.blockPosition(), 0);
                    }
                } else if (TCPhantomCreeper.this.horizontalCollision || TCPhantomCreeper.this.hurtTime > 0) {
                    TCPhantomCreeper.this.attackPhase = TCPhantomCreeper.AttackPhase.CIRCLE;
                }

            }
        }
    }
}
