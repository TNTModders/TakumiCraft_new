package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBiomeModifierCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.enchantment.*;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


public class TCDatapackRegistryProvider extends RegistriesDatapackGenerator {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE,
                    bootstrap -> {
                        RuleTest ruletest1 = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
                        RuleTest ruletest2 = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
                        RuleTest ruletest3 = new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD);
                        List<OreConfiguration.TargetBlockState> list = List.of(
                                OreConfiguration.target(ruletest1, TCBlockCore.GUNORE.defaultBlockState()),
                                OreConfiguration.target(ruletest2, TCBlockCore.DEEPSLATE_GUNORE.defaultBlockState()),
                                OreConfiguration.target(ruletest3, TCBlockCore.GUNORE_CREEPER.defaultBlockState()));
                        bootstrap.register(TCBiomeModifierCore.GUNORE_CONFIGURED_FEATURE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(list, 16)));
                    })
            .add(Registries.PLACED_FEATURE,
                    bootstrap -> {
                        HolderGetter<ConfiguredFeature<?, ?>> configured = bootstrap.lookup(Registries.CONFIGURED_FEATURE);
                        bootstrap.register(TCBiomeModifierCore.GUNORE_PLACED_FEATURE, new PlacedFeature(configured.getOrThrow(TCBiomeModifierCore.GUNORE_CONFIGURED_FEATURE), commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(0), VerticalAnchor.belowTop(0)))));
                    })
            .add(Registries.ENCHANTMENT, bootstrap -> {
                registerEnchantment(bootstrap, TCEnchantmentCore.ANTI_POWERED, new TCAntiPoweredContext(bootstrap));
                registerEnchantment(bootstrap, TCEnchantmentCore.MINESWEEPER, new TCMinesweeperContext(bootstrap));
                registerEnchantment(bootstrap, TCEnchantmentCore.BLAST_POWERED, new TCBlastPoweredContext(bootstrap));
                registerEnchantment(bootstrap, TCEnchantmentCore.ANTI_EXPLOSION, new TCAntiExplosionContext(bootstrap));
            });

    public TCDatapackRegistryProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider.thenApply(provider -> RegistryPatchGenerator.createLookup(lookupProvider, BUILDER).join().patches()), Set.of(TakumiCraftCore.MODID));
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }

    private static void registerEnchantment(BootstrapContext<Enchantment> bootstrap, ResourceKey<Enchantment> key, AbstractTCEnchantmentContext context) {
        bootstrap.register(key, context.getEnchantment());
    }
}
