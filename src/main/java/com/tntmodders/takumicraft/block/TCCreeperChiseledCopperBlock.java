package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;

public class TCCreeperChiseledCopperBlock extends TCCreeperCopperBlock {
    private final TCCreeperCopperBlock baseBlock;

    public TCCreeperChiseledCopperBlock(WeatheringCopper.WeatherState weather, Block block, TCCreeperCopperBlock base) {
        super(weather, block);
        this.baseBlock = base;
    }

    @Override
    protected String getBaseRegistryName() {
        return "creepercopper_chiseled";
    }

    @Override
    protected String baseEnUSName() {
        return "Creeper Chiesed Copper";
    }

    @Override
    protected String baseJaJPName() {
        return "匠式硬質意匠銅";
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        super.addRecipes(provider, itemLike, consumer);
        provider.saveRecipe(itemLike, consumer, SingleItemRecipeBuilder.stonecutting(Ingredient.of(this.baseBlock), RecipeCategory.BUILDING_BLOCKS, itemLike, 4), "stonecutting");
    }
}
