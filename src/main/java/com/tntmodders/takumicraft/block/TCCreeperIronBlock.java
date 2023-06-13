package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;

public class TCCreeperIronBlock extends AbstractTCAntiExplosionBlock {
    public TCCreeperIronBlock() {
        super(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));
    }

    @Override
    public String getRegistryName() {
        return "creeperiron";
    }

    @Override
    public String getEnUSName() {
        return "Creeper Iron Block";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質鋼";
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION, BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE);
    }
}
