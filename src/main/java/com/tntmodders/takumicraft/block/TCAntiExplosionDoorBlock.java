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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.List;
import java.util.function.Supplier;

public class TCAntiExplosionDoorBlock extends DoorBlock implements ITCBlocks, ITCRecipe {
    private final Block baseBlock;

    public TCAntiExplosionDoorBlock(Supplier<BlockState> state, BlockSetType type) {
        super(type, Properties.ofFullCopy(state.get().getBlock()));
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
        return this.getITCBlock().getRegistryName() + "_door";
    }

    @Override
    public String getEnUSName() {
        return this.getITCBlock().getEnUSName() + " Door";
    }

    @Override
    public String getJaJPName() {
        return this.getITCBlock().getJaJPName() + "扉";
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        List<TagKey<Block>> tagKeyList = Lists.newArrayList(this.getITCBlock().getBlockTags());
        tagKeyList.add(BlockTags.DOORS);
        if (this.type().soundType() == SoundType.WOOD) {
            tagKeyList.add(BlockTags.WOODEN_DOORS);
        }
        return tagKeyList;
    }

    @Override
    public EnumTCBlockStateModelType getBlockStateModelType() {
        return EnumTCBlockStateModelType.DOOR;
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS,
                        itemLike, 3)
                .define('#', this.baseBlock)
                .pattern("##")
                .pattern("##")
                .pattern("##")
                .unlockedBy("has_baseblock", TCRecipeProvider.hasItem(this.baseBlock)));
    }


    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return () -> new TCBlockLoot(block, true);
    }
}
