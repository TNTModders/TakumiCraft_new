package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.entity.mobs.*;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TCEntityCore {
    public static final NonNullList<EntityType<?>> ENTITY_TYPES = NonNullList.create();
    public static final NonNullList<AbstractTCCreeper.TCCreeperContext<?>> ENTITY_CONTEXTS = NonNullList.create();
    public static final AbstractTCCreeper.TCCreeperContext<TCSilentCreeper> SILENT = new TCSilentCreeper.TCSilentCreeperContext();
    public static final AbstractTCCreeper.TCCreeperContext<TCLavaCreeper> LAVA = new TCLavaCreeper.TCLavaCreeperContext();
    public static final AbstractTCCreeper.TCCreeperContext<TCSandCreeper> SAND = new TCSandCreeper.TCSandCreeperContext();
    public static final AbstractTCCreeper.TCCreeperContext<TCWaterCreeper> WATER = new TCWaterCreeper.TCWaterCreeperContext();
    public static final AbstractTCCreeper.TCCreeperContext<TCIceCreeper> ICE = new TCIceCreeper.TCIceCreeperContext();
    public static final AbstractTCCreeper.TCCreeperContext<TCFireCreeper> FIRE = new TCFireCreeper.TCFireCreeperContext();
    public static final AbstractTCCreeper.TCCreeperContext<TCZombieCreeper> ZOMBIE = new TCZombieCreeper.TCZombieCreeperContext();
    public static final AbstractTCCreeper.TCCreeperContext<TCZombieVillagerCreeper> ZOMBIE_VILLAGER = new TCZombieVillagerCreeper.TCZombieVillagerCreeperContext();
    public static final AbstractTCCreeper.TCCreeperContext<TCDrownedCreeper> DROWNED = new TCDrownedCreeper.TCDrownedCreeperContext();
    public static final AbstractTCCreeper.TCCreeperContext<TCEnderCreeper> ENDER = new TCEnderCreeper.TCEnderCreeperContext();
    public static final AbstractTCCreeper.TCCreeperContext<TCLightCreeper> LIGHT = new TCLightCreeper.TCLightCreeperContext();
    public static final AbstractTCCreeper.TCCreeperContext<TCSlimeCreeper> SLIME = new TCSlimeCreeper.TCSlimeCreeperContext();
    public static final AbstractTCCreeper.TCCreeperContext<TCPhantomCreeper> PHANTOM = new TCPhantomCreeper.TCPhantomCreeperContext();
    //public static final AbstractTCCreeper.TCCreeperContext<TCFireFlyCreeper>FIREFLY;
    public static final AbstractTCCreeper.TCCreeperContext<TCNaturalCreeper> NATURAL = new TCNaturalCreeper.TCNaturalCreeperContext();
    public static final AbstractTCCreeper.TCCreeperContext<TCBangCreeper> BANG = new TCBangCreeper.TCBangCreeperContext();
    public static final AbstractTCCreeper.TCCreeperContext<TCHuskCreeper> HUSK = new TCHuskCreeper.TCHuskCreeperContext();

    public static void registerEntityType(RegisterEvent event) {
        TCLoggingUtils.startRegistry("Entity");
        List<Field> fieldList = Arrays.asList(TCEntityCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof AbstractTCCreeper.TCCreeperContext<?> context) {
                    EntityType<?> type = context.entityType();
                    event.register(ForgeRegistries.ENTITIES.getRegistryKey(), entityTypeRegisterHelper -> entityTypeRegisterHelper.register(new ResourceLocation(TakumiCraftCore.MODID,context.getRegistryName()), type));
                    ENTITY_TYPES.add(type);
                    ENTITY_CONTEXTS.add(context);
                    TCLoggingUtils.entryRegistry("Entity", context.getRegistryName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        registerAdditionalEntityType(event);
        ENTITY_CONTEXTS.sort(Comparator.comparing(context -> context.getRank().getLevel()));
        TCLoggingUtils.logMessage("Entity", ENTITY_CONTEXTS.size() + " Takumis Registered.");
        TCLoggingUtils.completeRegistry("Entity");
    }

    private static void registerAdditionalEntityType(RegisterEvent event) {

    }

    public static void registerAttribute(EntityAttributeCreationEvent event) {
        TCLoggingUtils.startRegistry("EntityAttribute");
        List<Field> fieldList = Arrays.asList(TCEntityCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof AbstractTCCreeper.TCCreeperContext<?> context) {
                    EntityType<? extends LivingEntity> type = ((EntityType<? extends LivingEntity>) context.entityType());
                    AttributeSupplier.Builder builder = context.entityAttribute();
                    event.put(type, builder.build());
                    context.registerSpawn(((EntityType<AbstractTCCreeper>) type));
                    TCLoggingUtils.entryRegistry("EntityAttribute", context.getRegistryName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        TCLoggingUtils.completeRegistry("EntityAttribute");
    }
}
