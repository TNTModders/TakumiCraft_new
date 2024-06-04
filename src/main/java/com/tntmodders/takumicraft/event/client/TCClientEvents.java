package com.tntmodders.takumicraft.event.client;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.screen.TCTakumiBookScreen;
import com.tntmodders.takumicraft.core.TCConfigCore;
import com.tntmodders.takumicraft.core.client.TCKeyBindingCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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

    @SubscribeEvent
    public void ScreenRender(ScreenEvent.Init event) {
        if (TCConfigCore.TCCommonConfig.COMMON.isDebugMode.get() && event.getScreen() instanceof BackupConfirmScreen screen) {
            screen.renderables.forEach(renderable -> {
                if (renderable instanceof Button button) {
                    String string = button.getMessage().toString();
                    if (string.contains("selectWorld.backupJoinSkipButton")) {
                        TCLoggingUtils.info("skipped");
                        button.onPress();
                    }
                }
            });
        }
    }
}
