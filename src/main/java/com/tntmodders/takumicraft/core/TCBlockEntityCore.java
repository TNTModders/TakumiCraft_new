package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.entity.*;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TCBlockEntityCore {
    public static final BlockEntityType<TCCreeperShulkerBoxBlockEntity> SHULKER = new TCBlockEntityType<>(TCCreeperShulkerBoxBlockEntity::new, TCBlockCore.CREEPER_SHULKER);

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
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeperprotector"), () -> PROTECTOR);
        TCLoggingUtils.completeRegistry("BlockEntity");
    }

    public static final BlockEntityType<TCCreeperBedBlockEntity> CREEPER_BED = new TCBlockEntityType<>(TCCreeperBedBlockEntity::new, createBedList());

    public static Set<Block> createBedList() {
        List<Block> list = new ArrayList();
        list.add(TCBlockCore.SUPER_CREEPER_BED);
        list.addAll(TCBlockCore.CREEPER_BED_MAP.values());
        return Set.copyOf(list);
    }

    public static class TCBlockEntityType<T extends BlockEntity> extends BlockEntityType<T> {

        public TCBlockEntityType(BlockEntitySupplier<? extends T> supplier, Set<Block> blockSet) {
            super(supplier, blockSet);
        }

        public TCBlockEntityType(BlockEntitySupplier<? extends T> supplier, Block... blocks) {
            this(supplier, Set.of(blocks));
        }
    }

    public static final BlockEntityType<TCMonsterBombBlockEntity> MONSTER_BOMB = new TCBlockEntityType<>(TCMonsterBombBlockEntity::new, TCBlockCore.YUKARI_BOMB);


    public static final BlockEntityType<TCAcidBlockEntity> ACID = new TCBlockEntityType<>(TCAcidBlockEntity::new, TCBlockCore.ACID);



    public static final BlockEntityType<TCCreeperChestBlockEntity> CHEST = new TCBlockEntityType<>(TCCreeperChestBlockEntity::new, TCBlockCore.CREEPER_CHEST);
    public static final BlockEntityType<TCCreeperBarrelBlockEntity> BARREL = new TCBlockEntityType<>(TCCreeperBarrelBlockEntity::new, TCBlockCore.CREEPER_BARREL);
    public static final BlockEntityType<TCCreeperCampFireBlockEntity> CAMPFIRE = new TCBlockEntityType<>(TCCreeperCampFireBlockEntity::new, TCBlockCore.CREEPER_CAMPFIRE);
    public static final BlockEntityType<TCCreeperSignBlockEntity> SIGN = new TCBlockEntityType<>(TCCreeperSignBlockEntity::new, TCBlockCore.CREEPER_SIGN, TCBlockCore.CREEPER_SIGN_WALL);
    public static final BlockEntityType<TCCreeperHangingSignBlockEntity> HANGING_SIGN = new TCBlockEntityType<>(TCCreeperHangingSignBlockEntity::new, TCBlockCore.CREEPER_HANGING_SIGN, TCBlockCore.CREEPER_HANGING_SIGN_WALL);

    public static final BlockEntityType<TCCreeperSuperBlockEntity> SUPER_BLOCK = new TCBlockEntityType<>(TCCreeperSuperBlockEntity::new, TCBlockCore.SUPER_BLOCK);
    public static final BlockEntityType<TCTakenokoBlockEntity> TAKENOKO = new TCBlockEntityType<>(TCTakenokoBlockEntity::new, TCBlockCore.TAKENOKO);
    public static final BlockEntityType<TCCreeperProtectorBlockEntity> PROTECTOR = new TCBlockEntityType<>(TCCreeperProtectorBlockEntity::new, TCBlockCore.CREEPER_PROTECTOR);


}
