package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.entity.TCCreeperHangingSignBlockEntity;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.item.TCCreeperSignBlockItem;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import com.tntmodders.takumicraft.world.level.block.state.properties.TCWoodType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import java.util.function.Function;

public class TCCreeperHangingSignBlock extends CeilingHangingSignBlock implements ITCBlocks, ITCRecipe {
    public TCCreeperHangingSignBlock() {
        super(TCWoodType.CREEPER_WOOD, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F, 1000000F).ignitedByLava().setId(TCBlockCore.TCBlockId("creeperhangingsign")));
    }

    @Override
    public String getRegistryName() {
        return "creeperhangingsign";
    }

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }

    @Override
    public String getEnUSName() {
        return "Creeper Hanging Sign";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質吊看板";
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return new TCCreeperSignBlockItem(TCBlockCore.CREEPER_HANGING_SIGN, TCBlockCore.CREEPER_HANGING_SIGN_WALL, Direction.UP);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_154556_, BlockState p_154557_) {
        return new TCCreeperHangingSignBlockEntity(p_154556_, p_154557_);
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        provider.simpleBlock(this, provider.models().sign(this.getRegistryName(), provider.blockTexture(this)));
        ResourceLocation name = ResourceLocation.tryBuild(TakumiCraftCore.MODID, this.getRegistryName());
        provider.itemModels().singleTexture(name.getPath(), provider.mcLoc("item/generated"), "layer0", ResourceLocation.tryBuild(name.getNamespace(), "block/" + name.getPath()));
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION, BlockTags.CEILING_HANGING_SIGNS);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.DECORATIONS, itemLike, 3).define('#', TCBlockCore.CREEPER_PLANKS).define('C', TCBlockCore.CREEPER_CHAIN).pattern("C C").pattern("###").pattern("###").unlockedBy("has_creeperplanks", provider.hasItem(TCBlockCore.CREEPER_PLANKS)));
    }
}
