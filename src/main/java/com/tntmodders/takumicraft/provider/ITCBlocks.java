package com.tntmodders.takumicraft.provider;

import com.ibm.icu.impl.Pair;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public interface ITCBlocks extends ITCTranslator {
    String getRegistryName();

    default EnumTCBlockStateModelType getBlockStateModelType() {
        return EnumTCBlockStateModelType.SIMPLE;
    }

    Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block);

    default List<TagKey<Block>> getBlockTags() {
        return new ArrayList<>();
    }

    default List<Pair<TagKey<Block>, TagKey<Item>>> getItemTags() {
        return new ArrayList<>();
    }

    enum EnumTCBlockStateModelType {
        SIMPLE,
        HALF,
        STAIRS,
        NONE
    }
}
