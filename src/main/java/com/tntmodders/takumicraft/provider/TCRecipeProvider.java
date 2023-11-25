package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import static net.minecraft.data.recipes.RecipeBuilder.getDefaultRecipeId;

public class TCRecipeProvider extends RecipeProvider {
    public TCRecipeProvider(PackOutput output) {
        super(output);
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> hasItem(ItemLike itemLike) {
        return has(itemLike);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
        TCLoggingUtils.startRegistry("Recipe");
        TCBlockCore.BLOCKS.forEach(block -> {
            if (block instanceof ITCRecipe && block instanceof ITCBlocks) {
                ((ITCRecipe) block).addRecipes(this, block, consumer);
                TCLoggingUtils.entryRegistry("Recipe", ((ITCBlocks) block).getRegistryName());
            }
        });
        TCItemCore.ITEMS.forEach(item -> {
            if (item instanceof ITCRecipe && item instanceof ITCItems) {
                ((ITCRecipe) item).addRecipes(this, item, consumer);
                TCLoggingUtils.entryRegistry("Recipe", ((ITCItems) item).getRegistryName());
            }
        });
        this.additionalRecipes(consumer);
        TCLoggingUtils.completeRegistry("Recipe");
    }

    public void saveRecipe(ItemLike itemLike, RecipeOutput output, RecipeBuilder recipe) {
        if (recipe instanceof SimpleCookingRecipeBuilder) {
            if (((SimpleCookingRecipeBuilder) recipe).serializer == RecipeSerializer.BLASTING_RECIPE) {
                recipe.unlockedBy("has_" + itemLike.asItem(), has(itemLike)).save(output, RecipeProvider.getBlastingRecipeName(itemLike));
            } else if (((SimpleCookingRecipeBuilder) recipe).serializer == RecipeSerializer.SMELTING_RECIPE) {
                recipe.unlockedBy("has_" + itemLike.asItem(), has(itemLike)).save(output, RecipeProvider.getSmeltingRecipeName(itemLike));
            }
        } else {
            recipe.unlockedBy("has_" + itemLike.asItem(), has(itemLike)).save(output);
        }
    }

    public void saveRecipe(ItemLike itemLike, RecipeOutput output, RecipeBuilder recipe, String suffix) {
        recipe.unlockedBy("has_" + itemLike.asItem(), has(itemLike)).save(output, getDefaultRecipeId(itemLike) + "_" + suffix);
    }

    private void additionalRecipes(RecipeOutput consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.CREEPER_HEAD).define('#', Items.GUNPOWDER).pattern(" # ").pattern("###").pattern(" # ").unlockedBy("has_gunpowder", hasItem(Items.GUNPOWDER)).save(consumer);
    }
}
