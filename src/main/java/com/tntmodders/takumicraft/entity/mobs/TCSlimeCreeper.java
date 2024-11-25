package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.client.renderer.entity.TCSlimeCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Function;
import java.util.stream.Stream;

public class TCSlimeCreeper extends AbstractTCCreeper {
    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 127;
    private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(TCSlimeCreeper.class, EntityDataSerializers.INT);
    public float targetSquish;
    public float squish;
    public float oSquish;
    private boolean wasOnGround;

    public TCSlimeCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 1;
        this.moveControl = new TCSlimeCreeper.SlimeMoveControl(this);
    }

    public static boolean checkSlimeSpawnRules(EntityType<AbstractTCCreeper> p_33621_, LevelAccessor p_33622_, EntitySpawnReason p_33623_, BlockPos p_33624_, RandomSource p_33625_) {
        if (p_33622_.getDifficulty() != Difficulty.PEACEFUL) {
            if (p_33622_.getBiome(p_33624_).is(Biomes.SWAMP) && p_33624_.getY() > 50 && p_33624_.getY() < 70 && p_33625_.nextFloat() < 0.5F && p_33625_.nextFloat() < p_33622_.getMoonBrightness() && p_33622_.getMaxLocalRawBrightness(p_33624_) <= p_33625_.nextInt(8)) {
                return checkMobSpawnRules(p_33621_, p_33622_, p_33623_, p_33624_, p_33625_);
            }

            if (!(p_33622_ instanceof WorldGenLevel)) {
                return false;
            }

            ChunkPos chunkpos = new ChunkPos(p_33624_);
            boolean flag = WorldgenRandom.seedSlimeChunk(chunkpos.x, chunkpos.z, ((WorldGenLevel) p_33622_).getSeed(), 987234911L).nextInt(10) == 0;
            if (p_33625_.nextInt(10) == 0 && flag && p_33624_.getY() < 40) {
                return checkMobSpawnRules(p_33621_, p_33622_, p_33623_, p_33624_, p_33625_);
            }
        }

        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new SwellGoal(this));
        this.goalSelector.addGoal(1, new TCSlimeCreeper.SlimeFloatGoal(this));
        this.goalSelector.addGoal(2, new TCSlimeCreeper.SlimeAttackGoal(this));
        this.goalSelector.addGoal(3, new TCSlimeCreeper.SlimeRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new TCSlimeCreeper.SlimeKeepOnJumpingGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                (entity, level) -> Math.abs(entity.getY() - this.getY()) <= 4.0D));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_330760_) {
        super.defineSynchedData(p_330760_);
        p_330760_.define(ID_SIZE, 1);
    }

    protected void setSize(int p_33594_, boolean p_33595_) {
        int i = Mth.clamp(p_33594_, 1, 127);
        this.entityData.set(ID_SIZE, i);
        this.reapplyPosition();
        this.refreshDimensions();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(i * i);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2F + 0.1F * (float) i);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(i);
        if (p_33595_) {
            this.setHealth(this.getMaxHealth());
        }

        this.xpReward = i;
    }

    public int getSize() {
        return this.entityData.get(ID_SIZE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_33619_) {
        super.addAdditionalSaveData(p_33619_);
        p_33619_.putInt("Size", this.getSize() - 1);
        p_33619_.putBoolean("wasOnGround", this.wasOnGround);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_33607_) {
        this.setSize(p_33607_.getInt("Size") + 1, false);
        super.readAdditionalSaveData(p_33607_);
        this.wasOnGround = p_33607_.getBoolean("wasOnGround");
    }

    public boolean isTiny() {
        return this.getSize() <= 1;
    }

    protected ParticleOptions getParticleType() {
        return ParticleTypes.ITEM_SLIME;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return this.getSize() > 0;
    }

    @Override
    public void die(DamageSource source) {
        if (!source.is(DamageTypes.PLAYER_ATTACK)) {
            this.explodeCreeper();
        }
        super.die(source);
    }

    @Override
    public void tick() {
        this.squish += (this.targetSquish - this.squish) * 0.5F;
        this.oSquish = this.squish;
        super.tick();
        if (this.onGround() && !this.wasOnGround) {
            int i = this.getSize();

            if (spawnCustomParticles()) i = 0;
            for (int j = 0; j < i * 8; ++j) {
                float f = this.random.nextFloat() * ((float) Math.PI * 2F);
                float f1 = this.random.nextFloat() * 0.5F + 0.5F;
                float f2 = Mth.sin(f) * (float) i * 0.5F * f1;
                float f3 = Mth.cos(f) * (float) i * 0.5F * f1;
                this.level().addParticle(this.getParticleType(), this.getX() + (double) f2, this.getY(), this.getZ() + (double) f3, 0.0D, 0.0D, 0.0D);
            }

            this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.targetSquish = -0.5F;
        } else if (!this.onGround() && this.wasOnGround) {
            this.targetSquish = 1.0F;
        }

        this.wasOnGround = this.onGround();
        this.decreaseSquish();
    }

    protected void decreaseSquish() {
        this.targetSquish *= 0.6F;
    }

    protected int getJumpDelay() {
        return this.random.nextInt(20) + 10;
    }

    @Override
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33609_) {
        if (ID_SIZE.equals(p_33609_)) {
            this.refreshDimensions();
            this.setYRot(this.yHeadRot);
            this.yBodyRot = this.yHeadRot;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.doWaterSplashEffect();
            }
        }

        super.onSyncedDataUpdated(p_33609_);
    }

    @Override
    public EntityType<? extends TCSlimeCreeper> getType() {
        return (EntityType<? extends TCSlimeCreeper>) super.getType();
    }

    @Override
    public void remove(Entity.RemovalReason p_149847_) {
        int i = this.getSize();
        if (!this.level().isClientSide && i > 1 && this.isDeadOrDying()) {
            Component component = this.getCustomName();
            boolean flag = this.isNoAi();
            float f = (float) i / 4.0F;
            int j = i / 2;
            int k = 2 + this.random.nextInt(3);

            for (int l = 0; l < k; ++l) {
                float f1 = ((float) (l % 2) - 0.5F) * f;
                float f2 = ((float) (l / 2) - 0.5F) * f;
                TCSlimeCreeper slime = this.getType().create(this.level(), EntitySpawnReason.TRIGGERED);
                if (slime != null) {
                    if (this.isPersistenceRequired()) {
                        slime.setPersistenceRequired();
                    }

                    slime.setCustomName(component);
                    slime.setNoAi(flag);
                    slime.setInvulnerable(this.isInvulnerable());
                    slime.setSize(j, true);
                    slime.moveTo(this.getX() + (double) f1, this.getY() + 0.5D, this.getZ() + (double) f2, this.random.nextFloat() * 360.0F, 0.0F);
                    this.level().addFreshEntity(slime);
                }
            }
        }

        super.remove(p_149847_);
    }

    private void spawnChildren(int i, boolean flg, int hp) {
        if (!this.level().isClientSide && i > 1 && flg) {
            Component component = this.getCustomName();
            boolean flag = this.isNoAi();
            float f = (float) i / 4.0F;
            int j = i / 2;
            int k = 2 + this.random.nextInt(3);

            for (int l = 0; l < k; ++l) {
                float f1 = ((float) (l % 2) - 0.5F) * f;
                float f2 = ((float) (l / 2) - 0.5F) * f;
                TCSlimeCreeper slime = this.getType().create(this.level(), EntitySpawnReason.TRIGGERED);
                if (this.isPersistenceRequired()) {
                    slime.setPersistenceRequired();
                }

                slime.setCustomName(component);
                slime.setNoAi(flag);
                slime.setInvulnerable(this.isInvulnerable());
                slime.setSize(j, true);
                slime.moveTo(this.getX() + (double) f1, this.getY() + 1.5D, this.getZ() + (double) f2, this.random.nextFloat() * 360.0F, 0.0F);
                this.level().addFreshEntity(slime);
                if (hp > 0) {
                    slime.setHealth(hp);
                }
            }
        }
    }

    @Override
    public void push(Entity p_33636_) {
        super.push(p_33636_);
        if (p_33636_ instanceof IronGolem && this.isDealsDamage()) {
            this.dealDamage((LivingEntity) p_33636_);
        }
    }

    @Override
    public void playerTouch(Player p_33611_) {
        if (this.isDealsDamage()) {
            this.dealDamage(p_33611_);
        }
    }

    protected void dealDamage(LivingEntity p_33638_) {
        if (this.isAlive() && this.level() instanceof ServerLevel serverLevel) {
            int i = this.getSize();
            if (this.distanceToSqr(p_33638_) < 0.6D * (double) i * 0.6D * (double) i && this.hasLineOfSight(p_33638_) && p_33638_.hurtServer(serverLevel, serverLevel.damageSources().mobAttack(this), this.getAttackDamage())) {
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                if (this.level() instanceof ServerLevel serverlevel) {
                    EnchantmentHelper.doPostAttackEffects(serverlevel, p_33638_, this.damageSources().mobAttack(this));
                }
            }
        }
    }

    protected boolean isDealsDamage() {
        return !this.isTiny() && this.isEffectiveAi();
    }

    protected float getAttackDamage() {
        return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33631_) {
        return this.isTiny() ? SoundEvents.SLIME_HURT_SMALL : SoundEvents.SLIME_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isTiny() ? SoundEvents.SLIME_DEATH_SMALL : SoundEvents.SLIME_DEATH;
    }

    protected SoundEvent getSquishSound() {
        return this.isTiny() ? SoundEvents.SLIME_SQUISH_SMALL : SoundEvents.SLIME_SQUISH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F * (float) this.getSize();
    }

    @Override
    public int getMaxHeadXRot() {
        return 0;
    }

    protected boolean doPlayJumpSound() {
        return this.getSize() > 0;
    }

    @Override
    public void jumpFromGround() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, this.getJumpPower(), vec3.z);
        this.hasImpulse = true;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33601_, DifficultyInstance p_33602_, EntitySpawnReason p_33603_, @Nullable SpawnGroupData p_33604_) {
        int i = this.random.nextInt(4);
        if (i < 2 && this.random.nextFloat() < 0.5F * p_33602_.getSpecialMultiplier()) {
            ++i;
        }

        int j = 1 << i;
        this.setSize(j, true);
        return super.finalizeSpawn(p_33601_, p_33602_, p_33603_, p_33604_);
    }

    float getSoundPitch() {
        float f = this.isTiny() ? 1.4F : 0.8F;
        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
    }

    protected SoundEvent getJumpSound() {
        return this.isTiny() ? SoundEvents.SLIME_JUMP_SMALL : SoundEvents.SLIME_JUMP;
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose p_334284_) {
        return super.getDefaultDimensions(p_334284_).scale(0.255F * (float) this.getSize());
    }

    protected boolean spawnCustomParticles() {
        return false;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return source.is(DamageTypes.EXPLOSION) && !(source.getEntity() instanceof TCSlimeCreeper) || super.hurtServer(level, source,
                damage);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.SLIME;
    }

    @Override
    public void explodeCreeper() {
        if (!this.level().isClientSide) {
            float f = this.isPowered() ? 2.0F : 1.0F;
            this.dead = true;
            TCExplosionUtils.createExplosion(this.level(), this, this.getX(), this.getY(), this.getZ(),
                    (float) this.explosionRadius * f * (Math.min(this.getSize(), 5) + 0.5f), false);
            int i = this.getSize();
            this.spawnChildren(i, true, 1);
            this.discard();
            this.spawnLingeringCloud();
        }
    }

    static class SlimeAttackGoal extends Goal {
        private final TCSlimeCreeper slime;
        private int growTiredTimer;

        public SlimeAttackGoal(TCSlimeCreeper p_33648_) {
            this.slime = p_33648_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity == null) {
                return false;
            } else {
                return this.slime.canAttack(livingentity) && this.slime.getMoveControl() instanceof SlimeMoveControl;
            }
        }

        @Override
        public void start() {
            this.growTiredTimer = reducedTickDelay(300);
            super.start();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!this.slime.canAttack(livingentity)) {
                return false;
            } else {
                return --this.growTiredTimer > 0;
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity != null) {
                this.slime.lookAt(livingentity, 10.0F, 10.0F);
            }

            ((TCSlimeCreeper.SlimeMoveControl) this.slime.getMoveControl()).setDirection(this.slime.getYRot(), this.slime.isDealsDamage());
        }
    }

    static class SlimeFloatGoal extends Goal {
        private final TCSlimeCreeper slime;

        public SlimeFloatGoal(TCSlimeCreeper p_33655_) {
            this.slime = p_33655_;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
            p_33655_.getNavigation().setCanFloat(true);
        }

        @Override
        public boolean canUse() {
            return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof TCSlimeCreeper.SlimeMoveControl;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.slime.getRandom().nextFloat() < 0.8F) {
                this.slime.getJumpControl().jump();
            }

            ((TCSlimeCreeper.SlimeMoveControl) this.slime.getMoveControl()).setWantedMovement(1.2D);
        }
    }

    static class SlimeKeepOnJumpingGoal extends Goal {
        private final TCSlimeCreeper slime;

        public SlimeKeepOnJumpingGoal(TCSlimeCreeper p_33660_) {
            this.slime = p_33660_;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !this.slime.isPassenger();
        }

        @Override
        public void tick() {
            ((TCSlimeCreeper.SlimeMoveControl) this.slime.getMoveControl()).setWantedMovement(1.0D);
        }
    }

    static class SlimeMoveControl extends MoveControl {
        private final TCSlimeCreeper slime;
        private float yRot;
        private int jumpDelay;
        private boolean isAggressive;

        public SlimeMoveControl(TCSlimeCreeper p_33668_) {
            super(p_33668_);
            this.slime = p_33668_;
            this.yRot = 180.0F * p_33668_.getYRot() / (float) Math.PI;
        }

        public void setDirection(float p_33673_, boolean p_33674_) {
            this.yRot = p_33673_;
            this.isAggressive = p_33674_;
        }

        public void setWantedMovement(double p_33671_) {
            this.speedModifier = p_33671_;
            this.operation = MoveControl.Operation.MOVE_TO;
        }

        @Override
        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = MoveControl.Operation.WAIT;
                if (this.mob.onGround()) {
                    this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.slime.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }

                        this.slime.getJumpControl().jump();
                        if (this.slime.doPlayJumpSound()) {
                            this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
                        }
                    } else {
                        this.slime.xxa = 0.0F;
                        this.slime.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }

            }
        }
    }

    static class SlimeRandomDirectionGoal extends Goal {
        private final TCSlimeCreeper slime;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public SlimeRandomDirectionGoal(TCSlimeCreeper p_33679_) {
            this.slime = p_33679_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.slime.getTarget() == null && (this.slime.onGround() || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION)) && this.slime.getMoveControl() instanceof TCSlimeCreeper.SlimeMoveControl;
        }

        @Override
        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = this.adjustedTickDelay(40 + this.slime.getRandom().nextInt(60));
                this.chosenDegrees = (float) this.slime.getRandom().nextInt(360);
            }

            ((TCSlimeCreeper.SlimeMoveControl) this.slime.getMoveControl()).setDirection(this.chosenDegrees, false);
        }
    }

    public static class TCSlimeCreeperContext implements TCCreeperContext<TCSlimeCreeper> {
        private static final String NAME = "slimecreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCSlimeCreeper::new, MobCategory.MONSTER).sized(2.04f, 2.04f).clientTrackingRange(10)
                .build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "すらいむたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "It is called 'CreeperClusterBomb', consecutive, large slimy explosion.";
        }

        @Override
        public String getJaJPDesc() {
            return "爆発性スライム。死ぬと爆発、膨らみ爆発、人呼んで「匠式クラスター爆弾」。";
        }

        @Override
        public String getEnUSName() {
            return "Slime Creeper";
        }

        @Override
        public String getJaJPName() {
            return "スライム匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x338833;
        }

        @Override
        public int getSecondaryColor() {
            return 0x55aa55;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCSlimeCreeper>) type, TCSlimeCreeperRenderer::new);
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
        public boolean registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            event.register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TCSlimeCreeper::checkSlimeSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
            return true;
        }

        @Nullable
        @Override
        public Function<HolderLookup.Provider, LootTableSubProvider> getCreeperLoot(EntityType<?> type) {
            return new Function<>() {
                @Override
                public LootTableSubProvider apply(HolderLookup.Provider provider) {
                    return new EntityLootSubProvider(FeatureFlags.REGISTRY.allFlags(), provider) {
                        @Override
                        public Stream<EntityType<?>> getKnownEntityTypes() {
                            return Stream.of(type);
                        }

                        @Override
                        public void generate() {
                            LootTable.Builder builder = LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                    .add(LootItem.lootTableItem(Items.SLIME_BALL).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0.0F, 1.0F)))));
                            this.add(CREEPER, TCSlimeCreeperContext.this.additionalBuilder(provider, builder));
                        }
                    };
                }
            };
        }
    }
}