package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public abstract class AbstractTCAntiExplosionBlock extends Block implements ITCBlocks {

    public AbstractTCAntiExplosionBlock(Properties properties) {
        super(properties.explosionResistance(1000000f));

    }

    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return () -> new TCBlockLoot(block, true);
    }
}
