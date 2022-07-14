package com.tntmodders.takumicraft.event.client;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.screen.TCTakumiBookScreen;
import com.tntmodders.takumicraft.core.client.TCKeyBindingCore;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TakumiCraftCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TCClientEvents {
    public static final TCClientEvents INSTANCE = new TCClientEvents();

    @SubscribeEvent
    public void onKeyPressed(InputEvent.Key event) {
        if (Minecraft.getInstance().screen == null) {
            if (event.getKey() == TCKeyBindingCore.MAP_TAKUMIBOOK.getKey().getValue()) {
                Minecraft.getInstance().setScreen(new TCTakumiBookScreen());
            }
        }
    }
}
