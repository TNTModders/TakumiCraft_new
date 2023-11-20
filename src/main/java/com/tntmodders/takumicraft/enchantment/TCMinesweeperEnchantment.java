package com.tntmodders.takumicraft.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class TCMinesweeperEnchantment extends AbstractTCEnchantment {
    public TCMinesweeperEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
