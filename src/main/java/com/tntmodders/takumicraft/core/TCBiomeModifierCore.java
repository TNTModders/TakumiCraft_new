package com.tntmodders.takumicraft.core;

import com.mojang.serialization.Codec;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.world.biome.TCOreGenModifier;
import com.tntmodders.takumicraft.world.biome.TCSpawnModifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TCBiomeModifierCore {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, TakumiCraftCore.MODID);

    public static final RegistryObject<Codec<TCSpawnModifier>> TC_SPAWN_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("tc_spawn_modifier", () -> Codec.unit(TCSpawnModifier.INSTANCE));
    public static final RegistryObject<Codec<? extends BiomeModifier>> TC_OREGEN_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("tc_gunore_modifier", TCOreGenModifier::makeCodec);


    public static final ResourceKey<ConfiguredFeature<?, ?>> GUNORE_CONFIGURED_FEATURE = ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(TakumiCraftCore.MODID, "tc_ore_gunore"));
    public static final ResourceKey<PlacedFeature> GUNORE_PLACED_FEATURE = ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(TakumiCraftCore.MODID, "tc_ore_gunore"));

    public static void register(IEventBus event) {
        BIOME_MODIFIER_SERIALIZERS.register(event);
    }
}
