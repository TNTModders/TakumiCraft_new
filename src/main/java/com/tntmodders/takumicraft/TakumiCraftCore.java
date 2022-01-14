package com.tntmodders.takumicraft;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCEvents;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.core.client.TCRenderCore;
import com.tntmodders.takumicraft.provider.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TakumiCraftCore.MODID)
public class TakumiCraftCore {
    public static final String MODID = "takumicraft";
    public static final Logger TC_LOGGER = LogManager.getLogger();

    public TakumiCraftCore() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::registerProviders);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(TCEvents.INSTANCE);
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

    @Mod.EventBusSubscriber(modid = TakumiCraftCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegister) {
            TCBlockCore.register(blockRegister);
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegister) {
            TCItemCore.register(itemRegister);
        }

        @SubscribeEvent
        public static void onEntitiesRegistry(final RegistryEvent.Register<EntityType<?>> entityTypeRegister) {
            TCEntityCore.registerEntityType(entityTypeRegister);
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
    }
}
