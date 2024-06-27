package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class TCLootTableProvider extends LootTableProvider {
    public TCLootTableProvider(PackOutput packOutput, CompletableFuture lookup) {
        super(packOutput, Set.of(), VanillaLootTableProvider.create(packOutput, lookup).getTables(), lookup);
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
            Function<HolderLookup.Provider, LootTableSubProvider> creeperLoot = context.getCreeperLoot(context.entityType());
            if (creeperLoot != null) {
                TCLoggingUtils.entryRegistry("LootTable", context.getRegistryName());
                tableList.add(new SubProviderEntry(creeperLoot, LootContextParamSets.ENTITY));
            }
        });
        TCLoggingUtils.completeRegistry("LootTable");
        return tableList;
    }

    @Override
    protected void validate(Registry<LootTable> map, ValidationContext validationcontext, ProblemReporter report) {
    }
}
