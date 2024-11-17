package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.List;
import java.util.function.Function;

public class TCLadderBlock extends LadderBlock implements ITCBlocks, ITCRecipe {
    public TCLadderBlock() {
        super(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().forceSolidOff().strength(0.4F).sound(SoundType.LADDER).noOcclusion().pushReaction(PushReaction.DESTROY).explosionResistance(1000000f).setId(TCBlockCore.TCBlockId("creeperladder")));
    }

    @Override
    public String getRegistryName() {
        return "creeperladder";
    }

    @Override
    public String getEnUSName() {
        return "Creeper Ladder";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質梯子";
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION, BlockTags.MINEABLE_WITH_AXE, BlockTags.CLIMBABLE);
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ResourceLocation location = provider.blockTexture(this);
        ModelFile model = provider.models().withExistingParent(provider.name(this), "ladder").texture("particle", location).texture("texture", location).renderType("cutout");
        provider.horizontalBlock(this, model);
        provider.singleBlockItem(this, location);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS, TCBlockCore.CREEPER_LADDER, 8).define('#', TCBlockCore.CREEPER_BOMB).define('B', Blocks.LADDER).pattern("BBB").pattern("B#B").pattern("BBB").unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB)));
    }


    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }
}
