package com.tntmodders.takumicraft.item;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

public class TCCreeperTorchBlockItem extends AbstractTCStandingBlockItem {
    public TCCreeperTorchBlockItem(Block block, Block wallBlock) {
        super(block, wallBlock, Direction.DOWN);
    }
}
