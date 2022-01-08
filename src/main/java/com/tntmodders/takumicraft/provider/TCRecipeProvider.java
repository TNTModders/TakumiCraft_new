package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TCRecipeProvider extends RecipeProvider {
    public TCRecipeProvider(DataGenerator gen) {
        super(gen);
    }

    public static InventoryChangeTrigger.TriggerInstance hasItem(ItemLike itemLike) {
        return has(itemLike);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        TCLoggingUtils.startRegistry("Recipe");
        TCBlockCore.BLOCKS.forEach(block -> {
            if (block instanceof ITCRecipe) {
                ((ITCRecipe) block).addRecipe().save(consumer);
            }
        });
        TCItemCore.ITEMS.forEach(item -> {
            if (item instanceof ITCRecipe) {
                ((ITCRecipe) item).addRecipe().save(consumer);
            }
        });
        this.additionalRecipes(consumer);
        TCLoggingUtils.completeRegistry("Recipe");
    }

    private void additionalRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(Items.CREEPER_HEAD)
                .define('#', Items.GUNPOWDER)
                .pattern(" # ")
                .pattern("###")
                .pattern(" # ")
                .unlockedBy("has_gunpowder", hasItem(Items.GUNPOWDER))
                .save(consumer);
    }
}
