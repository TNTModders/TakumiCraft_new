package com.tntmodders.takumicraft.enchantment;

import com.tntmodders.takumicraft.TakumiCraftCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.Tags;

public class TCAntiExplosionContext extends AbstractTCEnchantmentContext {
    private final Enchantment enchantment;

    public TCAntiExplosionContext(BootstrapContext<Enchantment> bootstrap) {
        super(bootstrap);
        enchantment = Enchantment.enchantment(Enchantment.definition(bootstrap.lookup(Registries.ITEM).getOrThrow(Tags.Items.ARMORS), 1, 1, Enchantment.constantCost(25), Enchantment.constantCost(50), 10, EquipmentSlotGroup.ARMOR)).exclusiveWith(bootstrap.lookup(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE)).build(ResourceLocation.tryBuild(TakumiCraftCore.MODID, this.getRegistryName()));
    }

    @Override
    public String getEnUSName() {
        return "Anti Explosion";
    }

    @Override
    public String getJaJPName() {
        return "爆発無効";
    }

    @Override
    public String getRegistryName() {
        return "anti_explosion";
    }

    @Override
    public Enchantment getEnchantment() {
        return this.enchantment;
    }
}
