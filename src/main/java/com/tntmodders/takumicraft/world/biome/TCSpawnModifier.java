package com.tntmodders.takumicraft.world.biome;

import com.mojang.serialization.MapCodec;
import com.tntmodders.takumicraft.core.TCBiomeModifierCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.core.Holder;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public class TCSpawnModifier implements BiomeModifier {
    public static final TCSpawnModifier INSTANCE = new TCSpawnModifier();

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            TCEntityCore.ENTITY_CONTEXTS.forEach(context -> context.registerModifierSpawn(biome, builder));
            WeightedRandomList<MobSpawnSettings.SpawnerData> list = biome.get().getMobSettings().getMobs(MobCategory.CREATURE);
        }

    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return TCBiomeModifierCore.TC_SPAWN_MODIFIER_TYPE.get();
    }
}
