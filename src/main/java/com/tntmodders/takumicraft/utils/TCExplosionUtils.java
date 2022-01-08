package com.tntmodders.takumicraft.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;

public class TCExplosionUtils {
    public static void createExplosion(Level level, Entity source, BlockPos pos, float power) {
        if (!level.isClientSide) {
            Explosion.BlockInteraction interaction = ForgeEventFactory.getMobGriefingEvent(level, source) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
            level.explode(source, pos.getX(), pos.getY(), pos.getZ(), power, interaction);
        }
    }
}
