package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.entity.*;
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
    public static final BlockEntityType<TCCreeperBedBlockEntity> CREEPER_BED = BlockEntityType.Builder.of(TCCreeperBedBlockEntity::new, createBedList().toArray(new Block[0])).build(Util.fetchChoiceType(References.BLOCK_ENTITY, "creeperbed"));

    public static final BlockEntityType<TCMonsterBombBlockEntity> MONSTER_BOMB = BlockEntityType.Builder.of(TCMonsterBombBlockEntity::new, TCBlockCore.YUKARI_BOMB).build(Util.fetchChoiceType(References.BLOCK_ENTITY, "monsterbomb"));

    public static final BlockEntityType<TCAcidBlockEntity> ACID = BlockEntityType.Builder.of(TCAcidBlockEntity::new, TCBlockCore.ACID).build(Util.fetchChoiceType(References.BLOCK_ENTITY, "acidblock"));

    public static final BlockEntityType<TCCreeperChestBlockEntity> CHEST = BlockEntityType.Builder.of(TCCreeperChestBlockEntity::new, TCBlockCore.CREEPER_CHEST).build(Util.fetchChoiceType(References.BLOCK_ENTITY, "creeperchest"));
    public static final BlockEntityType<TCCreeperBarrelBlockEntity> BARREL = BlockEntityType.Builder.of(TCCreeperBarrelBlockEntity::new, TCBlockCore.CREEPER_BARREL).build(Util.fetchChoiceType(References.BLOCK_ENTITY, "creeperbarrel"));
    public static final BlockEntityType<TCCreeperCampFireBlockEntity> CAMPFIRE = BlockEntityType.Builder.of(TCCreeperCampFireBlockEntity::new, TCBlockCore.CREEPER_CAMPFIRE).build(Util.fetchChoiceType(References.BLOCK_ENTITY, "creepercampfire"));
    public static final BlockEntityType<TCCreeperSignBlockEntity> SIGN = BlockEntityType.Builder.of(TCCreeperSignBlockEntity::new, TCBlockCore.CREEPER_SIGN, TCBlockCore.CREEPER_SIGN_WALL).build(Util.fetchChoiceType(References.BLOCK_ENTITY, "creepersign"));
    public static final BlockEntityType<TCCreeperHangingSignBlockEntity> HANGING_SIGN = BlockEntityType.Builder.of(TCCreeperHangingSignBlockEntity::new, TCBlockCore.CREEPER_HANGING_SIGN, TCBlockCore.CREEPER_HANGING_SIGN_WALL).build(Util.fetchChoiceType(References.BLOCK_ENTITY, "creeperhangingsign"));
    public static final BlockEntityType<TCCreeperShulkerBoxBlockEntity> SHULKER = BlockEntityType.Builder.of(TCCreeperShulkerBoxBlockEntity::new, TCBlockCore.CREEPER_SHULKER).build(Util.fetchChoiceType(References.BLOCK_ENTITY, "creepershulkerbox"));
    public static final BlockEntityType<TCCreeperSuperBlockEntity> SUPER_BLOCK = BlockEntityType.Builder.of(TCCreeperSuperBlockEntity::new, TCBlockCore.SUPER_BLOCK).build(Util.fetchChoiceType(References.BLOCK_ENTITY, "takumiblock"));
    public static final BlockEntityType<TCTakenokoBlockEntity> TAKENOKO = BlockEntityType.Builder.of(TCTakenokoBlockEntity::new, TCBlockCore.TAKENOKO).build(Util.fetchChoiceType(References.BLOCK_ENTITY, "takenoko"));

    public static void register(final RegisterEvent event) {
        TCLoggingUtils.startRegistry("BlockEntity");
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeperbed"), () -> CREEPER_BED);
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), ResourceLocation.tryBuild(TakumiCraftCore.MODID, "acidblock"), () -> ACID);
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), ResourceLocation.tryBuild(TakumiCraftCore.MODID, "monsterbomb"), () -> MONSTER_BOMB);
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeperchest"), () -> CHEST);
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeperbarrel"), () -> BARREL);
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creepercampfire"), () -> CAMPFIRE);
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creepersign"), () -> SIGN);
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeperhangingsign"), () -> HANGING_SIGN);
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creepershulkerbox"), () -> SHULKER);
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), ResourceLocation.tryBuild(TakumiCraftCore.MODID, "takumiblock"), () -> SUPER_BLOCK);
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), ResourceLocation.tryBuild(TakumiCraftCore.MODID, "takenoko"), () -> TAKENOKO);
        TCLoggingUtils.completeRegistry("BlockEntity");
    }

    public static List<Block> createBedList() {
        List<Block> list = new ArrayList();
        list.add(TCBlockCore.SUPER_CREEPER_BED);
        list.addAll(TCBlockCore.CREEPER_BED_MAP.values());
        return list;
    }
}
