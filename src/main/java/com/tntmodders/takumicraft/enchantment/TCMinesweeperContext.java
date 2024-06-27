package com.tntmodders.takumicraft.enchantment;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

public class TCMinesweeperContext extends AbstractTCEnchantmentContext {
    private final Enchantment enchantment;

    public TCMinesweeperContext(BootstrapContext<Enchantment> bootstrap) {
        super(bootstrap);
        enchantment = Enchantment.enchantment(Enchantment.definition(bootstrap.lookup(Registries.ITEM).getOrThrow(TCItemCore.MINESWEEPER_TOOLS), 6, 1, Enchantment.dynamicCost(5, 9), Enchantment.dynamicCost(20, 9), 4, EquipmentSlotGroup.MAINHAND)).build(ResourceLocation.tryBuild(TakumiCraftCore.MODID, this.getRegistryName()));
    }

    @Override
    public String getEnUSName() {
        return "Minesweeper";
    }

    @Override
    public String getJaJPName() {
        return "地雷除去";
    }

    @Override
    public String getRegistryName() {
        return "minesweeper";
    }

    @Override
    public Enchantment getEnchantment() {
        return enchantment;
    }
}
