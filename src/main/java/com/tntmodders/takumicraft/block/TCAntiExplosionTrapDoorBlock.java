package com.tntmodders.takumicraft.block;

import com.google.common.collect.Lists;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.List;
import java.util.function.Supplier;

public class TCAntiExplosionTrapDoorBlock extends TrapDoorBlock implements ITCBlocks, ITCRecipe {
    private final Block baseBlock;
    private final boolean orientable;

    public TCAntiExplosionTrapDoorBlock(Supplier<BlockState> state, BlockSetType type) {
        this(state, type, true);
    }

    public TCAntiExplosionTrapDoorBlock(Supplier<BlockState> state, BlockSetType type, boolean orientable) {
        super(Properties.copy(state.get().getBlock()), type);
        this.baseBlock = state.get().getBlock();
        this.orientable = orientable;
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
        return this.getITCBlock().getEnUSName() + " Trap Door";
    }

    @Override
    public String getJaJPName() {
        return this.getITCBlock().getJaJPName() + "天扉";
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
    public EnumTCBlockStateModelType getBlockStateModelType() {
        return EnumTCBlockStateModelType.TRAP_DOOR;
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, itemLike)
                .define('#', this.baseBlock)
                .pattern("##")
                .pattern("##")
                .unlockedBy("has_baseblock", TCRecipeProvider.hasItem(this.baseBlock)));
    }


    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return () -> new TCBlockLoot(block, true);
    }
}
