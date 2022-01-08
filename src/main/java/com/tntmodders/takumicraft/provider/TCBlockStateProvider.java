package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TCBlockStateProvider extends BlockStateProvider {
    public TCBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, TakumiCraftCore.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        TCLoggingUtils.startRegistry("BlockStateModel");
        TCBlockCore.BLOCKS.forEach(block -> {
            if (block instanceof ITCBlocks) {
                switch (((ITCBlocks) block).getBlockStateModelType()) {
                    case SIMPLE -> this.simpleBlockWithItem(block);
                    case HALF -> this.halfBlockWithItem(block);
                }
                TCLoggingUtils.entryRegistry("BlockStateModel_" + ((ITCBlocks) block).getBlockStateModelType().name(), block.getRegistryName().getPath());
            }
        });
        TCLoggingUtils.completeRegistry("BlockStateModel");
    }

    private void simpleBlockWithItem(Block block) {
        ModelFile model = this.cubeAll(block);
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    private void halfBlockWithItem(Block block) {
        if (block instanceof SlabBlock) {
            ModelFile model = this.cubeAll(block);
            this.slabBlock(((SlabBlock) block), model, model, model);
        }
    }
}
