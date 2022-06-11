package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.TCCreeperBombBlock;
import com.tntmodders.takumicraft.block.TCDeepGunOreBlock;
import com.tntmodders.takumicraft.block.TCGunOreBlock;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class TCBlockCore {
    public static final NonNullList<Block> BLOCKS = NonNullList.create();
    public static final Block CREEPER_BOMB = new TCCreeperBombBlock();
    public static final Block GUNORE = new TCGunOreBlock();
    public static final Block DEEPSLATE_GUNORE =new TCDeepGunOreBlock();

    public static final TagKey<Block> GUNORES=TagKey.create(Registry.BLOCK_REGISTRY,new ResourceLocation(TakumiCraftCore.MODID,"gunores"));

    public static void register(final RegisterEvent event) {
        TCLoggingUtils.startRegistry("Block");
        List<Field> fieldList = Arrays.asList(TCBlockCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof ITCBlocks && obj instanceof Block block) {
                    event.register(ForgeRegistries.BLOCKS.getRegistryKey(), blockRegisterHelper -> blockRegisterHelper.register(new ResourceLocation(TakumiCraftCore.MODID, ((ITCBlocks) block).getRegistryName()), block));
                    BLOCKS.add(block);
                    TCLoggingUtils.entryRegistry("Block", ((ITCBlocks) block).getRegistryName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        TCLoggingUtils.completeRegistry("Block");
    }
}
