package com.tntmodders.takumicraft.provider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ITCBlocks extends ITCTranslator {
    EnumTCBlockStateModelType getBlockStateModelType();

    Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>> getBlockLoot();

    enum EnumTCBlockStateModelType {
        SIMPLE,
        HALF,
        STAIRS,
        NONE
    }
}
