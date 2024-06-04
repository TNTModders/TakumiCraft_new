package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.util.List;
import java.util.function.Supplier;

public class TCCreeperGlassPaneBlock extends IronBarsBlock implements ITCBlocks, ITCRecipe {
    private final Block baseTakumiBlock;

    public TCCreeperGlassPaneBlock(Block takumiBlock) {
        super(BlockBehaviour.Properties.of().sound(SoundType.GLASS).instrument(NoteBlockInstrument.HAT).strength(6F).sound(SoundType.GLASS).noOcclusion().isValidSpawn((state, getter, pos, type) -> TCBlockCore.never(state, getter, pos)).isRedstoneConductor(TCBlockCore::never).isSuffocating(TCBlockCore::never).isViewBlocking(TCBlockCore::never).explosionResistance(1000000f));
        this.baseTakumiBlock = takumiBlock;
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ResourceLocation sourceName = provider.blockTexture(this.getBaseTakumiBlock());
        ResourceLocation topName = provider.blockFolder(new ResourceLocation(TakumiCraftCore.MODID, "creeperglasspane_top"));
        provider.paneBlockWithRenderType(this, sourceName, topName, "cutout");
        provider.singleBlockItem(this, sourceName);
    }

    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return () -> new TCBlockLoot(block, true);
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION);
    }

    public Block getBaseTakumiBlock() {
        return baseTakumiBlock;
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlockCore.CREEPER_GLASS_PANE, 8).define('#', TCBlockCore.CREEPER_BOMB).define('B', Blocks.GLASS_PANE).pattern("BBB").pattern("B#B").pattern("BBB").unlockedBy("has_creeperbomb", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_BOMB)).group("creeperglasspane"));

        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlockCore.CREEPER_GLASS_PANE, 16).define('#', TCBlockCore.CREEPER_GLASS).pattern("###").pattern("###").unlockedBy("has_creeperglass", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_GLASS)).group("creeperglasspane_from_creeperglass"), "from_creeperglass");
    }

    @Override
    public String getRegistryName() {
        return "creeperglasspane";
    }

    @Override
    public String getEnUSName() {
        return "Creeper Glass Pane";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質板硝子";
    }

    @Override
    public void drop(Block block, TCBlockLoot loot) {
        loot.dropWhenSilkTouch(block);
    }
}
