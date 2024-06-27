package com.tntmodders.takumicraft.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;

import java.util.function.Function;

public interface ITCEntities {
    Function<HolderLookup.Provider, LootTableSubProvider> getEntityLoot();
}
