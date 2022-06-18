package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.provider.ITCBlocks;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractTCAntiExplosionBlock extends Block implements ITCBlocks {

    public AbstractTCAntiExplosionBlock(Properties properties) {
        super(properties.explosionResistance(1000000f));

    }

    @Override
    public Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>> getBlockLoot(Block block) {
        return () -> new BlockLoot() {

            @Override
            protected Iterable<Block> getKnownBlocks() {
                return List.of(block);
            }

            @Override
            protected void addTables() {
                this.dropSelf(block);
            }
        };
    }
}
