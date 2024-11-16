package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.data.recipes.RecipeBuilder.getDefaultRecipeId;

public class TCRecipeProvider extends RecipeProvider {
    public final HolderGetter<Item> items;
    private final RecipeOutput consumer;

    protected TCRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
        this.consumer = output;
        this.items = provider.lookupOrThrow(Registries.ITEM);
    }

    public Criterion<InventoryChangeTrigger.TriggerInstance> hasItem(ItemLike itemLike) {
        return has(itemLike);
    }

    public Criterion<InventoryChangeTrigger.TriggerInstance> hasItemTag(TagKey<Item> tag) {
        return has(tag);
    }

    @Override
    protected void buildRecipes() {
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
        recipe.unlockedBy("has_" + itemLike.asItem(), has(itemLike)).save(output);
    }

    public void saveRecipe(ItemLike itemLike, RecipeOutput output, RecipeBuilder recipe, String suffix) {
        recipe.unlockedBy("has_" + itemLike.asItem(), has(itemLike)).save(output, getDefaultRecipeId(itemLike) + "_" + suffix);
    }

    public void saveBlastingRecipe(ItemLike itemLike, RecipeOutput output, RecipeBuilder recipe) {
        recipe.unlockedBy("has_" + itemLike.asItem(), has(itemLike)).save(output, RecipeProvider.getBlastingRecipeName(itemLike));
    }

    public void saveSmeltingRecipe(ItemLike itemLike, RecipeOutput output, RecipeBuilder recipe) {
        recipe.unlockedBy("has_" + itemLike.asItem(), has(itemLike)).save(output, RecipeProvider.getSmeltingRecipeName(itemLike));
    }

    private void additionalRecipes(RecipeOutput consumer) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, Items.CREEPER_HEAD).define('#', Items.GUNPOWDER).pattern(" # ").pattern("###").pattern(" # ").unlockedBy("has_gunpowder", hasItem(Items.GUNPOWDER)).save(consumer);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput p_365720_, CompletableFuture<HolderLookup.Provider> p_365098_) {
            super(p_365720_, p_365098_);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider p_369764_, RecipeOutput p_363473_) {
            return new TCRecipeProvider(p_369764_, p_363473_);
        }

        @Override
        public String getName() {
            return "Takumi Craft Recipes";
        }
    }
}
