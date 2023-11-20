package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.enchantment.AbstractTCEnchantment;
import com.tntmodders.takumicraft.enchantment.TCAntiPoweredEnchantment;
import com.tntmodders.takumicraft.enchantment.TCMinesweeperEnchantment;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class TCEnchantmentCore {
    public static final AbstractTCEnchantment ANTI_POWERED = new TCAntiPoweredEnchantment();
    public static final AbstractTCEnchantment MINESWEEPER = new TCMinesweeperEnchantment();

    public static final NonNullList<AbstractTCEnchantment> ENCHANTMENTS = NonNullList.create();

    public static void register(final RegisterEvent event) {
        TCLoggingUtils.startRegistry("Enchantment");
        List<Field> fieldList = Arrays.asList(TCEnchantmentCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof AbstractTCEnchantment ench) {
                    event.register(ForgeRegistries.ENCHANTMENTS.getRegistryKey(), helper -> helper.register(new ResourceLocation(TakumiCraftCore.MODID, ench.getRegistryName()), ench));
                    ENCHANTMENTS.add(ench);
                    TCLoggingUtils.entryRegistry("Enchantment", ench.getRegistryName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        TCLoggingUtils.completeRegistry("Enchantment");
    }
}
