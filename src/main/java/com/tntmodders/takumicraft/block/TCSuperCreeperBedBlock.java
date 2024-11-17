package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.block.entity.TCCreeperBedBlockEntity;
import com.tntmodders.takumicraft.client.renderer.block.TCBEWLRenderer;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Consumer;

public class TCSuperCreeperBedBlock extends TCCreeperBedBlock {
    public TCSuperCreeperBedBlock() {
        super("super_creeperbed", DyeColor.GREEN);
    }


    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.getBlockEntity(pos) instanceof TCCreeperBedBlockEntity bed) {
            if (player.getItemInHand(hand).isEmpty() && player.isShiftKeyDown()) {
                if (bed.getBlock() != null && bed.getBlock() != Blocks.AIR) {
                    this.setBlockToBed(bed, player, state, pos, Blocks.AIR);
                    return InteractionResult.CONSUME;
                }
            }
            if (player.getItemInHand(hand).getItem() instanceof BlockItem blockItem) {
                if (Block.isShapeFullBlock(blockItem.getBlock().defaultBlockState().getOcclusionShape())) {
                    this.setBlockToBed(bed, player, state, pos, blockItem.getBlock());
                    return InteractionResult.CONSUME;
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, result);
    }

    private void setBlockToBed(TCCreeperBedBlockEntity bed, Player player, BlockState state, BlockPos pos, Block block) {
        bed.setTexture(ForgeRegistries.BLOCKS.getKey(block));
        if (player instanceof ServerPlayer serverPlayer) {
            Packet<?> pkt = bed.getUpdatePacket();
            if (pkt != null)
                serverPlayer.connection.send(pkt);
        }
        if (bed.getLevel().getBlockEntity(pos.relative(getConnectedDirection(state), 1)) instanceof TCCreeperBedBlockEntity bed2) {
            bed2.setTexture(ForgeRegistries.BLOCKS.getKey(block));
            if (player instanceof ServerPlayer serverPlayer) {
                Packet<?> pkt = bed2.getUpdatePacket();
                if (pkt != null)
                    serverPlayer.connection.send(pkt);
            }
        }
        bed.setChanged();
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.DECORATIONS,
                        TCBlockCore.SUPER_CREEPER_BED)
                .define('#', TCItemCore.CREEPER_BED)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_creeperbed", provider.hasItemTag(TCItemCore.CREEPER_BED))
                .group("creeperbed"));
    }

    @Override
    public String getRegistryName() {
        return "super_creeperbed";
    }

    @Override
    public String getEnUSName() {
        return "Super Creeper Bed";
    }

    @Override
    public String getJaJPName() {
        return "匠式超硬質寝具";
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_152175_, BlockState p_152176_) {
        return new TCCreeperBedBlockEntity(p_152175_, p_152176_, DyeColor.GREEN, true);
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return new TCBlockItem(block, new Item.Properties().rarity(Rarity.EPIC)) {
            @Override
            public void initializeClient(Consumer<IClientItemExtensions> consumer) {
                consumer.accept(new IClientItemExtensions() {
                    @Override
                    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                        return new TCBEWLRenderer();
                    }
                });
            }

            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
                super.appendHoverText(stack, context, components, tooltipFlag);
                components.add(Component.translatable("item.takumicraft.super_creeperbed.desc"));
            }

            @Override
            public int getDefaultMaxStackSize() {
                return 1;
            }
        };
    }
}
