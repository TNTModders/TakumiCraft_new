package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.provider.ITCBlocks;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class TCYukariDummyBlock extends Block implements ITCBlocks {
    public TCYukariDummyBlock() {
        super(Properties.of().strength(0f, 0f));
    }

    @Override
    public String getRegistryName() {
        return "yukaridummy";
    }

    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return null;
    }

    @Override
    public String getJaJPName() {
        return "ゆかり式高性能爆弾";
    }

    @Override
    public String getEnUSName() {
        return "Yukari Creeper Bomb";
    }
}
