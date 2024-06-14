package com.tntmodders.takumicraft.block;

import com.mojang.serialization.MapCodec;
import com.tntmodders.takumicraft.block.entity.TCCreeperSuperBlockEntity;
import com.tntmodders.takumicraft.client.renderer.block.TCBEWLRenderer;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TCCreeperSuperBlock extends BaseEntityBlock implements EntityBlock, ITCBlocks {

    public static final MapCodec<TCCreeperSuperBlock> CODEC = simpleCodec(p_309280_ -> new TCCreeperSuperBlock());

    public TCCreeperSuperBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 1000000.0F).noLootTable().noOcclusion().isViewBlocking(TCBlockCore::never).isValidSpawn(TCBlockCore::never).lightLevel(p_50886_ -> 15).pushReaction(PushReaction.BLOCK).dynamicShape());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.getBlockEntity(pos) instanceof TCCreeperSuperBlockEntity superBlock) {
            if (stack.getItem() instanceof BlockItem blockItem) {
                if (blockItem.getBlock() == this) {

                } else {
                    this.setBlocktoSuperBlock(superBlock, player, hand, result, blockItem.getBlock());
                }
            } else if (player.getItemInHand(hand).isEmpty() && player.isShiftKeyDown()) {
                this.setBlocktoSuperBlock(superBlock, player, hand, result, Blocks.AIR);
            }

        }
        return ItemInteractionResult.CONSUME_PARTIAL;
    }

    private void setBlocktoSuperBlock(TCCreeperSuperBlockEntity superBlock, Player player, InteractionHand hand, BlockHitResult result, Block block) {
        if (player instanceof ServerPlayer serverPlayer) {
            BlockState insideState = block.getStateForPlacement(new BlockPlaceContext(new UseOnContext(player, hand, result)));
            superBlock.setState(insideState);
            Packet<?> pkt = superBlock.getUpdatePacket();
            if (pkt != null) {
                serverPlayer.connection.send(pkt);
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
        return null;
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return new TCBlockItem(block) {
            @Override
            public void initializeClient(Consumer<IClientItemExtensions> consumer) {
                consumer.accept(new IClientItemExtensions() {
                    @Override
                    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                        return new TCBEWLRenderer();
                    }
                });
            }
        };
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
}
