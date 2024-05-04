package com.tntmodders.takumicraft.core;

import com.google.common.collect.Lists;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.item.TCSpawnEggItem;
import com.tntmodders.takumicraft.provider.ITCItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
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
                if (!(item instanceof ITCItems) || !((ITCItems) item).hideOnCreativeTab()) {
                    if (((ITCItems) item).isSPOnCreativeTab()) {
                        ((ITCItems) item).performSPOnCreativeTab(params, output);
                    } else {
                        output.accept(item);
                    }
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

    public static void generatePotionEffectTypes(
            CreativeModeTab.Output p_270129_, HolderLookup<Potion> p_270334_, Item p_270968_, CreativeModeTab.TabVisibility p_270778_, FeatureFlagSet p_331502_
    ) {
        p_270334_.listElements()
                .filter(p_327145_ -> p_327145_.value().isEnabled(p_331502_))
                .map(p_327116_ -> PotionContents.createItemStack(p_270968_, p_327116_))
                .forEach(p_270000_ -> p_270129_.accept(p_270000_, p_270778_));
    }
}
