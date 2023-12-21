package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class TCLootTableProvider extends LootTableProvider {
    public TCLootTableProvider(PackOutput packOutput) {
        super(packOutput, Set.of(), VanillaLootTableProvider.create(packOutput).getTables());
    }

    @Override
    public List<LootTableProvider.SubProviderEntry> getTables() {
        TCLoggingUtils.startRegistry("LootTable");
        List<LootTableProvider.SubProviderEntry> tableList = new ArrayList<>();
        TCBlockCore.BLOCKS.forEach(block -> {
            if (block instanceof ITCBlocks itcBlocks) {
                if (itcBlocks.getBlockLootSubProvider(block) != null) {
                    TCLoggingUtils.entryRegistry("LootTable", ((ITCBlocks) block).getRegistryName());
                    tableList.add(new SubProviderEntry(itcBlocks.getBlockLootSubProvider(block), LootContextParamSets.BLOCK));
                }
            }
        });

        TCEntityCore.ENTITY_CONTEXTS.forEach(context -> {
            Supplier<LootTableSubProvider> supplier = context.getCreeperLoot(context.entityType());
            if (supplier != null) {
                TCLoggingUtils.entryRegistry("LootTable", context.getRegistryName());
                tableList.add(new SubProviderEntry(supplier, LootContextParamSets.ENTITY));
            }
        });
        TCLoggingUtils.completeRegistry("LootTable");
        return tableList;
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
    }
}
