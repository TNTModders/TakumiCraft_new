package com.tntmodders.takumicraft.enchantment;

import com.tntmodders.takumicraft.core.TCItemCore;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public class TCMinesweeperEnchantment extends AbstractTCEnchantment {
    public TCMinesweeperEnchantment() {
        super(definition(TCItemCore.MINESWEEPER_TOOLS, 6, 1, Enchantment.dynamicCost(5, 9), Enchantment.dynamicCost(20, 9), 4, EquipmentSlot.MAINHAND));
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
}
