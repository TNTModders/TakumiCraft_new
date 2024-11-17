package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.client.renderer.entity.TCBatCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

import javax.annotation.Nullable;
import java.time.LocalDate;

public class TCBatCreeper extends AbstractTCCreeper {
    public static final float FLAP_LENGTH_SECONDS = 0.5F;
    public static final float TICKS_PER_FLAP = 10.0F;
    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(TCBatCreeper.class, EntityDataSerializers.BYTE);
    private static final int FLAG_RESTING = 1;
    private static final TargetingConditions BAT_RESTING_TARGETING = TargetingConditions.forNonCombat().range(4.0);
    public final AnimationState flyAnimationState = new AnimationState();
    public final AnimationState restAnimationState = new AnimationState();
    @Nullable
    private BlockPos targetPosition;

    public TCBatCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        if (!level.isClientSide) {
            this.setResting(true);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0);
    }

    public static boolean checkBatSpawnRules(EntityType<AbstractTCCreeper> p_218099_, LevelAccessor p_218100_, EntitySpawnReason p_218101_, BlockPos p_218102_, RandomSource p_218103_) {
        if (p_218102_.getY() >= p_218100_.getSeaLevel()) {
            return false;
        } else {
            int i = p_218100_.getMaxLocalRawBrightness(p_218102_);
            int j = 4;
            if (isHalloween()) {
                j = 7;
            } else if (p_218103_.nextBoolean()) {
                return false;
            }

            return i <= p_218103_.nextInt(j) && checkMobSpawnRules(p_218099_, p_218100_, p_218101_, p_218102_, p_218103_);
        }
    }

    private static boolean isHalloween() {
        LocalDate localdate = LocalDate.now();
        int i = localdate.getDayOfMonth();
        int j = localdate.getMonth().getValue();
        return j == 10 && i >= 20 || j == 11 && i <= 3;
    }

    @Override
    public boolean isFlapping() {
        return !this.isResting() && (float) this.tickCount % 10.0F == 0.0F;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_332675_) {
        super.defineSynchedData(p_332675_);
        p_332675_.define(DATA_ID_FLAGS, (byte) 0);
    }

    @Override
    protected float getSoundVolume() {
        return 0.1F;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 0.95F;
    }

    @Nullable
    @Override
    public SoundEvent getAmbientSound() {
        return this.isResting() && this.random.nextInt(4) != 0 ? null : SoundEvents.BAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_27451_) {
        return SoundEvents.BAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BAT_DEATH;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity p_27415_) {
    }

    @Override
    protected void pushEntities() {
    }

    public boolean isResting() {
        return (this.entityData.get(DATA_ID_FLAGS) & 1) != 0;
    }

    public void setResting(boolean p_27457_) {
        byte b0 = this.entityData.get(DATA_ID_FLAGS);
        if (p_27457_) {
            this.entityData.set(DATA_ID_FLAGS, (byte) (b0 | 1));
        } else {
            this.entityData.set(DATA_ID_FLAGS, (byte) (b0 & -2));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isResting()) {
            this.setDeltaMovement(Vec3.ZERO);
            this.setPosRaw(this.getX(), (double) Mth.floor(this.getY()) + 1.0 - (double) this.getBbHeight(), this.getZ());
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
        }

        this.setupAnimationStates();
    }

    @Override
    protected void customServerAiStep(ServerLevel p_369019_) {
        super.customServerAiStep(p_369019_);
        BlockPos blockpos = this.blockPosition();
        BlockPos blockpos1 = blockpos.above();
        if (this.isResting()) {
            boolean flag = this.isSilent();
            if (p_369019_.getBlockState(blockpos1).isRedstoneConductor(p_369019_, blockpos)) {
                if (this.random.nextInt(200) == 0) {
                    this.yHeadRot = (float) this.random.nextInt(360);
                }

                if (p_369019_.getNearestPlayer(BAT_RESTING_TARGETING, this) != null) {
                    this.setResting(false);
                    if (!flag) {
                        p_369019_.levelEvent(null, 1025, blockpos, 0);
                    }
                }
            } else {
                this.setResting(false);
                if (!flag) {
                    p_369019_.levelEvent(null, 1025, blockpos, 0);
                }
            }
        } else {
            if (this.targetPosition != null && (!p_369019_.isEmptyBlock(this.targetPosition) || this.targetPosition.getY() <= p_369019_.getMinY())) {
                this.targetPosition = null;
            }

            if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerToCenterThan(this.position(), 2.0)) {
                this.targetPosition = BlockPos.containing(
                        this.getX() + (double) this.random.nextInt(7) - (double) this.random.nextInt(7),
                        this.getY() + (double) this.random.nextInt(6) - 2.0,
                        this.getZ() + (double) this.random.nextInt(7) - (double) this.random.nextInt(7)
                );
            }

            double d2 = (double) this.targetPosition.getX() + 0.5 - this.getX();
            double d0 = (double) this.targetPosition.getY() + 0.1 - this.getY();
            double d1 = (double) this.targetPosition.getZ() + 0.5 - this.getZ();
            Vec3 vec3 = this.getDeltaMovement();
            Vec3 vec31 = vec3.add(
                    (Math.signum(d2) * 0.5 - vec3.x) * 0.1F, (Math.signum(d0) * 0.7F - vec3.y) * 0.1F, (Math.signum(d1) * 0.5 - vec3.z) * 0.1F
            );
            this.setDeltaMovement(vec31);
            float f = (float) (Mth.atan2(vec31.z, vec31.x) * 180.0F / (float) Math.PI) - 90.0F;
            float f1 = Mth.wrapDegrees(f - this.getYRot());
            this.zza = 0.5F;
            this.setYRot(this.getYRot() + f1);
            if (this.random.nextInt(100) == 0 && p_369019_.getBlockState(blockpos1).isRedstoneConductor(p_369019_, blockpos1)) {
                this.setResting(true);
            }
        }
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    protected void checkFallDamage(double p_27419_, boolean p_27420_, BlockState p_27421_, BlockPos p_27422_) {
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (this.isInvulnerableTo(level, source)) {
            return false;
        } else {
            if (!this.level().isClientSide && this.isResting()) {
                this.setResting(false);
            }

            return super.hurtServer(level, source, amount);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_27427_) {
        super.readAdditionalSaveData(p_27427_);
        this.entityData.set(DATA_ID_FLAGS, p_27427_.getByte("BatFlags"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_27443_) {
        super.addAdditionalSaveData(p_27443_);
        p_27443_.putByte("BatFlags", this.entityData.get(DATA_ID_FLAGS));
    }

    private void setupAnimationStates() {
        if (this.isResting()) {
            this.flyAnimationState.stop();
            this.restAnimationState.startIfStopped(this.tickCount);
        } else {
            this.restAnimationState.stop();
            this.flyAnimationState.startIfStopped(this.tickCount);
        }
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.BAT;
    }

    public static class TCBatCreeperContext implements TCCreeperContext<TCBatCreeper> {
        private static final String NAME = "batcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCBatCreeper::new, MobCategory.MONSTER).sized(0.5F, 0.9F).eyeHeight(0.45F).clientTrackingRange(5).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "こうもりたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Flying bat, of course it is explosive when you see green one.";
        }

        @Override
        public String getJaJPDesc() {
            return "洞窟に潜む緑の蝙蝠、勿論爆発性。";
        }

        @Override
        public String getEnUSName() {
            return "Bat Creeper";
        }

        @Override
        public String getJaJPName() {
            return "蝙蝠匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x004400;
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
            event.register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TCBatCreeper::checkBatSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
            return true;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return TCBatCreeper.createAttributes();
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCBatCreeper>) type, TCBatCreeperRenderer::new);
        }
    }
}
