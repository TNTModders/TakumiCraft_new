package com.tntmodders.takumicraft.provider;

import net.minecraft.data.loot.LootTableSubProvider;

import java.util.function.Supplier;

public interface ITCEntities{
    Supplier<LootTableSubProvider> getEntityLoot();
}
