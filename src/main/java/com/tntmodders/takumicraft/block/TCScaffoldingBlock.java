package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.item.TCScaffoldingBlockItem;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.List;
import java.util.function.Function;

public class TCScaffoldingBlock extends ScaffoldingBlock implements ITCBlocks, ITCRecipe {
    public static final int STABILITY_MAX_DISTANCE = 7;

    public TCScaffoldingBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).noCollission().sound(SoundType.SCAFFOLDING).dynamicShape().isValidSpawn(TCBlockCore::never).pushReaction(PushReaction.DESTROY).isRedstoneConductor(TCBlockCore::never).explosionResistance(1000000f));
    }

    public static int getDistanceTC(BlockGetter p_56025_, BlockPos p_56026_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = p_56026_.mutable().move(Direction.DOWN);
        BlockState blockstate = p_56025_.getBlockState(blockpos$mutableblockpos);
        int i = STABILITY_MAX_DISTANCE;
        if (blockstate.is(TCBlockCore.CREEPER_SCAFFOLDING) || blockstate.is(Blocks.SCAFFOLDING)) {
            i = blockstate.getValue(DISTANCE);
        } else if (blockstate.isFaceSturdy(p_56025_, blockpos$mutableblockpos, Direction.UP)) {
            return 0;
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState blockstate1 = p_56025_.getBlockState(blockpos$mutableblockpos.setWithOffset(p_56026_, direction));
            if (blockstate1.is(TCBlockCore.CREEPER_SCAFFOLDING) || blockstate1.is(Blocks.SCAFFOLDING)) {
                i = Math.min(i, blockstate1.getValue(DISTANCE) + 1);
                if (i == 1) {
                    break;
                }
            }
        }

        return i;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_56023_) {
        BlockPos blockpos = p_56023_.getClickedPos();
        Level level = p_56023_.getLevel();
        int i = getDistanceTC(level, blockpos);
        return this.defaultBlockState().setValue(WATERLOGGED, level.getFluidState(blockpos).getType() == Fluids.WATER).setValue(DISTANCE, i).setValue(BOTTOM, this.isBottom(level, blockpos, i));
    }

    @Override
    public void tick(BlockState p_222019_, ServerLevel p_222020_, BlockPos p_222021_, RandomSource p_222022_) {
        int i = getDistanceTC(p_222020_, p_222021_);
        BlockState blockstate = p_222019_.setValue(DISTANCE, i).setValue(BOTTOM, this.isBottom(p_222020_, p_222021_, i));
        if (blockstate.getValue(DISTANCE) == STABILITY_MAX_DISTANCE) {
            if (p_222019_.getValue(DISTANCE) == STABILITY_MAX_DISTANCE) {
                FallingBlockEntity.fall(p_222020_, p_222021_, blockstate);
            } else {
                p_222020_.destroyBlock(p_222021_, true);
            }
        } else if (p_222019_ != blockstate) {
            p_222020_.setBlock(p_222021_, blockstate, 3);
        }

    }

    @Override
    public boolean canSurvive(BlockState p_56040_, LevelReader p_56041_, BlockPos p_56042_) {
        return getDistanceTC(p_56041_, p_56042_) < STABILITY_MAX_DISTANCE;
    }

    private boolean isBottom(BlockGetter p_56028_, BlockPos p_56029_, int p_56030_) {
        return p_56030_ > 0 && !p_56028_.getBlockState(p_56029_.below()).is(this);
    }

    @Override
    public boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return true;
    }

    @Override
    public String getRegistryName() {
        return "creeperscaffolding";
    }

    @Override
    public String getEnUSName() {
        return "Creeper Scaffolding";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質足場";
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION, BlockTags.MINEABLE_WITH_AXE, BlockTags.CLIMBABLE);
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ResourceLocation top = provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, provider.name(this) + "_top"));
        ResourceLocation side = provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, provider.name(this) + "_side"));
        ResourceLocation bottom = provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, provider.name(this) + "_bottom"));

        ModelFile model_stable = provider.models().withExistingParent(provider.name(this) + "_stable", "scaffolding_stable").texture("particle", top).texture("top", top).texture("side", side).texture("bottom", bottom).renderType("cutout");
        ModelFile model_unstable = provider.models().withExistingParent(provider.name(this) + "_unstable", "scaffolding_unstable").texture("particle", top).texture("top", top).texture("side", side).texture("bottom", bottom).renderType("cutout");

        provider.getVariantBuilder(this).partialState().with(ScaffoldingBlock.BOTTOM, false).addModels(new ConfiguredModel(model_stable)).partialState().with(ScaffoldingBlock.BOTTOM, true).addModels(new ConfiguredModel(model_unstable));
        provider.simpleBlockItem(this, model_stable);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS, TCBlockCore.CREEPER_SCAFFOLDING, 8).define('#', TCBlockCore.CREEPER_BOMB).define('B', Blocks.SCAFFOLDING).pattern("BBB").pattern("B#B").pattern("BBB").unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB)));
    }


    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return new TCScaffoldingBlockItem(block);
    }
}
