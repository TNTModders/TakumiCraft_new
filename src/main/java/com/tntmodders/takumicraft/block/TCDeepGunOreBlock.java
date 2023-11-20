package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.Tags;

public class TCDeepGunOreBlock extends TCGunOreBlock {
    public TCDeepGunOreBlock() {
        super(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(10f, 0f).mapColor(MapColor.DEEPSLATE), "deepslate_gunore");
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
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput output) {
        provider.saveRecipe(itemLike, output, SimpleCookingRecipeBuilder.smelting(Ingredient.of(itemLike),
                RecipeCategory.BUILDING_BLOCKS, Items.GUNPOWDER, 1f, 100));
        provider.saveRecipe(itemLike, output, SimpleCookingRecipeBuilder.blasting(Ingredient.of(itemLike),
                RecipeCategory.BUILDING_BLOCKS, Items.GUNPOWDER, 1f, 50));
    }

    @Override
    protected TagKey<Block> oreTag() {
        return Tags.Blocks.ORES_IN_GROUND_DEEPSLATE;
    }

    @Override
    public void drop(Block block, TCBlockLoot loot) {
        loot.add(block, p_250359_ -> loot.createOreDrop(p_250359_, Items.GUNPOWDER).withPool(LootPool.lootPool().when(TCBlockLoot.HAS_MINESWEEPER).setRolls(ConstantValue.exactly(1.0F)).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 8.0F)))));
    }
}
