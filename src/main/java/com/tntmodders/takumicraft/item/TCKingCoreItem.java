package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class TCKingCoreItem extends Item implements ITCItems, ITCTranslator {

    public TCKingCoreItem() {
        super(new Properties().rarity(Rarity.EPIC).fireResistant().setId(TCItemCore.TCItemId("kingcore")));
    }

    @Override
    public boolean isFoil(ItemStack p_41172_) {
        return true;
    }

    @Override
    public String getEnUSName() {
        return "King Core";
    }

    @Override
    public String getJaJPName() {
        return "王匠の証";
    }

    @Override
    public String getRegistryName() {
        return "kingcore";
    }
}
