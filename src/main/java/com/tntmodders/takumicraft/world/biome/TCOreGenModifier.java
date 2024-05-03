package com.tntmodders.takumicraft.world.biome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public record TCOreGenModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features,
                               GenerationStep.Decoration step) implements BiomeModifier {
    public static final MapCodec<ForgeBiomeModifiers.AddFeaturesBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(ForgeBiomeModifiers.AddFeaturesBiomeModifier::biomes),
            PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(ForgeBiomeModifiers.AddFeaturesBiomeModifier::features),
            GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(ForgeBiomeModifiers.AddFeaturesBiomeModifier::step)
    ).apply(builder, ForgeBiomeModifiers.AddFeaturesBiomeModifier::new));

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD && this.biomes.contains(biome)) {
            BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
            this.features.forEach(holder -> generationSettings.addFeature(this.step, holder));
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}

/*public class TCOreGenModifier implements BiomeModifier {
    private final HolderSet<PlacedFeature> features;


    public TCOreGenModifier(HolderSet<PlacedFeature> features) {
        this.features = features;
    }

    public static MapCodec<TCOreGenModifier> makeCodec() {
        return RecordCodecBuilder.create(config -> config.group(PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(otherConfig -> otherConfig.features)).apply(config, TCOreGenModifier::new));
    }

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD && biome.is(BiomeTags.IS_OVERWORLD)) {
            builder.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, this.features);
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return TCOreGenModifier.makeCodec();
    }
}*/
