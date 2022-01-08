package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCCreativeModeTabCore;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class TCBlockItem extends BlockItem {
    public TCBlockItem(Block block) {
        super(block, new Properties().tab(TCCreativeModeTabCore.TAB_TC));
        this.setRegistryName(block.getRegistryName());
    }
}
