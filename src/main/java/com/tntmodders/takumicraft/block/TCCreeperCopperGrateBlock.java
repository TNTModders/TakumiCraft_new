package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.Nullable;

public class TCCreeperCopperGrateBlock extends TCCreeperCopperBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final TCCreeperCopperBlock baseBlock;

    public TCCreeperCopperGrateBlock(WeatheringCopper.WeatherState weather, Block block, TCCreeperCopperBlock base) {
        super(weather, block, BlockBehaviour.Properties.of().strength(3.0F, 1000000.0F).sound(SoundType.COPPER_GRATE).mapColor(MapColor.COLOR_ORANGE).noOcclusion().requiresCorrectToolForDrops().isValidSpawn(TCBlockCore::never).isRedstoneConductor(TCBlockCore::never).isSuffocating(TCBlockCore::never).isViewBlocking(TCBlockCore::never).setId(TCBlockCore.TCBlockId(TCCreeperCopperBlock.getName("creepercopper_grate", weather))));
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.FALSE));
        this.baseBlock = base;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_311201_) {
        FluidState fluidstate = p_311201_.getLevel().getFluidState(p_311201_.getClickedPos());
        return super.getStateForPlacement(p_311201_).setValue(WATERLOGGED, fluidstate.is(Fluids.WATER));
    }

    @Override
    protected BlockState updateShape(BlockState p_60541_, LevelReader p_368027_, ScheduledTickAccess p_366146_, BlockPos p_60545_, Direction p_60542_, BlockPos p_60546_, BlockState p_60543_, RandomSource p_363918_) {
        if (p_60541_.getValue(WATERLOGGED)) {
            p_366146_.scheduleTick(p_60545_, Fluids.WATER, Fluids.WATER.getTickDelay(p_368027_));
        }
        return super.updateShape(p_60541_, p_368027_, p_366146_, p_60545_, p_60542_, p_60546_, p_60543_, p_363918_);
    }

    @Override
    protected FluidState getFluidState(BlockState p_312084_) {
        return p_312084_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(true) : super.getFluidState(p_312084_);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_311516_) {
        p_311516_.add(WATERLOGGED);
    }

    @Override
    protected VoxelShape getVisualShape(BlockState p_312193_, BlockGetter p_310654_, BlockPos p_310658_, CollisionContext p_311129_) {
        return Shapes.empty();
    }

    @Override
    protected float getShadeBrightness(BlockState p_312407_, BlockGetter p_310193_, BlockPos p_311965_) {
        return 1.0F;
    }


    @Override
    protected boolean propagatesSkylightDown(BlockState p_331634_) {
        return true;
    }

    @Override
    protected boolean skipRendering(BlockState p_53972_, BlockState p_53973_, Direction p_53974_) {
        return p_53973_.is(this) || super.skipRendering(p_53972_, p_53973_, p_53974_);
    }

    @Override
    protected String getBaseRegistryName() {
        return "creepercopper_grate";
    }

    @Override
    protected String baseEnUSName() {
        return "Creeper Copper Grate";
    }

    @Override
    protected String baseJaJPName() {
        return "匠式硬質銅格子";
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ModelFile model = provider.models().cubeAll(provider.name(this), provider.blockTexture(this)).renderType("cutout");
        provider.simpleBlock(this, model);
        provider.simpleBlockItem(this, model);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        super.addRecipes(provider, itemLike, consumer);
        provider.saveRecipe(itemLike, consumer, SingleItemRecipeBuilder.stonecutting(Ingredient.of(this.baseBlock), RecipeCategory.BUILDING_BLOCKS, itemLike, 4), "stonecutting");
    }
}
