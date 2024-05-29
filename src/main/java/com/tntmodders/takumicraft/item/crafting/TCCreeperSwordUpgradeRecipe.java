package com.tntmodders.takumicraft.item.crafting;

import com.mojang.serialization.MapCodec;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.core.TCRecipeSerializerCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.item.TCElementCoreItem;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

import java.util.UUID;
import java.util.stream.Stream;

import static com.tntmodders.takumicraft.item.TCCreeperSwordItem.*;

public class TCCreeperSwordUpgradeRecipe implements SmithingRecipe {

    public TCCreeperSwordUpgradeRecipe() {
    }

    final Ingredient template = Ingredient.of(TCItemCore.ELEMENT_CORE);
    final Ingredient base = Ingredient.of(TCItemCore.CREEPER_SWORD);
    final ItemStack result = TCItemCore.CREEPER_SWORD.getDefaultInstance();

    @Override
    public boolean matches(Container p_266855_, Level p_266781_) {
        return this.template.test(p_266855_.getItem(0)) && this.base.test(p_266855_.getItem(1));
    }

    @Override
    public ItemStack assemble(Container p_267036_, HolderLookup.Provider p_331030_) {
        ItemStack itemstack = p_267036_.getItem(1).transmuteCopy(this.result.getItem(), this.result.getCount());
        itemstack.applyComponents(this.result.getComponentsPatch());
        upgradeNewAttributes(itemstack, (TCElementCoreItem) p_267036_.getItem(0).getItem());
        itemstack.setDamageValue(itemstack.getDamageValue() - 10);
        CustomData data;
        if (p_267036_.getItem(0).getItem() instanceof TCElementCoreItem item) {
            AbstractTCCreeper.TCCreeperContext.EnumTakumiElement element = item.getElement();
            int[] ints = new int[6];
            if (itemstack.has(DataComponents.CUSTOM_DATA) && itemstack.get(DataComponents.CUSTOM_DATA).copyTag().contains("elemcount")) {
                CompoundTag tag = itemstack.get(DataComponents.CUSTOM_DATA).copyTag();
                ints = tag.getIntArray("elemcount");
                ints[element.getId() - 1]++;
                tag.putIntArray("elemcount", ints);
                data = CustomData.of(tag);

            } else {
                ints = new int[6];
                ints[element.getId() - 1]++;
                data = CustomData.of(CompoundTag.builder().putIntArray("elemcount", ints).build());
            }
            itemstack.set(DataComponents.CUSTOM_DATA, data);
        }
        return itemstack;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_330801_) {
        return this.result;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack p_267113_) {
        return this.template.test(p_267113_);
    }

    @Override
    public boolean isBaseIngredient(ItemStack p_267276_) {
        return this.base.test(p_267276_);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack p_267260_) {
        return p_267260_.isEmpty();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipeSerializerCore.CREEPER_SWORD_UPGRADE;
    }

    @Override
    public boolean isIncomplete() {
        return Stream.of(this.template, this.base).anyMatch(net.minecraftforge.common.ForgeHooks::hasNoElements);
    }

    public static class Serializer implements RecipeSerializer<TCCreeperSwordUpgradeRecipe> {
        private static final MapCodec<TCCreeperSwordUpgradeRecipe> CODEC = MapCodec.unit(new TCCreeperSwordUpgradeRecipe());
        public static final StreamCodec<RegistryFriendlyByteBuf, TCCreeperSwordUpgradeRecipe> STREAM_CODEC = StreamCodec.of(
                TCCreeperSwordUpgradeRecipe.Serializer::toNetwork, TCCreeperSwordUpgradeRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<TCCreeperSwordUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TCCreeperSwordUpgradeRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static TCCreeperSwordUpgradeRecipe fromNetwork(RegistryFriendlyByteBuf p_333917_) {
            return new TCCreeperSwordUpgradeRecipe();
        }

        private static void toNetwork(RegistryFriendlyByteBuf p_329920_, TCCreeperSwordUpgradeRecipe p_266927_) {
        }
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
                if (stack.has(DataComponents.CUSTOM_DATA) && stack.get(DataComponents.CUSTOM_DATA).copyTag().contains("max_" + holder().getRegisteredName())) {
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
