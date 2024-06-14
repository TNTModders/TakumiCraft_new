package com.tntmodders.takumicraft.block.entity;

import com.tntmodders.takumicraft.core.TCBlockEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class TCCreeperSuperBlockEntity extends BlockEntity {
    private BlockState state;

    public TCCreeperSuperBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntityCore.SUPER_BLOCK, pos, state);
    }

    @Nullable
    public BlockState getState() {
        if (this.state == null || this.state.getBlock() == Blocks.AIR) {
            return null;
        }
        return state;
    }

    public void setState(BlockState state) {
        this.state = state;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        HolderGetter<Block> holdergetter = this.level != null ? this.level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup();
        this.state = NbtUtils.readBlockState(holdergetter, tag.getCompound("state"));

    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (this.state != null) {
            tag.put("state", NbtUtils.writeBlockState(this.state));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider compoundTag) {
        CompoundTag tag = this.saveWithoutMetadata(compoundTag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
