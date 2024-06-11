package com.tntmodders.takumicraft.block.entity;

import com.tntmodders.takumicraft.core.TCBlockEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TCCreeperShulkerBoxBlockEntity extends ShulkerBoxBlockEntity {
    public TCCreeperShulkerBoxBlockEntity(@Nullable DyeColor p_155666_, BlockPos p_155667_, BlockState p_155668_) {
        super(p_155666_, p_155667_, p_155668_);
    }

    public TCCreeperShulkerBoxBlockEntity(BlockPos p_155670_, BlockState p_155671_) {
        super(p_155670_, p_155671_);
    }

    @Override
    public BlockEntityType<?> getType() {
        return TCBlockEntityCore.SHULKER;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("takumicraft.container.creepershulkerBox");
    }
}
