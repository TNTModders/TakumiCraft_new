package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TakumiCraftCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TCEvents {
    public static final TCEvents INSTANCE = new TCEvents();

    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate event) {
        if (!event.getExplosion().getExploder().level.isClientSide) {
            if (event.getExplosion().getExploder() instanceof AbstractTCCreeper) {
                ((AbstractTCCreeper) event.getExplosion().getExploder()).explodeCreeperEvent(event);
            }
        }
    }
}
