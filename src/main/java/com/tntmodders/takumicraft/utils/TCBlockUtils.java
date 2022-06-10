package com.tntmodders.takumicraft.utils;

import com.tntmodders.takumicraft.TakumiCraftCore;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class TCBlockUtils {
    public static Block getTCBlockFromRegistry(String path){
        return Registry.BLOCK.get(new ResourceLocation(TakumiCraftCore.MODID, path));
    }
}
