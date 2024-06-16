package com.tntmodders.takumicraft.block;

import com.mojang.serialization.MapCodec;
import com.tntmodders.takumicraft.block.entity.TCCreeperSuperBlockEntity;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCConfigCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.item.TCCreeperSuperBlockItem;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class TCCreeperSuperBlock extends BaseEntityBlock implements EntityBlock, ITCBlocks, ITCRecipe {

    public static final MapCodec<TCCreeperSuperBlock> CODEC = simpleCodec(p_309280_ -> new TCCreeperSuperBlock());

    public TCCreeperSuperBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 1000000.0F).noOcclusion().isViewBlocking(TCBlockCore::never).isValidSpawn(TCBlockCore::never).isRedstoneConductor(TCBlockCore::never).lightLevel(p_50886_ -> 15).pushReaction(PushReaction.BLOCK).dynamicShape());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity livingEntity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, livingEntity, stack);
        if (livingEntity instanceof ServerPlayer player && TCConfigCore.TCServerConfig.SERVER.useTakumiBlockLock.get() && level.getBlockEntity(pos) instanceof TCCreeperSuperBlockEntity superBlock) {
            superBlock.setLock(player);
            superBlock.setChanged();
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TCCreeperSuperBlockEntity superBlock) {
            if (superBlock.canChange(player)) {
                if (stack.getItem() instanceof BlockItem blockItem) {
                    if (blockItem.getBlock() == this) {
                        superBlock.setHideOverlay(!superBlock.isHideOverlay());
                        if (player instanceof ServerPlayer serverPlayer) {
                            Packet<?> pkt = superBlock.getUpdatePacket();
                            if (pkt != null) {
                                serverPlayer.connection.send(pkt);
                            }
                        }
                        superBlock.setChanged();
                    } else {
                        this.setBlocktoSuperBlock(state, superBlock, player, hand, result, blockItem.getBlock());
                    }
                } else if (player.getItemInHand(hand).isEmpty() && player.isShiftKeyDown()) {
                    this.setBlocktoSuperBlock(state, superBlock, player, hand, result, Blocks.AIR);
                }
            }
        }
        return ItemInteractionResult.CONSUME_PARTIAL;
    }

    private void setBlocktoSuperBlock(BlockState state, TCCreeperSuperBlockEntity superBlock, Player player, InteractionHand hand, BlockHitResult result, Block block) {
        if (player instanceof ServerPlayer serverPlayer) {
            BlockState insideState = block.getStateForPlacement(new TCSuperBlockPlaceContext(superBlock.getLevel(), player, hand, player.getItemInHand(hand), result));
            superBlock.setState(insideState);
            Packet<?> pkt = superBlock.getUpdatePacket();
            if (pkt != null) {
                serverPlayer.connection.send(pkt);
            }
        }
        superBlock.setChanged();
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        super.attack(state, level, pos, player);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer && serverPlayer.gameMode.getGameModeForPlayer() != GameType.ADVENTURE) {
            if (level.getBlockEntity(pos) instanceof TCCreeperSuperBlockEntity superBlock && superBlock.canChange(serverPlayer)) {
                level.destroyBlock(pos, false, player);
                ItemStack stack = new ItemStack(this);
                CompoundTag tag = new CompoundTag();
                tag.putBoolean("replace", false);
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                ItemEntity entity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                level.addFreshEntity(entity);
            }
        }
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState p_60572_, BlockGetter p_60573_, BlockPos p_60574_, CollisionContext p_60575_) {
        if (p_60573_.getBlockEntity(p_60574_) instanceof TCCreeperSuperBlockEntity superBlock) {
            if (superBlock.getState() != null) {
                return superBlock.getState().getCollisionShape(p_60573_, p_60574_, p_60575_);
            }
        }
        return super.getCollisionShape(p_60572_, p_60573_, p_60574_, p_60575_);
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState p_60581_, BlockGetter p_60582_, BlockPos p_60583_) {
        return Shapes.block();
    }

    @Override
    protected VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        if (p_60556_.getBlockEntity(p_60557_) instanceof TCCreeperSuperBlockEntity superBlock) {
            if (superBlock.getState() != null) {
                VoxelShape shape = superBlock.getState().getShape(p_60556_, p_60557_, p_60558_);
                return shape.isEmpty() ? super.getShape(p_60555_, p_60556_, p_60557_, p_60558_) : shape;
            }
        }
        return super.getShape(p_60555_, p_60556_, p_60557_, p_60558_);
    }

    @Override
    protected float getShadeBrightness(BlockState p_312407_, BlockGetter p_310193_, BlockPos p_311965_) {
        return 1.0F;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState p_312717_, BlockGetter p_312877_, BlockPos p_312899_) {
        return true;
    }


    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public String getRegistryName() {
        return "takumiblock";
    }

    @Override
    public String getEnUSName() {
        return "Creeper Super Block";
    }

    @Override
    public String getJaJPName() {
        return "匠式超硬質ブロック";
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCCreeperSuperBlockEntity(pos, state);
    }

    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return () -> new TCBlockLoot(block, true);
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return new TCCreeperSuperBlockItem(block);
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ModelFile model = provider.cubeAll(this);
        provider.simpleBlock(this, model);
        provider.itemModels().withExistingParent(provider.name(this), "minecraft:item/shulker_box").texture("particle", provider.blockTexture(this));
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION, BlockTags.DRAGON_IMMUNE, BlockTags.WITHER_IMMUNE);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS,
                        TCBlockCore.SUPER_BLOCK, 8)
                .define('#', TCItemCore.KING_CORE)
                .define('B', TCBlockCore.CREEPER_BOMB)
                .pattern("BBB")
                .pattern("B#B")
                .pattern("BBB")
                .unlockedBy("has_kingcore", TCRecipeProvider.hasItem(TCItemCore.KING_CORE)));
    }

    static class TCSuperBlockPlaceContext extends BlockPlaceContext {
        public TCSuperBlockPlaceContext(Level p_43638_, @Nullable Player p_43639_, InteractionHand p_43640_, ItemStack p_43641_, BlockHitResult p_43642_) {
            super(p_43638_, p_43639_, p_43640_, p_43641_, p_43642_);
            this.replaceClicked = true;
        }
    }
}
