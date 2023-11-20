package com.tntmodders.takumicraft.data.loot;

import com.tntmodders.takumicraft.provider.ITCBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
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
        if (block instanceof ITCBlocks) {
            ((ITCBlocks) block).drop(block, this);
        } else {
            this.dropSelf(block);
        }
    }

    @Override
    public void add(Block p_250610_, LootTable.Builder p_249817_) {
        super.add(p_250610_, p_249817_);
    }

    @Override
    public void add(Block p_251966_, Function<Block, LootTable.Builder> p_251699_) {
        super.add(p_251966_, p_251699_);
    }

    @Override
    public void dropSelf(Block p_249181_) {
        super.dropSelf(p_249181_);
    }

    @Override
    public void dropWhenSilkTouch(Block p_250855_) {
        super.dropWhenSilkTouch(p_250855_);
    }

    @Override
    public LootTable.Builder createOreDrop(Block p_250450_, Item p_249745_) {
        return super.createOreDrop(p_250450_, p_249745_);
    }
}
