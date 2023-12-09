package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.TCCreeperBedBlock;
import com.tntmodders.takumicraft.block.entity.TCCreeperBedBlockEntity;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;


public class TCBlockEntityCore {
    public static final BlockEntityType<TCCreeperBedBlockEntity> CREEPER_BED = BlockEntityType.Builder.of((pos, state) -> state.getBlock() instanceof TCCreeperBedBlock ? new TCCreeperBedBlockEntity(pos, state, ((TCCreeperBedBlock) state.getBlock()).getColor()) : new TCCreeperBedBlockEntity(pos, state), TCBlockCore.CREEPER_BED_MAP.values().toArray(new Block[0])).build(Util.fetchChoiceType(References.BLOCK_ENTITY, "creeperbed"));

    public static void register(final RegisterEvent event) {
        TCLoggingUtils.startRegistry("BlockEntity");
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), new ResourceLocation(TakumiCraftCore.MODID, "creeperbed"), () -> CREEPER_BED);
        TCLoggingUtils.completeRegistry("BlockEntity");
    }
}
