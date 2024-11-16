package com.tntmodders.takumicraft.entity.projectile;

import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class TCAmethystBomb extends ThrowableItemProjectile {
    public static final EntityType<TCAmethystBomb> AMETHYST_BOMB = EntityType.Builder.<TCAmethystBomb>of(TCAmethystBomb::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(TCEntityCore.TCEntityId("amethystbomb"));

    private int frozenTick;

    public TCAmethystBomb(EntityType<? extends TCAmethystBomb> p_37391_, Level p_37392_) {
        super(p_37391_, p_37392_);
    }

    public TCAmethystBomb(Level p_37399_, LivingEntity p_37400_) {
        super(AMETHYST_BOMB, p_37400_, p_37399_, Blocks.SMALL_AMETHYST_BUD.asItem().getDefaultInstance());
    }

    public TCAmethystBomb(Level p_37394_, double p_37395_, double p_37396_, double p_37397_) {
        super(AMETHYST_BOMB, p_37395_, p_37396_, p_37397_, p_37394_, Blocks.SMALL_AMETHYST_BUD.asItem().getDefaultInstance());
    }

    @Override
    protected Item getDefaultItem() {
        return Blocks.SMALL_AMETHYST_BUD.asItem();
    }

    @Override
    public ItemStack getItem() {
        ItemLike itemLike = this.getDefaultItem();
        if (this.frozenTick > 20) {
            itemLike = Blocks.AMETHYST_CLUSTER;
        } else if (this.frozenTick > 15) {
            itemLike = Blocks.LARGE_AMETHYST_BUD;
        } else if (this.frozenTick > 10) {
            itemLike = Blocks.MEDIUM_AMETHYST_BUD;
        }
        return new ItemStack(itemLike);
    }

    @Override
    public void tick() {
        if (this.getOwner() == null || !this.getOwner().isAlive()) {
            super.tick();
        }
        this.frozenTick++;
        if (this.frozenTick > 120) {
            this.discard();
        }
    }

    @Override
    public boolean hurtServer(ServerLevel p_367356_, DamageSource p_368526_, float p_366624_) {
        if (p_368526_.is(DamageTypes.PLAYER_ATTACK)) {
            this.discard();
        }
        return super.hurtServer(p_367356_, p_368526_, p_366624_);
    }

    private ParticleOptions getParticle() {
        ItemStack itemstack = new ItemStack(this.getDefaultItem());
        return new ItemParticleOption(ParticleTypes.ITEM, itemstack);
    }

    @Override
    public void handleEntityEvent(byte p_37402_) {
        if (p_37402_ == 3) {
            ParticleOptions particleoptions = this.getParticle();

            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }

    }

    @Override
    protected void onHitEntity(EntityHitResult p_37404_) {
        if (this.getOwner() == null || p_37404_.getEntity().getType().getCategory() != this.getOwner().getType().getCategory()) {
            super.onHitEntity(p_37404_);
            Entity entity = p_37404_.getEntity();
            int i = entity instanceof Blaze ? 3 : 0;
            entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float) i);
        }
    }

    @Override
    protected void onHit(HitResult p_37406_) {
        super.onHit(p_37406_);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
            TCExplosionUtils.createExplosion(this.level(), this, this.getOnPos(), 2f);
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        this.playSound(SoundEvents.AMETHYST_CLUSTER_BREAK);
        for (int i = 0; i < 20 + this.random.nextGaussian() * 10; i++) {
            this.level().addParticle(this.getParticle(), this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), this.random.nextGaussian() * 0.25, this.random.nextGaussian() * 0.25, this.random.nextGaussian() * 0.25);
        }
    }
}
