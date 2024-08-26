package com.tntmodders.takumicraft.entity.mobs;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;

public abstract class AbstractTCIllagerCreeper extends AbstractTCCreeper {
    public AbstractTCIllagerCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    public AbstractIllager.IllagerArmPose getArmPose() {
        return AbstractIllager.IllagerArmPose.CROSSED;
    }

    @Override
    public boolean canAttack(LivingEntity p_186270_) {
        return (!(p_186270_ instanceof AbstractVillager) || !p_186270_.isBaby()) && super.canAttack(p_186270_);
    }

    @Override
    public boolean isAlliedTo(Entity p_333400_) {
        if (super.isAlliedTo(p_333400_)) {
            return true;
        } else {
            return p_333400_.getType().is(EntityTypeTags.ILLAGER_FRIENDS) && this.getTeam() == null && p_333400_.getTeam() == null;
        }
    }

    public enum IllagerArmPose {
        CROSSED,
        ATTACKING,
        SPELLCASTING,
        BOW_AND_ARROW,
        CROSSBOW_HOLD,
        CROSSBOW_CHARGE,
        CELEBRATING,
        NEUTRAL
    }
}
