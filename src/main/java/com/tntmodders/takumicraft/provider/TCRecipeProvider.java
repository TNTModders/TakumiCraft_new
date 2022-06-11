package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
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
            if (block instanceof ITCRecipe && block instanceof ITCBlocks) {
                ((ITCRecipe) block).addRecipes(this, block,consumer);
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

    public void saveRecipe(ItemLike itemLike,  @NotNull Consumer<FinishedRecipe> consumer, RecipeBuilder recipe) {
        if (recipe instanceof SimpleCookingRecipeBuilder) {
            if (((SimpleCookingRecipeBuilder) recipe).serializer == RecipeSerializer.BLASTING_RECIPE) {
                recipe.unlockedBy("has_" + itemLike.asItem(), has(itemLike)).save(consumer, RecipeProvider.getBlastingRecipeName(itemLike));
            }else if(((SimpleCookingRecipeBuilder) recipe).serializer==RecipeSerializer.SMELTING_RECIPE){
                recipe.unlockedBy("has_" + itemLike.asItem(), has(itemLike)).save(consumer,RecipeProvider.getSmeltingRecipeName(itemLike));
            }
        } else {
            recipe.unlockedBy("has_" + itemLike.asItem(), has(itemLike)).save(consumer);
        }
    }

    private void additionalRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(Items.CREEPER_HEAD).define('#', Items.GUNPOWDER).pattern(" # ").pattern("###").pattern(" # ").unlockedBy("has_gunpowder", hasItem(Items.GUNPOWDER)).save(consumer);
    }
}
