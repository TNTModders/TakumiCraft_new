package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public abstract class AbstractTCAntiExplosionBlock extends Block implements ITCBlocks {

    public AbstractTCAntiExplosionBlock(Properties properties) {
        super(properties.explosionResistance(1000000f));

    }

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }
}
