package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.provider.ITCItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.function.Supplier;

public class TCSpawnEggItem extends ForgeSpawnEggItem implements ITCItems {
    private final AbstractTCCreeper.TCCreeperContext<?> context;
    private final String registryName;

    public TCSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, AbstractTCCreeper.TCCreeperContext<?> context) {
        super(type, context.getPrimaryColor(), context.getSecondaryColor(), new Properties());
        this.context = context;
        this.registryName = context.getRegistryName() + "_spawn_egg";
    }

    @Override
    public boolean hideOnCreativeTab() {
        return true;
    }

    public AbstractTCCreeper.TCCreeperContext<?> getContext() {
        return this.context;
    }

    @Override
    public String getRegistryName() {
        return this.registryName;
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
        return context.getJaJPName() + "のスポーンエッグ";
    }
}
