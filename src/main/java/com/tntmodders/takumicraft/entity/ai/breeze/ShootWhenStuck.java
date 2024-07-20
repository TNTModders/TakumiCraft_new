package com.tntmodders.takumicraft.entity.ai.breeze;

import com.tntmodders.takumicraft.entity.mobs.TCBreezeCreeper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Map;

public class ShootWhenStuck extends Behavior<TCBreezeCreeper> {
    public ShootWhenStuck() {
        super(
                Map.of(
                        MemoryModuleType.ATTACK_TARGET,
                        MemoryStatus.VALUE_PRESENT,
                        MemoryModuleType.BREEZE_JUMP_INHALING,
                        MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.BREEZE_JUMP_TARGET,
                        MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.WALK_TARGET,
                        MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.BREEZE_SHOOT,
                        MemoryStatus.VALUE_ABSENT
                )
        );
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel p_312625_, TCBreezeCreeper p_311731_) {
        return p_311731_.isPassenger() || p_311731_.isInWater() || p_311731_.getEffect(MobEffects.LEVITATION) != null;
    }

    @Override
    protected boolean canStillUse(ServerLevel p_310843_, TCBreezeCreeper p_311345_, long p_311650_) {
        return false;
    }

    @Override
    protected void start(ServerLevel p_311028_, TCBreezeCreeper p_309885_, long p_313079_) {
        p_309885_.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT, Unit.INSTANCE, 60L);
    }
}