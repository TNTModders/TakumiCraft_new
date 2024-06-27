package com.tntmodders.takumicraft.enchantment;

import com.tntmodders.takumicraft.provider.ITCTranslator;
import net.minecraft.world.item.enchantment.Enchantment;

public abstract class AbstractTCEnchantment implements ITCTranslator {

    protected AbstractTCEnchantment(Enchantment.EnchantmentDefinition definition) {
    }

    public abstract Enchantment getEnchantment();

    public abstract String getRegistryName();
}
