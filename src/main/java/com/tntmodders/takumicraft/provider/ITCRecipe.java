package com.tntmodders.takumicraft.provider;

import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;

public interface ITCRecipe {
    NonNullList<RecipeBuilder> addRecipes();
}
