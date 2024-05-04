package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.entity.TCAcidBlockEntity;
import com.tntmodders.takumicraft.block.entity.TCCreeperBedBlockEntity;
import com.tntmodders.takumicraft.block.entity.TCMonsterBombBlockEntity;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;

public class TCBlockEntityCore {
    public static void register(final RegisterEvent event) {
        TCLoggingUtils.startRegistry("BlockEntity");
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), new ResourceLocation(TakumiCraftCore.MODID, "creeperbed"), () -> CREEPER_BED);
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), new ResourceLocation(TakumiCraftCore.MODID, "acidblock"), () -> ACID);
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), new ResourceLocation(TakumiCraftCore.MODID, "monsterbomb"), () -> MONSTER_BOMB);
        TCLoggingUtils.completeRegistry("BlockEntity");
    }

    public static List<Block> createBedList() {
        List<Block> list = new ArrayList();
        list.add(TCBlockCore.SUPER_CREEPER_BED);
        list.addAll(TCBlockCore.CREEPER_BED_MAP.values());
        return list;
    }

    public static final BlockEntityType<TCCreeperBedBlockEntity> CREEPER_BED = BlockEntityType.Builder.of(TCCreeperBedBlockEntity::new, createBedList().toArray(new Block[0]))
            .build(Util.fetchChoiceType(References.BLOCK_ENTITY, "creeperbed"));



    public static final BlockEntityType<TCMonsterBombBlockEntity> MONSTER_BOMB = BlockEntityType.Builder.of(TCMonsterBombBlockEntity::new, TCBlockCore.YUKARI_BOMB)
            .build(Util.fetchChoiceType(References.BLOCK_ENTITY, "monsterbomb"));

    public static final BlockEntityType<TCAcidBlockEntity> ACID = BlockEntityType.Builder.of(TCAcidBlockEntity::new, TCBlockCore.ACID)
            .build(Util.fetchChoiceType(References.BLOCK_ENTITY, "acidblock"));


}
