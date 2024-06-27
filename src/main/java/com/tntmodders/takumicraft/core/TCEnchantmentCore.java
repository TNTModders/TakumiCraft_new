package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.enchantment.AbstractTCEnchantmentContext;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.RegisterEvent;

public class TCEnchantmentCore {

    public static final ResourceKey<Enchantment> ANTI_POWERED = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.tryBuild(TakumiCraftCore.MODID, "anti_powered"));
    public static final ResourceKey<Enchantment> MINESWEEPER = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.tryBuild(TakumiCraftCore.MODID, "minesweeper"));
    public static final ResourceKey<Enchantment> BLAST_POWERED = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.tryBuild(TakumiCraftCore.MODID, "blast_powered"));

    public static final NonNullList<AbstractTCEnchantmentContext> ENCHANTMENTS = NonNullList.create();

    public static void register(final RegisterEvent event) {
        TCLoggingUtils.startRegistry("Enchantment");
        TCLoggingUtils.completeRegistry("Enchantment");
    }
}
