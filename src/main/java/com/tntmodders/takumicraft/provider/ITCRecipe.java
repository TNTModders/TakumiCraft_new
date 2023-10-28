package com.tntmodders.takumicraft.provider;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.ItemLike;

public interface ITCRecipe {
    void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer);
}
