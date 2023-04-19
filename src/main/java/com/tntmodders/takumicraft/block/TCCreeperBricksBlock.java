package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.List;

public class TCCreeperBricksBlock extends AbstractTCAntiExplosionBlock {
    public TCCreeperBricksBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
    }

    @Override
    public String getRegistryName() {
        return "creeperbrick";
    }

    @Override
    public String getEnUSName() {
        return "Creeper Bricks Block";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質煉瓦";
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION, BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE);
    }
}
