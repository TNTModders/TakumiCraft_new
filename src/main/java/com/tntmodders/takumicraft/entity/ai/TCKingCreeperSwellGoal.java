package com.tntmodders.takumicraft.entity.ai;

import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.SwellGoal;

import javax.annotation.Nullable;

public class TCKingCreeperSwellGoal extends SwellGoal {
    private final TCKingCreeper creeper;
    @Nullable
    private LivingEntity target;

    public TCKingCreeperSwellGoal(TCKingCreeper king) {
        super(king);
        this.creeper = king;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.creeper.getTarget();
        return !(this.creeper.getSwellDir() < -1 && this.creeper.getSwell() > 0) && this.creeper.getSwellDir() > 0 || livingentity != null && this.creeper.distanceToSqr(livingentity) < 128;
    }

    @Override
    public void start() {
        super.start();
        this.creeper.setRandomAttackID();
        this.target = this.creeper.getTarget();
    }


    @Override
    public void stop() {
        super.stop();
        this.target = null;
    }

    @Override
    public void tick() {
        if (this.target == null) {
            this.creeper.setSwellDir(-1);
        } else {
            this.creeper.setSwellDir(1);
        }
    }
}
