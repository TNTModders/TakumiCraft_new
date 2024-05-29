package com.tntmodders.takumicraft.world.biome;

import com.mojang.serialization.MapCodec;
import com.tntmodders.takumicraft.core.TCBiomeModifierCore;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public record TCOreGenModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features) implements BiomeModifier {

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD && biomes.contains(biome)) {
            this.features.forEach(holder -> builder.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, holder));
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return TCBiomeModifierCore.TC_SPAWN_MODIFIER_TYPE.get();
    }
}