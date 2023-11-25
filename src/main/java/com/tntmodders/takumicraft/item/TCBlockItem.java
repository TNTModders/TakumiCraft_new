package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.provider.ITCBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class TCBlockItem extends BlockItem {
    private final Block block;

    public TCBlockItem(Block block) {
        super(block, new Properties());
        this.block = block;
    }

    public boolean hideOnCreativeTab() {
        return this.block instanceof ITCBlocks && ((ITCBlocks) this.block).hideOnCreativeTab();
    }

    public String getRegistryName() {
        return ((ITCBlocks) block).getRegistryName();
    }
}
