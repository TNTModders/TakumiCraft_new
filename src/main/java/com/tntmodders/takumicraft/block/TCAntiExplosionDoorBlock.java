package com.tntmodders.takumicraft.block;

import com.google.common.collect.Lists;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class TCAntiExplosionDoorBlock extends DoorBlock implements ITCBlocks, ITCRecipe {
    private final Block baseBlock;
    private final String group;

    public TCAntiExplosionDoorBlock(Supplier<BlockState> state, BlockSetType type) {
        this(state, type, "");
    }

    public TCAntiExplosionDoorBlock(Supplier<BlockState> state, BlockSetType type, String group) {
        super(type, Properties.ofFullCopy(state.get().getBlock()).noOcclusion().setId(TCBlockCore.TCBlockId(((ITCBlocks) state.get().getBlock()).getRegistryName() + "_door")));
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
        return this.getITCBlock().getRegistryName() + "_door";
    }

    @Override
    public String getEnUSName() {
        return TCBlockUtils.getNamewithSuffix(this.getITCBlock().getEnUSName(), " Door");
    }

    @Override
    public String getJaJPName() {
        return TCBlockUtils.getNamewithSuffix(this.getITCBlock().getJaJPName(), "扉");
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
    public void registerStateAndModel(TCBlockStateProvider provider) {
        provider.doorBlockWithRenderType(this, provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, provider.key(this.getBaseBlock()).getPath() + "_door_bottom")), provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, provider.key(this.getBaseBlock()).getPath() + "_door_top")), "cutout");
        provider.singleBlockItem(this, provider.blockTexture(this));
    }

    @Override
    public String getBlockRenderType() {
        return "cutout";
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.REDSTONE,
                        itemLike, 3)
                .define('#', this.baseBlock)
                .pattern("##")
                .pattern("##")
                .pattern("##")
                .unlockedBy("has_baseblock", provider.hasItem(this.baseBlock));
        if (!this.group.isEmpty()) {
            builder.group(this.group);
        }
        provider.saveRecipe(itemLike, consumer, builder);
    }


    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }

    @Override
    public void drop(Block block, TCBlockLoot loot) {
        loot.add(block, loot::createDoorTable);
    }
}
