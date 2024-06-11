package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import java.util.List;
import java.util.function.Supplier;

public class TCCreeperTorchBlock_Wall extends WallTorchBlock implements ITCBlocks {
    public TCCreeperTorchBlock_Wall() {
        super(ParticleTypes.SMOKE, BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel(p_50886_ -> 15).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY).lootFrom(() -> TCBlockCore.CREEPER_TORCH).explosionResistance(10000000f));
    }

    @Override
    public String getRegistryName() {
        return "creepertorch_wall";
    }

    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return null;
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return null;
    }

    @Override
    public String getEnUSName() {
        return "Creeper Torch";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質松明";
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        provider.horizontalBlock(this, provider.models().withExistingParent(provider.key(this).toString(), "block/wall_torch").texture("torch", provider.blockTexture(TCBlockCore.CREEPER_TORCH)).renderType("cutout"), 90);
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION, BlockTags.WALL_POST_OVERRIDE);
    }
}
