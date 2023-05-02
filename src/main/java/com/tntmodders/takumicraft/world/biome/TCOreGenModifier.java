package com.tntmodders.takumicraft.world.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public class TCOreGenModifier implements BiomeModifier {
    private final Holder<PlacedFeature> features;


    public TCOreGenModifier(Holder<PlacedFeature> features) {
        this.features = features;
    }

    public static Codec<TCOreGenModifier> makeCodec() {
        return RecordCodecBuilder.create(config -> config.group(PlacedFeature.CODEC.fieldOf("feature").forGetter(otherConfig -> otherConfig.features)).apply(config, TCOreGenModifier::new));
    }

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD && biome.is(BiomeTags.IS_OVERWORLD)) {
            builder.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, this.features);
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return TCOreGenModifier.makeCodec();
    }
}
