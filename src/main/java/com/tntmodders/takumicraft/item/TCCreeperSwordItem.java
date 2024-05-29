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
import net.minecraft.world.item.component.CustomData;
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

    public static ItemStack upgradeNewAttributes(ItemStack stack, TCElementCoreItem element) {
        ItemAttributeModifiers modifiers = stack.getComponents().get(DataComponents.ATTRIBUTE_MODIFIERS);
        CreeperSwordUpgrader damage = new CreeperSwordUpgrader(Attributes.ATTACK_DAMAGE, element.getAddAtk());
        CreeperSwordUpgrader speed = new CreeperSwordUpgrader(Attributes.ATTACK_SPEED, element.getAddSpeed());
        CreeperSwordUpgrader range = new CreeperSwordUpgrader(Attributes.ENTITY_INTERACTION_RANGE, element.getAddRange());
        modifiers = modifiers
                .withModifierAdded(damage.holder(), new AttributeModifier(damage.getUUID(), "CreeperSword Modifier", damage.getModifyAmount(modifiers, stack), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .withModifierAdded(speed.holder(), new AttributeModifier(speed.getUUID(), "CreeperSowrd Modifier", speed.getModifyAmount(modifiers, stack), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .withModifierAdded(range.holder(), new AttributeModifier(range.getUUID(), "CreeperSword Modifier", range.getModifyAmount(modifiers, stack), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, modifiers);
        return stack;
    }

    record CreeperSwordUpgrader(Holder<Attribute> holder, double amount) {
        public UUID getUUID() {
            if (holder().equals(Attributes.ATTACK_SPEED)) {
                return ATTACK_SPEED_UUID;
            } else if (holder().equals(Attributes.ENTITY_INTERACTION_RANGE)) {
                return ATTACK_RANGE_UUID;
            }
            return ATTACK_DAMAGE_UUID;
        }

        public double getSetAmount(ItemAttributeModifiers modifiers) {
            double d = 0;
            for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
                if (entry.modifier().id().equals(this.getUUID())) {
                    d += entry.modifier().amount();
                }
            }
            return d == 0 ? getDefaultAmount(modifiers) : d;
        }

        public double getDefaultAmount(ItemAttributeModifiers modifiers) {
            double def = 6;
            if (holder().equals(Attributes.ATTACK_SPEED)) {
                def = 1.6;
            } else if (holder().equals(Attributes.ENTITY_INTERACTION_RANGE)) {
                def = 0;
            }
            return def;
        }

        public double getModifyAmount(ItemAttributeModifiers modifiers, ItemStack stack) {
            double def = getDefaultAmount(modifiers);
            double ret = getSetAmount(modifiers) + amount();
            double min = def * 0.5;
            double max = def * 2;
            if (holder().equals(Attributes.ENTITY_INTERACTION_RANGE)) {
                min = -2;
                max = 2;
            }

            if (ret < min) {
                return min;
            } else if (ret >= max) {
                CustomData data;
                if (stack.has(DataComponents.CUSTOM_DATA)) {
                    CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
                    tag.putBoolean("max_" + holder().getRegisteredName(), true);
                    data = CustomData.of(tag);

                } else {
                    data = CustomData.of(CompoundTag.builder().put("max_" + holder().getRegisteredName(), true).build());
                }
                stack.set(DataComponents.CUSTOM_DATA, data);
                return max;
            }
            if (stack.has(DataComponents.CUSTOM_DATA)) {
                CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
                if (tag.contains("max_" + holder().getRegisteredName())) {
                    tag.putBoolean("max_" + holder().getRegisteredName(), false);
                }
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
            return ret;
        }
    }
}
