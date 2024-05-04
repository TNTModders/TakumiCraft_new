package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TCCreeperTintedGlassBlock extends TCCreeperGlassBlock {

    @Override
    public EnumTCBlockStateModelType getBlockStateModelType() {
        return EnumTCBlockStateModelType.STAINED_GLASS;
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS,
                        TCBlockCore.CREEPER_TINTED_GLASS, 8)
                .define('#', TCBlockCore.CREEPER_BOMB)
                .define('B', Blocks.TINTED_GLASS)
                .pattern("BBB")
                .pattern("B#B")
                .pattern("BBB")
                .unlockedBy("has_creeperbomb", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_BOMB))
                .group("creeperglass"));

        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS,
                        TCBlockCore.CREEPER_TINTED_GLASS, 2)
                .define('#', TCBlockCore.CREEPER_GLASS)
                .define('A', Items.AMETHYST_SHARD)
                .pattern(" A ")
                .pattern("A#A")
                .pattern(" A ")
                .unlockedBy("has_creeperglass", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_GLASS))
                .group("creeperglass"), "from_creeperglass");
    }

    @Override
    public String getRegistryName() {
        return "creepertintedglass";
    }

    @Override
    public String getEnUSName() {
        return "Creeper Tinted Glass";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質遮光硝子";
    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_154824_, BlockGetter p_154825_, BlockPos p_154826_) {
        return false;
    }

    @Override
    public int getLightBlock(BlockState p_154828_, BlockGetter p_154829_, BlockPos p_154830_) {
        return p_154829_.getMaxLightLevel();
    }
}
