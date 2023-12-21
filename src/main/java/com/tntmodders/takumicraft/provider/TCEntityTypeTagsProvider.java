package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TCEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public TCEntityTypeTagsProvider(PackOutput p_256095_, CompletableFuture<HolderLookup.Provider> p_256572_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_256095_, p_256572_, TakumiCraftCore.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        TCLoggingUtils.startRegistry("Entity Tag");
        List<Field> fieldList = Arrays.asList(TCEntityCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof AbstractTCCreeper.TCCreeperContext<?> context) {
                    if (!context.getEntityTypeTags().isEmpty()) {
                        context.getEntityTypeTags().forEach(blockTagKey -> {
                            this.tag(blockTagKey).add(context.entityType());
                            TCLoggingUtils.entryRegistry("Entity Tag", context.getRegistryName() + " as " + blockTagKey);
                        });
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        this.additionalTag();
        TCLoggingUtils.completeRegistry("Entity Tag");
    }

    private void additionalTag() {
    }
}
