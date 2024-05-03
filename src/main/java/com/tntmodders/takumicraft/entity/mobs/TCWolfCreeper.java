package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCWolfCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.function.Predicate;

public class TCWolfCreeper extends AbstractTCCreeper {
    public static final Predicate<LivingEntity> PREY_SELECTOR = p_296809_ -> {
        EntityType<?> entitytype = p_296809_.getType();
        return entitytype == EntityType.SHEEP || entitytype == EntityType.RABBIT || entitytype == EntityType.FOX;
    };
    private float interestedAngle;
    private float interestedAngleO;
    private boolean isWet;
    private boolean isShaking;
    private float shakeAnim;
    private float shakeAnimO;

    public TCWolfCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
    }

    public static boolean checkWolfSpawnRules(EntityType<? extends AbstractTCCreeper> p_218292_, LevelAccessor p_218293_, MobSpawnType p_218294_, BlockPos p_218295_, RandomSource p_218296_) {
        return p_218293_.getBlockState(p_218295_.below()).is(BlockTags.WOLVES_SPAWNABLE_ON) && isBrightEnoughToSpawn(p_218293_, p_218295_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TCWolfCreeper.WolfPanicGoal(1.5D));
        this.goalSelector.addGoal(3, new TCWolfCreeper.WolfAvoidEntityGoal<>(this, Llama.class, 24.0F, 1.5D, 1.5D));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Animal.class, 10, false, false, PREY_SELECTOR));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
    }

    @Override
    protected void playStepSound(BlockPos p_30415_, BlockState p_30416_) {
        this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WOLF_GROWL;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_30424_) {
        return SoundEvents.WOLF_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.isWet && !this.isShaking && !this.isPathFinding() && this.onGround()) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
            this.level().broadcastEntityEvent(this, (byte) 8);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()) {
            this.interestedAngleO = this.interestedAngle;
            this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;

            if (this.isInWaterRainOrBubble()) {
                this.isWet = true;
                if (this.isShaking && !this.level().isClientSide) {
                    this.level().broadcastEntityEvent(this, (byte) 56);
                    this.cancelShake();
                }
            } else if ((this.isWet || this.isShaking) && this.isShaking) {
                if (this.shakeAnim == 0.0F) {
                    this.playSound(SoundEvents.WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    this.gameEvent(GameEvent.ENTITY_ACTION);
                }

                this.shakeAnimO = this.shakeAnim;
                this.shakeAnim += 0.05F;
                if (this.shakeAnimO >= 2.0F) {
                    this.isWet = false;
                    this.isShaking = false;
                    this.shakeAnimO = 0.0F;
                    this.shakeAnim = 0.0F;
                }

                if (this.shakeAnim > 0.4F) {
                    float f = (float) this.getY();
                    int i = (int) (Mth.sin((this.shakeAnim - 0.4F) * (float) Math.PI) * 7.0F);
                    Vec3 vec3 = this.getDeltaMovement();

                    for (int j = 0; j < i; ++j) {
                        float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                        float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                        this.level().addParticle(ParticleTypes.SPLASH, this.getX() + (double) f1, f + 0.8F, this.getZ() + (double) f2, vec3.x, vec3.y, vec3.z);
                    }
                }
            }

        }
    }

    private void cancelShake() {
        this.isShaking = false;
        this.shakeAnim = 0.0F;
        this.shakeAnimO = 0.0F;
    }

    @Override
    public void die(DamageSource p_30384_) {
        this.isWet = false;
        this.isShaking = false;
        this.shakeAnimO = 0.0F;
        this.shakeAnim = 0.0F;
        super.die(p_30384_);
    }

    public boolean isWet() {
        return this.isWet;
    }

    public float getWetShade(float p_30447_) {
        return Math.min(0.5F + Mth.lerp(p_30447_, this.shakeAnimO, this.shakeAnim) / 2.0F * 0.5F, 1.0F);
    }

    public float getBodyRollAngle(float p_30433_, float p_30434_) {
        float f = (Mth.lerp(p_30433_, this.shakeAnimO, this.shakeAnim) + p_30434_) / 1.8F;
        if (f < 0.0F) {
            f = 0.0F;
        } else if (f > 1.0F) {
            f = 1.0F;
        }

        return Mth.sin(f * (float) Math.PI) * Mth.sin(f * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
    }

    public float getHeadRollAngle(float p_30449_) {
        return Mth.lerp(p_30449_, this.interestedAngleO, this.interestedAngle) * 0.15F * (float) Math.PI;
    }

    @Override
    public boolean doHurtTarget(Entity p_30372_) {
        boolean flag = p_30372_.hurt(this.damageSources().mobAttack(this), (float) (int) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (flag) {
            this.doEnchantDamageEffects(this, p_30372_);
        }

        return flag;
    }

    @Override
    public void handleEntityEvent(byte p_30379_) {
        if (p_30379_ == 8) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
        } else if (p_30379_ == 56) {
            this.cancelShake();
        } else {
            super.handleEntityEvent(p_30379_);
        }

    }

    public float getTailAngle() {
        return 1.5393804F;
    }

    public boolean isFood(ItemStack p_30440_) {
        return p_30440_.is(ItemTags.WOLF_FOOD);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 8;
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0D, 0.6F * this.getEyeHeight(), this.getBbWidth() * 0.4F);
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().removeIf(entity -> entity instanceof TCWolfCreeper || entity instanceof Wolf);
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
        if (!this.level().isClientSide) {
            int r = this.isPowered() ? 5 : 2;
            for (int t = 0; t < r * 1.5; t++) {
                Wolf wolf = new Wolf(EntityType.WOLF, this.level());
                wolf.copyPosition(this);
                wolf.setTarget(this.getTarget());
                wolf.setRemainingPersistentAngerTime(1000000);
                wolf.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.65);
                this.level().addFreshEntity(wolf);
            }
        }
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.WOLF;
    }

    public static class TCWolfCreeperContext implements TCCreeperContext<TCWolfCreeper> {
        private static final String NAME = "wolfcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCWolfCreeper::new, MobCategory.MONSTER).sized(0.6F, 0.85F).clientTrackingRange(10).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "おおかみたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "A creeper looks like a wolf. Call dependents from the darkness of night.";
        }

        @Override
        public String getJaJPDesc() {
            return "狼型の匠。仲間を呼んで悪いスティーブを倒す。骨をあげても仲良くならない。";
        }

        @Override
        public String getEnUSName() {
            return "TCWolfCreeper Creeper";
        }

        @Override
        public String getJaJPName() {
            return "狼匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x888888;
        }

        @Override
        public int getSecondaryColor() {
            return 0xccffcc;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.MAX_HEALTH, 15.0D).add(Attributes.MOVEMENT_SPEED, 0.3F);
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCWolfCreeper>) type, TCWolfCreeperRenderer::new);
        }

        @Override
        public boolean registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            event.register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TCWolfCreeper::checkWolfSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
            return true;
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
        public int getSpawnWeight() {
            return this.getRank().getSpawnWeight();
        }

        @Override
        public ItemLike getMainDropItem() {
            return Items.BONE;
        }
    }

    class WolfAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
        private final TCWolfCreeper wolf;

        public WolfAvoidEntityGoal(TCWolfCreeper p_30454_, Class<T> p_30455_, float p_30456_, double p_30457_, double p_30458_) {
            super(p_30454_, p_30455_, p_30456_, p_30457_, p_30458_);
            this.wolf = p_30454_;
        }

        @Override
        public boolean canUse() {
            if (super.canUse() && this.toAvoid instanceof Llama) {
                return this.avoidLlama((Llama) this.toAvoid);
            } else {
                return false;
            }
        }

        private boolean avoidLlama(Llama p_30461_) {
            return p_30461_.getStrength() >= TCWolfCreeper.this.random.nextInt(5);
        }

        @Override
        public void start() {
            TCWolfCreeper.this.setTarget(null);
            super.start();
        }

        @Override
        public void tick() {
            TCWolfCreeper.this.setTarget(null);
            super.tick();
        }
    }

    class WolfPanicGoal extends PanicGoal {
        public WolfPanicGoal(double p_203124_) {
            super(TCWolfCreeper.this, p_203124_);
        }

        @Override
        protected boolean shouldPanic() {
            return this.mob.isFreezing() || this.mob.isOnFire();
        }
    }
}
