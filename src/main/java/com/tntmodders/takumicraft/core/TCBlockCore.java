package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.*;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TCBlockCore {
    public static final NonNullList<Block> BLOCKS = NonNullList.create();
    public static final Block CREEPER_BOMB = new TCCreeperBombBlock();
    public static final Block GUNORE = new TCGunOreBlock();
    public static final Block DEEPSLATE_GUNORE = new TCDeepGunOreBlock();
    public static final Block CREEPER_IRON = new TCCreeperIronBlock();
    public static final Block CREEPER_IRON_STAIRS = new TCAntiExplosionStairsBlock(CREEPER_IRON::defaultBlockState, true);
    public static final Block CREEPER_IRON_SLAB = new TCAntiExplosionHalfBlock(CREEPER_IRON::defaultBlockState, true);
    public static final Block CREEPER_IRON_WALL = new TCAntiExplosionWallBlock(CREEPER_IRON::defaultBlockState);
    public static final Block CREEPER_BRICKS = new TCCreeperBricksBlock();
    public static final Block CREEPER_BRICKS_STAIRS = new TCAntiExplosionStairsBlock(CREEPER_BRICKS::defaultBlockState, true);
    public static final Block CREEPER_BRICKS_SLAB = new TCAntiExplosionHalfBlock(CREEPER_BRICKS::defaultBlockState, true);
    public static final Block CREEPER_BRICKS_WALL = new TCAntiExplosionWallBlock(CREEPER_BRICKS::defaultBlockState);
    public static final Block CREEPER_GLASS = new TCCreeperGlassBlock();
    public static final Map<DyeColor, TCColoredCreeperGlassBlock> CREEPER_COLORED_GLASS_MAP = new HashMap<>();
    public static final Block CREEPER_GLASS_PANE = new TCCreeperGlassPaneBlock(CREEPER_GLASS);
    public static final Map<DyeColor, TCColoredCreeperGlassPaneBlock> CREEPER_COLORED_GLASS_PANE_MAP = new HashMap<>();
    public static final Block CREEPER_TINTED_GLASS = new TCCreeperTintedGlassBlock();
    public static final Block CREEPER_LADDER = new TCLadderBlock();
    public static final Block CREEPER_SCAFFOLDING = new TCScaffoldingBlock();

    public static final TagKey<Block> GUNORES = TagKey.create(Registries.BLOCK, new ResourceLocation("forge", "gunores"));
    public static final TagKey<Block> ANTI_EXPLOSION = TagKey.create(Registries.BLOCK, new ResourceLocation("forge", "anti_explosion"));

    static {
        Arrays.stream(DyeColor.values()).forEach(dyeColor -> CREEPER_COLORED_GLASS_MAP.put(dyeColor, new TCColoredCreeperGlassBlock(dyeColor)));
        Arrays.stream(DyeColor.values()).forEach(dyeColor -> CREEPER_COLORED_GLASS_PANE_MAP.put(dyeColor, new TCColoredCreeperGlassPaneBlock(dyeColor)));
    }

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
                } else if (obj instanceof Map map) {
                    map.values().forEach(value -> {
                        if (value instanceof ITCBlocks && value instanceof Block block) {
                            event.register(ForgeRegistries.BLOCKS.getRegistryKey(), blockRegisterHelper -> blockRegisterHelper.register(new ResourceLocation(TakumiCraftCore.MODID, ((ITCBlocks) block).getRegistryName()), block));
                            BLOCKS.add(block);
                            TCLoggingUtils.entryRegistry("Block", ((ITCBlocks) block).getRegistryName());
                        }
                    });
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        TCLoggingUtils.completeRegistry("Block");
    }

    private static boolean always(BlockState p_50775_, BlockGetter p_50776_, BlockPos p_50777_) {
        return true;
    }

    public static boolean never(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_) {
        return false;
    }

    public static Boolean never(BlockState p_50779_, BlockGetter p_50780_, BlockPos p_50781_, EntityType<?> p_50782_) {
        return false;
    }
}
