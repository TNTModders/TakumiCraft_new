package com.tntmodders.takumicraft.item.crafting;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.core.TCRecipeSerializerCore;
import com.tntmodders.takumicraft.item.TCTippedCreeperArrowItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TCTippedCreeperArrowRecipe extends CustomRecipe {
    public TCTippedCreeperArrowRecipe(CraftingBookCategory p_249010_) {
        super(p_249010_);
    }

    @Override
    public boolean matches(CraftingInput p_44515_, Level p_44516_) {
        if (p_44515_.width() == 3 && p_44515_.height() == 3) {
            for (int i = 0; i < p_44515_.width(); ++i) {
                for (int j = 0; j < p_44515_.height(); ++j) {
                    ItemStack itemstack = p_44515_.getItem(i + j * p_44515_.width());
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
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
        ItemStack itemstack = craftingInput.getItem(1 + craftingInput.width());
        if (!itemstack.is(Items.LINGERING_POTION)) {
            if (itemstack.is(Items.WITHER_ROSE)) {
                ItemStack itemstack1 = new ItemStack(TCItemCore.TIPPED_CREEPER_ARROW, 8);
                itemstack.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withEffectAdded(TCTippedCreeperArrowItem.WITHER));
                return itemstack1;
            }
            return ItemStack.EMPTY;
        } else {
            ItemStack itemstack1 = new ItemStack(TCItemCore.TIPPED_CREEPER_ARROW, 8);
            itemstack1.set(DataComponents.POTION_CONTENTS, itemstack.get(DataComponents.POTION_CONTENTS));
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
