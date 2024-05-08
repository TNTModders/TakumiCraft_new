package com.tntmodders.takumicraft.entity.ai.boss.king;

import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.SwellGoal;

public class TCKingCreeperSwellGoal extends SwellGoal {
    private final TCKingCreeper creeper;

    public TCKingCreeperSwellGoal(TCKingCreeper king) {
        super(king);
        this.creeper = king;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.creeper.getTarget();
        return !(this.creeper.getSwellDir() < -1 && this.creeper.getSwell() > 0) && this.creeper.getSwellDir() > 0 || livingentity != null && this.creeper.distanceToSqr(livingentity) < 36;
    }

    @Override
    public void start() {
        super.start();
        this.creeper.setRandomAttackID();
    }
}
