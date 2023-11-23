package com.tntmodders.takumicraft.world.biome;

import com.mojang.serialization.Codec;
import com.tntmodders.takumicraft.core.TCBiomeModifierCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
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
            TCLoggingUtils.startRegistry("EntitySpawnModifier");
            TCEntityCore.ENTITY_CONTEXTS.forEach(context -> {
                context.registerModifierSpawn(biome, builder);
                TCLoggingUtils.entryRegistry("EntitySpawnModifier", context.getEnUSName());
            });
            WeightedRandomList<MobSpawnSettings.SpawnerData> list = biome.get().getMobSettings().getMobs(MobCategory.CREATURE);
            TCLoggingUtils.completeRegistry("EntitySpawnModifier");
        }

    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return TCBiomeModifierCore.TC_SPAWN_MODIFIER_TYPE.get();
    }
}
