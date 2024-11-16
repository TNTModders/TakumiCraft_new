package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.client.renderer.entity.TCVexCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityRenderersEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class TCVexCreeper extends AbstractTCCreeper implements TraceableEntity {
    public static final float FLAP_DEGREES_PER_TICK = 45.836624F;
    public static final int TICKS_PER_FLAP = Mth.ceil((float) (Math.PI * 5.0 / 4.0));
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(TCVexCreeper.class, EntityDataSerializers.BYTE);
    private static final int FLAG_IS_CHARGING = 1;
    @Nullable
    Mob owner;
    @Nullable
    private BlockPos boundOrigin;
    private boolean hasLimitedLife;
    private int limitedLifeTicks;

    public TCVexCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new TCVexCreeper.VexMoveControl(this);
        this.xpReward = 3;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.VEX;
    }

    @Override
    public boolean isFlapping() {
        return this.tickCount % TICKS_PER_FLAP == 0;
    }

    @Override
    public void move(MoverType p_33997_, Vec3 p_33998_) {
        super.move(p_33997_, p_33998_);
        //this.checkInsideBlocks();
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
        if (this.hasLimitedLife && --this.limitedLifeTicks <= 0) {
            this.limitedLifeTicks = 20;
            this.hurt(this.damageSources().starve(), 1.0F);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TCVexCreeper.VexChargeAttackGoal());
        this.goalSelector.addGoal(8, new TCVexCreeper.VexRandomMoveGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new TCVexCreeper.VexCopyOwnerTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 14.0).add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_336017_) {
        super.defineSynchedData(p_336017_);
        p_336017_.define(DATA_FLAGS_ID, (byte) 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_34008_) {
        super.readAdditionalSaveData(p_34008_);
        if (p_34008_.contains("BoundX")) {
            this.boundOrigin = new BlockPos(p_34008_.getInt("BoundX"), p_34008_.getInt("BoundY"), p_34008_.getInt("BoundZ"));
        }

        if (p_34008_.contains("LifeTicks")) {
            this.setLimitedLife(p_34008_.getInt("LifeTicks"));
        }
    }

    @Override
    public void restoreFrom(Entity p_309610_) {
        super.restoreFrom(p_309610_);
        if (p_309610_ instanceof TCVexCreeper vex) {
            this.owner = vex.getOwner();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_34015_) {
        super.addAdditionalSaveData(p_34015_);
        if (this.boundOrigin != null) {
            p_34015_.putInt("BoundX", this.boundOrigin.getX());
            p_34015_.putInt("BoundY", this.boundOrigin.getY());
            p_34015_.putInt("BoundZ", this.boundOrigin.getZ());
        }

        if (this.hasLimitedLife) {
            p_34015_.putInt("LifeTicks", this.limitedLifeTicks);
        }
    }

    @Override
    @Nullable
    public Mob getOwner() {
        return this.owner;
    }

    @Nullable
    public BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    public void setBoundOrigin(@Nullable BlockPos p_34034_) {
        this.boundOrigin = p_34034_;
    }

    private boolean getVexFlag(int p_34011_) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & p_34011_) != 0;
    }

    private void setVexFlag(int p_33990_, boolean p_33991_) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (p_33991_) {
            i |= p_33990_;
        } else {
            i &= ~p_33990_;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte) (i & 0xFF));
    }

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setIsCharging(boolean p_34043_) {
        this.setVexFlag(1, p_34043_);
    }

    public void setOwner(Mob p_33995_) {
        this.owner = p_33995_;
    }

    public void setLimitedLife(int p_33988_) {
        this.hasLimitedLife = true;
        this.limitedLifeTicks = p_33988_;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VEX_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VEX_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_34023_) {
        return SoundEvents.VEX_HURT;
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34002_, DifficultyInstance p_34003_, EntitySpawnReason p_34004_, @Nullable SpawnGroupData p_34005_) {
        RandomSource randomsource = p_34002_.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, p_34003_);
        this.populateDefaultEquipmentEnchantments(p_34002_, randomsource, p_34003_);
        return super.finalizeSpawn(p_34002_, p_34003_, p_34004_, p_34005_);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource p_219135_, DifficultyInstance p_219136_) {
        ItemStack stack = p_219135_.nextInt(10) == 0 ? p_219135_.nextInt(10) == 0 ? new ItemStack(Items.NETHERITE_SWORD) : new ItemStack(Items.DIAMOND_SWORD) : new ItemStack(Items.IRON_SWORD);
        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    class VexChargeAttackGoal extends Goal {
        public VexChargeAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = TCVexCreeper.this.getTarget();
            return livingentity != null && livingentity.isAlive() && !TCVexCreeper.this.getMoveControl().hasWanted() && TCVexCreeper.this.random.nextInt(reducedTickDelay(7)) == 0 && TCVexCreeper.this.distanceToSqr(livingentity) > 4.0;
        }

        @Override
        public boolean canContinueToUse() {
            return TCVexCreeper.this.getMoveControl().hasWanted() && TCVexCreeper.this.isCharging() && TCVexCreeper.this.getTarget() != null && TCVexCreeper.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity livingentity = TCVexCreeper.this.getTarget();
            if (livingentity != null) {
                Vec3 vec3 = livingentity.getEyePosition();
                TCVexCreeper.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0);
            }

            TCVexCreeper.this.setIsCharging(true);
            TCVexCreeper.this.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
        }

        @Override
        public void stop() {
            TCVexCreeper.this.setIsCharging(false);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = TCVexCreeper.this.getTarget();
            if (livingentity != null) {
                if (TCVexCreeper.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                    TCVexCreeper.this.doHurtTarget(getServerLevel(TCVexCreeper.this.level()), livingentity);
                    TCVexCreeper.this.setIsCharging(false);
                    TCVexCreeper.this.ignite();
                } else {
                    double d0 = TCVexCreeper.this.distanceToSqr(livingentity);
                    if (d0 < 9.0) {
                        Vec3 vec3 = livingentity.getEyePosition();
                        TCVexCreeper.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0);
                    }
                }
            }
        }
    }

    class VexCopyOwnerTargetGoal extends TargetGoal {
        private final TargetingConditions copyOwnerTargeting = TargetingConditions.forNonCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

        public VexCopyOwnerTargetGoal(final PathfinderMob p_34056_) {
            super(p_34056_, false);
        }

        @Override
        public boolean canUse() {
            return TCVexCreeper.this.owner != null && TCVexCreeper.this.owner.getTarget() != null && this.canAttack(TCVexCreeper.this.owner.getTarget(), this.copyOwnerTargeting);
        }

        @Override
        public void start() {
            TCVexCreeper.this.setTarget(TCVexCreeper.this.owner.getTarget());
            super.start();
        }
    }

    class VexMoveControl extends MoveControl {
        public VexMoveControl(final TCVexCreeper p_34062_) {
            super(p_34062_);
        }

        @Override
        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vec3 = new Vec3(this.wantedX - TCVexCreeper.this.getX(), this.wantedY - TCVexCreeper.this.getY(), this.wantedZ - TCVexCreeper.this.getZ());
                double d0 = vec3.length();
                if (d0 < TCVexCreeper.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    TCVexCreeper.this.setDeltaMovement(TCVexCreeper.this.getDeltaMovement().scale(0.5));
                } else {
                    TCVexCreeper.this.setDeltaMovement(TCVexCreeper.this.getDeltaMovement().add(vec3.scale(this.speedModifier * 0.05 / d0)));
                    if (TCVexCreeper.this.getTarget() == null) {
                        Vec3 vec31 = TCVexCreeper.this.getDeltaMovement();
                        TCVexCreeper.this.setYRot(-((float) Mth.atan2(vec31.x, vec31.z)) * (180.0F / (float) Math.PI));
                        TCVexCreeper.this.yBodyRot = TCVexCreeper.this.getYRot();
                    } else {
                        double d2 = TCVexCreeper.this.getTarget().getX() - TCVexCreeper.this.getX();
                        double d1 = TCVexCreeper.this.getTarget().getZ() - TCVexCreeper.this.getZ();
                        TCVexCreeper.this.setYRot(-((float) Mth.atan2(d2, d1)) * (180.0F / (float) Math.PI));
                        TCVexCreeper.this.yBodyRot = TCVexCreeper.this.getYRot();
                    }
                }
            }
        }
    }

    class VexRandomMoveGoal extends Goal {
        public VexRandomMoveGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !TCVexCreeper.this.getMoveControl().hasWanted() && TCVexCreeper.this.random.nextInt(reducedTickDelay(7)) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void tick() {
            BlockPos blockpos = TCVexCreeper.this.getBoundOrigin();
            if (blockpos == null) {
                blockpos = TCVexCreeper.this.blockPosition();
            }

            for (int i = 0; i < 3; i++) {
                BlockPos blockpos1 = blockpos.offset(
                        TCVexCreeper.this.random.nextInt(15) - 7, TCVexCreeper.this.random.nextInt(11) - 5, TCVexCreeper.this.random.nextInt(15) - 7
                );
                if (TCVexCreeper.this.level().isEmptyBlock(blockpos1)) {
                    TCVexCreeper.this.moveControl
                            .setWantedPosition((double) blockpos1.getX() + 0.5, (double) blockpos1.getY() + 0.5, (double) blockpos1.getZ() + 0.5, 0.25);
                    if (TCVexCreeper.this.getTarget() == null) {
                        TCVexCreeper.this.getLookControl()
                                .setLookAt(
                                        (double) blockpos1.getX() + 0.5, (double) blockpos1.getY() + 0.5, (double) blockpos1.getZ() + 0.5, 180.0F, 20.0F
                                );
                    }
                    break;
                }
            }
        }
    }


    public static class TCVexCreeperContext implements TCCreeperContext<TCVexCreeper> {
        private static final String NAME = "vexcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCVexCreeper::new, MobCategory.MONSTER).fireImmune().sized(0.4F, 0.8F).eyeHeight(0.51875F).passengerAttachments(0.7375F).ridingOffset(0.04F).clientTrackingRange(8).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "ゔぇっくすたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Servant of illager, explosive flies and strong attack.";
        }

        @Override
        public String getJaJPDesc() {
            return "下僕は館で目覚め、羽撃き始める。闇夜風雷、轟く音。";
        }

        @Override
        public String getEnUSName() {
            return "Vex Creeper";
        }

        @Override
        public String getJaJPName() {
            return "ヴェックス匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x336633;
        }

        @Override
        public int getSecondaryColor() {
            return 0x996699;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return TCVexCreeper.createAttributes();
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCVexCreeper>) type, TCVexCreeperRenderer::new);
        }
    }
}
