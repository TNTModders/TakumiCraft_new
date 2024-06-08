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
import net.minecraft.world.level.block.state.properties.BlockSetType;
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
    public static final Block CREEPER_IRON_HALF = new TCAntiExplosionHalfBlock(CREEPER_IRON::defaultBlockState, true);
    public static final Block CREEPER_IRON_WALL = new TCAntiExplosionWallBlock(CREEPER_IRON::defaultBlockState);
    public static final Block CREEPER_IRON_DOOR = new TCAntiExplosionDoorBlock(CREEPER_IRON::defaultBlockState, BlockSetType.IRON);
    public static final Block CREEPER_IRON_TRAPDOOR = new TCAntiExplosionTrapDoorBlock(CREEPER_IRON::defaultBlockState, BlockSetType.IRON);
    public static final Block CREEPER_BRICKS = new TCCreeperBricksBlock();
    public static final Block CREEPER_BRICKS_STAIRS = new TCAntiExplosionStairsBlock(CREEPER_BRICKS::defaultBlockState, true);
    public static final Block CREEPER_BRICKS_HALF = new TCAntiExplosionHalfBlock(CREEPER_BRICKS::defaultBlockState, true);
    public static final Block CREEPER_BRICKS_WALL = new TCAntiExplosionWallBlock(CREEPER_BRICKS::defaultBlockState);
    public static final Block CREEPER_BRICKS_DOOR = new TCAntiExplosionDoorBlock(CREEPER_BRICKS::defaultBlockState, BlockSetType.STONE);
    public static final Block CREEPER_BRICKS_TRAPDOOR = new TCAntiExplosionTrapDoorBlock(CREEPER_BRICKS::defaultBlockState, BlockSetType.STONE, false);
    public static final Block CREEPER_GLASS = new TCCreeperGlassBlock();
    public static final Map<DyeColor, TCColoredCreeperGlassBlock> CREEPER_COLORED_GLASS_MAP = new HashMap<>();
    public static final Block CREEPER_GLASS_PANE = new TCCreeperGlassPaneBlock(CREEPER_GLASS);
    public static final Map<DyeColor, TCColoredCreeperGlassPaneBlock> CREEPER_COLORED_GLASS_PANE_MAP = new HashMap<>();
    public static final Block CREEPER_TINTED_GLASS = new TCCreeperTintedGlassBlock();
    public static final Block CREEPER_LADDER = new TCLadderBlock();
    public static final Block CREEPER_SCAFFOLDING = new TCScaffoldingBlock();
    public static final Block CREEPER_PLANKS = new TCCreeperPlanksBlock();
    public static final Block CREEPER_PLANKS_STAIRS = new TCAntiExplosionStairsBlock(CREEPER_PLANKS::defaultBlockState, false);
    public static final Block CREEPER_PLANKS_HALF = new TCAntiExplosionHalfBlock(CREEPER_PLANKS::defaultBlockState, false);
    public static final Block CREEPER_PLANKS_FENCE = new TCAntiExplosionFenceBlock(CREEPER_PLANKS::defaultBlockState);
    public static final Block CREEPER_PLANKS_FENCE_GATE = new TCAntiExplosionFenceGateBlock(CREEPER_PLANKS::defaultBlockState);
    public static final Block CREEPER_PLANKS_DOOR = new TCAntiExplosionDoorBlock(CREEPER_PLANKS::defaultBlockState, BlockSetType.OAK);
    public static final Block CREEPER_PLANKS_TRAPDOOR = new TCAntiExplosionTrapDoorBlock(CREEPER_PLANKS::defaultBlockState, BlockSetType.OAK);
    public static final Map<DyeColor, TCWoolBlock> CREEPER_WOOL_MAP = new HashMap<>();
    public static final Map<DyeColor, TCCarpetBlock> CREEPER_CARPET_MAP = new HashMap<>();
    public static final Map<DyeColor, TCCreeperBedBlock> CREEPER_BED_MAP = new HashMap<>();
    public static final Block SUPER_CREEPER_BED = new TCSuperCreeperBedBlock();
    public static final Block ACID = new TCAcidBlock();
    public static final Block YUKARI_DUMMY = new TCYukariDummyBlock();
    public static final Block YUKARI_BOMB = new TCMonsterBombBlock(TCEntityCore.YUKARI);
    public static final Block TAKUMI_ALTAR = new TCAltarBlock();
    public static final Block CREEPER_CHEST = new TCCreeperChestBlock();
    public static final Block CREEPER_BARREL = new TCCreeperBarrelBlock();
    public static final Block CREEPER_ICE = new TCCreeperIceBlock();
    public static final Block CREEPER_SLIME = new TCCreeperSlimeBlock();
    public static final Block CREEPER_CHAIN = new TCCreeperChainBlock();
    public static final Block CREEPER_LANTERN = new TCCreeperLanternBlock();
    public static final Block CREEPER_ILLUMINATION = new TCCreeperIlluminationBlock();
    public static final Block CREEPER_IRON_BARS = new TCCreeperIronBarsBlock();

    public static final TagKey<Block> GUNORES = TagKey.create(Registries.BLOCK, new ResourceLocation(TakumiCraftCore.MODID, "gunores"));
    public static final TagKey<Block> ANTI_EXPLOSION = TagKey.create(Registries.BLOCK, new ResourceLocation(TakumiCraftCore.MODID, "anti_explosion"));
    public static final TagKey<Block> CREEPER_BED = TagKey.create(Registries.BLOCK, new ResourceLocation(TakumiCraftCore.MODID, "creeperbed"));
    public static final TagKey<Block> EXPLOSIVES = TagKey.create(Registries.BLOCK, new ResourceLocation(TakumiCraftCore.MODID, "explosives"));

    static {
        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            CREEPER_COLORED_GLASS_MAP.put(dyeColor, new TCColoredCreeperGlassBlock(dyeColor));
            CREEPER_COLORED_GLASS_PANE_MAP.put(dyeColor, new TCColoredCreeperGlassPaneBlock(dyeColor));
            CREEPER_WOOL_MAP.put(dyeColor, new TCWoolBlock(dyeColor));
            CREEPER_CARPET_MAP.put(dyeColor, new TCCarpetBlock(dyeColor));
            CREEPER_BED_MAP.put(dyeColor, new TCCreeperBedBlock(dyeColor));
        });
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
