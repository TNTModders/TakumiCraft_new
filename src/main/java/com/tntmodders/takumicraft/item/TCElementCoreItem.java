package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper.TCCreeperContext.EnumTakumiElement;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import com.tntmodders.takumicraft.provider.TCLanguageProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class TCElementCoreItem extends Item implements ITCItems, ITCTranslator {

    private final EnumTakumiElement element;

    public TCElementCoreItem(EnumTakumiElement elem) {
        super(new Properties().rarity(Rarity.UNCOMMON));
        this.element = elem;
    }

    public static Item getElementCoreFormElement(EnumTakumiElement element) {
        return switch (element.getMainElement()) {
            case FIRE -> TCItemCore.ELEMENTCORE_FIRE;
            case GRASS -> TCItemCore.ELEMENTCORE_GRASS;
            case WATER -> TCItemCore.ELEMENTCORE_WATER;
            case WIND -> TCItemCore.ELEMENTCORE_WIND;
            case GROUND -> TCItemCore.ELEMENTCORE_GROUND;
            default -> TCItemCore.ELEMENTCORE_NORMAL;
        };
    }

    @Override
    public boolean isFoil(ItemStack p_41172_) {
        return true;
    }

    @Override
    public String getEnUSName() {
        if (this.element == EnumTakumiElement.NORMAL) {
            return "Element Core";
        }
        return "Element Core [" + TCLanguageProvider.TCEnUSLanguageProvider.TC_ENUS_LANGMAP.get("takumicraft.elem." + element.getElementID()) + "]";
    }

    @Override
    public String getJaJPName() {
        if (this.element == EnumTakumiElement.NORMAL) {
            return "匠素結晶";
        }
        return "匠素結晶" + "[" + TCLanguageProvider.TCJaJPLanguageProvider.TC_JAJP_LANGMAP.get("takumicraft.elem." + element.getElementID()) + "]";
    }

    @Override
    public String getRegistryName() {
        return "elementcore_" + element.getElementID();
    }
}
