package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.ITCItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class TCTakumiSpecialMeatItem extends Item implements ITCItems {
    private final Item base;
    private final Item rawBase;
    private final boolean isFish;
    public static final NonNullList<Item> BASE_LIST = NonNullList.create();
    public static final NonNullList<Item> RAW_BASE_LIST = NonNullList.create();
    public static final NonNullList<TCTakumiSpecialMeatItem> MEAT_LIST = NonNullList.create();

    public TCTakumiSpecialMeatItem(Item item, Item rawItem, boolean isFish) {
        super(new Properties().food(new FoodProperties.Builder().nutrition(item.components().get(DataComponents.FOOD).nutrition() * 3).saturationModifier(item.components().get(DataComponents.FOOD).saturation() * 3).fast().build()).rarity(Rarity.EPIC));
        this.base = item;
        this.rawBase = rawItem;
        this.isFish = isFish;
        BASE_LIST.add(this.base);
        RAW_BASE_LIST.add(this.rawBase);
        MEAT_LIST.add(this);
    }

    public static boolean canConvertToSpecialMeat(ItemStack stack) {
        return TCTakumiSpecialMeatItem.BASE_LIST.contains(stack.getItem()) || TCTakumiSpecialMeatItem.RAW_BASE_LIST.contains(stack.getItem());
    }

    public static ItemStack getSpecialMeat(ItemStack stack) {
        for (Item item : TCItemCore.ITEMS) {
            if (item instanceof TCTakumiSpecialMeatItem meatItem && (stack.is(meatItem.getBase()) || meatItem.getRawBase() != null && stack.is(meatItem.getRawBase()))) {
                return new ItemStack(meatItem, stack.getCount());
            }
        }
        return stack;
    }

    public Item getBase() {
        return base;
    }

    public Item getRawBase() {
        return rawBase;
    }

    @Override
    public EnumTCItemModelType getItemModelType() {
        return EnumTCItemModelType.SPECIAL_MEAT;
    }

    @Override
    public String getRegistryName() {
        return "takumispecialmeat_" + ForgeRegistries.ITEMS.getKey(this.base).getPath();
    }

    @Override
    public String getEnUSName() {
        return this.isFish ? "Exploded Meat?" : "Exploded Meat";
    }

    @Override
    public String getJaJPName() {
        return this.isFish ? "匠式特上肉?" : this.getRawBase() == Items.ROTTEN_FLESH ? "匠式特上?肉" : "匠式特上肉";
    }

    @Override
    public List<TagKey<Item>> getItemTags() {
        return List.of(TCItemCore.SPECIAL_MEATS);
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, components, tooltipFlag);
        components.add(Component.translatable("item.takumicraft.takumispecialmeat_cooked_beef").append(":").append(Component.translatable(this.base.getDescriptionId())));
    }
}
