package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCCreativeModeTabCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.provider.ITCItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.function.Supplier;

public class TCSpawnEggItem extends ForgeSpawnEggItem implements ITCItems {
    private final AbstractTCCreeper.TCCreeperContext<?> context;

    public TCSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, AbstractTCCreeper.TCCreeperContext<?> context) {
        super(type, context.getPrimaryColor(), context.getSecondaryColor(), new Properties().tab(TCCreativeModeTabCore.TAB_EGGS));
        this.context = context;
        this.setRegistryName(TakumiCraftCore.MODID, context.entityType().getRegistryName().getPath() + "_spawn_egg");
    }

    @Override
    public EnumTCItemModelType getItemModelType() {
        return EnumTCItemModelType.SPAWN_EGG;
    }

    @Override
    public String getEnUSName() {
        return "Spawn " + context.getEnUSName();
    }

    @Override
    public String getJaJPName() {
        return "スポーン " + context.getJaJPName();
    }
}
