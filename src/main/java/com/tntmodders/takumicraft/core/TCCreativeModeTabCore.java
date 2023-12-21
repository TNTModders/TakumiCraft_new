package com.tntmodders.takumicraft.core;

import com.google.common.collect.Lists;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.item.TCSpawnEggItem;
import com.tntmodders.takumicraft.provider.ITCItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class TCCreativeModeTabCore {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), TakumiCraftCore.MODID);
    public static CreativeModeTab TAB_TC;
    public static CreativeModeTab TAB_EGGS;

    public static void register(final RegisterEvent event) {
        TAB_TC = CreativeModeTab.builder().title(Component.translatable("item_group.takumicraft")).icon(() -> new ItemStack(TCBlockCore.CREEPER_BOMB)).displayItems((params, output) -> {
            List<Item> itemList = TCItemCore.ITEMS;
            itemList.sort(Comparator.comparing(Item::getDescriptionId));
            itemList.forEach(item -> {
                if (!(item instanceof SpawnEggItem) && !(item instanceof ITCItems && ((ITCItems) item).hideOnCreativeTab())) {
                    output.accept(item);
                }
            });
            Collection<TCBlockItem> itemCollection = TCItemCore.BLOCKITEMS.values();
            List<TCBlockItem> blockItemList = Lists.newArrayList(itemCollection);
            blockItemList.sort(Comparator.comparing(o -> o.getBlock().getDescriptionId()));
            blockItemList.forEach(item -> {
                if (!item.hideOnCreativeTab()) {
                    output.accept(item);
                }
            });

        }).build();
        event.register(BuiltInRegistries.CREATIVE_MODE_TAB.key(), tabRegistryHelper -> tabRegistryHelper.register(new ResourceLocation(TakumiCraftCore.MODID, "takumicraft"), TAB_TC));
        TAB_EGGS = CreativeModeTab.builder().title(Component.translatable("item_group.takumicraft.egg")).icon(() -> new ItemStack(Items.CREEPER_SPAWN_EGG)).displayItems((params, output) -> ForgeRegistries.ITEMS.forEach(item -> {
            if (item instanceof TCSpawnEggItem) {
                output.accept(item);
            }
        })).build();
        event.register(BuiltInRegistries.CREATIVE_MODE_TAB.key(), tabRegistryHelper -> tabRegistryHelper.register(new ResourceLocation(TakumiCraftCore.MODID, "takumicraft.egg"), TAB_EGGS));
    }

    public static void registerItems(BuildCreativeModeTabContentsEvent event) {
    }
}
