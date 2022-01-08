package com.tntmodders.takumicraft;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TakumiCraftCore.MOD_ID)
public class TakumiCraftCore {
    public static final String MOD_ID = "takumicraft";
    public static final Logger TC_LOGGER = LogManager.getLogger();

    public TakumiCraftCore() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::registerProviders);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

    private void registerProviders(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        if (event.includeClient()) {
            gen.addProvider(new TCBlockStateProvider(gen, event.getExistingFileHelper()));
            gen.addProvider(new TCItemModelProvider(gen, event.getExistingFileHelper()));
            gen.addProvider(new TCLanguageProvider.TCEnUSLanguageProvider(gen));
            gen.addProvider(new TCLanguageProvider.TCJaJPLanguageProvider(gen));
        }
        if (event.includeServer()) {
            gen.addProvider(new TCRecipeProvider(gen));
            gen.addProvider(new TCLootTableProvider(gen));
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            TCBlockCore.register(blockRegistryEvent);
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            TCItemCore.register(itemRegistryEvent);
        }
    }
}
