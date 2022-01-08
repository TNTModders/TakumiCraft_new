package com.tntmodders.takumicraft.provider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ITCEntities extends ITCTranslator {
    Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>> getBlockLoot();
}
