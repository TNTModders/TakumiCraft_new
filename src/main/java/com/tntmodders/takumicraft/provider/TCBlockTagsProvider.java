package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TCBlockTagsProvider extends BlockTagsProvider {

    public TCBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TakumiCraftCore.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        TCLoggingUtils.startRegistry("Block Tag");
        List<Field> fieldList = Arrays.asList(TCBlockCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof Block && obj instanceof ITCBlocks block) {
                    if (!block.getBlockTags().isEmpty()) {
                        block.getBlockTags().forEach(blockTagKey -> {
                            this.tag(blockTagKey).add((Block) block);
                            TCLoggingUtils.entryRegistry("Block Tag", block.getRegistryName() + " as " + blockTagKey);
                        });
                    }
                } else if (obj instanceof Map map) {
                    map.values().forEach(value -> {
                        if (value instanceof ITCBlocks block) {
                            block.getBlockTags().forEach(blockTagKey -> {
                                this.tag(blockTagKey).add((Block) block);
                                TCLoggingUtils.entryRegistry("Block Tag", block.getRegistryName() + " as " + blockTagKey);
                            });
                        }
                    });
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        this.additionalTag();
        TCLoggingUtils.completeRegistry("Block Tag");
    }

    private void additionalTag() {
        this.tag(TCBlockCore.ANTI_EXPLOSION).add(Blocks.BEDROCK, Blocks.END_PORTAL, Blocks.END_PORTAL_FRAME, Blocks.COMMAND_BLOCK, Blocks.BARRIER, Blocks.LIGHT,
                Blocks.END_GATEWAY, Blocks.REPEATING_COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.STRUCTURE_BLOCK, Blocks.JIGSAW);
    }
}
