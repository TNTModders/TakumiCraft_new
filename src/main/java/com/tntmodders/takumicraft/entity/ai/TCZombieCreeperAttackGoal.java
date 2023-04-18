package com.tntmodders.takumicraft.entity.ai;

import com.tntmodders.takumicraft.entity.mobs.TCZombieCreeper;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class TCZombieCreeperAttackGoal extends MeleeAttackGoal {
    private final TCZombieCreeper zombie;
    private int raiseArmTicks;

    public TCZombieCreeperAttackGoal(TCZombieCreeper p_26019_, double p_26020_, boolean p_26021_) {
        super(p_26019_, p_26020_, p_26021_);
        this.zombie = p_26019_;
    }

    @Override
    public void start() {
        super.start();
        this.raiseArmTicks = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.zombie.setAggressive(false);
    }

    @Override
    public void tick() {
        super.tick();
        ++this.raiseArmTicks;
        this.zombie.setAggressive(this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2);

    }
}
