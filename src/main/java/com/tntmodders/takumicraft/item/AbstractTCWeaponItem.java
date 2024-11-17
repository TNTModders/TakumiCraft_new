package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.world.item.TCToolMaterials;
import net.minecraft.world.item.SwordItem;

public abstract class AbstractTCWeaponItem extends SwordItem implements ITCItems {

    public final AbstractTCCreeper.TCCreeperContext.EnumTakumiElement element;

    public AbstractTCWeaponItem(AbstractTCCreeper.TCCreeperContext.EnumTakumiElement elem, Properties p_43272_) {
        this(elem, p_43272_, -2.4f);
    }

    public AbstractTCWeaponItem(AbstractTCCreeper.TCCreeperContext.EnumTakumiElement elem, Properties properties, float swingSpeed) {
        super(TCToolMaterials.TKM_SABER, 3, swingSpeed, properties);
        this.element = elem;
    }

    @Override
    public String getRegistryName() {
        return "typesword_" + this.element.getElementID();
    }
}
