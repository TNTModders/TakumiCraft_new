package com.tntmodders.takumicraft.enchantment;

import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.enchantment.Enchantment;

public abstract class AbstractTCEnchantmentContext implements ITCTranslator {
    public AbstractTCEnchantmentContext(BootstrapContext<Enchantment> bootstrap) {
        TCEnchantmentCore.ENCHANTMENTS.add(this);
    }

    public abstract String getRegistryName();

    public abstract Enchantment getEnchantment();
}
