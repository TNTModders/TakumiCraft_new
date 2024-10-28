package com.tntmodders.takumicraft.utils;

import com.tntmodders.takumicraft.TakumiCraftCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class TCEntityUtils {
    public static Component getEntityName(EntityType<?> type) {
        return Component.translatable(type.getDescriptionId());
    }

    public static ResourceKey<EntityType<?>> TCEntityId(String id) {
        return ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.tryBuild(TakumiCraftCore.MODID, id));
    }

    public static Component getUnknown() {
        return Component.empty().append("???");
    }

    public static String getEntityLangCode(EntityType<?> type, @NotNull String suffix) {
        return "entity.takumicraft." + type.toShortString() + suffix;
    }

    public static boolean isXmas() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) >= 24 && calendar.get(Calendar.DATE) <= 26;
        //return true;
    }

    public static boolean isNewYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1 == 1 && calendar.get(Calendar.DATE) <= 8 || calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) == 31;
        //return true;
    }

    public static void setThunder(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.setWeatherParameters(0, ServerLevel.THUNDER_DURATION.sample(serverLevel.getRandom()), true, true);
        }
    }
}
