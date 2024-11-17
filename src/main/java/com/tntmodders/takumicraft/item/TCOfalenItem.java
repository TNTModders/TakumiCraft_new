package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class TCOfalenItem extends Item implements ITCItems, ITCTranslator {

    public TCOfalenItem() {
        super(new Properties().rarity(Rarity.EPIC).setId(TCItemCore.TCItemId("takumi_ofalen")));
    }

    @Override
    public boolean isFoil(ItemStack p_41172_) {
        return true;
    }

    @Override
    public String getEnUSName() {
        return "Creeper Ofalen";
    }

    @Override
    public String getJaJPName() {
        return "匠オファレン";
    }

    @Override
    public String getRegistryName() {
        return "takumi_ofalen";
    }
}
