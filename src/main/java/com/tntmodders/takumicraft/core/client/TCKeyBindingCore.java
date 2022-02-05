package com.tntmodders.takumicraft.core.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

public class TCKeyBindingCore {
    public static final KeyMapping MAP_TAKUMIBOOK = new KeyMapping("key.takumicraft.takumibook", InputConstants.Type.KEYSYM, 73/*I*/, TakumiCraftCore.MODID);

    public static void register() {
        TCLoggingUtils.startRegistry("KeyMap");
        ClientRegistry.registerKeyBinding(MAP_TAKUMIBOOK);
        TCLoggingUtils.completeRegistry("KeyMap");
    }
}
