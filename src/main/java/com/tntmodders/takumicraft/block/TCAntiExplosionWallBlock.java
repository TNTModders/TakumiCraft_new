package com.tntmodders.takumicraft.block;

import com.google.common.collect.Lists;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Supplier;

public class TCAntiExplosionWallBlock extends WallBlock implements ITCBlocks, ITCRecipe {
    private final Block baseBlock;

    public TCAntiExplosionWallBlock(Supplier<BlockState> state) {
        super(Properties.copy(state.get().getBlock()));
        this.baseBlock = state.get().getBlock();
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    private ITCBlocks getITCBlock() {
        return (ITCBlocks) this.baseBlock;
    }

    @Override
    public String getRegistryName() {
        return this.getITCBlock().getRegistryName() + "_wall";
    }

    @Override
    public String getEnUSName() {
        return this.getITCBlock().getEnUSName() + " Wall";
    }

    @Override
    public String getJaJPName() {
        return this.getITCBlock().getJaJPName() + "壁";
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        List<TagKey<Block>> tagKeyList = Lists.newArrayList(this.getITCBlock().getBlockTags());
        tagKeyList.add(BlockTags.WALLS);
        return tagKeyList;
    }

    @Override
    public EnumTCBlockStateModelType getBlockStateModelType() {
        return EnumTCBlockStateModelType.WALL;
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS,
                        itemLike, 6)
                .define('#', this.baseBlock)
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_baseblock", TCRecipeProvider.hasItem(this.baseBlock)));
        provider.saveRecipe(itemLike, consumer, SingleItemRecipeBuilder.stonecutting(Ingredient.of(this.baseBlock), RecipeCategory.BUILDING_BLOCKS, itemLike), "stonecutting");
    }


    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return () -> new TCBlockLoot(block, true);
    }
}
