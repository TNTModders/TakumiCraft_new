package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.*;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
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
    public static final Map<DyeColor, TCCreeperCarpetBlock> CREEPER_CARPET_MAP = new HashMap<>();
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

    public static final TCCreeperCopperBlock CREEPER_COPPER = new TCCreeperCopperBlock(WeatheringCopper.WeatherState.UNAFFECTED, Blocks.WAXED_COPPER_BLOCK);
    public static final TCCreeperCopperBlock CREEPER_COPPER_EXPOSED = new TCCreeperCopperBlock(WeatheringCopper.WeatherState.EXPOSED, Blocks.WAXED_EXPOSED_COPPER);
    public static final TCCreeperCopperBlock CREEPER_COPPER_WEATHERED = new TCCreeperCopperBlock(WeatheringCopper.WeatherState.WEATHERED, Blocks.WAXED_WEATHERED_COPPER);
    public static final TCCreeperCopperBlock CREEPER_COPPER_OXIDIZED = new TCCreeperCopperBlock(WeatheringCopper.WeatherState.OXIDIZED, Blocks.WAXED_OXIDIZED_COPPER);
    public static final Block CREEPER_COPPER_CHISELED = new TCCreeperChiseledCopperBlock(WeatheringCopper.WeatherState.UNAFFECTED, Blocks.WAXED_CHISELED_COPPER, CREEPER_COPPER);
    public static final Block CREEPER_COPPER_CHISELED_EXPOSED = new TCCreeperChiseledCopperBlock(WeatheringCopper.WeatherState.EXPOSED, Blocks.WAXED_EXPOSED_CHISELED_COPPER, CREEPER_COPPER_EXPOSED);
    public static final Block CREEPER_COPPER_CHISELED_WEATHERED = new TCCreeperChiseledCopperBlock(WeatheringCopper.WeatherState.WEATHERED, Blocks.WAXED_WEATHERED_CHISELED_COPPER, CREEPER_COPPER_WEATHERED);
    public static final Block CREEPER_COPPER_CHISELED_OXIDIZED = new TCCreeperChiseledCopperBlock(WeatheringCopper.WeatherState.OXIDIZED, Blocks.WAXED_OXIDIZED_CHISELED_COPPER, CREEPER_COPPER_WEATHERED);
    public static final Block CREEPER_COPPER_GRATE = new TCCreeperCopperGrateBlock(WeatheringCopper.WeatherState.UNAFFECTED, Blocks.COPPER_GRATE, CREEPER_COPPER);
    public static final Block CREEPER_COPPER_GRATE_EXPOSED = new TCCreeperCopperGrateBlock(WeatheringCopper.WeatherState.EXPOSED, Blocks.EXPOSED_COPPER_GRATE, CREEPER_COPPER_EXPOSED);
    public static final Block CREEPER_COPPER_GRATE_WEATHERED = new TCCreeperCopperGrateBlock(WeatheringCopper.WeatherState.WEATHERED, Blocks.WEATHERED_COPPER_GRATE, CREEPER_COPPER_WEATHERED);
    public static final Block CREEPER_COPPER_GRATE_OXIDIZED = new TCCreeperCopperGrateBlock(WeatheringCopper.WeatherState.OXIDIZED, Blocks.OXIDIZED_COPPER_GRATE, CREEPER_COPPER_OXIDIZED);
    public static final Block CREEPER_COPPER_CUT = new TCCreeperCutCopperBlock(WeatheringCopper.WeatherState.UNAFFECTED, Blocks.WAXED_CUT_COPPER, CREEPER_COPPER);
    public static final Block CREEPER_COPPER_CUT_HALF = new TCAntiExplosionHalfBlock(CREEPER_COPPER_CUT::defaultBlockState, true, "creepercopper_cut_half");
    public static final Block CREEPER_COPPER_CUT_STAIRS = new TCAntiExplosionStairsBlock(CREEPER_COPPER_CUT::defaultBlockState, true, "creepercopper_cut_stairs");
    public static final Block CREEPER_COPPER_CUT_WALL = new TCAntiExplosionWallBlock(CREEPER_COPPER_CUT::defaultBlockState, "creepercopper_cut_wall");
    public static final Block CREEPER_COPPER_CUT_EXPOSED = new TCCreeperCutCopperBlock(WeatheringCopper.WeatherState.EXPOSED, Blocks.WAXED_EXPOSED_CUT_COPPER, CREEPER_COPPER_EXPOSED);
    public static final Block CREEPER_COPPER_CUT_EXPOSED_HALF = new TCAntiExplosionHalfBlock(CREEPER_COPPER_CUT_EXPOSED::defaultBlockState, true, "creepercopper_cut_half");
    public static final Block CREEPER_COPPER_CUT_EXPOSED_STAIRS = new TCAntiExplosionStairsBlock(CREEPER_COPPER_CUT_EXPOSED::defaultBlockState, true, "creepercopper_cut_stairs");
    public static final Block CREEPER_COPPER_CUT_EXPOSED_WALL = new TCAntiExplosionWallBlock(CREEPER_COPPER_CUT_EXPOSED::defaultBlockState, "creepercopper_cut_wall");
    public static final Block CREEPER_COPPER_CUT_WEATHERED = new TCCreeperCutCopperBlock(WeatheringCopper.WeatherState.WEATHERED, Blocks.WAXED_WEATHERED_CUT_COPPER, CREEPER_COPPER_WEATHERED);
    public static final Block CREEPER_COPPER_CUT_WEATHERED_HALF = new TCAntiExplosionHalfBlock(CREEPER_COPPER_CUT_WEATHERED::defaultBlockState, true, "creepercopper_cut_half");
    public static final Block CREEPER_COPPER_CUT_WEATHERED_STAIRS = new TCAntiExplosionStairsBlock(CREEPER_COPPER_CUT_WEATHERED::defaultBlockState, true, "creepercopper_cut_stairs");
    public static final Block CREEPER_COPPER_CUT_WEATHERED_WALL = new TCAntiExplosionWallBlock(CREEPER_COPPER_CUT_WEATHERED::defaultBlockState, "creepercopper_cut_wall");
    public static final Block CREEPER_COPPER_CUT_OXIDIZED = new TCCreeperCutCopperBlock(WeatheringCopper.WeatherState.OXIDIZED, Blocks.WAXED_OXIDIZED_CUT_COPPER, CREEPER_COPPER_OXIDIZED);
    public static final Block CREEPER_COPPER_CUT_OXIDIZED_HALF = new TCAntiExplosionHalfBlock(CREEPER_COPPER_CUT_OXIDIZED::defaultBlockState, true, "creepercopper_cut_half");
    public static final Block CREEPER_COPPER_CUT_OXIDIZED_STAIRS = new TCAntiExplosionStairsBlock(CREEPER_COPPER_CUT_OXIDIZED::defaultBlockState, true, "creepercopper_cut_stairs");
    public static final Block CREEPER_COPPER_CUT_OXIDIZED_WALL = new TCAntiExplosionWallBlock(CREEPER_COPPER_CUT_OXIDIZED::defaultBlockState, "creepercopper_cut_wall");
    public static final Block CREEPER_COPPER_TRAPDOOR = new TCAntiExplosionTrapDoorBlock(CREEPER_COPPER::defaultBlockState, BlockSetType.COPPER, true, "creepercopper_trapdoor");
    public static final Block CREEPER_COPPER_EXPOSED_TRAPDOOR = new TCAntiExplosionTrapDoorBlock(CREEPER_COPPER_EXPOSED::defaultBlockState, BlockSetType.COPPER, true, "creepercopper_trapdoor");
    public static final Block CREEPER_COPPER_WEATHERED_TRAPDOOR = new TCAntiExplosionTrapDoorBlock(CREEPER_COPPER_WEATHERED::defaultBlockState, BlockSetType.COPPER, true, "creepercopper_trapdoor");
    public static final Block CREEPER_COPPER_OXIDIZED_TRAPDOOR = new TCAntiExplosionTrapDoorBlock(CREEPER_COPPER_OXIDIZED::defaultBlockState, BlockSetType.COPPER, true, "creepercopper_trapdoor");
    public static final Block CREEPER_COPPER_DOOR = new TCAntiExplosionDoorBlock(CREEPER_COPPER::defaultBlockState, BlockSetType.COPPER, "creepercopper_door");
    public static final Block CREEPER_COPPER_EXPOSED_DOOR = new TCAntiExplosionDoorBlock(CREEPER_COPPER_EXPOSED::defaultBlockState, BlockSetType.COPPER, "creepercopper_door");
    public static final Block CREEPER_COPPER_WEATHERED_DOOR = new TCAntiExplosionDoorBlock(CREEPER_COPPER_WEATHERED::defaultBlockState, BlockSetType.COPPER, "creepercopper_door");
    public static final Block CREEPER_COPPER_OXIDIZED_DOOR = new TCAntiExplosionDoorBlock(CREEPER_COPPER_OXIDIZED::defaultBlockState, BlockSetType.COPPER, "creepercopper_door");

    public static final Block CREEPER_CAMPFIRE = new TCCreeperCampFireBlock();
    public static final Block CREEPER_TORCH = new TCCreeperTorchBlock();
    public static final Block CREEPER_TORCH_WALL = new TCCreeperTorchBlock_Wall();
    public static final Block CREEPER_SIGN = new TCCreeperSignBlock();
    public static final Block CREEPER_SIGN_WALL = new TCCreeperSignBlock_Wall();
    public static final Block CREEPER_HANGING_SIGN = new TCCreeperHangingSignBlock();
    public static final Block CREEPER_HANGING_SIGN_WALL = new TCCreeperHangingSignBlock_Wall();
    public static final Block CREEPER_SHULKER = new TCCreeperShulkerBoxBlock();
    public static final Block SUPER_BLOCK = new TCCreeperSuperBlock();
    public static final Block TAKENOKO = new TCTakenokoBlock();
    public static final Block HOTSPRING = new TCHotspringBlock();
    public static final Block GUNORE_CREEPER = new TCGunOreCreeperBlock();
    public static final Block FALLING_BOMB = new TCFallingBombBlock();
    public static final Block CREEPER_PROTECTOR = new TCCreeperProtectorBlock();

    public static final TagKey<Block> GUNORES = TagKey.create(Registries.BLOCK, ResourceLocation.tryBuild(TakumiCraftCore.MODID, "gunores"));
    public static final TagKey<Block> ANTI_EXPLOSION = TagKey.create(Registries.BLOCK, ResourceLocation.tryBuild(TakumiCraftCore.MODID, "anti_explosion"));
    public static final TagKey<Block> CREEPER_BED = TagKey.create(Registries.BLOCK, ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeperbed"));
    public static final TagKey<Block> EXPLOSIVES = TagKey.create(Registries.BLOCK, ResourceLocation.tryBuild(TakumiCraftCore.MODID, "explosives"));

    static {
        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            CREEPER_COLORED_GLASS_MAP.put(dyeColor, new TCColoredCreeperGlassBlock(dyeColor));
            CREEPER_COLORED_GLASS_PANE_MAP.put(dyeColor, new TCColoredCreeperGlassPaneBlock(dyeColor));
            CREEPER_WOOL_MAP.put(dyeColor, new TCWoolBlock(dyeColor));
            CREEPER_CARPET_MAP.put(dyeColor, new TCCreeperCarpetBlock(dyeColor));
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
                    event.register(ForgeRegistries.BLOCKS.getRegistryKey(), blockRegisterHelper -> blockRegisterHelper.register(ResourceLocation.tryBuild(TakumiCraftCore.MODID, ((ITCBlocks) block).getRegistryName()), block));
                    BLOCKS.add(block);
                    TCLoggingUtils.entryRegistry("Block", ((ITCBlocks) block).getRegistryName());
                } else if (obj instanceof Map map) {
                    map.values().forEach(value -> {
                        if (value instanceof ITCBlocks && value instanceof Block block) {
                            event.register(ForgeRegistries.BLOCKS.getRegistryKey(), blockRegisterHelper -> blockRegisterHelper.register(ResourceLocation.tryBuild(TakumiCraftCore.MODID, ((ITCBlocks) block).getRegistryName()), block));
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

    public static BlockBehaviour.Properties variant(Block p_364015_, boolean p_361131_) {
        BlockBehaviour.Properties blockbehaviour$properties = p_364015_.properties();
        BlockBehaviour.Properties blockbehaviour$properties1 = BlockBehaviour.Properties.of().overrideLootTable(p_364015_.getLootTable());
        if (p_361131_) {
            blockbehaviour$properties1 = blockbehaviour$properties1.overrideDescription(p_364015_.getDescriptionId());
        }

        return blockbehaviour$properties1;
    }

    public static ResourceKey<Block> TCBlockId(String id) {
        return ResourceKey.create(Registries.BLOCK, ResourceLocation.tryBuild(TakumiCraftCore.MODID, id));
    }
}
