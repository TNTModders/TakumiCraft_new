package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.item.TCSpawnEggItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

public class TCCreativeModeTabCore {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), TakumiCraftCore.MODID);
    public static CreativeModeTab TAB_TC;
    public static CreativeModeTab TAB_EGGS;

    public static void register(final RegisterEvent event) {
        TAB_TC = CreativeModeTab.builder()
                .title(Component.translatable("item_group.takumicraft"))
                .icon(() -> new ItemStack(TCBlockCore.CREEPER_BOMB))
                .displayItems((params, output) -> {
                    TCItemCore.ITEMS.forEach(item -> {
                        if (!(item instanceof SpawnEggItem)) {
                            output.accept(item);
                        }
                    });
                    TCItemCore.BLOCKITEMS.values().forEach(output::accept);
                }).build();
        event.register(BuiltInRegistries.CREATIVE_MODE_TAB.key(), tabRegistryHelper -> tabRegistryHelper.register(new ResourceLocation(TakumiCraftCore.MODID, "takumicraft"), TAB_TC));
        TAB_EGGS = CreativeModeTab.builder()
                .title(Component.translatable("item_group.takumicraft.egg"))
                .icon(() -> new ItemStack(Items.CREEPER_SPAWN_EGG))
                .displayItems((params, output) -> ForgeRegistries.ITEMS.forEach(item -> {
                    if (item instanceof TCSpawnEggItem) {
                        output.accept(item);
                    }
                })).build();
        event.register(BuiltInRegistries.CREATIVE_MODE_TAB.key(), tabRegistryHelper -> tabRegistryHelper.register(new ResourceLocation(TakumiCraftCore.MODID, "takumicraft.egg"), TAB_EGGS));
    }

    public static void registerItems(BuildCreativeModeTabContentsEvent event) {
    }
}
