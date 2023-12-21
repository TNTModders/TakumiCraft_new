package com.tntmodders.takumicraft.block.entity;

import com.tntmodders.takumicraft.core.TCBlockEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TCMonsterBombBlockEntity extends BlockEntity {
    public TCMonsterBombBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntityCore.MONSTER_BOMB, pos, state);
    }
}
