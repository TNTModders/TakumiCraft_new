package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.TCCreeperBedBlock;
import com.tntmodders.takumicraft.block.entity.TCAcidBlockEntity;
import com.tntmodders.takumicraft.block.entity.TCCreeperBedBlockEntity;
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
    private static final List<Block> CREEPER_BEDS = new ArrayList<>();
    public static BlockEntityType<TCCreeperBedBlockEntity> CREEPER_BED;
    public static BlockEntityType<TCAcidBlockEntity> ACID;

    static {
        CREEPER_BEDS.addAll(TCBlockCore.CREEPER_BED_MAP.values());
        CREEPER_BEDS.add(TCBlockCore.SUPER_CREEPER_BED);
    }

    public static void register(final RegisterEvent event) {
        TCLoggingUtils.startRegistry("BlockEntity");
        CREEPER_BED = BlockEntityType.Builder.of((pos, state) ->
                state.getBlock() instanceof TCCreeperBedBlock ? new TCCreeperBedBlockEntity(pos, state, ((TCCreeperBedBlock) state.getBlock()).getColor(), state.getBlock() == TCBlockCore.SUPER_CREEPER_BED) : new TCCreeperBedBlockEntity(pos, state), CREEPER_BEDS.toArray(new Block[0])).build(Util.fetchChoiceType(References.BLOCK_ENTITY, "creeperbed"));
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), new ResourceLocation(TakumiCraftCore.MODID, "creeperbed"), () -> CREEPER_BED);
        ACID = BlockEntityType.Builder.of(TCAcidBlockEntity::new, TCBlockCore.ACID).build(Util.fetchChoiceType(References.BLOCK_ENTITY, "acidblock"));
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), new ResourceLocation(TakumiCraftCore.MODID, "acidblock"), () -> ACID);
        TCLoggingUtils.completeRegistry("BlockEntity");
    }
}
