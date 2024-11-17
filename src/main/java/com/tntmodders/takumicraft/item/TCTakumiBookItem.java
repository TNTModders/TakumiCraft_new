package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class TCTakumiBookItem extends Item implements ITCItems, ITCTranslator {
    public TCTakumiBookItem() {
        super(new Properties().rarity(Rarity.EPIC).stacksTo(1).setId(TCItemCore.TCItemId("takumibook")));
    }

    @Override
    public boolean hideOnCreativeTab() {
        return true;
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

    @Override
    public String getRegistryName() {
        return "takumibook";
    }
}
