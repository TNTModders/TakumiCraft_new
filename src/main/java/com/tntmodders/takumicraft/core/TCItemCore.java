package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.item.TCCreeperRodItem;
import com.tntmodders.takumicraft.item.TCSpawnEggItem;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TCItemCore {
    public static final NonNullList<Item> ITEMS = NonNullList.create();
    public static final HashMap<Block, Item> BLOCKITEMS = new HashMap<>();

    public static final Item CREEPER_ROD = new TCCreeperRodItem();

    public static void register(final RegistryEvent.Register<Item> event) {
        TCLoggingUtils.startRegistry("Item");
        List<Field> fieldList = Arrays.asList(TCItemCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof Item) {
                    event.getRegistry().register(((Item) obj));
                    ITEMS.add(((Item) obj));
                    TCLoggingUtils.entryRegistry("Item", ((Item) obj).getRegistryName().getPath());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        /*List<Field> blockFieldList = Arrays.asList(TCBlockCore.class.getDeclaredFields());
        blockFieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof Block) {
                    TCBlockItem blockItem = new TCBlockItem(((Block) obj));
                    event.getRegistry().register(blockItem);
                    BLOCKITEMS.put(((Block) obj), blockItem);
                    TCLoggingUtils.entryRegistry("BlockItem", ((Block) obj).getRegistryName().getPath());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });*/
        TCBlockCore.BLOCKS.forEach(block -> {
            TCBlockItem blockItem = new TCBlockItem(block);
            event.getRegistry().register(blockItem);
            BLOCKITEMS.put(block, blockItem);
            TCLoggingUtils.entryRegistry("BlockItem", block.getRegistryName().getPath());
        });

        List<Field> entityFieldList = Arrays.asList(TCEntityCore.class.getDeclaredFields());
        entityFieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof AbstractTCCreeper.TCCreeperContext<?> context) {
                    TCSpawnEggItem eggItem = new TCSpawnEggItem(() -> (EntityType<? extends Mob>) context.entityType(), context);
                    event.getRegistry().register(eggItem);
                    ITEMS.add(eggItem);
                    TCLoggingUtils.entryRegistry("SpawnEggItem", context.entityType().getRegistryName().getPath());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        TCLoggingUtils.completeRegistry("Item");
    }
}
