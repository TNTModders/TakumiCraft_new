package com.tntmodders.takumicraft.block;

import com.google.common.collect.Lists;
import com.tntmodders.takumicraft.TakumiCraftCore;
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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class TCAntiExplosionTrapDoorBlock extends TrapDoorBlock implements ITCBlocks, ITCRecipe {
    private final Block baseBlock;
    private final boolean orientable;
    private final String group;

    public TCAntiExplosionTrapDoorBlock(Supplier<BlockState> state, BlockSetType type) {
        this(state, type, true);
    }

    public TCAntiExplosionTrapDoorBlock(Supplier<BlockState> state, BlockSetType type, boolean orientable) {
        this(state, type, orientable, "");
    }

    public TCAntiExplosionTrapDoorBlock(Supplier<BlockState> state, BlockSetType type, boolean orientable, String group) {
        super(type, Properties.ofFullCopy(state.get().getBlock()).noOcclusion());
        this.baseBlock = state.get().getBlock();
        this.orientable = orientable;
        this.group = group;
    }

    public boolean isOrientable() {
        return orientable;
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    private ITCBlocks getITCBlock() {
        return (ITCBlocks) this.baseBlock;
    }

    @Override
    public String getRegistryName() {
        return this.getITCBlock().getRegistryName() + "_trapdoor";
    }

    @Override
    public String getEnUSName() {
        return TCBlockUtils.getNamewithSuffix(this.getITCBlock().getEnUSName(), " Trap Door");
    }

    @Override
    public String getJaJPName() {
        return TCBlockUtils.getNamewithSuffix(this.getITCBlock().getJaJPName(), "天扉");
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        List<TagKey<Block>> tagKeyList = Lists.newArrayList(this.getITCBlock().getBlockTags());
        tagKeyList.add(BlockTags.TRAPDOORS);
        if (this.soundType == SoundType.WOOD) {
            tagKeyList.add(BlockTags.WOODEN_TRAPDOORS);
        }
        return tagKeyList;
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        provider.trapdoorBlockWithRenderType(this, provider.blockTexture(this), this.isOrientable(), "cutout");
        provider.itemModels().withExistingParent(this.getRegistryName(), provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, this.getRegistryName() + "_bottom")));
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, itemLike).define('#', this.baseBlock).pattern("##").pattern("##").unlockedBy("has_baseblock", TCRecipeProvider.hasItem(this.baseBlock));
        if (!this.group.isEmpty()) {
            builder.group(this.group);
        }
        provider.saveRecipe(itemLike, consumer, builder);
    }


    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }
}
