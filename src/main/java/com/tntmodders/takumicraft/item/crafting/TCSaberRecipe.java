package com.tntmodders.takumicraft.item.crafting;

import com.mojang.serialization.MapCodec;
import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.core.TCRecipeSerializerCore;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class TCSaberRecipe implements SmithingRecipe {
    final Ingredient template = Ingredient.of(TCItemCore.KING_CORE);
    final Ingredient base = Ingredient.of(TCItemCore.CREEPER_SWORD);
    final ItemStack result = TCItemCore.LIGHTSABER.getDefaultInstance();
    @Nullable
    private PlacementInfo placementInfo;
    public TCSaberRecipe() {
    }

    @Override
    public boolean matches(SmithingRecipeInput p_266855_, Level p_266781_) {
        ItemStack stack = p_266855_.getItem(1);
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
            int count = 0;
            for (Holder<Attribute> holder : List.of(Attributes.ATTACK_DAMAGE, Attributes.ATTACK_SPEED, Attributes.ENTITY_INTERACTION_RANGE)) {
                if (tag.contains("max_" + holder.getRegisteredName()) && tag.getBoolean("max_" + holder.getRegisteredName())) {
                    count++;
                }
            }
            if (count > 1) {
                return this.template.test(p_266855_.getItem(0)) && this.base.test(stack);
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput p_267036_, HolderLookup.Provider p_331030_) {
        ItemStack stack = this.result;
        stack.enchant(p_331030_.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(TCEnchantmentCore.ANTI_POWERED), 1);
        return stack;
    }

    @Override
    public Optional<Ingredient> templateIngredient() {
        return Optional.of(this.template);
    }

    @Override
    public Optional<Ingredient> baseIngredient() {
        return Optional.of(this.base);
    }

    @Override
    public Optional<Ingredient> additionIngredient() {
        return Optional.empty();
    }

    @Override
    public RecipeSerializer<SmithingRecipe> getSerializer() {
        return TCRecipeSerializerCore.SABER;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.createFromOptionals(List.of(this.templateIngredient(), this.baseIngredient(), this.additionIngredient()));
        }

        return this.placementInfo;
    }

    public static class Serializer implements RecipeSerializer<TCSaberRecipe> {
        public static final StreamCodec<RegistryFriendlyByteBuf, TCSaberRecipe> STREAM_CODEC = StreamCodec.of(
                TCSaberRecipe.Serializer::toNetwork, TCSaberRecipe.Serializer::fromNetwork
        );
        private static final MapCodec<TCSaberRecipe> CODEC = MapCodec.unit(new TCSaberRecipe());

        private static TCSaberRecipe fromNetwork(RegistryFriendlyByteBuf p_333917_) {
            return new TCSaberRecipe();
        }

        private static void toNetwork(RegistryFriendlyByteBuf p_329920_, TCSaberRecipe p_266927_) {
        }

        @Override
        public MapCodec<TCSaberRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TCSaberRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
