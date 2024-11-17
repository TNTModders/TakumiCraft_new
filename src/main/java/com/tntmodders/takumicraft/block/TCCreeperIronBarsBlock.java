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
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.util.function.Function;

public class TCCreeperIronBarsBlock extends IronBarsBlock implements ITCBlocks, ITCRecipe {
    public TCCreeperIronBarsBlock() {
        super(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3f, 1000000f).noOcclusion().setId(TCBlockCore.TCBlockId("creeperiron_bars")));
    }

    @Override
    public String getRegistryName() {
        return "creeperiron_bars";
    }

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS,
                        TCBlockCore.CREEPER_IRON_BARS, 8)
                .define('#', TCBlockCore.CREEPER_BOMB)
                .define('B', Blocks.IRON_BARS)
                .pattern("BBB")
                .pattern("B#B")
                .pattern("BBB")
                .unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB)));
    }

    @Override
    public String getEnUSName() {
        return "Creeper Iron Bars";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質鉄格子";
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ResourceLocation sourceName = provider.blockTexture(this);
        provider.paneBlockWithRenderType(this, sourceName, sourceName, "tripwire");
        provider.singleBlockItem(this, sourceName);
    }
}
