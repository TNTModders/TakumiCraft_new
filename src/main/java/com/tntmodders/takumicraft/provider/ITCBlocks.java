package com.tntmodders.takumicraft.provider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ITCBlocks extends ITCTranslator {
    default EnumTCBlockStateModelType getBlockStateModelType() {
        return EnumTCBlockStateModelType.SIMPLE;
    }

    Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>> getBlockLoot(Block block);

    enum EnumTCBlockStateModelType {
        SIMPLE,
        HALF,
        STAIRS,
        NONE
    }
}
