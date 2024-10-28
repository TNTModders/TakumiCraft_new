package com.tntmodders.takumicraft.entity.mobs;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.tntmodders.takumicraft.client.renderer.entity.TCGoatCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.ai.goat.TCGoatCreeperAi;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

import javax.annotation.Nullable;

public class TCGoatCreeper extends AbstractTCCreeper {

    public TCGoatCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.GOAT;
    }

    public static final EntityDimensions LONG_JUMPING_DIMENSIONS = EntityDimensions.scalable(0.9F, 1.3F).scale(0.7F);
    protected static final ImmutableList<SensorType<? extends Sensor<? super TCGoatCreeper>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.GOAT_TEMPTATIONS);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.BREED_TARGET, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleType.RAM_TARGET, MemoryModuleType.IS_PANICKING);
    public static final int GOAT_FALL_DAMAGE_REDUCTION = 10;
    public static final double GOAT_SCREAMING_CHANCE = 0.02;
    public static final double UNIHORN_CHANCE = 0.1F;
    private static final EntityDataAccessor<Boolean> DATA_IS_SCREAMING_GOAT = SynchedEntityData.defineId(TCGoatCreeper.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_LEFT_HORN = SynchedEntityData.defineId(TCGoatCreeper.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_RIGHT_HORN = SynchedEntityData.defineId(TCGoatCreeper.class, EntityDataSerializers.BOOLEAN);
    private boolean isLoweringHead;
    private int lowerHeadTick;

    public ItemStack createHorn() {
        RandomSource randomsource = RandomSource.create(this.getUUID().hashCode());
        TagKey<Instrument> tagkey = this.isScreamingGoat() ? InstrumentTags.SCREAMING_GOAT_HORNS : InstrumentTags.REGULAR_GOAT_HORNS;
        HolderSet<Instrument> holderset = BuiltInRegistries.INSTRUMENT.getOrCreateTag(tagkey);
        return InstrumentItem.create(Items.GOAT_HORN, holderset.getRandomElement(randomsource).get());
    }

    @Override
    protected Brain.Provider<TCGoatCreeper> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> p_149371_) {
        return TCGoatCreeperAi.makeBrain(this.brainProvider().makeBrain(p_149371_));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.4F).add(Attributes.ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0, 1.2));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0, 1.2));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    protected int calculateFallDamage(float p_149389_, float p_149390_) {
        return super.calculateFallDamage(p_149389_, p_149390_) - 10;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_AMBIENT : SoundEvents.GOAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_149387_) {
        return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_HURT : SoundEvents.GOAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_DEATH : SoundEvents.GOAT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos p_149382_, BlockState p_149383_) {
        this.playSound(SoundEvents.GOAT_STEP, 0.15F, 1.0F);
    }

    protected SoundEvent getMilkingSound() {
        return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_MILK : SoundEvents.GOAT_MILK;
    }

    @Override
    public Brain<TCGoatCreeper> getBrain() {
        return (Brain<TCGoatCreeper>) super.getBrain();
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("goatBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("goatActivityUpdate");
        TCGoatCreeperAi.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public int getMaxHeadYRot() {
        return 15;
    }

    @Override
    public void setYHeadRot(float p_149400_) {
        int i = this.getMaxHeadYRot();
        float f = Mth.degreesDifference(this.yBodyRot, p_149400_);
        float f1 = Mth.clamp(f, (float) -i, (float) i);
        super.setYHeadRot(this.yBodyRot + f1);
    }

    @Override
    public SoundEvent getEatingSound(ItemStack p_149394_) {
        return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_EAT : SoundEvents.GOAT_EAT;
    }

    @Override
    public InteractionResult mobInteract(Player p_149379_, InteractionHand p_149380_) {
        ItemStack itemstack = p_149379_.getItemInHand(p_149380_);
        if (itemstack.is(Items.BUCKET)) {
            p_149379_.playSound(this.getMilkingSound(), 1.0F, 1.0F);
            ItemStack itemstack1 = ItemUtils.createFilledResult(itemstack, p_149379_, Items.MILK_BUCKET.getDefaultInstance());
            p_149379_.setItemInHand(p_149380_, itemstack1);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(p_149379_, p_149380_);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_149365_, DifficultyInstance p_149366_, EntitySpawnReason p_149367_, @Nullable SpawnGroupData p_149368_) {
        RandomSource randomsource = p_149365_.getRandom();
        TCGoatCreeperAi.initMemories(this, randomsource);
        this.setScreamingGoat(randomsource.nextDouble() < 0.02);
        if ((double) randomsource.nextFloat() < 0.1F) {
            EntityDataAccessor<Boolean> entitydataaccessor = randomsource.nextBoolean() ? DATA_HAS_LEFT_HORN : DATA_HAS_RIGHT_HORN;
            this.entityData.set(entitydataaccessor, false);
        }

        return super.finalizeSpawn(p_149365_, p_149366_, p_149367_, p_149368_);
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose p_335397_) {
        return p_335397_ == Pose.LONG_JUMPING ? LONG_JUMPING_DIMENSIONS.scale(this.getAgeScale()) : super.getDefaultDimensions(p_335397_);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_149385_) {
        super.addAdditionalSaveData(p_149385_);
        p_149385_.putBoolean("IsScreamingGoat", this.isScreamingGoat());
        p_149385_.putBoolean("HasLeftHorn", this.hasLeftHorn());
        p_149385_.putBoolean("HasRightHorn", this.hasRightHorn());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_149373_) {
        super.readAdditionalSaveData(p_149373_);
        this.setScreamingGoat(p_149373_.getBoolean("IsScreamingGoat"));
        this.entityData.set(DATA_HAS_LEFT_HORN, p_149373_.getBoolean("HasLeftHorn"));
        this.entityData.set(DATA_HAS_RIGHT_HORN, p_149373_.getBoolean("HasRightHorn"));
    }

    @Override
    public void handleEntityEvent(byte p_149356_) {
        if (p_149356_ == 58) {
            this.isLoweringHead = true;
        } else if (p_149356_ == 59) {
            this.isLoweringHead = false;
        } else {
            super.handleEntityEvent(p_149356_);
        }
    }

    @Override
    public void aiStep() {
        if (this.isLoweringHead) {
            this.lowerHeadTick++;
        } else {
            this.lowerHeadTick -= 2;
        }

        this.lowerHeadTick = Mth.clamp(this.lowerHeadTick, 0, 20);
        super.aiStep();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_336110_) {
        super.defineSynchedData(p_336110_);
        p_336110_.define(DATA_IS_SCREAMING_GOAT, false);
        p_336110_.define(DATA_HAS_LEFT_HORN, true);
        p_336110_.define(DATA_HAS_RIGHT_HORN, true);
    }

    public boolean hasLeftHorn() {
        return this.entityData.get(DATA_HAS_LEFT_HORN);
    }

    public boolean hasRightHorn() {
        return this.entityData.get(DATA_HAS_RIGHT_HORN);
    }

    public boolean dropHorn() {
        boolean flag = this.hasLeftHorn();
        boolean flag1 = this.hasRightHorn();
        if (!flag && !flag1) {
            this.ignite();
            return false;
        } else {
            EntityDataAccessor<Boolean> entitydataaccessor;
            if (!flag) {
                entitydataaccessor = DATA_HAS_RIGHT_HORN;
            } else if (!flag1) {
                entitydataaccessor = DATA_HAS_LEFT_HORN;
            } else {
                entitydataaccessor = this.random.nextBoolean() ? DATA_HAS_LEFT_HORN : DATA_HAS_RIGHT_HORN;
            }

            this.entityData.set(entitydataaccessor, false);
            Vec3 vec3 = this.position();
            ItemStack itemstack = this.createHorn();
            double d0 = Mth.randomBetween(this.random, -0.2F, 0.2F);
            double d1 = Mth.randomBetween(this.random, 0.3F, 0.7F);
            double d2 = Mth.randomBetween(this.random, -0.2F, 0.2F);
            ItemEntity itementity = new ItemEntity(this.level(), vec3.x(), vec3.y(), vec3.z(), itemstack, d0, d1, d2);
            this.level().addFreshEntity(itementity);
            return true;
        }
    }

    public void addHorns() {
        this.entityData.set(DATA_HAS_LEFT_HORN, true);
        this.entityData.set(DATA_HAS_RIGHT_HORN, true);
    }

    public void removeHorns() {
        this.entityData.set(DATA_HAS_LEFT_HORN, false);
        this.entityData.set(DATA_HAS_RIGHT_HORN, false);
    }

    public boolean isScreamingGoat() {
        return this.entityData.get(DATA_IS_SCREAMING_GOAT);
    }

    public void setScreamingGoat(boolean p_149406_) {
        this.entityData.set(DATA_IS_SCREAMING_GOAT, p_149406_);
    }

    public float getRammingXHeadRot() {
        return (float) this.lowerHeadTick / 20.0F * 30.0F * (float) (Math.PI / 180.0);
    }

    public static boolean checkGoatSpawnRules(EntityType<AbstractTCCreeper> p_218753_, LevelAccessor p_218754_, EntitySpawnReason p_218755_, BlockPos p_218756_, RandomSource p_218757_) {
        return p_218754_.getBlockState(p_218756_.below()).is(BlockTags.GOATS_SPAWNABLE_ON) && isBrightEnoughToSpawn(p_218754_, p_218756_);
    }

    public static class TCGoatCreeperContext implements TCCreeperContext<TCGoatCreeper> {
        private static final String NAME = "goatcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCGoatCreeper::new, MobCategory.MONSTER).sized(0.9F, 1.3F).passengerAttachments(1.1125F).clientTrackingRange(10).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "やぎたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "You are the GOAT!";
        }

        @Override
        public String getJaJPDesc() {
            return "史上最も偉大なる、爆ぜる角を持つ山羊の主。";
        }

        @Override
        public String getEnUSName() {
            return "Goat Creeper";
        }

        @Override
        public String getJaJPName() {
            return "山羊匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x226633;
        }

        @Override
        public int getSecondaryColor() {
            return 0x889988;
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
        public boolean registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            event.register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TCGoatCreeper::checkGoatSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
            return true;
        }

        @Override
        public void registerModifierSpawn(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (biome.is(BiomeTags.IS_MOUNTAIN)) {
                TCCreeperContext.super.registerModifierSpawn(biome, builder);
            }
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCGoatCreeper>) type, TCGoatCreeperRenderer::new);
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return TCGoatCreeper.createAttributes();
        }
    }
}
