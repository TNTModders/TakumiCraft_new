package com.tntmodders.takumicraft.block.entity;

import com.tntmodders.takumicraft.core.TCBlockEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TCCreeperSignBlockEntity extends SignBlockEntity {
    public TCCreeperSignBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntityCore.SIGN, pos, state);
    }

    public TCCreeperSignBlockEntity(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
