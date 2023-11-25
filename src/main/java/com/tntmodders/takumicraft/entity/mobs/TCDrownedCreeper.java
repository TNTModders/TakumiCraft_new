package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCDrownedCreeperRenderer;
import com.tntmodders.takumicraft.entity.ai.TCZombieCreeperAttackGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class TCDrownedCreeper extends TCZombieCreeper implements RangedAttackMob {
    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;
    boolean searchingForLand;

    public TCDrownedCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1.0f);
        this.moveControl = new TCDrownedCreeper.DrownedMoveControl(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.waterNavigation = new WaterBoundPathNavigation(this, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
    }

    public static boolean checkDrownedSpawnRules(EntityType<? extends AbstractTCCreeper> p_32350_, ServerLevelAccessor p_32351_, MobSpawnType p_32352_, BlockPos p_32353_, RandomSource p_32354_) {
        if (!p_32351_.getFluidState(p_32353_.below()).is(FluidTags.WATER)) {
            return false;
        } else {
            Holder<Biome> holder = p_32351_.getBiome(p_32353_);
            boolean flag = p_32351_.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(p_32351_, p_32353_, p_32354_) && (p_32352_ == MobSpawnType.SPAWNER || p_32351_.getFluidState(p_32353_).is(FluidTags.WATER));
            if (!holder.is(Biomes.RIVER) && !holder.is(Biomes.FROZEN_RIVER)) {
                return p_32354_.nextInt(40) == 0 && isDeepEnoughToSpawn(p_32351_, p_32353_) && flag;
            } else {
                return p_32354_.nextInt(15) == 0 && flag;
            }
        }
    }

    private static boolean isDeepEnoughToSpawn(LevelAccessor p_32367_, BlockPos p_32368_) {
        return p_32368_.getY() < p_32367_.getSeaLevel() - 5;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return new TCDrownedCreeperContext();
    }

    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(1, new SwellGoal(this));
        this.goalSelector.addGoal(1, new TCDrownedCreeper.DrownedGoToWaterGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new TCDrownedCreeper.DrownedTridentAttackGoal(this, 1.0D, 40, 10.0F));
        this.goalSelector.addGoal(2, new TCDrownedCreeper.DrownedAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new TCDrownedCreeper.DrownedGoToBeachGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new TCDrownedCreeper.DrownedSwimUpGoal(this, 1.0D, this.level().getSeaLevel()));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, TCDrownedCreeper.class).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::okTarget));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Axolotl.class, true, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_32372_, DifficultyInstance p_32373_, MobSpawnType p_32374_, @Nullable SpawnGroupData p_32375_, @Nullable CompoundTag p_32376_) {
        p_32375_ = super.finalizeSpawn(p_32372_, p_32373_, p_32374_, p_32375_, p_32376_);
        if (this.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty() && this.random.nextFloat() < 0.03F) {
            this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
            this.handDropChances[EquipmentSlot.OFFHAND.getIndex()] = 2.0F;
        }

        return p_32375_;
    }

    @Override
    protected boolean supportsBreakDoorGoal() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInWater() ? SoundEvents.DROWNED_AMBIENT_WATER : SoundEvents.DROWNED_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_32386_) {
        return this.isInWater() ? SoundEvents.DROWNED_HURT_WATER : SoundEvents.DROWNED_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isInWater() ? SoundEvents.DROWNED_DEATH_WATER : SoundEvents.DROWNED_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.DROWNED_STEP;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.DROWNED_SWIM;
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource source, DifficultyInstance p_32348_) {
        if ((double) this.random.nextFloat() > 0.9D) {
            int i = this.random.nextInt(16);
            if (i < 10) {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
            }
        }

    }

    @Override
    protected boolean canReplaceCurrentItem(ItemStack p_32364_, ItemStack p_32365_) {
        if (p_32365_.is(Items.NAUTILUS_SHELL)) {
            return false;
        } else if (p_32365_.is(Items.TRIDENT)) {
            if (p_32364_.is(Items.TRIDENT)) {
                return p_32364_.getDamageValue() < p_32365_.getDamageValue();
            } else {
                return false;
            }
        } else {
            return p_32364_.is(Items.TRIDENT) || super.canReplaceCurrentItem(p_32364_, p_32365_);
        }
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader p_32370_) {
        return p_32370_.isUnobstructed(this);
    }

    public boolean okTarget(@Nullable LivingEntity p_32396_) {
        if (p_32396_ != null) {
            return !this.level().isDay() || p_32396_.isInWater();
        } else {
            return false;
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.isSwimming();
    }

    boolean wantsToSwim() {
        if (this.searchingForLand) {
            return true;
        } else {
            LivingEntity livingentity = this.getTarget();
            return livingentity != null && livingentity.isInWater();
        }
    }

    @Override
    public void travel(Vec3 p_32394_) {
        if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
            this.moveRelative(0.01F, p_32394_);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(p_32394_);
        }

    }

    @Override
    public void updateSwimming() {
        if (!this.level().isClientSide) {
            if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }

    }

    protected boolean closeToNextPos() {
        Path path = this.getNavigation().getPath();
        if (path != null) {
            BlockPos blockpos = path.getTarget();
            if (blockpos != null) {
                double d0 = this.distanceToSqr(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                return d0 < 4.0D;
            }
        }

        return false;
    }

    @Override
    public void performRangedAttack(LivingEntity p_32356_, float p_32357_) {
        ThrownTrident throwntrident = new ThrownTrident(this.level(), this, new ItemStack(Items.TRIDENT));
        double d0 = p_32356_.getX() - this.getX();
        double d1 = p_32356_.getY(0.3333333333333333D) - throwntrident.getY();
        double d2 = p_32356_.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        throwntrident.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(throwntrident);
    }

    public void setSearchingForLand(boolean p_32399_) {
        this.searchingForLand = p_32399_;
    }

    static class DrownedAttackGoal extends TCZombieCreeperAttackGoal {
        private final TCDrownedCreeper drowned;

        public DrownedAttackGoal(TCDrownedCreeper p_32402_, double p_32403_, boolean p_32404_) {
            super(p_32402_, p_32403_, p_32404_);
            this.drowned = p_32402_;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && drowned.okTarget(drowned.getTarget());
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && drowned.okTarget(drowned.getTarget());
        }
    }

    static class DrownedGoToBeachGoal extends MoveToBlockGoal {
        private final TCDrownedCreeper drowned;

        public DrownedGoToBeachGoal(TCDrownedCreeper p_32409_, double p_32410_) {
            super(p_32409_, p_32410_, 8, 2);
            this.drowned = p_32409_;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !drowned.level().isDay() && drowned.isInWater() && drowned.getY() >= (double) (drowned.level().getSeaLevel() - 3);
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse();
        }

        @Override
        protected boolean isValidTarget(LevelReader p_32413_, BlockPos p_32414_) {
            BlockPos blockpos = p_32414_.above();
            return p_32413_.isEmptyBlock(blockpos) && p_32413_.isEmptyBlock(blockpos.above()) && p_32413_.getBlockState(p_32414_).entityCanStandOn(p_32413_, p_32414_, this.drowned);
        }

        @Override
        public void start() {
            drowned.setSearchingForLand(false);
            drowned.navigation = drowned.groundNavigation;
            super.start();
        }

        @Override
        public void stop() {
            super.stop();
        }
    }

    static class DrownedGoToWaterGoal extends Goal {
        private final PathfinderMob mob;
        private final double speedModifier;
        private final Level level;
        private double wantedX;
        private double wantedY;
        private double wantedZ;

        public DrownedGoToWaterGoal(PathfinderMob p_32425_, double p_32426_) {
            this.mob = p_32425_;
            this.speedModifier = p_32426_;
            level = p_32425_.level();
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.level.isDay()) {
                return false;
            } else if (this.mob.isInWater()) {
                return false;
            } else {
                Vec3 vec3 = this.getWaterPos();
                if (vec3 == null) {
                    return false;
                } else {
                    this.wantedX = vec3.x;
                    this.wantedY = vec3.y;
                    this.wantedZ = vec3.z;
                    return true;
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return !this.mob.getNavigation().isDone();
        }

        @Override
        public void start() {
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }

        @Nullable
        private Vec3 getWaterPos() {
            RandomSource random = this.mob.getRandom();
            BlockPos blockpos = this.mob.blockPosition();

            for (int i = 0; i < 10; ++i) {
                BlockPos blockpos1 = blockpos.offset(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
                if (this.level.getBlockState(blockpos1).is(Blocks.WATER)) {
                    return Vec3.atBottomCenterOf(blockpos1);
                }
            }

            return null;
        }
    }

    static class DrownedMoveControl extends MoveControl {
        private final TCDrownedCreeper drowned;

        public DrownedMoveControl(TCDrownedCreeper creeper) {
            super(creeper);
            this.drowned = creeper;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = drowned.getTarget();
            if (drowned.wantsToSwim() && drowned.isInWater()) {
                if (livingentity != null && livingentity.getY() > drowned.getY() || drowned.searchingForLand) {
                    drowned.setDeltaMovement(drowned.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                }

                if (this.operation != MoveControl.Operation.MOVE_TO || drowned.getNavigation().isDone()) {
                    drowned.setSpeed(0.0F);
                    return;
                }

                double d0 = this.wantedX - drowned.getX();
                double d1 = this.wantedY - drowned.getY();
                double d2 = this.wantedZ - drowned.getZ();
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                d1 /= d3;
                float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                drowned.setYRot(this.rotlerp(drowned.getYRot(), f, 90.0F));
                drowned.yBodyRot = drowned.getYRot();
                float f1 = (float) (this.speedModifier * drowned.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float f2 = Mth.lerp(0.125F, drowned.getSpeed(), f1);
                drowned.setSpeed(f2);
                drowned.setDeltaMovement(drowned.getDeltaMovement().add((double) f2 * d0 * 0.005D, (double) f2 * d1 * 0.1D, (double) f2 * d2 * 0.005D));
            } else {
                if (!drowned.onGround()) {
                    drowned.setDeltaMovement(drowned.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }

                super.tick();
            }

        }
    }

    static class DrownedSwimUpGoal extends Goal {
        private final TCDrownedCreeper drowned;
        private final double speedModifier;
        private final int seaLevel;
        private boolean stuck;

        public DrownedSwimUpGoal(TCDrownedCreeper p_32440_, double p_32441_, int p_32442_) {
            this.drowned = p_32440_;
            this.speedModifier = p_32441_;
            this.seaLevel = p_32442_;
        }

        @Override
        public boolean canUse() {
            return !drowned.level().isDay() && drowned.isInWater() && drowned.getY() < (double) (this.seaLevel - 2);
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() && !this.stuck;
        }

        @Override
        public void tick() {
            if (drowned.getY() < (double) (this.seaLevel - 1) && (drowned.getNavigation().isDone() || drowned.closeToNextPos())) {
                Vec3 vec3 = DefaultRandomPos.getPosTowards(this.drowned, 4, 8, new Vec3(drowned.getX(), this.seaLevel - 1, drowned.getZ()), (float) Math.PI / 2F);
                if (vec3 == null) {
                    this.stuck = true;
                    return;
                }

                drowned.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, this.speedModifier);
            }

        }

        @Override
        public void start() {
            drowned.setSearchingForLand(true);
            this.stuck = false;
        }

        @Override
        public void stop() {
            drowned.setSearchingForLand(false);
        }
    }

    static class DrownedTridentAttackGoal extends RangedAttackGoal {
        private final TCDrownedCreeper drowned;

        public DrownedTridentAttackGoal(RangedAttackMob p_32450_, double p_32451_, int p_32452_, float p_32453_) {
            super(p_32450_, p_32451_, p_32452_, p_32453_);
            this.drowned = (TCDrownedCreeper) p_32450_;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && drowned.getMainHandItem().is(Items.TRIDENT);
        }

        @Override
        public void start() {
            super.start();
            drowned.setAggressive(true);
            drowned.startUsingItem(InteractionHand.MAIN_HAND);
        }

        @Override
        public void stop() {
            super.stop();
            drowned.stopUsingItem();
            drowned.setAggressive(false);
        }
    }


    public static class TCDrownedCreeperContext implements TCCreeperContext<TCDrownedCreeper> {
        private static final String NAME = "drownedcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCDrownedCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "どらうんどたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Danger from the sea, deeper than the cave.";
        }

        @Override
        public String getJaJPDesc() {
            return "深き海より来る匠、三叉の矛を奪いて地に降りる。";
        }

        @Override
        public String getEnUSName() {
            return "Drowned Creeper";
        }

        @Override
        public String getJaJPName() {
            return "ドラウンド匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 39168;
        }

        @Override
        public int getSecondaryColor() {
            return 0x000044;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 35.0D).add(Attributes.MOVEMENT_SPEED, 0.23F).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.ARMOR, 2.0D).add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCDrownedCreeper>) type, TCDrownedCreeperRenderer::new);
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
        public void registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            SpawnPlacements.register(type, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TCDrownedCreeper::checkDrownedSpawnRules);
        }
    }
}
