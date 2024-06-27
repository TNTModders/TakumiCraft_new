package com.tntmodders.takumicraft.utils;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCBlockUtils {
    public static Block getTCBlockFromRegistry(String path) {
        return ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryBuild(TakumiCraftCore.MODID, path));
    }

    //Anti-Explosion-Safe setBlock
    public static boolean TCSetBlock(Level level, BlockPos pos, BlockState state) {
        if (TCAntiExplosiveBlock(level, pos, level.getBlockState(pos))) {
            return false;
        }
        return level.setBlock(pos, state, 3);
    }

    public static boolean TCAntiExplosiveBlock(Level level, BlockPos pos, BlockState state) {
        return state.getExplosionResistance(level, pos, null) >= Blocks.OBSIDIAN.getExplosionResistance(state, level, pos, null) || !state.is(Blocks.AIR) && state.is(TCBlockCore.ANTI_EXPLOSION) || state.getBlock().defaultDestroyTime() < 0f;
    }

    public static String getNamewithSuffix(String name, String suffix) {
        String bracket1 = "[";
        String bracket2 = "]";
        if (name.contains(bracket1) && name.contains(bracket2)) {
            String regex = "\\[.+?\\]";
            List<String> output = new ArrayList<>();
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(name);
            String editedName = name;
            while (matcher.find()) {
                String match = matcher.group();
                output.add(match);
                editedName = editedName.replace(match, " ");
            }
            if (!output.isEmpty()) {
                StringBuilder ret = new StringBuilder(editedName + suffix);
                output.forEach(ret::append);
                String out = ret.toString();
                String s = suffix.contains(" ") ? "  " : " ";
                out = out.replace(s, "");
                if (suffix.contains(" ")) {
                    out = out.replace("[", " [");
                }
                return out;
            }
        }
        return name + suffix;
    }
}
