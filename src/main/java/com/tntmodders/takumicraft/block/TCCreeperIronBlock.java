package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;

public class TCCreeperIronBlock extends AbstractTCAntiExplosionBlock implements ITCRecipe {
    public TCCreeperIronBlock() {
        super(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));
    }

    @Override
    public String getRegistryName() {
        return "creeperiron";
    }

    @Override
    public String getEnUSName() {
        return "Creeper Iron Block";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質鋼";
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION, BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS,
                        TCBlockCore.CREEPER_IRON, 8)
                .define('#', TCBlockCore.CREEPER_BOMB)
                .define('B', Blocks.IRON_BLOCK)
                .pattern("BBB")
                .pattern("B#B")
                .pattern("BBB")
                .unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB)));
    }
}
