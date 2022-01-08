package com.tntmodders.takumicraft.core;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class TCCreativeModeTabCore {
    public static final CreativeModeTab TAB_TC = new CreativeModeTab("takumicraft") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(TCBlockCore.CREEPER_BOMB);
        }
    };
}
