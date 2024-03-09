package com.tntmodders.takumicraft.item.crafting;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.core.TCRecipeSerializerCore;
import com.tntmodders.takumicraft.item.TCTippedCreeperArrowItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;

public class TippedCreeperArrowRecipe extends CustomRecipe {
    public TippedCreeperArrowRecipe(CraftingBookCategory p_249010_) {
        super(p_249010_);
    }

    @Override
    public boolean matches(CraftingContainer p_44515_, Level p_44516_) {
        if (p_44515_.getWidth() == 3 && p_44515_.getHeight() == 3) {
            for (int i = 0; i < p_44515_.getWidth(); ++i) {
                for (int j = 0; j < p_44515_.getHeight(); ++j) {
                    ItemStack itemstack = p_44515_.getItem(i + j * p_44515_.getWidth());
                    if (itemstack.isEmpty()) {
                        return false;
                    }

                    if (i == 1 && j == 1) {
                        if (!itemstack.is(Items.LINGERING_POTION) && !itemstack.is(Items.WITHER_ROSE)) {
                            return false;
                        }
                    } else if (!itemstack.is(TCItemCore.CREEPER_ARROW)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public ItemStack assemble(CraftingContainer p_44513_, RegistryAccess p_267186_) {
        ItemStack itemstack = p_44513_.getItem(1 + p_44513_.getWidth());
        if (!itemstack.is(Items.LINGERING_POTION)) {
            if (itemstack.is(Items.WITHER_ROSE)) {
                ItemStack itemstack1 = new ItemStack(TCItemCore.TIPPED_CREEPER_ARROW, 8);
                PotionUtils.setCustomEffects(itemstack1, List.of(TCTippedCreeperArrowItem.WITHER));
                return itemstack1;
            }
            return ItemStack.EMPTY;
        } else {
            ItemStack itemstack1 = new ItemStack(TCItemCore.TIPPED_CREEPER_ARROW, 8);
            PotionUtils.setPotion(itemstack1, PotionUtils.getPotion(itemstack));
            PotionUtils.setCustomEffects(itemstack1, PotionUtils.getCustomEffects(itemstack));
            return itemstack1;
        }
    }

    @Override
    public boolean canCraftInDimensions(int p_44505_, int p_44506_) {
        return p_44505_ >= 2 && p_44506_ >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipeSerializerCore.CREEPER_ARROW_TIP;
    }
}
