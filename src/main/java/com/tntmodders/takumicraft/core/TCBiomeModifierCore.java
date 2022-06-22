package com.tntmodders.takumicraft.core;

import com.mojang.serialization.Codec;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.utils.TCBlockUtils;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class TCBiomeModifierCore {
    public static final DeferredRegister<BiomeModifier> BIOME_MODIFIER = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIERS, TakumiCraftCore.MODID);
    public static final RegistryObject<BiomeModifier> TC_OREGEN = BIOME_MODIFIER.register("tc_oregen", TCBiomeModifier::new);
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SER = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, TakumiCraftCore.MODID);
    public static final RegistryObject<Codec<TCBiomeModifier>> TC_OREGEN_SER = BIOME_MODIFIER_SER.register("tc_oregen_ser", () -> Codec.unit(TCBiomeModifier.INSTANCE));

    public static final RuleTest STONE_ORE_REPLACEABLES = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
    public static final RuleTest DEEPSLATE_ORE_REPLACEABLES = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

    public static List<OreConfiguration.TargetBlockState> ORE_GUNORE_TARGET_LIST;
    public static Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_GUNORE;
    public static Holder<PlacedFeature> ORE_GUNORE_UPPER;
    public static Holder<PlacedFeature> ORE_GUNORE_MIDDLE;

    private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }

    private static List<PlacementModifier> rareOrePlacement(int p_195350_, PlacementModifier p_195351_) {
        return orePlacement(RarityFilter.onAverageOnceEvery(p_195350_), p_195351_);
    }

    public static void registerModifiers(RegisterEvent event) {
        ORE_GUNORE_TARGET_LIST = List.of(
                OreConfiguration.target(STONE_ORE_REPLACEABLES, TCBlockUtils.getTCBlockFromRegistry("gunore").defaultBlockState()),
                OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, TCBlockUtils.getTCBlockFromRegistry("deepslate_gunore").defaultBlockState()));
        ORE_GUNORE = FeatureUtils.register("tc_ore_gunore", Feature.ORE, new OreConfiguration(ORE_GUNORE_TARGET_LIST, 25));
        ORE_GUNORE_UPPER = PlacementUtils.register("tc_ore_gunore_upper", ORE_GUNORE, commonOrePlacement(45, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
        ORE_GUNORE_MIDDLE = PlacementUtils.register("tc_ore_gunore_middle", ORE_GUNORE, commonOrePlacement(20, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
    }

    public static void register(IEventBus eventBus) {
        BIOME_MODIFIER_SER.register(eventBus);
        BIOME_MODIFIER.register(eventBus);
    }

    public static class TCBiomeModifier implements BiomeModifier {
        public static final TCBiomeModifier INSTANCE = new TCBiomeModifier();

        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            switch (phase) {
                case ADD -> {
                    if (biome.containsTag(BiomeTags.IS_OVERWORLD)) {
                        builder.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_GUNORE_UPPER);
                        builder.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_GUNORE_MIDDLE);
                    }
                    TCEntityCore.ENTITY_CONTEXTS.forEach(context -> context.addSpawn(builder.getMobSpawnSettings()));
                }
                default -> {
                }
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec() {
            return TCBiomeModifierCore.TC_OREGEN_SER.get();
        }
    }
}
