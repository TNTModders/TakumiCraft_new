package com.tntmodders.takumicraft;

import com.tntmodders.takumicraft.core.*;
import com.tntmodders.takumicraft.core.client.*;
import com.tntmodders.takumicraft.event.TCEvents;
import com.tntmodders.takumicraft.event.client.TCClientEvents;
import com.tntmodders.takumicraft.provider.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod(TakumiCraftCore.MODID)
public class TakumiCraftCore {
    public static final String MODID = "takumicraft";
    public static final Logger TC_LOGGER = LogManager.getLogger();

    public TakumiCraftCore() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        TCConfigCore.register();
        modEventBus.addListener(this::registerProviders);
        modEventBus.addListener(this::complete);
        TCBiomeModifierCore.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(TCEvents.INSTANCE);
        if (FMLEnvironment.dist.isClient()) {
            MinecraftForge.EVENT_BUS.register(TCClientEvents.INSTANCE);
        }
    }

    //DO NOT REFER TO CONFIG
    private void setup(final FMLCommonSetupEvent event) {
    }

    private void complete(FMLLoadCompleteEvent event) {
    }

    private void registerProviders(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        gen.addProvider(event.includeClient(), new TCBlockStateProvider(packOutput, fileHelper));
        gen.addProvider(event.includeClient(), new TCItemModelProvider(packOutput, fileHelper));
        gen.addProvider(event.includeClient(), new TCLanguageProvider.TCEnUSLanguageProvider(gen));
        gen.addProvider(event.includeClient(), new TCLanguageProvider.TCJaJPLanguageProvider(gen));
        TCBlockTagsProvider blockTagsProvider = new TCBlockTagsProvider(packOutput, lookupProvider, fileHelper);
        gen.addProvider(event.includeServer(), blockTagsProvider);
        gen.addProvider(event.includeServer(), new TCItemTagsProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), fileHelper));
        gen.addProvider(event.includeServer(), new TCEntityTypeTagsProvider(packOutput, lookupProvider, fileHelper));
        gen.addProvider(event.includeServer(), new TCRecipeProvider(packOutput, lookupProvider));
        gen.addProvider(event.includeServer(), new TCLootTableProvider(packOutput, lookupProvider));
        gen.addProvider(event.includeServer(), new TCAdvancementProvider(packOutput, lookupProvider,
                fileHelper, List.of(new TCAdvancementProvider.TCAdvancementGenerator())));
        gen.addProvider(event.includeServer(), new TCOreGenProvider(packOutput, lookupProvider));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(modid = TakumiCraftCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void OnRegistry(final RegisterEvent event) {
            if (ForgeRegistries.BLOCKS.equals(event.getForgeRegistry())) {
                TCBlockCore.register(event);
            }
            if (ForgeRegistries.ITEMS.equals(event.getForgeRegistry())) {
                TCItemCore.register(event);
            }
            if (ForgeRegistries.ENTITY_TYPES.equals(event.getForgeRegistry())) {
                TCEntityCore.registerEntityType(event);
            }
            if (ForgeRegistries.ENCHANTMENTS.equals(event.getForgeRegistry())) {
                TCEnchantmentCore.register(event);
            }
            if (BuiltInRegistries.CREATIVE_MODE_TAB.equals(event.getVanillaRegistry())) {
                TCCreativeModeTabCore.register(event);
            }
            if (ForgeRegistries.RECIPE_SERIALIZERS.equals(event.getForgeRegistry())) {
                TCRecipeSerializerCore.register(event);
            }
            if (ForgeRegistries.PARTICLE_TYPES.equals(event.getForgeRegistry())) {
                TCParticleTypeCore.register(event);
            }
            if (ForgeRegistries.BLOCK_ENTITY_TYPES.equals(event.getForgeRegistry())) {
                TCBlockEntityCore.register(event);
            }
        }

        @SubscribeEvent
        public static void registerEntityAttribute(EntityAttributeCreationEvent event) {
            TCEntityCore.registerAttribute(event);
        }

        @SubscribeEvent
        public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
            TCEntityCore.registerSpawn(event);
        }

        @SubscribeEvent
        public static void registerCreativeModeTabItems(BuildCreativeModeTabContentsEvent event) {
            TCCreativeModeTabCore.registerItems(event);
        }
    }

    @Mod.EventBusSubscriber(modid = TakumiCraftCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class RegistryClientEvents {

        @SubscribeEvent
        public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions layerRegister) {
            TCRenderCore.registerLayerDefinition(layerRegister);
        }

        @SubscribeEvent
        public static void registerEntityRender(EntityRenderersEvent.RegisterRenderers rendererRegister) {
            TCRenderCore.registerEntityRender(rendererRegister);
        }

        @SubscribeEvent
        public static void clientInit(FMLClientSetupEvent event) {
            TCSearchTreeCore.register();
            TCItemPropertyCore.register();
        }

        @SubscribeEvent
        public static void registerKeyBinding(RegisterKeyMappingsEvent event) {
            TCKeyBindingCore.register(event);
        }

        @SubscribeEvent
        public static void registerParticleEngine(RegisterParticleProvidersEvent event) {
            TCParticleTypeCore.registerParticleEngine(event);
        }

        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            TCColorsCore.registerItemColors(event);
        }
    }
}
