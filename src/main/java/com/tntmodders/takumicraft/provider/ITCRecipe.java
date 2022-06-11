package com.tntmodders.takumicraft.provider;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public interface ITCRecipe {
    void addRecipes(TCRecipeProvider provider, ItemLike itemLike, Consumer<FinishedRecipe> consumer);
}
