package com.tntmodders.takumicraft.item.crafting;

import com.mojang.serialization.MapCodec;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.core.TCRecipeSerializerCore;
import com.tntmodders.takumicraft.item.TCCreeperSwordItem;
import com.tntmodders.takumicraft.item.TCElementCoreItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

import java.util.stream.Stream;

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
        TCCreeperSwordItem.upgradeNewAttributes(itemstack, (TCElementCoreItem) p_267036_.getItem(0).getItem());
        itemstack.setDamageValue(itemstack.getDamageValue() - 10);
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
}
