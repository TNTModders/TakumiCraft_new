package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCCreativeModeTabCore;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class TCCreeperRodItem extends Item implements ITCItems, ITCTranslator {
    public TCCreeperRodItem() {
        super(new Properties().tab(TCCreativeModeTabCore.TAB_TC).rarity(Rarity.EPIC).stacksTo(1));
        this.setRegistryName(TakumiCraftCore.MOD_ID, "tester");
    }

    @Override
    public EnumTCItemModelType getItemModelType() {
        return EnumTCItemModelType.SIMPLE;
    }

    @Override
    public String getEnUSname() {
        return "Creeper Rod";
    }

    @Override
    public String getJaJPname() {
        return "匠式錫杖";
    }
}
