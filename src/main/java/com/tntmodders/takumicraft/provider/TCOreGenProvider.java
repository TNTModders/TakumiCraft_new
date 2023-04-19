package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.data.worldgen.features.TCOreFeatures;
import com.tntmodders.takumicraft.data.worldgen.placement.TCOrePlacements;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TCOreGenProvider extends DatapackBuiltinEntriesProvider {
    public TCOreGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, new RegistrySetBuilder().add(Registries.CONFIGURED_FEATURE, TCOreFeatures::bootstap)
                        .add(Registries.PLACED_FEATURE, TCOrePlacements::bootstap),
                Set.of(TakumiCraftCore.MODID));
    }
}
