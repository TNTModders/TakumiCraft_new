package com.tntmodders.takumicraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCCreativeModeTabCore;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class TCCreeperRodItem extends Item implements ITCItems, ITCTranslator {

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public TCCreeperRodItem() {
        super(new Properties().tab(TCCreativeModeTabCore.TAB_TC).rarity(Rarity.EPIC).stacksTo(1));
        this.setRegistryName(TakumiCraftCore.MODID, "tester");
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 19, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot p_43274_) {
        return p_43274_ == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(p_43274_);
    }

    @Override
    public String getEnUSName() {
        return "Creeper Rod";
    }

    @Override
    public String getJaJPName() {
        return "匠式錫杖";
    }
}
