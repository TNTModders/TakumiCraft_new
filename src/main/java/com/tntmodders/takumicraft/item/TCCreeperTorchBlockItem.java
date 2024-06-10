package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class TCCreeperTorchBlockItem extends AbstractTCStandingBlockItem implements ITCRecipe {
    public TCCreeperTorchBlockItem(Block block, Block wallBlock) {
        super(block, wallBlock, Direction.DOWN);
    }

    @Override
    public String getRegistryName() {
        return "creepertorch";
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, itemLike, 2)
                .define('#', TCBlockCore.CREEPER_BOMB)
                .define('S', Items.STICK)
                .pattern("#")
                .pattern("S")
                .unlockedBy("has_creeperbomb", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_BOMB)));
    }

    @Override
    public String getEnUSName() {
        return "Creeper Torch";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質松明";
    }
}
