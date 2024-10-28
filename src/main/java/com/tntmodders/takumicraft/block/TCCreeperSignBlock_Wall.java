package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.entity.TCCreeperSignBlockEntity;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.world.level.block.state.properties.TCWoodType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import java.util.function.Function;

public class TCCreeperSignBlock_Wall extends WallSignBlock implements ITCBlocks {


    public TCCreeperSignBlock_Wall() {
        super(TCWoodType.CREEPER_WOOD, TCBlockCore.variant(TCBlockCore.CREEPER_SIGN, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F, 1000000F).ignitedByLava());
    }

    @Override
    public String getRegistryName() {
        return "creepersign_wall";
    }

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return null;
    }

    @Override
    public String getEnUSName() {
        return "Creeper Sign";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質看板";
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        provider.simpleBlock(this, provider.models().sign(this.getRegistryName(), provider.blockTexture(TCBlockCore.CREEPER_SIGN)));
        ResourceLocation name = ResourceLocation.tryBuild(TakumiCraftCore.MODID, this.getRegistryName());
        provider.itemModels().singleTexture(name.getPath(), provider.mcLoc("item/generated"), "layer0", ResourceLocation.tryBuild(name.getNamespace(), "block/creepersign"));
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return null;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_154556_, BlockState p_154557_) {
        return new TCCreeperSignBlockEntity(p_154556_, p_154557_);
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION, BlockTags.WALL_SIGNS);
    }
}
