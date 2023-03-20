package com.tntmodders.takumicraft.utils;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class TCBlockUtils {
    public static Block getTCBlockFromRegistry(String path) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(TakumiCraftCore.MODID, path));
    }

    //Anti-Explosion-Safe setBlock
    public static boolean TCSetBlock(Level level, BlockPos pos, BlockState state) {
        if (level.getBlockState(pos).is(TCBlockCore.ANTI_EXPLOSION)) {
            return false;
        }
        return level.setBlock(pos, state, 3);
    }
}
