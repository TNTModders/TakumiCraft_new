package com.tntmodders.takumicraft.core.client;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.item.TCSpawnEggItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

public class TCColorsCore {
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((p_92693_, p_92694_) -> p_92694_ == 0 ? FastColor.ARGB32.opaque(p_92693_.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor()) : -1, TCItemCore.TIPPED_CREEPER_ARROW);
        TCItemCore.ITEMS.stream().filter(item -> item instanceof TCSpawnEggItem).forEach(egg -> event.register((stack, layer) -> FastColor.ARGB32.opaque(((TCSpawnEggItem) egg).getColor(layer)), egg));
    }
}
