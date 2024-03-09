package com.tntmodders.takumicraft.core.client;

import com.tntmodders.takumicraft.core.TCItemCore;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

public class TCColorsCore {
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((p_92693_, p_92694_) -> p_92694_ == 0 ? PotionUtils.getColor(p_92693_) : -1, TCItemCore.TIPPED_CREEPER_ARROW);
    }
}
