package com.tntmodders.takumicraft.world.biome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tntmodders.takumicraft.core.TCBiomeModifierCore;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public record TCOreGenModifier(HolderSet<PlacedFeature> features) implements BiomeModifier {
    public static final MapCodec<TCOreGenModifier> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(TCOreGenModifier::features)
    ).apply(builder, TCOreGenModifier::new));

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD && biome.is(BiomeTags.IS_OVERWORLD)) {
            builder.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, this.features.get(0));
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return TCBiomeModifierCore.TC_OREGEN_MODIFIER_TYPE.get();
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
