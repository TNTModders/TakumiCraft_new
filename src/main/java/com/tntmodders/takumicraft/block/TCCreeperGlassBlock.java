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
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.List;
import java.util.function.Function;

public class TCCreeperGlassBlock extends TransparentBlock implements ITCBlocks, ITCRecipe {
    public TCCreeperGlassBlock(String name) {
        super(BlockBehaviour.Properties.of().strength(6f).sound(SoundType.GLASS).noOcclusion().isValidSpawn((state, getter, pos, type) -> TCBlockCore.never(state, getter, pos)).isRedstoneConductor(TCBlockCore::never).isSuffocating(TCBlockCore::never).isViewBlocking(TCBlockCore::never).explosionResistance(1000000f).setId(TCBlockCore.TCBlockId(name)));

    }

    public TCCreeperGlassBlock() {
        this("creeperglass");
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ModelFile model = provider.glassCubeAll(this);
        provider.simpleBlock(this, model);
        provider.simpleBlockItem(this, model);
    }

    @Override
    public String getBlockRenderType() {
        return "cutout";
    }

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS, TCBlockCore.CREEPER_GLASS, 8).define('#', TCBlockCore.CREEPER_BOMB).define('B', Blocks.GLASS).pattern("BBB").pattern("B#B").pattern("BBB").unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB)).group("creeperglass"));
    }

    @Override
    public String getRegistryName() {
        return "creeperglass";
    }

    @Override
    public String getEnUSName() {
        return "Creeper Glass";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質硝子";
    }

    @Override
    public void drop(Block block, TCBlockLoot loot) {
        loot.dropWhenSilkTouch(block);
    }
}
