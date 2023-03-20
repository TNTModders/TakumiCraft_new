package com.tntmodders.takumicraft.data.loot;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TCBlockLoot extends BlockLootSubProvider {
    private final Block block;

    public TCBlockLoot(Block blockIn, boolean isExplosionResistance) {
        super(isExplosionResistance ? Stream.of(blockIn).map(ItemLike::asItem).collect(Collectors.toSet()) : Set.of(),
                FeatureFlags.REGISTRY.allFlags());
        this.block = blockIn;
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(block);
    }

    @Override
    protected void generate() {
        this.dropSelf(block);
    }
}
