package com.tntmodders.takumicraft;

import com.tntmodders.takumicraft.core.*;
import com.tntmodders.takumicraft.core.client.TCKeyBindingCore;
import com.tntmodders.takumicraft.core.client.TCRenderCore;
import com.tntmodders.takumicraft.core.client.TCSearchTreeCore;
import com.tntmodders.takumicraft.event.TCEvents;
import com.tntmodders.takumicraft.event.client.TCClientEvents;
import com.tntmodders.takumicraft.provider.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(TCEvents.INSTANCE);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.register(TCClientEvents.INSTANCE);
        }
        TCBiomeModifierCore.register(modEventBus);
    }

    //DO NOT REFER TO CONFIG
    private void setup(final FMLCommonSetupEvent event) {
    }

    private void complete(FMLLoadCompleteEvent event) {
    }

    private void registerProviders(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        gen.addProvider(event.includeClient(), new TCBlockStateProvider(gen, event.getExistingFileHelper()));
        gen.addProvider(event.includeClient(), new TCItemModelProvider(gen, event.getExistingFileHelper()));
        gen.addProvider(event.includeClient(), new TCLanguageProvider.TCEnUSLanguageProvider(gen));
        gen.addProvider(event.includeClient(), new TCLanguageProvider.TCJaJPLanguageProvider(gen));

        BlockTagsProvider blockTagsProvider = new TCBlockTagsProvider(gen, event.getExistingFileHelper());
        gen.addProvider(event.includeServer(), blockTagsProvider);
        gen.addProvider(event.includeServer(), new TCItemTagsProvider(gen, blockTagsProvider, event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new TCRecipeProvider(gen));
        gen.addProvider(event.includeServer(), new TCLootTableProvider(gen));
        gen.addProvider(event.includeServer(), new TCAdvancementProvider(gen, event.getExistingFileHelper()));
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
            if (ForgeRegistries.ENTITIES.equals(event.getForgeRegistry())) {
                TCEntityCore.registerEntityType(event);
            }
            if (ForgeRegistries.FEATURES.equals(event.getForgeRegistry())) {
                TCBiomeModifierCore.registerModifiers(event);
            }
        }

        @SubscribeEvent
        public static void registerEntityAttribute(EntityAttributeCreationEvent event) {
            TCEntityCore.registerAttribute(event);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(modid = TakumiCraftCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class RegistryClientEvents {
        @SubscribeEvent
        public static void registerEntityRender(EntityRenderersEvent.RegisterRenderers rendererRegister) {
            TCRenderCore.registerEntityRender(rendererRegister);
        }

        @SubscribeEvent
        public static void ClientInit(FMLClientSetupEvent event) {
            TCKeyBindingCore.register();
            TCSearchTreeCore.register();
        }
    }
}
