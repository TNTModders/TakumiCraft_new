package com.tntmodders.takumicraft.data.worldgen.placement;

import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class TCOrePlacements {
    public static final ResourceKey<PlacedFeature> ORE_GUNORE = PlacementUtils.createKey("tc_ore_gunore");
/*
    public static void bootstap(BootstapContext<PlacedFeature> context) {
 *//*       HolderGetter<ConfiguredFeature<?, ?>> holdergetter = context.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> holder_gunore = holdergetter.getOrThrow(TCOreFeatures.ORE_GUNORE);
        PlacementUtils.register(context, ORE_GUNORE, holder_gunore, commonOrePlacement(30,
                HeightRangePlacement.uniform(VerticalAnchor.absolute(136), VerticalAnchor.top())));*//*
        context.register(ORE_GUNORE, new PlacedFeature(context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(TCOreFeatures.ORE_GUNORE), List.of()));
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }

    private static List<PlacementModifier> rareOrePlacement(int p_195350_, PlacementModifier p_195351_) {
        return orePlacement(RarityFilter.onAverageOnceEvery(p_195350_), p_195351_);
    }*/
}
