package com.tntmodders.takumicraft.block;

import com.google.common.collect.Lists;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import com.tntmodders.takumicraft.utils.TCBlockUtils;
import net.minecraft.core.HolderLookup;
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
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class TCAntiExplosionWallBlock extends WallBlock implements ITCBlocks, ITCRecipe {
    private final Block baseBlock;
    private final String group;

    public TCAntiExplosionWallBlock(Supplier<BlockState> state) {
        this(state, "");
    }

    public TCAntiExplosionWallBlock(Supplier<BlockState> state, String group) {
        super(Properties.ofFullCopy(state.get().getBlock()));
        this.baseBlock = state.get().getBlock();
        this.group = group;
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    private ITCBlocks getITCBlock() {
        return (ITCBlocks) this.baseBlock;
    }

    @Override
    public String getRegistryName() {
        return TCBlockUtils.getNamewithSuffix(this.getITCBlock().getRegistryName(), "_wall");
    }

    @Override
    public String getEnUSName() {
        return TCBlockUtils.getNamewithSuffix(this.getITCBlock().getEnUSName(), " Wall");
    }

    @Override
    public String getJaJPName() {
        return TCBlockUtils.getNamewithSuffix(this.getITCBlock().getJaJPName(), "壁");
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        List<TagKey<Block>> tagKeyList = Lists.newArrayList(this.getITCBlock().getBlockTags());
        tagKeyList.add(BlockTags.WALLS);
        return tagKeyList;
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        provider.wallBlock(this, provider.blockTexture(this.getBaseBlock()));
        ModelFile model = provider.models().withExistingParent(this.getRegistryName(), "block/wall_inventory").texture("wall", provider.blockTexture(this.getBaseBlock()));
        provider.simpleBlockItem(this, model);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.DECORATIONS, itemLike, 6).define('#', this.baseBlock).pattern("###").pattern("###").unlockedBy("has_baseblock", provider.hasItem(this.baseBlock));
        if (!this.group.isEmpty()) {
            builder.group(this.group);
        }
        provider.saveRecipe(itemLike, consumer, builder);
        provider.saveRecipe(itemLike, consumer, SingleItemRecipeBuilder.stonecutting(Ingredient.of(this.baseBlock), RecipeCategory.DECORATIONS, itemLike), "stonecutting");
    }


    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }
}
