package com.tntmodders.takumicraft.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class TCEntityUtils {
    public static Component getEntityName(EntityType<?> type) {
        return Component.translatable(type.getDescriptionId());
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
}
