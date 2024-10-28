package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.entity.TCCreeperBarrelBlockEntity;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class TCCreeperBarrelBlock extends BarrelBlock implements ITCBlocks, ITCRecipe {
    public static final BooleanProperty EXPLOSIVE = BooleanProperty.create("explosive");

    public TCCreeperBarrelBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).explosionResistance(1000000f));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(EXPLOSIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49088_) {
        p_49088_.add(FACING, OPEN, EXPLOSIVE);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        if (level.getBlockEntity(pos) instanceof TCCreeperBarrelBlockEntity barrel) {
            return 0f;
        }
        return super.getExplosionResistance(state, level, pos, explosion);
    }

    @Override
    public void onRemovedfromExplosionList(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof TCCreeperBarrelBlockEntity barrel) {
            if (barrel.isFullofExplosives()) {
                int power = barrel.getExplosivesCount();
                level.destroyBlock(pos, false);
                TCExplosionUtils.createExplosion(level, null, pos, Math.max((float) Math.sqrt(power) * 2.5f, 50f));
                if (!level.isClientSide() && !level.players().isEmpty()) {
                    level.players().forEach(player -> {
                        if (player instanceof ServerPlayer serverPlayer && player.distanceToSqr(pos.getCenter()) < 100) {
                            serverPlayer.getAdvancements().award(Objects.requireNonNull(serverPlayer.server.getAdvancements().get(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeperbarrel"))), "impossible");
                        }
                    });
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_152102_, BlockState p_152103_) {
        return new TCCreeperBarrelBlockEntity(p_152102_, p_152103_);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState p_49069_, Level p_49070_, BlockPos p_49071_, Player p_49072_, BlockHitResult p_49074_) {
        if (p_49070_.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = p_49070_.getBlockEntity(p_49071_);
            if (blockentity instanceof TCCreeperBarrelBlockEntity && p_49070_ instanceof ServerLevel serverLevel) {
                p_49072_.openMenu((TCCreeperBarrelBlockEntity) blockentity);
                p_49072_.awardStat(Stats.OPEN_BARREL);
                PiglinAi.angerNearbyPiglins(serverLevel, p_49072_, true);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected void tick(BlockState p_220758_, ServerLevel p_220759_, BlockPos p_220760_, RandomSource p_220761_) {
        BlockEntity blockentity = p_220759_.getBlockEntity(p_220760_);
        if (blockentity instanceof TCCreeperBarrelBlockEntity) {
            ((TCCreeperBarrelBlockEntity) blockentity).recheckOpen();
        }
    }

    @Override
    public String getRegistryName() {
        return "creeperbarrel";
    }

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }

    @Override
    public String getEnUSName() {
        return "Creeper Barrel";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質樽";
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ResourceLocation top = provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, provider.name(this) + "_top"));
        ResourceLocation side = provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, provider.name(this) + "_side"));
        ResourceLocation bottom = provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, provider.name(this) + "_bottom"));
        ResourceLocation top_open = provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, provider.name(this) + "_top_open"));
        ResourceLocation top_exp = provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, provider.name(this) + "_top_explosive"));
        ResourceLocation side_exp = provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, provider.name(this) + "_side_explosive"));
        ResourceLocation bottom_exp = provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, provider.name(this) + "_bottom_explosive"));
        ResourceLocation top_open_exp = provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, provider.name(this) + "_top_open_explosive"));
        ModelFile model = provider.models().withExistingParent(provider.name(this), "barrel").texture("particle", top).texture("top", top).texture("side", side).texture("bottom", bottom);
        ModelFile model_open = provider.models().withExistingParent(provider.name(this) + "_open", "barrel_open").texture("particle", top).texture("top", top_open).texture("side", side).texture("bottom", bottom);
        ModelFile model_explosive = provider.models().withExistingParent(provider.name(this) + "_explosive", "barrel").texture("particle", top_exp).texture("top", top_exp).texture("side", side_exp).texture("bottom", bottom_exp);
        ModelFile model_open_explosive = provider.models().withExistingParent(provider.name(this) + "_open_explosive", "barrel_open").texture("particle", top_exp).texture("top", top_open_exp).texture("side", side_exp).texture("bottom", bottom_exp);
        provider.directionalBlock(this, state -> state.getValue(TCCreeperBarrelBlock.EXPLOSIVE) ? state.getValue(BarrelBlock.OPEN) ? new ConfiguredModel(model_open_explosive).model : new ConfiguredModel(model_explosive).model : state.getValue(BarrelBlock.OPEN) ? new ConfiguredModel(model_open).model : new ConfiguredModel(model).model);
        provider.itemModels().getBuilder(provider.key(this).getPath()).override().predicate(ResourceLocation.withDefaultNamespace("custom_model_data"), 0f).model(model).end().override().predicate(ResourceLocation.withDefaultNamespace("custom_model_data"), 898f).model(model_explosive).end();
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION);
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return new TCBlockItem(block) {
            @Override
            public void appendHoverText(ItemStack p_40572_, TooltipContext p_327780_, List<Component> components, TooltipFlag p_40575_) {
                super.appendHoverText(p_40572_, p_327780_, components, p_40575_);
                components.add(Component.translatable("item.takumicraft.creeperbarrel.desc"));
            }
        };
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS, TCBlockCore.CREEPER_BARREL, 1).define('#', TCBlockCore.CREEPER_PLANKS).define('H', TCBlockCore.CREEPER_PLANKS_HALF).pattern("#H#").pattern("# #").pattern("#H#").unlockedBy("has_creeperplanks", provider.hasItem(TCBlockCore.CREEPER_PLANKS)));
    }
}
