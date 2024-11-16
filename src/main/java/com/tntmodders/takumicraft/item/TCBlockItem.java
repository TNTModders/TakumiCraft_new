package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TCBlockItem<T extends Block & ITCBlocks> extends BlockItem {
    private final Block block;

    public TCBlockItem(T block) {
        this(block, new Properties());
    }

    public TCBlockItem(T block, Item.Properties properties) {
        super(block, properties.setId(TCItemCore.TCItemId(block.getRegistryName())));
        this.block = block;
    }

    public boolean hideOnCreativeTab() {
        return this.block instanceof ITCBlocks && ((ITCBlocks) this.block).hideOnCreativeTab();
    }

    public String getRegistryName() {
        return ((ITCBlocks) block).getRegistryName();
    }
}
