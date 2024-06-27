package com.tntmodders.takumicraft.enchantment;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

public class TCAntiPoweredContext extends AbstractTCEnchantmentContext {
    private final Enchantment enchantment;

    public TCAntiPoweredContext(BootstrapContext<Enchantment> bootstrap) {
        super(bootstrap);
        enchantment = Enchantment.enchantment(Enchantment.definition(bootstrap.lookup(Registries.ITEM).getOrThrow(TCItemCore.ANTI_POWERED), 8, 1, Enchantment.dynamicCost(5, 9), Enchantment.dynamicCost(20, 9), 4, EquipmentSlotGroup.MAINHAND)).build(ResourceLocation.tryBuild(TakumiCraftCore.MODID, this.getRegistryName()));
    }

    @Override
    public String getEnUSName() {
        return "Anti Powered";
    }

    @Override
    public String getJaJPName() {
        return "巨匠特攻";
    }

    @Override
    public String getRegistryName() {
        return "anti_powered";
    }

    @Override
    public Enchantment getEnchantment() {
        return this.enchantment;
    }
}
