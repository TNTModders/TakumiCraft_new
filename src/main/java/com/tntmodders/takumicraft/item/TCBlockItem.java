package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCCreativeModeTabCore;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class TCBlockItem extends BlockItem {
    private final Block block;

    public TCBlockItem(Block block) {
        super(block, new Properties().tab(TCCreativeModeTabCore.TAB_TC));
        this.block = block;
    }

    public String getRegistryName() {
        return ((ITCBlocks) block).getRegistryName();
    }
}
