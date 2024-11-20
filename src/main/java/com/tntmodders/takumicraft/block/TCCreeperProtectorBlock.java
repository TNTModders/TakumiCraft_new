package com.tntmodders.takumicraft.block;

import com.mojang.serialization.MapCodec;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.entity.TCCreeperProtectorBlockEntity;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
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
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TCCreeperProtectorBlock extends BaseEntityBlock implements ITCBlocks, ITCRecipe {
    public static final MapCodec<TCCreeperProtectorBlock> CODEC = simpleCodec(p_309280_ -> new TCCreeperProtectorBlock());
    public static final Map<Direction, BooleanProperty> FACEING_PROPERTIES = new HashMap<>();

    static {
        Direction.stream().forEach(direction -> FACEING_PROPERTIES.put(direction, BooleanProperty.create("face_" + direction.getName())));
    }

    public TCCreeperProtectorBlock() {
        super(Properties.of().strength(0.1f).sound(SoundType.GLASS).isValidSpawn((state, getter, pos, type) -> TCBlockCore.never(state, getter, pos)).isRedstoneConductor(TCBlockCore::never).isSuffocating(TCBlockCore::never).pushReaction(PushReaction.DESTROY).explosionResistance(1000000f).dynamicShape().isViewBlocking(TCBlockCore::never).noCollission().setId(TCBlockCore.TCBlockId("creeperprotector")));
        BlockState defaultState = this.getStateDefinition().any();
        for (Direction direction : Direction.values()) {
            defaultState = defaultState.setValue(FACEING_PROPERTIES.get(direction), true);
        }
        this.registerDefaultState(defaultState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        FACEING_PROPERTIES.values().forEach(builder::add);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        for (Direction direction : Direction.values()) {
            BlockState neighborState = context.getLevel().getBlockState(context.getClickedPos().relative(direction));
            state = state.setValue(FACEING_PROPERTIES.get(direction), !neighborState.isAir());
        }
        return state;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        boolean canSurvive = false;
        for (Direction direction : Direction.values()) {
            if (state.getValue(FACEING_PROPERTIES.get(direction))) {
                canSurvive = true;
                break;
            }
        }
        return canSurvive && super.canSurvive(state, level, pos);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos facePos, BlockState faceState, RandomSource random) {
        for (Direction direction1 : Direction.values()) {
            BlockState neighborState = levelReader.getBlockState(pos.relative(direction1));
            boolean face = state.getValue(FACEING_PROPERTIES.get(direction1));
            if (face == neighborState.isAir()) {
                state = state.setValue(FACEING_PROPERTIES.get(direction1), !face);
            }
        }
        return super.updateShape(state, levelReader, tickAccess, pos, direction, facePos, faceState, random);
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState faceState, Direction direction) {
        return !state.getValue(FACEING_PROPERTIES.get(direction));
    }

    @Override
    protected float getShadeBrightness(BlockState p_312407_, BlockGetter p_310193_, BlockPos p_311965_) {
        return 1.0F;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState p_312717_) {
        return true;
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ModelFile model = provider.models().withExistingParent(this.getRegistryName(), ResourceLocation.tryBuild(TakumiCraftCore.MODID, "template_" + this.getRegistryName())).renderType("cutout");
        provider.getVariantBuilder(this).forAllStatesExcept(state -> ConfiguredModel.builder().modelFile(model).build(), FACEING_PROPERTIES.values().toArray(new BooleanProperty[0]));
        provider.simpleBlockItem(this, model);
    }

    @Override
    public String getBlockRenderType() {
        return "cutout";
    }

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS, TCBlockCore.CREEPER_PROTECTOR, 8).define('#', TCBlockCore.CREEPER_BOMB).define('B', TCBlockCore.CREEPER_TINTED_GLASS).define('H', Items.HONEYCOMB).pattern("BBB").pattern("H#H").pattern("BBB").unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB)));
    }

    @Override
    public String getRegistryName() {
        return "creeperprotector";
    }

    @Override
    public String getEnUSName() {
        return "Creeper Protector";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質保蠟";
    }

    @Override
    public void drop(Block block, TCBlockLoot loot) {
        loot.dropWhenSilkTouch(block);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCCreeperProtectorBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
