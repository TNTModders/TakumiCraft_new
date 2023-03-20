package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.item.*;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TCItemCore {
    public static final NonNullList<Item> ITEMS = NonNullList.create();
    public static final HashMap<Block, Item> BLOCKITEMS = new HashMap<>();

    public static final Item CREEPER_ROD = new TCCreeperRodItem();
    public static final Item TAKUMIBOOK = new TCTakumiBookItem();
    public static final Item BOLTSTONE = new TCBoltstoneItem();

    public static final TagKey<Item> GUNORES = TagKey.create(Registries.ITEM, new ResourceLocation(TakumiCraftCore.MODID,
            "gunores"));

    public static void register(final RegisterEvent event) {
        TCLoggingUtils.startRegistry("Item");
        List<Field> fieldList = Arrays.asList(TCItemCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof ITCItems && obj instanceof Item item) {
                    event.register(ForgeRegistries.ITEMS.getRegistryKey(), itemRegisterHelper -> itemRegisterHelper.register(new ResourceLocation(TakumiCraftCore.MODID, ((ITCItems) item).getRegistryName()), item));
                    ITEMS.add(((Item) obj));
                    TCLoggingUtils.entryRegistry("Item", ((ITCItems) item).getRegistryName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        TCBlockCore.BLOCKS.forEach(block -> {
            TCBlockItem blockItem = new TCBlockItem(block);
            event.register(ForgeRegistries.ITEMS.getRegistryKey(), itemRegisterHelper -> itemRegisterHelper.register(new ResourceLocation(TakumiCraftCore.MODID, blockItem.getRegistryName()), blockItem));
            BLOCKITEMS.put(block, blockItem);
            TCLoggingUtils.entryRegistry("BlockItem", blockItem.getRegistryName());
        });

        List<Field> entityFieldList = Arrays.asList(TCEntityCore.class.getDeclaredFields());
        entityFieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof AbstractTCCreeper.TCCreeperContext<?> context) {
                    TCSpawnEggItem eggItem = new TCSpawnEggItem(() -> (EntityType<? extends Mob>) context.entityType(), context);
                    event.register(ForgeRegistries.ITEMS.getRegistryKey(), itemRegisterHelper -> itemRegisterHelper.register(new ResourceLocation(TakumiCraftCore.MODID, eggItem.getRegistryName()), eggItem));
                    ITEMS.add(eggItem);
                    TCLoggingUtils.entryRegistry("SpawnEggItem", context.getRegistryName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        TCLoggingUtils.completeRegistry("Item");
    }
}
