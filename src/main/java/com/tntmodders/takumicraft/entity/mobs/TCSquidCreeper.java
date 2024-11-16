package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.client.renderer.entity.TCSquidCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCParticleTypeCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

import java.util.List;

public class TCSquidCreeper extends AbstractTCWaterAnimalCreeper {
    public float xBodyRot;
    public float xBodyRotO;
    public float zBodyRot;
    public float zBodyRotO;
    public float tentacleMovement;
    public float oldTentacleMovement;
    public float tentacleAngle;
    public float oldTentacleAngle;
    private float speed;
    private float tentacleSpeed;
    private float rotateSpeed;
    private float tx;
    private float ty;
    private float tz;

    public TCSquidCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.removeAllGoals(goal -> goal instanceof FloatGoal);
        this.goalSelector.addGoal(0, new SquidRandomMovementGoal(this));
        this.goalSelector.addGoal(1, new SquidFleeGoal());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SQUID_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_29980_) {
        return SoundEvents.SQUID_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SQUID_DEATH;
    }

    protected SoundEvent getSquirtSound() {
        return SoundEvents.SQUID_SQUIRT;
    }

    @Override
    public boolean canBeLeashed() {
        return !this.isLeashed();
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.xBodyRotO = this.xBodyRot;
        this.zBodyRotO = this.zBodyRot;
        this.oldTentacleMovement = this.tentacleMovement;
        this.oldTentacleAngle = this.tentacleAngle;
        this.tentacleMovement += this.tentacleSpeed;
        if ((double) this.tentacleMovement > Math.PI * 2D) {
            if (this.level().isClientSide) {
                this.tentacleMovement = (float) Math.PI * 2F;
            } else {
                this.tentacleMovement -= (float) Math.PI * 2F;
                if (this.random.nextInt(10) == 0) {
                    this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
                }

                this.level().broadcastEntityEvent(this, (byte) 19);
            }
        }

        if (this.isInWaterOrBubble()) {
            if (this.tentacleMovement < (float) Math.PI) {
                float f = this.tentacleMovement / (float) Math.PI;
                this.tentacleAngle = Mth.sin(f * f * (float) Math.PI) * (float) Math.PI * 0.25F;
                if ((double) f > 0.75D) {
                    this.speed = 1.0F;
                    this.rotateSpeed = 1.0F;
                } else {
                    this.rotateSpeed *= 0.8F;
                }
            } else {
                this.tentacleAngle = 0.0F;
                this.speed *= 0.9F;
                this.rotateSpeed *= 0.99F;
            }

            if (!this.level().isClientSide) {
                this.setDeltaMovement(this.tx * this.speed, this.ty * this.speed, this.tz * this.speed);
            }

            Vec3 vec3 = this.getDeltaMovement();
            double d0 = vec3.horizontalDistance();
            this.yBodyRot += (-((float) Mth.atan2(vec3.x, vec3.z)) * (180F / (float) Math.PI) - this.yBodyRot) * 0.1F;
            this.setYRot(this.yBodyRot);
            this.zBodyRot += (float) Math.PI * this.rotateSpeed * 1.5F;
            this.xBodyRot += (-((float) Mth.atan2(d0, vec3.y)) * (180F / (float) Math.PI) - this.xBodyRot) * 0.1F;
        } else {
            this.tentacleAngle = Mth.abs(Mth.sin(this.tentacleMovement)) * (float) Math.PI * 0.25F;
            if (!this.level().isClientSide) {
                double d1 = this.getDeltaMovement().y;
                if (this.hasEffect(MobEffects.LEVITATION)) {
                    d1 = 0.05D * (double) (this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1);
                } else if (!this.isNoGravity()) {
                    d1 -= 0.08D;
                }

                this.setDeltaMovement(0.0D, d1 * (double) 0.98F, 0.0D);
            }

            this.xBodyRot += (-90.0F - this.xBodyRot) * 0.02F;
        }

    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource p_29963_, float p_29964_) {
        if (super.hurtServer(level, p_29963_, p_29964_) && this.getLastHurtByMob() != null) {
            if (!this.level().isClientSide) {
                this.spawnInk(30, false);
            }

            return true;
        } else {
            return false;
        }
    }

    private Vec3 rotateVector(Vec3 p_29986_) {
        Vec3 vec3 = p_29986_.xRot(this.xBodyRotO * ((float) Math.PI / 180F));
        return vec3.yRot(-this.yBodyRotO * ((float) Math.PI / 180F));
    }

    private void spawnInk(int n, boolean explode) {
        if (this.level() instanceof ServerLevel) {
            this.playSound(this.getSquirtSound(), this.getSoundVolume(), this.getVoicePitch());
            double d = explode ? this.explosionRadius * 1.2 : 0.1;
            Vec3 vec3 = this.rotateVector(new Vec3(0.0D, -1.0D, 0.0D)).add(this.getRandomX(d), this.getRandomY(d), this.getRandomZ(d));

            for (int i = 0; i < n; ++i) {
                Vec3 vec31 = this.rotateVector(new Vec3((double) this.random.nextFloat() * 0.6D - 0.3D, -1.0D, (double) this.random.nextFloat() * 0.6D - 0.3D));
                Vec3 vec32 = vec31.scale(d + this.random.nextFloat() * d);
                ((ServerLevel) this.level()).sendParticles(explode ? this.getTCInkParticle() : this.getInkParticle(), vec3.x, vec3.y + 0.5D, vec3.z, 0, vec32.x, vec32.y, vec32.z, 0.1F);
            }
        }

    }

    protected ParticleOptions getInkParticle() {
        return ParticleTypes.SQUID_INK;
    }

    protected ParticleOptions getTCInkParticle() {
        return TCParticleTypeCore.SQUID_INK;
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
        if (!this.level().isClientSide) {
            this.spawnInk(300, true);
        }
    }

    @Override
    public void travel(Vec3 p_29984_) {
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    @Override
    public void handleEntityEvent(byte p_29957_) {
        if (p_29957_ == 19) {
            this.tentacleMovement = 0.0F;
        } else {
            super.handleEntityEvent(p_29957_);
        }

    }

    public void setMovementVector(float p_29959_, float p_29960_, float p_29961_) {
        this.tx = p_29959_;
        this.ty = p_29960_;
        this.tz = p_29961_;
    }

    public boolean hasMovementVector() {
        return this.tx != 0.0F || this.ty != 0.0F || this.tz != 0.0F;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.SQUID;
    }

    static class SquidRandomMovementGoal extends Goal {
        private final TCSquidCreeper squid;

        public SquidRandomMovementGoal(TCSquidCreeper p_30004_) {
            this.squid = p_30004_;
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            int i = this.squid.getNoActionTime();
            if (i > 100) {
                this.squid.setMovementVector(0.0F, 0.0F, 0.0F);
            } else if (this.squid.getRandom().nextInt(reducedTickDelay(50)) == 0 || !this.squid.wasTouchingWater || !this.squid.hasMovementVector()) {
                float f = this.squid.getRandom().nextFloat() * ((float) Math.PI * 2F);
                float f1 = Mth.cos(f) * 0.2F;
                float f2 = -0.1F + this.squid.getRandom().nextFloat() * 0.2F;
                float f3 = Mth.sin(f) * 0.2F;
                this.squid.setMovementVector(f1, f2, f3);
            }

        }
    }

    public static class TCSquidCreeperContext implements TCCreeperContext<TCSquidCreeper> {
        private static final String NAME = "squidcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCSquidCreeper::new, MobCategory.MONSTER).sized(0.9F, 1.4F).clientTrackingRange(10).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "いかたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Torpedo? No, this is the era of explosive squid!";
        }

        @Override
        public String getJaJPDesc() {
            return "緑の烏賊は、もちろん爆発性。機雷のような厄介者。";
        }

        @Override
        public String getEnUSName() {
            return "Squid Creeper";
        }

        @Override
        public String getJaJPName() {
            return "烏賊匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x003333;
        }

        @Override
        public int getSecondaryColor() {
            return 0xccffcc;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.MAX_HEALTH, 10.0D);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCSquidCreeper>) type, TCSquidCreeperRenderer::new);
        }

        @Override
        public boolean registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            event.register(type, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TCSquidCreeper::checkSurfaceWaterAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
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
            return this.getRank().getSpawnWeight() / 5;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Items.INK_SAC;
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(1f, 16f);
        }

        @Override
        public List<TagKey<EntityType<?>>> getEntityTypeTags() {
            List list = TCCreeperContext.super.getEntityTypeTags();
            list.add(EntityTypeTags.CAN_BREATHE_UNDER_WATER);
            return list;
        }
    }

    class SquidFleeGoal extends Goal {
        private static final float SQUID_FLEE_SPEED = 3.0F;
        private static final float SQUID_FLEE_MIN_DISTANCE = 5.0F;
        private static final float SQUID_FLEE_MAX_DISTANCE = 10.0F;
        private int fleeTicks;

        @Override
        public boolean canUse() {
            LivingEntity livingentity = TCSquidCreeper.this.getLastHurtByMob();
            if (TCSquidCreeper.this.isInWater() && livingentity != null) {
                return TCSquidCreeper.this.distanceToSqr(livingentity) < 100.0D;
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            this.fleeTicks = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            ++this.fleeTicks;
            LivingEntity livingentity = TCSquidCreeper.this.getLastHurtByMob();
            if (livingentity != null) {
                Vec3 vec3 = new Vec3(TCSquidCreeper.this.getX() - livingentity.getX(), TCSquidCreeper.this.getY() - livingentity.getY(), TCSquidCreeper.this.getZ() - livingentity.getZ());
                BlockState blockstate = TCSquidCreeper.this.level().getBlockState(BlockPos.containing(TCSquidCreeper.this.getX() + vec3.x, TCSquidCreeper.this.getY() + vec3.y, TCSquidCreeper.this.getZ() + vec3.z));
                FluidState fluidstate = TCSquidCreeper.this.level().getFluidState(BlockPos.containing(TCSquidCreeper.this.getX() + vec3.x, TCSquidCreeper.this.getY() + vec3.y, TCSquidCreeper.this.getZ() + vec3.z));
                if (fluidstate.is(FluidTags.WATER) || blockstate.isAir()) {
                    double d0 = vec3.length();
                    if (d0 > 0.0D) {
                        vec3.normalize();
                        double d1 = 3.0D;
                        if (d0 > 5.0D) {
                            d1 -= (d0 - 5.0D) / 5.0D;
                        }

                        if (d1 > 0.0D) {
                            vec3 = vec3.scale(d1);
                        }
                    }

                    if (blockstate.isAir()) {
                        vec3 = vec3.subtract(0.0D, vec3.y, 0.0D);
                    }

                    TCSquidCreeper.this.setMovementVector((float) vec3.x / 20.0F, (float) vec3.y / 20.0F, (float) vec3.z / 20.0F);
                }

                if (this.fleeTicks % 10 == 5) {
                    TCSquidCreeper.this.level().addParticle(ParticleTypes.BUBBLE, TCSquidCreeper.this.getX(), TCSquidCreeper.this.getY(), TCSquidCreeper.this.getZ(), 0.0D, 0.0D, 0.0D);
                }

            }
        }
    }
}
