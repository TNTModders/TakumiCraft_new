package com.tntmodders.takumicraft.block.entity;

import com.tntmodders.takumicraft.core.TCBlockEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TCCreeperBedBlockEntity extends BlockEntity {
    private DyeColor color;

    public TCCreeperBedBlockEntity(BlockPos p_155115_, BlockState p_155116_) {
        super(TCBlockEntityCore.CREEPER_BED, p_155115_, p_155116_);
        this.color = ((BedBlock) p_155116_.getBlock()).getColor();
    }

    public TCCreeperBedBlockEntity(BlockPos p_155118_, BlockState p_155119_, DyeColor p_155120_) {
        super(TCBlockEntityCore.CREEPER_BED, p_155118_, p_155119_);
        this.color = p_155120_;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public DyeColor getColor() {
        return this.color;
    }

    public void setColor(DyeColor p_58730_) {
        this.color = p_58730_;
    }
}
