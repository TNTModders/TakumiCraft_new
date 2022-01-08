package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.block.CreeperTCBombBlock;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class TCBlockCore {
    public static final NonNullList<Block> BLOCKS = NonNullList.create();
    public static final Block CREEPER_BOMB = new CreeperTCBombBlock();

    public static void register(final RegistryEvent.Register<Block> event) {
        TCLoggingUtils.startRegistry("Block");
        List<Field> fieldList = Arrays.asList(TCBlockCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof Block) {
                    event.getRegistry().register(((Block) obj));
                    BLOCKS.add(((Block) obj));
                    TCLoggingUtils.entryRegistry("Block", ((Block) obj).getRegistryName().getPath());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        TCLoggingUtils.completeRegistry("Block");
    }
}
