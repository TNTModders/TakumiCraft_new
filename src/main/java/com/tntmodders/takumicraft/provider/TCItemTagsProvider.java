package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TCItemTagsProvider extends ItemTagsProvider {
    public TCItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, TakumiCraftCore.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        TCLoggingUtils.startRegistry("Item Tag");
        List<Field> fieldList = Arrays.asList(TCItemCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof Item && obj instanceof ITCItems item) {
                    if (!item.getItemTags().isEmpty()) {
                        item.getItemTags().forEach(itemTagKey -> {
                            this.tag(itemTagKey).add(((Item) item));
                            TCLoggingUtils.entryRegistry("Item Tag", item.getRegistryName() + " as " + itemTagKey);
                        });
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        List<Field> fieldList2 = Arrays.asList(TCBlockCore.class.getDeclaredFields());
        NonNullList<TagKey<Item>> blockTagCopyList = NonNullList.create();
        fieldList2.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof Block && obj instanceof ITCBlocks block) {
                    if (!block.getItemTags().isEmpty()) {
                        block.getItemTags().forEach(pair -> {
                            if (!blockTagCopyList.contains(pair.second)) {
                                this.copy(pair.first, pair.second);
                                blockTagCopyList.add(pair.second);
                                TCLoggingUtils.entryRegistry("Item Tag", block.getRegistryName() + " as " + pair.second);
                            }
                        });
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        TCLoggingUtils.completeRegistry("Item Tag");
    }
}
