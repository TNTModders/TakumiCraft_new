package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class TCBlockTagsProvider extends BlockTagsProvider {

    public TCBlockTagsProvider(DataGenerator p_126511_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126511_, TakumiCraftCore.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        TCLoggingUtils.startRegistry("Block Tag");
        List<Field> fieldList = Arrays.asList(TCBlockCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof Block && obj instanceof ITCBlocks block) {
                    if (!block.getBlockTags().isEmpty()) {
                        block.getBlockTags().forEach(blockTagKey -> {
                            this.tag(blockTagKey).add(((Block) block));
                            TCLoggingUtils.entryRegistry("Block Tag", block.getRegistryName() + " as " + blockTagKey);
                        });

                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        TCLoggingUtils.completeRegistry("Block Tag");
    }
}
