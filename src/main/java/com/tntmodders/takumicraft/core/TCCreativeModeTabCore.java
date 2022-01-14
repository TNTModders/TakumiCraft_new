package com.tntmodders.takumicraft.core;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TCCreativeModeTabCore {
    public static final CreativeModeTab TAB_TC = new CreativeModeTab("takumicraft") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(TCBlockCore.CREEPER_BOMB);
        }
    };

    public static final CreativeModeTab TAB_EGGS = new CreativeModeTab("takumicraft.egg") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.CREEPER_SPAWN_EGG);
        }
    };
}
