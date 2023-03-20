package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.function.Consumer;

public class TCDeepGunOreBlock extends TCGunOreBlock {
    public TCDeepGunOreBlock() {
        super(BlockBehaviour.Properties.of(Material.EXPLOSIVE).requiresCorrectToolForDrops().strength(10f, 0f).color(MaterialColor.STONE), "deepslate_gunore");
    }

    @Override
    public int getExpDrop(BlockState state, LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        return super.getExpDrop(state, level, randomSource, pos, fortuneLevel, silkTouchLevel) * 2;
    }

    @Override
    public float getPower() {
        return super.getPower() * 2;
    }

    @Override
    public String getEnUSName() {
        return "Deepslate Gunpowder Ore";
    }

    @Override
    public String getJaJPName() {
        return "深層火薬岩";
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, Consumer<FinishedRecipe> consumer) {
        provider.saveRecipe(itemLike, consumer, SimpleCookingRecipeBuilder.smelting(Ingredient.of(itemLike),
                RecipeCategory.BUILDING_BLOCKS, Items.GUNPOWDER, 1f, 100));
        provider.saveRecipe(itemLike, consumer, SimpleCookingRecipeBuilder.blasting(Ingredient.of(itemLike),
                RecipeCategory.BUILDING_BLOCKS, Items.GUNPOWDER, 1f, 50));
    }
}
