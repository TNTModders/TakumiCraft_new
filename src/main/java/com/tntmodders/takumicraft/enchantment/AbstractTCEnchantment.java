package com.tntmodders.takumicraft.enchantment;

import com.tntmodders.takumicraft.provider.ITCTranslator;
import net.minecraft.world.item.enchantment.Enchantment;

public abstract class AbstractTCEnchantment extends Enchantment implements ITCTranslator {
    protected AbstractTCEnchantment(EnchantmentDefinition definition) {
        super(definition);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    public abstract String getRegistryName();
}
