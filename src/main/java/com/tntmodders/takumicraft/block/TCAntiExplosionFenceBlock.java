package com.tntmodders.takumicraft.block;

import com.google.common.collect.Lists;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.List;
import java.util.function.Supplier;

public class TCAntiExplosionFenceBlock extends FenceBlock implements ITCBlocks, ITCRecipe {
    private final Block baseBlock;

    public TCAntiExplosionFenceBlock(Supplier<BlockState> state) {
        super(Properties.ofFullCopy(state.get().getBlock()));
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
        return this.getITCBlock().getRegistryName() + "_fence";
    }

    @Override
    public String getEnUSName() {
        return this.getITCBlock().getEnUSName() + " Fence";
    }

    @Override
    public String getJaJPName() {
        return this.getITCBlock().getJaJPName() + "柵";
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        List<TagKey<Block>> tagKeyList = Lists.newArrayList(this.getITCBlock().getBlockTags());
        tagKeyList.add(BlockTags.FENCES);
        tagKeyList.add(BlockTags.WOODEN_FENCES);
        return tagKeyList;
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        provider.fenceBlock(this, provider.blockTexture(this.getBaseBlock()));
        ModelFile model = provider.models().withExistingParent(this.getRegistryName(), "block/fence_inventory").texture("texture", provider.blockTexture(this.getBaseBlock()));
        provider.simpleBlockItem(this, model);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, itemLike, 3).define('#', this.baseBlock).define('S', Items.STICK).pattern("#S#").pattern("#S#").unlockedBy("has_baseblock", TCRecipeProvider.hasItem(this.baseBlock)));
    }


    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return () -> new TCBlockLoot(block, true);
    }
}
