package com.tntmodders.takumicraft.data.worldgen.features;

import com.tntmodders.takumicraft.core.TCBlockCore;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class TCOreFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_GUNORE = FeatureUtils.createKey("tc_ore_gunore");

    public static void bootstap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest ruletest1 = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest ruletest2 = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        HolderGetter<ConfiguredFeature<?, ?>> holdergetter = context.lookup(Registries.CONFIGURED_FEATURE);
        List<OreConfiguration.TargetBlockState> list5 = List.of(
                OreConfiguration.target(ruletest1, TCBlockCore.GUNORE.defaultBlockState()),
                OreConfiguration.target(ruletest2, TCBlockCore.DEEPSLATE_GUNORE.defaultBlockState()));
        Holder<ConfiguredFeature<?, ?>> holderGunOre = holdergetter.getOrThrow(OreFeatures.ORE_COAL);
        FeatureUtils.register(context, ORE_GUNORE, Feature.ORE, new OreConfiguration(list5, 17));
    }
}
