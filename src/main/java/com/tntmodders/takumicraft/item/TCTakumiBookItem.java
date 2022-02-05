package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCCreativeModeTabCore;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class TCTakumiBookItem extends Item implements ITCItems, ITCTranslator {
    public TCTakumiBookItem() {
        super(new Properties().tab(TCCreativeModeTabCore.TAB_TC).rarity(Rarity.EPIC).stacksTo(1));
        this.setRegistryName(TakumiCraftCore.MODID, "takumibook");
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return true;
    }

    @Override
    public String getEnUSName() {
        return "TakumiBook";
    }

    @Override
    public String getJaJPName() {
        return "匠の書";
    }
}
