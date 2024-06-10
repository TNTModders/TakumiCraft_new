package com.tntmodders.takumicraft.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class TCCreeperSignBlockItem extends AbstractTCStandingBlockItem {
    public TCCreeperSignBlockItem(Block block, Block wallBlock, Direction direction) {
        super(block, wallBlock, direction);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos p_43130_, Level p_43131_, @Nullable Player p_43132_, ItemStack p_43133_, BlockState p_43134_) {
        boolean flag = super.updateCustomBlockEntityTag(p_43130_, p_43131_, p_43132_, p_43133_, p_43134_);
        if (!p_43131_.isClientSide && !flag && p_43132_ != null && p_43131_.getBlockEntity(p_43130_) instanceof SignBlockEntity signblockentity && p_43131_.getBlockState(p_43130_).getBlock() instanceof SignBlock signblock) {
            signblock.openTextEdit(p_43132_, signblockentity, true);
        }

        return flag;
    }
}
