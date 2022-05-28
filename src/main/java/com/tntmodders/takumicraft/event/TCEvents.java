package com.tntmodders.takumicraft.event;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.entity.mobs.TCPhantomCreeper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TakumiCraftCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TCEvents {
    public static final TCEvents INSTANCE = new TCEvents();

    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate event) {
        if (!event.getWorld().isClientSide) {
            if (event.getExplosion().getExploder() instanceof AbstractTCCreeper) {
                ((AbstractTCCreeper) event.getExplosion().getExploder()).explodeCreeperEvent(event);
            }
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getEntity() instanceof Phantom && event.getWorld().getRandom().nextInt(10) == 0 && event.getEntity().level instanceof ServerLevel level) {
            BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
            TCPhantomCreeper creeper = ((TCPhantomCreeper) TCEntityCore.PHANTOM.entityType().create(level));
            creeper.moveTo(pos, 0f, 0f);
            creeper.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null, null);
            if (level.tryAddFreshEntityWithPassengers(creeper)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
