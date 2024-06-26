package com.tntmodders.takumicraft.block;

import com.google.common.collect.Lists;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import com.tntmodders.takumicraft.utils.TCBlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class TCAntiExplosionHalfBlock extends Block implements ITCBlocks, ITCRecipe, SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape BOTTOM_AABB;
    protected static final VoxelShape TOP_AABB;
    protected static final VoxelShape NORTH_AABB;
    protected static final VoxelShape WEST_AABB;
    protected static final VoxelShape EAST_AABB;
    protected static final VoxelShape SOUTH_AABB;

    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        BOTTOM_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
        TOP_AABB = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
        NORTH_AABB = Block.box(0.0, 0.0, 8.0, 16.0, 16.0, 16.0);
        WEST_AABB = Block.box(8.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        EAST_AABB = Block.box(0.0, 0.0, 0.0, 8.0, 16.0, 16.0);
        SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 8.0);
    }

    private final Block baseBlock;
    private final boolean addStoneCutterRecipe;
    private final String group;

    public TCAntiExplosionHalfBlock(Supplier<BlockState> state, boolean stoneCutter) {
        this(state, stoneCutter, "");
    }

    public TCAntiExplosionHalfBlock(Supplier<BlockState> state, boolean stoneCutter, String group) {
        super(Properties.ofFullCopy(state.get().getBlock()));
        this.baseBlock = state.get().getBlock();
        this.addStoneCutterRecipe = stoneCutter;
        this.group = group;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(FACING, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        VoxelShape voxelShape = switch (p_220053_1_.getValue(FACING)) {
            case UP -> TOP_AABB;
            case NORTH -> NORTH_AABB;
            case EAST -> EAST_AABB;
            case WEST -> WEST_AABB;
            case SOUTH -> SOUTH_AABB;
            default -> BOTTOM_AABB;
        };

        return voxelShape;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = context.getLevel().getBlockState(blockpos);
        FluidState fluidstate = context.getLevel().getFluidState(blockpos);
        BlockState blockstate1 = this.defaultBlockState().setValue(FACING, Direction.DOWN).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        Direction direction = context.getClickedFace();
        BlockState var10000 = switch (direction) {
            case NORTH, EAST, SOUTH, WEST -> blockstate1.setValue(FACING, direction);
            default ->
                    direction == Direction.DOWN || direction != Direction.UP && context.getClickLocation().y - (double) blockpos.getY() > 0.5 ? blockstate1.setValue(FACING, Direction.UP) : blockstate1.setValue(FACING, Direction.DOWN);
        };

        return var10000;
    }

    @Override
    public FluidState getFluidState(BlockState p_56397_) {
        return p_56397_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_56397_);
    }

    @Override
    public BlockState updateShape(BlockState p_56381_, Direction p_56382_, BlockState p_56383_, LevelAccessor p_56384_, BlockPos p_56385_, BlockPos p_56386_) {
        if (p_56381_.getValue(WATERLOGGED)) {
            p_56384_.scheduleTick(p_56385_, Fluids.WATER, Fluids.WATER.getTickDelay(p_56384_));
        }

        return super.updateShape(p_56381_, p_56382_, p_56383_, p_56384_, p_56385_, p_56386_);
    }


    @Override
    protected boolean isPathfindable(BlockState p_60475_, PathComputationType p_60478_) {
        return switch (p_60478_) {
            case LAND, AIR -> false;
            case WATER -> p_60475_.getFluidState().is(FluidTags.WATER);
        };
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    private ITCBlocks getITCBlock() {
        return (ITCBlocks) this.baseBlock;
    }

    public String getTextureName() {
        return this.getITCBlock().getRegistryName();
    }

    public String getTopTextureName() {
        return this.getITCBlock().getRegistryName();
    }

    public String getBottomTextureName() {
        return this.getITCBlock().getRegistryName();
    }

    @Override
    public String getRegistryName() {
        return this.getITCBlock().getRegistryName() + "_half";
    }

    @Override
    public String getEnUSName() {
        return TCBlockUtils.getNamewithSuffix(this.getITCBlock().getEnUSName(), " Half Block");
    }

    @Override
    public String getJaJPName() {
        return TCBlockUtils.getNamewithSuffix(this.getITCBlock().getJaJPName(), "半材");
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        List<TagKey<Block>> tagKeyList = Lists.newArrayList(this.getITCBlock().getBlockTags());
        tagKeyList.add(BlockTags.SLABS);
        return tagKeyList;
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        VariantBlockStateBuilder builder = provider.getVariantBuilder(this);
        Direction.stream().forEach(direction -> builder.partialState().with(TCAntiExplosionHalfBlock.FACING, direction).addModels(new ConfiguredModel(provider.models().withExistingParent(provider.name(this) + "_" + direction.getName(), ResourceLocation.tryBuild(TakumiCraftCore.MODID, "half_" + direction.getName())).texture("top", provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, this.getTopTextureName()))).texture("side", provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, this.getTextureName()))).texture("bottom", provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, this.getBottomTextureName()))).renderType("cutout"))));
        provider.itemModels().withExistingParent(this.getRegistryName(), provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, this.getRegistryName() + "_east")));
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, itemLike, 6).define('#', this.baseBlock).pattern("###").unlockedBy("has_baseblock", TCRecipeProvider.hasItem(this.baseBlock));
        if (!this.group.isEmpty()) {
            builder.group(this.group);
        }
        provider.saveRecipe(itemLike, consumer, builder);
        if (this.addStoneCutterRecipe) {
            provider.saveRecipe(itemLike, consumer, SingleItemRecipeBuilder.stonecutting(Ingredient.of(this.baseBlock), RecipeCategory.BUILDING_BLOCKS, itemLike, 2), "stonecutting");
        }
    }


    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }
}
