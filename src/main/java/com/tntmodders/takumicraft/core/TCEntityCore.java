package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.entity.misc.TCKingBlock;
import com.tntmodders.takumicraft.entity.misc.TCKingStorm;
import com.tntmodders.takumicraft.entity.mobs.*;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper.TCCreeperContext;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import com.tntmodders.takumicraft.entity.projectile.TCAmethystBomb;
import com.tntmodders.takumicraft.entity.projectile.TCBirdBomb;
import com.tntmodders.takumicraft.entity.projectile.TCCreeperArrow;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TCEntityCore {
    public static final NonNullList<EntityType<?>> ENTITY_TYPES = NonNullList.create();
    public static final NonNullList<TCCreeperContext<?>> ENTITY_CONTEXTS = NonNullList.create();

    public static final NonNullList<TCCreeperContext<?>> ALTAR_LIST = NonNullList.create();

    public static final TCCreeperContext<TCSilentCreeper> SILENT = new TCSilentCreeper.TCSilentCreeperContext();
    public static final TCCreeperContext<TCLavaCreeper> LAVA = new TCLavaCreeper.TCLavaCreeperContext();
    public static final TCCreeperContext<TCSandCreeper> SAND = new TCSandCreeper.TCSandCreeperContext();
    public static final TCCreeperContext<TCWaterCreeper> WATER = new TCWaterCreeper.TCWaterCreeperContext();
    public static final TCCreeperContext<TCIceCreeper> ICE = new TCIceCreeper.TCIceCreeperContext();
    public static final TCCreeperContext<TCFireCreeper> FIRE = new TCFireCreeper.TCFireCreeperContext();
    public static final TCCreeperContext<TCZombieCreeper> ZOMBIE = new TCZombieCreeper.TCZombieCreeperContext();
    public static final TCCreeperContext<TCZombieVillagerCreeper> ZOMBIE_VILLAGER = new TCZombieVillagerCreeper.TCZombieVillagerCreeperContext();
    public static final TCCreeperContext<TCDrownedCreeper> DROWNED = new TCDrownedCreeper.TCDrownedCreeperContext();
    public static final TCCreeperContext<TCEnderCreeper> ENDER = new TCEnderCreeper.TCEnderCreeperContext();
    public static final TCCreeperContext<TCLightCreeper> LIGHT = new TCLightCreeper.TCLightCreeperContext();
    public static final TCCreeperContext<TCSlimeCreeper> SLIME = new TCSlimeCreeper.TCSlimeCreeperContext();
    public static final TCCreeperContext<TCPhantomCreeper> PHANTOM = new TCPhantomCreeper.TCPhantomCreeperContext();
    public static final TCCreeperContext<TCNaturalCreeper> NATURAL = new TCNaturalCreeper.TCNaturalCreeperContext();
    public static final TCCreeperContext<TCBangCreeper> BANG = new TCBangCreeper.TCBangCreeperContext();
    public static final TCCreeperContext<TCHuskCreeper> HUSK = new TCHuskCreeper.TCHuskCreeperContext();
    public static final TCCreeperContext<TCHappinessCreeper> HAPPINESS = new TCHappinessCreeper.TCHappinessCreeperContext();
    public static final TCCreeperContext<TCCowCreeper> COW = new TCCowCreeper.TCCowCreeperContext();
    public static final TCCreeperContext<TCChildCreeper> CHILD = new TCChildCreeper.TCChildCreeperContext();
    public static final TCCreeperContext<TCCopperCreeper> COPPER = new TCCopperCreeper.TCCopperCreeperContext();
    public static final TCCreeperContext<TCPatinaCreeper> PATINA = new TCPatinaCreeper.TCPatinaCreeperContext();
    public static final TCCreeperContext<TCDiamondCreeper> DIAMOND = new TCDiamondCreeper.TCDiamondCreeperContext();
    public static final TCCreeperContext<TCRedstoneCreeper> REDSTONE = new TCRedstoneCreeper.TCRedstoneCreeperContext();
    public static final TCCreeperContext<TCEmeraldCreeper> EMERALD = new TCEmeraldCreeper.TCEmeraldCreeperContext();
    public static final TCCreeperContext<TCIronCreeper> IRON = new TCIronCreeper.TCIronCreeperContext();
    public static final TCCreeperContext<TCFallCreeper> FALL = new TCFallCreeper.TCFallCreeperContext();
    public static final TCCreeperContext<TCArrowCreeper> ARROW = new TCArrowCreeper.TCArrowCreeperContext();
    public static final TCCreeperContext<TCStoneCreeper> STONE = new TCStoneCreeper.TCStoneCreeperContext();
    public static final TCCreeperContext<TCWoodCreeper> WOOD = new TCWoodCreeper.TCWoodCreeperContext();
    public static final TCCreeperContext<TCDirtCreeper> DIRT = new TCDirtCreeper.TCDirtCreeperContext();
    public static final TCCreeperContext<TCObjetCreeper> OBJET = new TCObjetCreeper.TCObjetCreeperContext();
    public static final TCCreeperContext<TCGlassCreeper> GLASS = new TCGlassCreeper.TCGlassCreeperContext();
    public static final TCCreeperContext<TCDripstoneCreeper> DRIPSTONE = new TCDripstoneCreeper.TCDripstoneCreeperContext();
    public static final TCCreeperContext<TCAmethystCreeper> AMETHYST = new TCAmethystCreeper.TCAmethystCreeperContext();
    public static final TCCreeperContext<TCFireworksCreeper> FIREWORKS = new TCFireworksCreeper.TCFireworksCreeperContext();
    public static final TCCreeperContext<TCGlowstoneCreeper> GLOWSTONE = new TCGlowstoneCreeper.TCGlowstoneCreeperContext();
    public static final TCCreeperContext<TCPigCreeper> PIG = new TCPigCreeper.TCPigCreeperContext();
    public static final TCCreeperContext<TCHorseCreeper> HORSE = new TCHorseCreeper.TCHorseCreeperContext();
    public static final TCCreeperContext<TCRabbitCreeper> RABBIT = new TCRabbitCreeper.TCRabbitCreeperContext();
    public static final TCCreeperContext<TCSquidCreeper> SQUID = new TCSquidCreeper.TCSquidCreeperContext();
    public static final TCCreeperContext<TCGlowingSquidCreeper> GLOWING_SQUID = new TCGlowingSquidCreeper.TCSquidCreeperContext();
    public static final TCCreeperContext<TCSheepCreeper> SHEEP = new TCSheepCreeper.TCSheepCreeperContext();
    public static final TCCreeperContext<TCBirdCreeper> BIRD = new TCBirdCreeper.TCBirdCreeperContext();
    public static final TCCreeperContext<TCWolfCreeper> WOLF = new TCWolfCreeper.TCWolfCreeperContext();
    public static final TCCreeperContext<TCPowderSnowCreeper> POWDER_SNOW = new TCPowderSnowCreeper.TCPowderSnowCreeperContext();
    public static final TCCreeperContext<TCCherryCreeper> CHERRY = new TCCherryCreeper.TCCherryCreeperContext();
    public static final TCCreeperContext<TCAcidCreeper> ACID = new TCAcidCreeper.TCAcidCreeperContext();
    public static final TCCreeperContext<TCBoltCreeper> BOLT = new TCBoltCreeper.TCBoltCreeperContext();
    public static final TCCreeperContext<TCOfalenCreeper> OFALEN = new TCOfalenCreeper.TCOfalenCreeperContext();
    public static final TCCreeperContext<TCYukariCreeper> YUKARI = new TCYukariCreeper.TCYukariCreeperContext();
    public static final TCCreeperContext<TCArtCreeper> ART = new TCArtCreeper.TCArtCreeperContext();
    public static final TCCreeperContext<TCRewriteCreeper> REWRITE = new TCRewriteCreeper.TCRewriteCreeperContext();
    public static final TCCreeperContext<TCBedCreeper> BED = new TCBedCreeper.TCBedCreeperContext();
    public static final TCCreeperContext<TCReturnCreeper> RETURN = new TCReturnCreeper.TCReturnCreeperContext();
    public static final TCCreeperContext<TCSleeperCreeper> SLEEPER = new TCSleeperCreeper.TCSleeperCreeperContext();
    public static final TCCreeperContext<TCRestCreeper> REST = new TCRestCreeper.TCRestCreeperContext();
    public static final TCCreeperContext<TCMusicCreeper> MUSIC = new TCMusicCreeper.TCMusicCreeperContext();
    public static final TCCreeperContext<TCMuscleCreeper> MUSCLE = new TCMuscleCreeper.TCMuscleCreeperContext();
    public static final TCCreeperContext<TCSpiderCreeper> SPIDER = new TCSpiderCreeper.TCSpiderCreeperContext();
    public static final TCCreeperContext<TCMiniSpiderCreeper> MINI_SPIDER = new TCMiniSpiderCreeper.TCMiniSpiderCreeperContext();
    public static final TCCreeperContext<TCSkeletonCreeper> SKELETON = new TCSkeletonCreeper.TCSkeletonCreeperContext();
    public static final TCCreeperContext<TCStrayCreeper> STRAY = new TCStrayCreeper.TCStrayCreeperContext();
    public static final TCCreeperContext<TCWitherSkeletonCreeper> WITHER_SKELETON = new TCWitherSkeletonCreeper.TCWitherSkeletonCreeperContext();
    public static final TCCreeperContext<TCPotatoCreeper> POTATO = new TCPotatoCreeper.TCPotatoCreeperContext();
    public static final TCCreeperContext<TCKingCreeper> KING = new TCKingCreeper.TCKingCreeperContext();
    public static final TCCreeperContext<TCMaceCreeper> MACE = new TCMaceCreeper.TCMaceCreeperContext();
    public static final TCCreeperContext<TCNetheriteCreeper> NETHERITE = new TCNetheriteCreeper.TCNetheriteCreeperContext();
    public static final TCCreeperContext<TCSculkCreeper> SCULK = new TCSculkCreeper.TCSclukCreeperContext();

    public static final TagKey<EntityType<?>> TAKUMIS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TakumiCraftCore.MODID, "takumi"));
    public static final TagKey<EntityType<?>> NETHER_TAKUMIS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TakumiCraftCore.MODID, "nether_takumi"));
    public static final TagKey<EntityType<?>> END_TAKUMIS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TakumiCraftCore.MODID, "end_takumi"));

    public static void registerEntityType(RegisterEvent event) {
        TCLoggingUtils.startRegistry("Entity");
        List<Field> fieldList = Arrays.asList(TCEntityCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof TCCreeperContext<?> context) {
                    EntityType<?> type = context.entityType();
                    event.register(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), entityTypeRegisterHelper -> entityTypeRegisterHelper.register(new ResourceLocation(TakumiCraftCore.MODID, context.getRegistryName()), type));
                    ENTITY_TYPES.add(type);
                    ENTITY_CONTEXTS.add(context);
                    if (context.alterSpawn()) {
                        ALTAR_LIST.add(context);
                    }
                    TCLoggingUtils.entryRegistry("Entity", context.getRegistryName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        registerAdditionalEntityType(event);
        ENTITY_CONTEXTS.sort(Comparator.comparing(context -> context.getRank().getLevel()));
        TCConfigCore.registerSpawnConfig();
        TCLoggingUtils.logMessage("Entity", ENTITY_CONTEXTS.size() + " Takumis Registered.");
        TCLoggingUtils.completeRegistry("Entity");
    }

    private static void registerAdditionalEntityType(RegisterEvent event) {
        event.register(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), entityTypeRegisterHelper ->
                entityTypeRegisterHelper.register(new ResourceLocation(TakumiCraftCore.MODID, "creeperarrow"), TCCreeperArrow.ARROW));
        event.register(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), entityTypeRegisterHelper ->
                entityTypeRegisterHelper.register(new ResourceLocation(TakumiCraftCore.MODID, "amethystbomb"), TCAmethystBomb.AMETHYST_BOMB));
        event.register(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), entityTypeRegisterHelper ->
                entityTypeRegisterHelper.register(new ResourceLocation(TakumiCraftCore.MODID, "birdbomb"), TCBirdBomb.BIRD_BOMB));
        event.register(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), entityTypeRegisterHelper ->
                entityTypeRegisterHelper.register(new ResourceLocation(TakumiCraftCore.MODID, "king_storm"), TCKingStorm.KING_STORM));
        event.register(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), entityTypeRegisterHelper ->
                entityTypeRegisterHelper.register(new ResourceLocation(TakumiCraftCore.MODID, "king_block"), TCKingBlock.KING_BLOCK));
    }

    public static void registerAttribute(EntityAttributeCreationEvent event) {
        TCLoggingUtils.startRegistry("EntityAttribute");
        List<Field> fieldList = Arrays.asList(TCEntityCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof TCCreeperContext<?> context) {
                    EntityType<? extends LivingEntity> type = (EntityType<? extends LivingEntity>) context.entityType();
                    AttributeSupplier.Builder builder = context.entityAttribute();
                    event.put(type, builder.build());
                    TCLoggingUtils.entryRegistry("EntityAttribute", context.getRegistryName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        registerAdditionalAttribute(event);
        TCLoggingUtils.completeRegistry("EntityAttribute");
    }

    private static void registerAdditionalAttribute(EntityAttributeCreationEvent event) {
        event.put(TCKingStorm.KING_STORM, TCKingStorm.createAttributes().build());
    }

    public static void registerSpawn(SpawnPlacementRegisterEvent event) {
        TCLoggingUtils.startRegistry("EntitySpawn");
        List<Field> fieldList = Arrays.asList(TCEntityCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof TCCreeperContext<?> context) {
                    EntityType<? extends LivingEntity> type = (EntityType<? extends LivingEntity>) context.entityType();
                    if (context.registerSpawn(event, (EntityType<AbstractTCCreeper>) type)) {
                        TCLoggingUtils.logMessage("EntitySpawn", context.getEnUSName());
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        TCLoggingUtils.completeRegistry("EntitySpawn");
    }
}
