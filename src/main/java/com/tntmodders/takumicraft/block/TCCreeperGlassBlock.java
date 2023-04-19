package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import java.util.function.Supplier;

public class TCCreeperGlassBlock extends AbstractGlassBlock implements ITCBlocks {
    public TCCreeperGlassBlock() {
        super(BlockBehaviour.Properties.of(Material.GLASS)
                .strength(6f)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn((state, getter, pos, type) -> TCBlockCore.never(state, getter, pos))
                .isRedstoneConductor(TCBlockCore::never).isSuffocating(TCBlockCore::never).isViewBlocking(TCBlockCore::never).explosionResistance(1000000f));
    }

    @Override
    public EnumTCBlockStateModelType getBlockStateModelType() {
        return EnumTCBlockStateModelType.GLASS;
    }

    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return () -> new TCBlockLoot(block, true);
    }

    @Override
    public String getRegistryName() {
        return "creeperglass";
    }

    @Override
    public String getEnUSName() {
        return "Creeper Glass";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質硝子";
    }
}
