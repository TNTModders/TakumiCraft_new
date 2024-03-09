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

    default void performSPOnCreativeTab(CreativeModeTab.Output output) {
    }

    default List<TagKey<Item>> getItemTags() {
        return new ArrayList<>();
    }

    default EnumTCItemModelType getItemModelType() {
        return EnumTCItemModelType.SIMPLE;
    }

    enum EnumTCItemModelType {
        SIMPLE,
        SP,
        SPAWN_EGG,
        HANDHELD,
        BOW,
        SPECIAL_MEAT,
        NONE
    }
}
