package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.List;
import java.util.function.Function;

public class TCCreeperChainBlock extends ChainBlock implements ITCBlocks, ITCRecipe {
    public TCCreeperChainBlock() {
        super(BlockBehaviour.Properties.of().forceSolidOn().requiresCorrectToolForDrops().strength(5.0F, 1000000.0F).sound(SoundType.CHAIN).noOcclusion().setId(TCBlockCore.TCBlockId("creeperchain")));
    }

    @Override
    public String getRegistryName() {
        return "creeperchain";
    }

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS, TCBlockCore.CREEPER_CHAIN, 8).define('#', TCBlockCore.CREEPER_BOMB).define('B', Blocks.CHAIN).pattern("BBB").pattern("B#B").pattern("BBB").unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB)));
    }

    @Override
    public String getEnUSName() {
        return "Creeper Chain";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質鎖";
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ModelFile model = provider.models().withExistingParent(provider.key(this).toString(), "block/chain").texture("all", provider.blockTexture(this)).texture("particle", provider.blockTexture(this)).renderType("cutout");
        provider.getVariantBuilder(this).partialState().with(TCCreeperChainBlock.AXIS, Direction.Axis.X).setModels(ConfiguredModel.builder().modelFile(model).rotationX(90).rotationY(90).build()).partialState().with(TCCreeperChainBlock.AXIS, Direction.Axis.Y).setModels(ConfiguredModel.builder().modelFile(model).build()).partialState().with(TCCreeperChainBlock.AXIS, Direction.Axis.Z).setModels(ConfiguredModel.builder().modelFile(model).rotationX(90).build());
        provider.itemModels().basicItem(this.asItem());
    }

    @Override
    public String getBlockRenderType() {
        return "cutout";
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION);
    }
}
