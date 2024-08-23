package com.tntmodders.takumicraft.entity.mobs;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractTCBlockCreeper extends AbstractTCCreeper {
    public AbstractTCBlockCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    public abstract BlockState getBlock();

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public void push(Entity p_33474_) {
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    protected AABB makeBoundingBox() {
        return super.makeBoundingBox();
    }

    @Override
    public void move(MoverType p_33424_, Vec3 p_33425_) {
    }

    @Override
    public Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    public void setDeltaMovement(Vec3 p_149804_) {
    }

}
