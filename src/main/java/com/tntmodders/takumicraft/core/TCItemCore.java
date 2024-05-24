package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.item.*;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TCItemCore {
    public static final NonNullList<Item> ITEMS = NonNullList.create();
    public static final HashMap<AbstractTCCreeper.TCCreeperContext, Item> EGGITEMS = new HashMap<>();
    public static final HashMap<Block, TCBlockItem> BLOCKITEMS = new HashMap<>();

    public static final Item CREEPER_ROD = new TCTesterItem();
    public static final Item TAKUMIBOOK = new TCTakumiBookItem();
    public static final Item BOLTSTONE = new TCBoltstoneItem();
    public static final Item LIGHTSABER = new TCSaberItem();
    public static final Item CREEPER_SWORD = new TCCreeperSwordItem();
    public static final Item ELEMENTCORE_NORMAL = new TCElementCoreItem(AbstractTCCreeper.TCCreeperContext.EnumTakumiElement.NORMAL);
    public static final Item ELEMENTCORE_FIRE = new TCElementCoreItem(AbstractTCCreeper.TCCreeperContext.EnumTakumiElement.FIRE);
    public static final Item ELEMENTCORE_WATER = new TCElementCoreItem(AbstractTCCreeper.TCCreeperContext.EnumTakumiElement.WATER);
    public static final Item ELEMENTCORE_GRASS = new TCElementCoreItem(AbstractTCCreeper.TCCreeperContext.EnumTakumiElement.GRASS);
    public static final Item ELEMENTCORE_GROUND = new TCElementCoreItem(AbstractTCCreeper.TCCreeperContext.EnumTakumiElement.GROUND);
    public static final Item ELEMENTCORE_WIND = new TCElementCoreItem(AbstractTCCreeper.TCCreeperContext.EnumTakumiElement.WIND);
    public static final Item CREEPER_SHIELD = new TCCreeperShieldItem();
    public static final Item CREEPER_BOW = new TCCreeperBowItem();
    public static final Item CREEPER_ARROW = new TCCreeperArrowItem();
    public static final Item TIPPED_CREEPER_ARROW = new TCTippedCreeperArrowItem();
    public static final Item MINESWEEPER_PICKAXE = new TCMinesweeperPickaxeItem();
    public static final Item MINESWEEPER_AXE = new TCMinesweeperAxeItem();
    public static final Item MINESWEEPER_SHOVEL = new TCMinesweeperShovelItem();
    public static final Item OFALEN = new TCOfalenItem();
    public static final Item SPMEAT_BEEF = new TCTakumiSpecialMeatItem(Items.COOKED_BEEF, Items.BEEF, false);
    public static final Item SPMEAT_PORK = new TCTakumiSpecialMeatItem(Items.COOKED_PORKCHOP, Items.PORKCHOP, false);
    public static final Item SPMEAT_MUTTON = new TCTakumiSpecialMeatItem(Items.COOKED_MUTTON, Items.MUTTON, false);
    public static final Item SPMEAT_CHICKEN = new TCTakumiSpecialMeatItem(Items.COOKED_CHICKEN, Items.CHICKEN, false);
    public static final Item SPMEAT_RABBIT = new TCTakumiSpecialMeatItem(Items.COOKED_RABBIT, Items.RABBIT, false);
    public static final Item SPMEAT_COD = new TCTakumiSpecialMeatItem(Items.COOKED_COD, Items.COD, true);
    public static final Item SPMEAT_SALMON = new TCTakumiSpecialMeatItem(Items.COOKED_SALMON, Items.SALMON, true);
    public static final Item SPMEAT_ZOMBIE = new TCTakumiSpecialMeatItem(Items.ROTTEN_FLESH, Items.ROTTEN_FLESH, false);
    public static final Item CREEPER_MACE = new TCCreeperMaceItem();
    public static final Item KING_CORE = new TCKingCoreItem();

    public static final TagKey<Item> GUNORES = TagKey.create(Registries.ITEM, new ResourceLocation(TakumiCraftCore.MODID,
            "gunores"));
    public static final TagKey<Item> EXPLOSIVE_SHIELDS = TagKey.create(Registries.ITEM, new ResourceLocation(TakumiCraftCore.MODID,
            "can_shield_explosion"));
    public static final TagKey<Item> CREEPER_ARROWS = TagKey.create(Registries.ITEM, new ResourceLocation(TakumiCraftCore.MODID, "creeper_arrow"));
    public static final TagKey<Item> MINESWEEPER_TOOLS = TagKey.create(Registries.ITEM, new ResourceLocation(TakumiCraftCore.MODID, "minesweeper_tool"));
    public static final TagKey<Item> CREEPER_BED = TagKey.create(Registries.ITEM, new ResourceLocation(TakumiCraftCore.MODID, "creeperbed"));
    public static final TagKey<Item> SPECIAL_MEATS = TagKey.create(Registries.ITEM, new ResourceLocation(TakumiCraftCore.MODID, "takumispecialmeat"));
    public static final TagKey<Item> ELEMENT_CORE = TagKey.create(Registries.ITEM, new ResourceLocation(TakumiCraftCore.MODID, "elementcore"));

    public static final TagKey<Item> ANTI_POWERED = TagKey.create(Registries.ITEM, new ResourceLocation(TakumiCraftCore.MODID, "anti_powered"));
    public static final TagKey<Item> BLAST_POWERED = TagKey.create(Registries.ITEM, new ResourceLocation(TakumiCraftCore.MODID, "blast_powered"));

    public static void register(final RegisterEvent event) {
        TCLoggingUtils.startRegistry("Item");
        List<Field> fieldList = Arrays.asList(TCItemCore.class.getDeclaredFields());
        fieldList.forEach(field -> {
            try {
                Object obj = field.get(null);
                if (obj instanceof ITCItems && obj instanceof Item item) {
                    event.register(ForgeRegistries.ITEMS.getRegistryKey(), itemRegisterHelper -> itemRegisterHelper.register(new ResourceLocation(TakumiCraftCore.MODID, ((ITCItems) item).getRegistryName()), item));
                    ITEMS.add((Item) obj);
                    TCLoggingUtils.entryRegistry("Item", ((ITCItems) item).getRegistryName());
                } else if (obj instanceof Map map) {
                    map.values().forEach(value -> {
                        if (value instanceof ITCItems && value instanceof Item item) {
                            event.register(ForgeRegistries.ITEMS.getRegistryKey(), itemRegisterHelper -> itemRegisterHelper.register(new ResourceLocation(TakumiCraftCore.MODID, ((ITCItems) item).getRegistryName()), item));
                            ITEMS.add((Item) obj);
                            TCLoggingUtils.entryRegistry("Item", ((ITCItems) item).getRegistryName());
                        }
                    });
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        TCBlockCore.BLOCKS.forEach(block -> {
            TCBlockItem blockItem = block instanceof ITCBlocks ? ((ITCBlocks) block).getCustomBlockItem(block) : new TCBlockItem(block);
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
                    EGGITEMS.put(context, eggItem);
                    TCLoggingUtils.entryRegistry("SpawnEggItem", context.getRegistryName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        TCLoggingUtils.completeRegistry("Item");
    }
}
