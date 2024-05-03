package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.world.item.TCTier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

public abstract class AbstractTCWeaponItem extends SwordItem implements ITCItems {

    public final AbstractTCCreeper.TCCreeperContext.EnumTakumiElement element;

    public AbstractTCWeaponItem(AbstractTCCreeper.TCCreeperContext.EnumTakumiElement elem, int p_43270_, float p_43271_, Properties p_43272_) {
        super(TCTier.TKM_SABER, p_43272_.attributes(SwordItem.createAttributes(Tiers.NETHERITE, p_43270_, p_43271_)));
        this.element = elem;
    }

    @Override
    public String getRegistryName() {
        return "typesword_" + this.element.getElementID();
    }
}
