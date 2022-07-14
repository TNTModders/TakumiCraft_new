package com.tntmodders.takumicraft.core.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

public class TCKeyBindingCore {
    public static final KeyMapping MAP_TAKUMIBOOK = new KeyMapping("key.takumicraft.takumibook", InputConstants.Type.KEYSYM, 73/*I*/, TakumiCraftCore.MODID);

    public static void register(RegisterKeyMappingsEvent event) {
        TCLoggingUtils.startRegistry("KeyMap");
        event.register(MAP_TAKUMIBOOK);
        TCLoggingUtils.completeRegistry("KeyMap");
    }
}
