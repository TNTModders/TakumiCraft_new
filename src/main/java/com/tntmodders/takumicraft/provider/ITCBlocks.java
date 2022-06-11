package com.tntmodders.takumicraft.provider;

import com.ibm.icu.impl.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ITCBlocks extends ITCTranslator {
    String getRegistryName();

    default EnumTCBlockStateModelType getBlockStateModelType() {
        return EnumTCBlockStateModelType.SIMPLE;
    }

    Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>> getBlockLoot(Block block);

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
