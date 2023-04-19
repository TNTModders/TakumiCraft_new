package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.item.TCSpawnEggItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class TCCreativeModeTabCore {
    public static CreativeModeTab TAB_TC;
    public static CreativeModeTab TAB_EGGS;

    public static void register(CreativeModeTabEvent.Register event) {
        TAB_TC = event.registerCreativeModeTab(new ResourceLocation(TakumiCraftCore.MODID, "takumicraft"),
                builder -> builder.title(Component.translatable("item_group.takumicraft"))
                        .icon(() -> new ItemStack(TCBlockCore.CREEPER_BOMB))
                        .displayItems((params, output) -> {
                            TCItemCore.ITEMS.forEach(item -> {
                                if (!(item instanceof SpawnEggItem)) {
                                    output.accept(item);
                                }
                            });
                            TCItemCore.BLOCKITEMS.values().forEach(output::accept);
                        }));
        TAB_EGGS = event.registerCreativeModeTab(new ResourceLocation(TakumiCraftCore.MODID, "takumicraft.egg"),
                builder -> builder.title(Component.translatable("item_group.takumicraft.egg"))
                        .icon(() -> new ItemStack(Items.CREEPER_SPAWN_EGG))
                        .displayItems((params, output) -> ForgeRegistries.ITEMS.forEach(item -> {
                            if (item instanceof TCSpawnEggItem) {
                                output.accept(item);
                            }
                        })));
    }

    public static void registerItems(CreativeModeTabEvent.BuildContents event) {
    }
}
