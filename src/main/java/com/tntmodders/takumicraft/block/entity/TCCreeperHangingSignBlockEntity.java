package com.tntmodders.takumicraft.block.entity;

import com.tntmodders.takumicraft.core.TCBlockEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TCCreeperHangingSignBlockEntity extends HangingSignBlockEntity {
    public TCCreeperHangingSignBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntityCore.HANGING_SIGN, pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return TCBlockEntityCore.HANGING_SIGN;
    }
}
