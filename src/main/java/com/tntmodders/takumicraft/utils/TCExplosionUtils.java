package com.tntmodders.takumicraft.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class TCExplosionUtils {
    public static void createExplosion(Level level, Entity source, BlockPos pos, float power) {
        createExplosion(level, source, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, power, false);
    }

    public static void createExplosion(Level level, Entity source, double x, double y, double z, float power, boolean fire) {
        if (!level.isClientSide) {
            //level.getProfiler().push("TC_Explosion");
            level.explode(source, x, y, z, power, fire, source == null ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.MOB);
            //level.getProfiler().pop();
        }
    }
}
