package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.client.renderer.entity.TCBeeCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class TCBeeCreeper extends AbstractTCCreeper implements FlyingAnimal {

    public static final float FLAP_DEGREES_PER_TICK = 120.32113F;
    public static final int TICKS_PER_FLAP = Mth.ceil(1.4959966F);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(TCBeeCreeper.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(TCBeeCreeper.class, EntityDataSerializers.INT);
    private static final int FLAG_ROLL = 2;
    private static final int STING_DEATH_COUNTDOWN = 1200;
    private static final int TICKS_BEFORE_GOING_TO_KNOWN_FLOWER = 2400;
    private static final int TICKS_WITHOUT_NECTAR_BEFORE_GOING_HOME = 3600;
    private static final int MIN_ATTACK_DIST = 4;
    private static final int MAX_CROPS_GROWABLE = 10;
    private static final int POISON_SECONDS_NORMAL = 10;
    private static final int POISON_SECONDS_HARD = 18;
    private static final int TOO_FAR_DISTANCE = 32;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    @Nullable
    private UUID persistentAngerTarget;
    private float rollAmount;
    private float rollAmountO;
    private int timeSinceSting;
    private static final int COOLDOWN_BEFORE_LOCATING_NEW_HIVE = 200;
    private static final int COOLDOWN_BEFORE_LOCATING_NEW_FLOWER = 200;
    private int underWaterTicks;

    public TCBeeCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(PathType.COCOA, -1.0F);
        this.setPathfindingMalus(PathType.FENCE, -1.0F);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.BEE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_335977_) {
        super.defineSynchedData(p_335977_);
        p_335977_.define(DATA_FLAGS_ID, (byte) 0);
        p_335977_.define(DATA_REMAINING_ANGER_TIME, 0);
    }

    @Override
    public float getWalkTargetValue(BlockPos p_27788_, LevelReader p_27789_) {
        return p_27789_.getBlockState(p_27788_).isAir() ? 10.0F : 0.0F;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BeeAttackGoal(this, 1.4F, true));
        this.goalSelector.addGoal(1, new SwellGoal(this));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, p_328635_ -> p_328635_.is(ItemTags.BEE_FOOD), false));
        this.goalSelector.addGoal(8, new TCBeeCreeper.BeeWanderGoal());
        this.goalSelector.addGoal(9, new FloatGoal(this));
        this.targetSelector.addGoal(1, new BeeHurtByOtherGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(Entity p_27722_) {
        DamageSource damagesource = this.damageSources().sting(this);
        boolean flag = p_27722_.hurt(damagesource, (float) (int) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (flag) {
            if (this.level() instanceof ServerLevel serverlevel) {
                EnchantmentHelper.doPostAttackEffects(serverlevel, p_27722_, damagesource);
            }

            if (p_27722_ instanceof LivingEntity livingentity) {
                livingentity.setStingerCount(livingentity.getStingerCount() + 1);
                int i = 0;
                if (this.level().getDifficulty() == Difficulty.NORMAL) {
                    i = 10;
                } else if (this.level().getDifficulty() == Difficulty.HARD) {
                    i = 18;
                }

                if (i > 0) {
                    if (livingentity.getStingerCount() > 1 && livingentity.hasEffect(MobEffects.POISON)) {
                        this.ignite();
                    }
                    livingentity.addEffect(new MobEffectInstance(MobEffects.POISON, i * 20, 2), this);
                }
            }
            this.playSound(SoundEvents.BEE_STING, 1.0F, 1.0F);
        }

        return flag;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.random.nextFloat() < 0.05F) {
            for (int i = 0; i < this.random.nextInt(2) + 1; i++) {
                this.spawnFluidParticle(this.level(), this.getX() - 0.3F, this.getX() + 0.3F, this.getZ() - 0.3F, this.getZ() + 0.3F, this.getY(0.5), ParticleTypes.FALLING_NECTAR);
            }
        }

        this.updateRollAmount();
    }

    private void spawnFluidParticle(Level p_27780_, double p_27781_, double p_27782_, double p_27783_, double p_27784_, double p_27785_, ParticleOptions p_27786_) {
        p_27780_.addParticle(p_27786_, Mth.lerp(p_27780_.random.nextDouble(), p_27781_, p_27782_), p_27785_, Mth.lerp(p_27780_.random.nextDouble(), p_27783_, p_27784_), 0.0, 0.0, 0.0);
    }

    void pathfindRandomlyTowards(BlockPos p_27881_) {
        Vec3 vec3 = Vec3.atBottomCenterOf(p_27881_);
        int i = 0;
        BlockPos blockpos = this.blockPosition();
        int j = (int) vec3.y - blockpos.getY();
        if (j > 2) {
            i = 4;
        } else if (j < -2) {
            i = -4;
        }

        int k = 6;
        int l = 8;
        int i1 = blockpos.distManhattan(p_27881_);
        if (i1 < 15) {
            k = i1 / 2;
            l = i1 / 2;
        }

        Vec3 vec31 = AirRandomPos.getPosTowards(this, k, l, i, vec3, (float) (Math.PI / 10));
        if (vec31 != null) {
            this.navigation.setMaxVisitedNodesMultiplier(0.5F);
            this.navigation.moveTo(vec31.x, vec31.y, vec31.z, 1.0);
        }
    }

    public float getRollAmount(float p_27936_) {
        return Mth.lerp(p_27936_, this.rollAmountO, this.rollAmount);
    }

    private void updateRollAmount() {
        this.rollAmountO = this.rollAmount;
        if (this.isRolling()) {
            this.rollAmount = Math.min(1.0F, this.rollAmount + 0.2F);
        } else {
            this.rollAmount = Math.max(0.0F, this.rollAmount - 0.24F);
        }
    }

    @Override
    protected void customServerAiStep() {
        if (this.isInWaterOrBubble()) {
            this.underWaterTicks++;
        } else {
            this.underWaterTicks = 0;
        }

        if (this.underWaterTicks > 20) {
            this.hurt(this.damageSources().drown(), 1.0F);
        }
    }


    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    public void setRemainingPersistentAngerTime(int p_27795_) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, p_27795_);
    }

    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    public void setPersistentAngerTarget(@Nullable UUID p_27791_) {
        this.persistentAngerTarget = p_27791_;
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    private boolean doesHiveHaveSpace(BlockPos p_27885_) {
        BlockEntity blockentity = this.level().getBlockEntity(p_27885_);
        return blockentity instanceof BeehiveBlockEntity && !((BeehiveBlockEntity) blockentity).isFull();
    }

    @VisibleForDebug
    public GoalSelector getGoalSelector() {
        return this.goalSelector;
    }

    private boolean isRolling() {
        return this.getFlag(2);
    }

    private void setRolling(boolean p_27930_) {
        this.setFlag(2, p_27930_);
    }

    boolean isTooFarAway(BlockPos p_27890_) {
        return !this.closerThan(p_27890_, 32);
    }

    private void setFlag(int p_27833_, boolean p_27834_) {
        if (p_27834_) {
            this.entityData.set(DATA_FLAGS_ID, (byte) (this.entityData.get(DATA_FLAGS_ID) | p_27833_));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte) (this.entityData.get(DATA_FLAGS_ID) & ~p_27833_));
        }
    }

    private boolean getFlag(int p_27922_) {
        return (this.entityData.get(DATA_FLAGS_ID) & p_27922_) != 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.FLYING_SPEED, 0.6F).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.ATTACK_DAMAGE, 2.0).add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected PathNavigation createNavigation(Level p_27815_) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, p_27815_) {
            @Override
            public boolean isStableDestination(BlockPos p_27947_) {
                return !this.level.getBlockState(p_27947_.below()).isAir();
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(false);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    boolean isFlowerValid(BlockPos p_27897_) {
        return this.level().isLoaded(p_27897_) && this.level().getBlockState(p_27897_).is(BlockTags.FLOWERS);
    }

    @Override
    protected void playStepSound(BlockPos p_27820_, BlockState p_27821_) {
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_27845_) {
        return SoundEvents.BEE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BEE_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected void checkFallDamage(double p_27754_, boolean p_27755_, BlockState p_27756_, BlockPos p_27757_) {
    }

    @Override
    public boolean isFlapping() {
        return this.isFlying() && this.tickCount % TICKS_PER_FLAP == 0;
    }

    @Override
    public boolean isFlying() {
        return !this.onGround();
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.5F * this.getEyeHeight(), this.getBbWidth() * 0.2F);
    }

    @Override
    protected void jumpInLiquid(TagKey<Fluid> p_204061_) {
        this.jumpInLiquidInternal();
    }

    private void jumpInLiquidInternal() {
        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.01D, 0.0D));
    }

    @Override
    public void jumpInFluid(net.minecraftforge.fluids.FluidType type) {
        this.jumpInLiquidInternal();
    }

    boolean closerThan(BlockPos p_27817_, int p_27818_) {
        return p_27817_.closerThan(this.blockPosition(), p_27818_);
    }

    static class BeeAttackGoal extends MeleeAttackGoal {
        BeeAttackGoal(final PathfinderMob p_27960_, final double p_27961_, final boolean p_27962_) {
            super(p_27960_, p_27961_, p_27962_);
        }

    }

    static class BeeHurtByOtherGoal extends HurtByTargetGoal {
        BeeHurtByOtherGoal(final TCBeeCreeper p_28033_) {
            super(p_28033_);
        }

        @Override
        protected void alertOther(Mob p_28035_, LivingEntity p_28036_) {
            if (p_28035_ instanceof TCBeeCreeper && this.mob.hasLineOfSight(p_28036_)) {
                p_28035_.setTarget(p_28036_);
            }
        }
    }

    class BeeWanderGoal extends Goal {
        private static final int WANDER_THRESHOLD = 22;

        BeeWanderGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return TCBeeCreeper.this.navigation.isDone() && TCBeeCreeper.this.random.nextInt(10) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return TCBeeCreeper.this.navigation.isInProgress();
        }

        @Override
        public void start() {
            Vec3 vec3 = this.findPos();
            if (vec3 != null) {
                TCBeeCreeper.this.navigation.moveTo(TCBeeCreeper.this.navigation.createPath(BlockPos.containing(vec3), 1), 1.0);
            }
        }

        @Nullable
        private Vec3 findPos() {
            Vec3 vec3 = TCBeeCreeper.this.getViewVector(0.0F);

            int i = 8;
            Vec3 vec32 = HoverRandomPos.getPos(TCBeeCreeper.this, 8, 7, vec3.x, vec3.z, (float) (Math.PI / 2), 3, 1);
            return vec32 != null ? vec32 : AirAndWaterRandomPos.getPos(TCBeeCreeper.this, 8, 4, -2, vec3.x, vec3.z, (float) (Math.PI / 2));
        }
    }

    public static class TCBeeCreeperContext implements TCCreeperContext<TCBeeCreeper> {
        private static final String NAME = "beecreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCBeeCreeper::new, MobCategory.MONSTER).sized(0.7F, 0.6F).eyeHeight(0.3F).clientTrackingRange(8).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "はちたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Bee careful, the poison bees sting twice.";
        }

        @Override
        public String getJaJPDesc() {
            return "気をつけよ、爆ぜるこの蜂は二度刺す。";
        }

        @Override
        public String getEnUSName() {
            return "Bee Creeper";
        }

        @Override
        public String getJaJPName() {
            return "蜂匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0xffff00;
        }

        @Override
        public int getSecondaryColor() {
            return 0x88ff88;
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
        public ItemLike getMainDropItem() {
            return Items.HONEY_BLOCK;
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCBeeCreeper>) type, TCBeeCreeperRenderer::new);
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return TCBeeCreeper.createAttributes();
        }

        @Override
        public int getSpawnWeight() {
            return TCCreeperContext.super.getSpawnWeight() / 50;
        }

        @Override
        public int getMaxSpawn() {
            return (int) (TCCreeperContext.super.getMaxSpawn() / 2.5);
        }

        @Override
        public boolean registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            event.register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AbstractTCCreeper::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
            return true;
        }
    }
}
