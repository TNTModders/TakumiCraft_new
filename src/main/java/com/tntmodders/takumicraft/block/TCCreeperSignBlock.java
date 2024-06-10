package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.entity.TCCreeperSignBlockEntity;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.item.TCCreeperSignBlockItem;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import java.util.function.Supplier;

public class TCCreeperSignBlock extends StandingSignBlock implements ITCBlocks, ITCRecipe {
    public TCCreeperSignBlock() {
        super(WoodType.OAK, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F, 1000000F).ignitedByLava());
    }

    @Override
    public String getRegistryName() {
        return "creepersign";
    }

    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return () -> new TCBlockLoot(block, true);
    }

    @Override
    public String getEnUSName() {
        return "Creeper Sign";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質看板";
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return new TCCreeperSignBlockItem(TCBlockCore.CREEPER_SIGN, TCBlockCore.CREEPER_SIGN_WALL, Direction.DOWN);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_154556_, BlockState p_154557_) {
        return new TCCreeperSignBlockEntity(p_154556_, p_154557_);
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        provider.simpleBlock(this, provider.models().sign(this.getRegistryName(), provider.blockTexture(this)));
        ResourceLocation name = new ResourceLocation(TakumiCraftCore.MODID, this.getRegistryName());
        provider.itemModels().singleTexture(name.getPath(), provider.mcLoc("item/generated"), "layer0", new ResourceLocation(name.getNamespace(), "block/" + name.getPath()));
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION, BlockTags.STANDING_SIGNS);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, itemLike, 3).define('#', TCBlockCore.CREEPER_PLANKS).define('S', Items.STICK).pattern("###").pattern("###").pattern(" S ").unlockedBy("has_creeperplanks", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_PLANKS)));
    }
}

