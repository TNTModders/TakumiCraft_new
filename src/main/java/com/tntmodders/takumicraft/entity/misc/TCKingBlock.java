package com.tntmodders.takumicraft.entity.misc;

import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class TCKingBlock extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Integer> WAIT_TICK = SynchedEntityData.defineId(TCKingBlock.class, EntityDataSerializers.INT);
    public static final EntityType<TCKingBlock> KING_BLOCK = EntityType.Builder.<TCKingBlock>of(TCKingBlock::new, MobCategory.MISC)
            .sized(1f, 1f).clientTrackingRange(4).updateInterval(20).build("king_block");

    private LivingEntity owner;
    private UUID ownerUUID;

    protected TCKingBlock(EntityType<? extends TCKingBlock> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    public TCKingBlock(Level worldIn, double x, double y, double z, int tick, LivingEntity entity) {
        this(KING_BLOCK, worldIn);
        this.setPos(x, y, z);
        this.setWaitTick(tick);
        this.setOwner(entity);
    }

    public void setOwner(@Nullable LivingEntity entity) {
        this.owner = entity;
        this.ownerUUID = entity == null ? null : entity.getUUID();
    }

    @Override
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity) entity;
            }
        }
        return this.owner;
    }


    @Override
    public void tick() {
        this.setNoGravity(this.tickCount < this.getWaitTick());
        if (this.tickCount == this.getWaitTick() / 10) {
            this.playSound(SoundEvents.CREEPER_PRIMED);
        }
        if (this.tickCount < this.getWaitTick()) {
            for (int i = 0; i < 10; i++) {
                this.level().addParticle(ParticleTypes.ENCHANT, this.getX() + this.random.nextDouble() * 2 - 1, this.getY() + this.random.nextDouble() * 2 - 1, this.getZ() + this.random.nextDouble() * 2 - 1, this.random.nextGaussian() * 1.2, this.random.nextGaussian() * 1.2, this.random.nextGaussian() * 1.2);
            }
            this.playSound(this.getSound(), 0.5f, 0.5f);
        } else {
            this.setDeltaMovement(0, -0.3, 0);
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
        super.tick();
        if (this.onGround() || this.level().collidesWithSuffocatingBlock(this, getBoundingBox().inflate(0.1f))) {
            if (!this.level().isClientSide()) {
                this.onGroundUpdate();
            }
            this.discard();
        }
    }

    protected SoundEvent getSound() {
        return SoundEvents.STONE_BREAK;
    }

    protected void onGroundUpdate() {
        float power = 3f;
        if (this.getOwner() instanceof Creeper creeper && creeper.isPowered()) {
            power = 5f;
        }
        TCExplosionUtils.createExplosion(this.level(), this.getOwner() == null ? this : this.getOwner(), this.getX(), this.getY() + 0.5, this.getZ(), power, true);
    }

    @Override
    public void move(MoverType type, Vec3 vec3) {
        if (this.tickCount > this.getWaitTick()) {
            super.move(type, new Vec3(0, vec3.y(), 0));
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(WAIT_TICK, 0);
    }

    public int getWaitTick() {
        return this.entityData.get(WAIT_TICK);
    }

    public void setWaitTick(int tick) {
        this.entityData.set(WAIT_TICK, tick);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setWaitTick(tag.getInt("waitTick"));
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("waitTick", this.getWaitTick());
        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    public boolean ignoreExplosion(Explosion p_309517_) {
        return true;
    }

    public float getGlowingSize() {
        float f = this.tickCount * 1.5f / this.getWaitTick();
        return f > 1 ? 1 : f;
    }
}
