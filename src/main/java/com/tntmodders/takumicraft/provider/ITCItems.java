package com.tntmodders.takumicraft.provider;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public interface ITCItems extends ITCTranslator {

    String getRegistryName();

    default boolean hideOnCreativeTab() {
        return false;
    }

    default boolean isSPOnCreativeTab() {
        return false;
    }

    default void performSPOnCreativeTab(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
    }

    default List<TagKey<Item>> getItemTags() {
        return new ArrayList<>();
    }

    default void registerItemModel(TCItemModelProvider provider) {
        if (this instanceof Item item) {
            provider.singleItem(item, "generated");
        }
    }
}
