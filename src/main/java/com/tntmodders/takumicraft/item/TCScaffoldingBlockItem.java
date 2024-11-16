package com.tntmodders.takumicraft.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static com.tntmodders.takumicraft.block.TCScaffoldingBlock.STABILITY_MAX_DISTANCE;
import static com.tntmodders.takumicraft.block.TCScaffoldingBlock.getDistanceTC;

public class TCScaffoldingBlockItem extends TCBlockItem {
    public TCScaffoldingBlockItem(Block block) {
        super(block);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return context.getLevel().getBlockState(context.getClickedPos()).is(Blocks.SCAFFOLDING) ? InteractionResult.FAIL : super.useOn(context);
    }

    @Override
    @Nullable
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState blockstate = level.getBlockState(blockpos);
        Block block = this.getBlock();
        if (!blockstate.is(block)) {
            if (blockstate.is(Blocks.SCAFFOLDING)) {
                return null;
            }
            return getDistanceTC(level, blockpos) == STABILITY_MAX_DISTANCE ? null : context;
        } else {
            Direction direction;
            if (context.isSecondaryUseActive()) {
                direction = context.isInside() ? context.getClickedFace().getOpposite() : context.getClickedFace();
            } else {
                direction = context.getClickedFace() == Direction.UP ? context.getHorizontalDirection() : Direction.UP;
            }

            int i = 0;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = blockpos.mutable().move(direction);

            while (i < STABILITY_MAX_DISTANCE) {
                if (!level.isClientSide && !level.isInWorldBounds(blockpos$mutableblockpos)) {
                    Player player = context.getPlayer();
                    int j = level.getMaxY();
                    if (player instanceof ServerPlayer && blockpos$mutableblockpos.getY() >= j) {
                        ((ServerPlayer) player).sendSystemMessage(Component.translatable("build.tooHigh", j - 1).withStyle(ChatFormatting.RED), true);
                    }
                    break;
                }

                blockstate = level.getBlockState(blockpos$mutableblockpos);
                if (!blockstate.is(this.getBlock())) {
                    if (blockstate.canBeReplaced(context) && !blockstate.is(Blocks.SCAFFOLDING)) {
                        return BlockPlaceContext.at(context, blockpos$mutableblockpos, direction);
                    }
                    break;
                }

                blockpos$mutableblockpos.move(direction);
                if (direction.getAxis().isHorizontal()) {
                    ++i;
                }
            }

            return null;
        }
    }

    @Override
    protected boolean mustSurvive() {
        return false;
    }
}
