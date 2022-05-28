package com.tntmodders.takumicraft.provider;

import com.mojang.datafixers.util.Pair;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TCLootTableProvider extends LootTableProvider {
    public TCLootTableProvider(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected @NotNull List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        TCLoggingUtils.startRegistry("LootTable");
        List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> tableList = new ArrayList<>();
        TCBlockCore.BLOCKS.forEach(block -> {
            if (block instanceof ITCBlocks itcBlocks) {
                TCLoggingUtils.entryRegistry("LootTable", block.getRegistryName().getPath());
                tableList.add(Pair.of(itcBlocks.getBlockLoot(block), LootContextParamSets.BLOCK));
            }
        });

        TCEntityCore.ENTITY_CONTEXTS.forEach(context -> {
            Supplier supplier = context.getCreeperLoot(context.entityType());
            if (supplier != null) {
                TCLoggingUtils.entryRegistry("LootTable", context.entityType().getRegistryName().getPath());
                tableList.add(Pair.of(supplier, LootContextParamSets.ENTITY));
            }
        });
        TCLoggingUtils.completeRegistry("LootTable");
        return tableList;
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
    }
}
