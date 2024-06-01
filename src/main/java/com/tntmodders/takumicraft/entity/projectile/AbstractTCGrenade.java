package com.tntmodders.takumicraft.entity.projectile;

import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public abstract class AbstractTCGrenade extends ThrowableItemProjectile {
    protected int count;

    public AbstractTCGrenade(EntityType<? extends ThrowableItemProjectile> p_37442_, Level p_37443_) {
        super(p_37442_, p_37443_);
    }

    public AbstractTCGrenade(EntityType<? extends ThrowableItemProjectile> p_37432_, double p_37433_, double p_37434_, double p_37435_, Level p_37436_) {
        super(p_37432_, p_37433_, p_37434_, p_37435_, p_37436_);
    }

    public AbstractTCGrenade(EntityType<? extends ThrowableItemProjectile> p_37438_, LivingEntity p_37439_, Level p_37440_) {
        super(p_37438_, p_37439_, p_37440_);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        this.count++;
        if (!this.level().isClientSide()) {
            if (this.count <= this.getCount()) {
                TCExplosionUtils.createExplosion(this.level(), this, this.getX(), this.getY(), this.getZ(), this.getPower(), false);
            } else {
                this.discard();
            }
        }
    }

    @Override
    public boolean shouldBlockExplode(Explosion p_19987_, BlockGetter p_19988_, BlockPos p_19989_, BlockState p_19990_, float p_19991_) {
        return this.getDestroy();
    }

    public abstract int getCount();

    public abstract int getPower();

    public abstract boolean getDestroy();
}
