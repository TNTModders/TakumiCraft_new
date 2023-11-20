package com.tntmodders.takumicraft.item.crafting;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.core.TCRecipeSerializerCore;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CreeperShieldDecorationRecipe extends CustomRecipe {
    public CreeperShieldDecorationRecipe(CraftingBookCategory p_251065_) {
        super(p_251065_);
    }

    @Override
    public boolean matches(CraftingContainer p_44308_, Level p_44309_) {
        ItemStack itemstack = ItemStack.EMPTY;
        ItemStack itemstack1 = ItemStack.EMPTY;

        for (int i = 0; i < p_44308_.getContainerSize(); ++i) {
            ItemStack itemstack2 = p_44308_.getItem(i);
            if (!itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof BannerItem) {
                    if (!itemstack1.isEmpty()) {
                        return false;
                    }

                    itemstack1 = itemstack2;
                } else {
                    if (!itemstack2.is(TCItemCore.CREEPER_SHIELD)) {
                        return false;
                    }

                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    if (BlockItem.getBlockEntityData(itemstack2) != null) {
                        return false;
                    }

                    itemstack = itemstack2;
                }
            }
        }

        return !itemstack.isEmpty() && !itemstack1.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer p_44306_, RegistryAccess p_267112_) {
        ItemStack itemstack = ItemStack.EMPTY;
        ItemStack itemstack1 = ItemStack.EMPTY;

        for (int i = 0; i < p_44306_.getContainerSize(); ++i) {
            ItemStack itemstack2 = p_44306_.getItem(i);
            if (!itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof BannerItem) {
                    itemstack = itemstack2;
                } else if (itemstack2.is(TCItemCore.CREEPER_SHIELD)) {
                    itemstack1 = itemstack2.copy();
                }
            }
        }

        if (!itemstack1.isEmpty()) {
            CompoundTag compoundtag = BlockItem.getBlockEntityData(itemstack);
            CompoundTag compoundtag1 = compoundtag == null ? new CompoundTag() : compoundtag.copy();
            compoundtag1.putInt("Base", ((BannerItem) itemstack.getItem()).getColor().getId());
            BlockItem.setBlockEntityData(itemstack1, BlockEntityType.BANNER, compoundtag1);
        }
        return itemstack1;
    }

    @Override
    public boolean canCraftInDimensions(int p_44298_, int p_44299_) {
        return p_44298_ * p_44299_ >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipeSerializerCore.CREEPER_SHIELD_DECO;
    }
}
