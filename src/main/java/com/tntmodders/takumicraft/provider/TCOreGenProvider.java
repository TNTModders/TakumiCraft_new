package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBiomeModifierCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TCOreGenProvider extends DatapackBuiltinEntriesProvider {

    public TCOreGenProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider, new RegistrySetBuilder().add(Registries.CONFIGURED_FEATURE, bootstrap -> {
            RuleTest ruletest1 = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
            RuleTest ruletest2 = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
            List<OreConfiguration.TargetBlockState> list = List.of(OreConfiguration.target(ruletest1, TCBlockCore.GUNORE.defaultBlockState()),
                    OreConfiguration.target(ruletest2, TCBlockCore.DEEPSLATE_GUNORE.defaultBlockState()));

            bootstrap.register(TCBiomeModifierCore.GUNORE_CONFIGURED_FEATURE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(list, 13)));
        }).add(Registries.PLACED_FEATURE, bootstrap -> {
            HolderGetter<ConfiguredFeature<?, ?>> configured = bootstrap.lookup(Registries.CONFIGURED_FEATURE);
            bootstrap.register(TCBiomeModifierCore.GUNORE_PLACED_FEATURE, new PlacedFeature(configured.getOrThrow(TCBiomeModifierCore.GUNORE_CONFIGURED_FEATURE), commonOrePlacement(30, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()))));
        }), Set.of(TakumiCraftCore.MODID));
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }
}