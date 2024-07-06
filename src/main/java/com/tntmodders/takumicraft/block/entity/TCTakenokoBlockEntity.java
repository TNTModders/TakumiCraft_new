package com.tntmodders.takumicraft.block.entity;

import com.tntmodders.takumicraft.block.TCTakenokoBlock;
import com.tntmodders.takumicraft.core.TCBlockEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TCTakenokoBlockEntity extends BlockEntity {
    int i = 0;

    public TCTakenokoBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntityCore.TAKENOKO, pos, state);
    }

    public static <T extends BlockEntity> void serverTick(Level level, BlockPos pos, BlockState state, T blockentity) {
        if (blockentity instanceof TCTakenokoBlockEntity takenoko && state.getBlock() instanceof TCTakenokoBlock block) {
            takenoko.i++;
            if (takenoko.i > 20) {
                block.growBamboo(level, pos);
            }
        }
    }
}
