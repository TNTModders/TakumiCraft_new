package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.entity.mobs.TCLavaCreeper;
import com.tntmodders.takumicraft.entity.mobs.TCSilentCreeper;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class TCEntityCore {
    public static final NonNullList<EntityType<?>> ENTITY_TYPES = NonNullList.create();
    public static final NonNullList<AbstractTCCreeper.TCCreeperContext<?>> ENTITY_CONTEXTS = NonNullList.create();
    public static final AbstractTCCreeper.TCCreeperContext<TCSilentCreeper> SILENT = new TCSilentCreeper.TCSilentCreeperContext();
    public static final AbstractTCCreeper.TCCreeperContext<TCLavaCreeper> LAVA = new TCLavaCreeper.TCLavatCreeperContext();

    public static void registerEntityType(RegistryEvent.Register<EntityType<?>> event) {
        TCLoggingUtils.startRegistry("Entity");
        List<Field> fieldList = Arrays.asList(TCEntityCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof AbstractTCCreeper.TCCreeperContext<?>) {
                    EntityType<?> type = ((AbstractTCCreeper.TCCreeperContext<?>) obj).entityType();
                    event.getRegistry().register(type);
                    ENTITY_TYPES.add(type);
                    ENTITY_CONTEXTS.add(((AbstractTCCreeper.TCCreeperContext<?>) obj));
                    TCLoggingUtils.entryRegistry("Entity", type.getRegistryName().getPath());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        registerAdditionalEntityType(event);
        TCLoggingUtils.completeRegistry("Entity");
    }

    private static void registerAdditionalEntityType(RegistryEvent.Register<EntityType<?>> event) {

    }

    public static void registerAttribute(EntityAttributeCreationEvent event) {
        TCLoggingUtils.startRegistry("EntityAttribute");
        List<Field> fieldList = Arrays.asList(TCEntityCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof AbstractTCCreeper.TCCreeperContext<?>) {
                    EntityType<? extends LivingEntity> type = ((EntityType<? extends LivingEntity>) ((AbstractTCCreeper.TCCreeperContext<?>) obj).entityType());
                    AttributeSupplier.Builder builder = ((AbstractTCCreeper.TCCreeperContext<?>) obj).entityAttribute();
                    event.put(type, builder.build());
                    TCLoggingUtils.entryRegistry("EntityAttribute", type.getRegistryName().getPath());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        TCLoggingUtils.completeRegistry("EntityAttribute");
    }
}
