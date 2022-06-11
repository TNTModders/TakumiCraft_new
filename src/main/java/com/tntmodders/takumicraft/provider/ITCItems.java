package com.tntmodders.takumicraft.provider;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public interface ITCItems extends ITCTranslator {

    String getRegistryName();

    default List<TagKey<Item>> getItemTags() {
        return new ArrayList<>();
    }

    default EnumTCItemModelType getItemModelType() {
        return EnumTCItemModelType.SIMPLE;
    }

    enum EnumTCItemModelType {
        SIMPLE,
        SHIELD,
        SPAWN_EGG,
        NONE
    }
}
