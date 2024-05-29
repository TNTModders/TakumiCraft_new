package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class TCCreeperSwordItem extends SwordItem implements ITCItems, ITCRecipe {
    public static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("D1E13C24-5227-0C58-D5EF-CA1762D01B04");
    public static final UUID ATTACK_SPEED_UUID = UUID.fromString("D1E13C24-5227-0C58-D5EF-CA1762D01B05");
    public static final UUID ATTACK_RANGE_UUID = UUID.fromString("D1E13C24-5227-0C58-D5EF-CA1762D01B06");

    private final double maxAtk = 12;
    private final double maxSpd = 3.2;
    private final double maxRange = 2;

    public TCCreeperSwordItem() {
        super(Tiers.IRON, new Item.Properties().attributes(createAttributes()));
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_UUID, "CreeperSword Modifier", 6, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_UUID, "CreeperSword Modifier", 1.6, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
        super.inventoryTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
        if (!p_41404_.isEnchanted()) {
            p_41404_.enchant(TCEnchantmentCore.ANTI_POWERED, 1);
        }
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, TooltipContext p_333372_, List<Component> components, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_333372_, components, p_41424_);
        components.add(Component.translatable("item.takumicraft.creepersword.desc"));
        if (p_41421_.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = p_41421_.get(DataComponents.CUSTOM_DATA).copyTag();
            if (tag.contains("elemcount")) {
                int[] ints = tag.getIntArray("elemcount");
                Component component = Component.translatable("item.takumicraft.creepersword.elemcount", ints[0], ints[1], ints[2], ints[3], ints[4], ints[5]).withStyle(ChatFormatting.DARK_GRAY);
                components.add(component);
            }

            int count = 0;
            for (Holder<Attribute> holder : List.of(Attributes.ATTACK_DAMAGE, Attributes.ATTACK_SPEED, Attributes.ENTITY_INTERACTION_RANGE)) {
                if (tag.contains("max_" + holder.getRegisteredName()) && tag.getBoolean("max_" + holder.getRegisteredName())) {
                    count++;
                }
            }
            if (count > 1) {
                Component component = Component.translatable("item.takumicraft.creepersword.canpowered").withStyle(ChatFormatting.GREEN);
                components.add(component);
            }
        }
    }

    @Override
    public String getEnUSName() {
        return "Creeper Sword";
    }

    @Override
    public String getJaJPName() {
        return "匠式宝剣";
    }

    @Override
    public String getRegistryName() {
        return "creepersword";
    }

    @Override
    public EnumTCItemModelType getItemModelType() {
        return EnumTCItemModelType.HANDHELD;
    }


    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(TCItemCore.ELEMENTCORE_FIRE, TCItemCore.ELEMENTCORE_NORMAL, TCItemCore.ELEMENTCORE_WATER, TCItemCore.ELEMENTCORE_GRASS, TCItemCore.ELEMENTCORE_WIND, TCItemCore.ELEMENTCORE_GROUND),
                        Ingredient.of(Items.IRON_SWORD), Ingredient.of(TCBlockCore.CREEPER_BOMB)
                        , RecipeCategory.COMBAT, TCItemCore.CREEPER_SWORD).unlocks("has_creeperbomb", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_BOMB))
                .save(consumer, "creepersword_smithing");
    }

    @Override
    public List<TagKey<Item>> getItemTags() {
        return List.of(TCItemCore.ANTI_POWERED, ItemTags.SWORDS);
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return true;
    }
}
