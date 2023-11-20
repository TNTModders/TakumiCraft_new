package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.TCCreeperGlassPaneBlock;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class TCBlockStateProvider extends BlockStateProvider {
    public TCBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TakumiCraftCore.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        TCLoggingUtils.startRegistry("BlockStateModel");
        TCBlockCore.BLOCKS.forEach(block -> {
            if (block instanceof ITCBlocks) {
                switch (((ITCBlocks) block).getBlockStateModelType()) {
                    case SIMPLE -> this.simpleBlockWithItem(block);
                    case HALF -> this.halfBlockWithItem(block);
                    case GLASS -> this.glassBlockWithItem(block);
                    case STAINED_GLASS -> this.stainedGlassBlockWithItem(block);
                    case PANE_GLASS, PANE_STAINED_GLASS ->
                            this.paneGlassBlockWithItem(block, ((ITCBlocks) block).getBlockStateModelType());
                }
                TCLoggingUtils.entryRegistry("BlockStateModel_" + ((ITCBlocks) block).getBlockStateModelType().name(), ((ITCBlocks) block).getRegistryName());
            }
        });

        TCLoggingUtils.completeRegistry("BlockStateModel");
    }

    private void simpleBlockWithItem(Block block) {
        ModelFile model = this.cubeAll(block);
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    private void glassBlockWithItem(Block block) {
        ModelFile model = this.glassCubeAll(block);
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    private ModelFile glassCubeAll(Block block) {
        return models().cubeAll(name(block), blockTexture(block)).renderType("cutout");
    }

    private void paneGlassBlockWithItem(Block block, ITCBlocks.EnumTCBlockStateModelType type) {
        if (block instanceof TCCreeperGlassPaneBlock pane) {
            ResourceLocation sourceName = blockFolder(key(pane.getBaseTakumiBlock()));
            if (type == ITCBlocks.EnumTCBlockStateModelType.PANE_GLASS) {
                ResourceLocation topName = blockFolder(new ResourceLocation(TakumiCraftCore.MODID, "creeperglasspane_top"));
                this.paneBlockWithRenderType(pane, sourceName, topName, type.getType());
            } else {
                this.paneBlockWithRenderType(pane, sourceName, sourceName, type.getType());
            }
            itemModels().getBuilder(key(block).getPath())
                    .parent(new ModelFile.UncheckedModelFile("item/generated"))
                    .texture("layer0", sourceName)
                    .renderType(type.getType());
        }

    }

    private void stainedGlassBlockWithItem(Block block) {
        ModelFile model = this.stainedGlassCubeAll(block);
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    private ModelFile stainedGlassCubeAll(Block block) {
        return models().cubeAll(name(block), blockTexture(block)).renderType("translucent");
    }

    private void halfBlockWithItem(Block block) {
        if (block instanceof SlabBlock) {
            ModelFile model = this.cubeAll(block);
            this.slabBlock((SlabBlock) block, model, model, model);
        }
    }

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    private ResourceLocation blockFolder(ResourceLocation name) {
        return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + '/' + name.getPath());
    }

    private String name(Block block) {
        return key(block).getPath();
    }


}
